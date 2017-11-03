package de.hampager.dapnetmobile.fragments;

import android.Manifest;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import org.osmdroid.api.IMapController;
import org.osmdroid.config.Configuration;
import org.osmdroid.events.MapEventsReceiver;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.FolderOverlay;
import org.osmdroid.views.overlay.ItemizedIconOverlay;
import org.osmdroid.views.overlay.ItemizedOverlayWithFocus;
import org.osmdroid.views.overlay.OverlayItem;
import org.osmdroid.views.overlay.infowindow.InfoWindow;

import java.util.ArrayList;

import de.hampager.dapnetmobile.BuildConfig;
import de.hampager.dapnetmobile.R;
import de.hampager.dapnetmobile.api.HamPagerService;
import de.hampager.dapnetmobile.api.TransmitterResource;
import de.hampager.dapnetmobile.api.ServiceGenerator;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;



/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * { MapFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link MapFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MapFragment extends Fragment implements MapEventsReceiver {
    static final int PERMISSION_REQUEST_WRITE_EXTERNAL_STORAGE=1;
    private static final String TAG = "MapFragment";
    //Items for our Overlays
    ArrayList<OverlayItem> onlineWide = new ArrayList<>();
    ArrayList<OverlayItem> offlineWide = new ArrayList<>();
    ArrayList<OverlayItem> onlinePers = new ArrayList<>();
    ArrayList<OverlayItem> offlinePers = new ArrayList<>();
    ItemizedOverlayWithFocus<OverlayItem> ewOverlay;
    ItemizedOverlayWithFocus<OverlayItem> dwOverlay;
    ItemizedOverlayWithFocus<OverlayItem> epOverlay;
    ItemizedOverlayWithFocus<OverlayItem> dpOverlay;
    Menu menu;
    FolderOverlay fo;
    private MapView map;
    public MapFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     * @return A new instance of fragment MapFragment.
     */
    public static MapFragment newInstance() {
        MapFragment fragment = new MapFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_map, container, false);
        Context ctx = getActivity().getApplicationContext();
        //important! set your user agent to prevent getting banned from the osm servers
        Configuration.getInstance().load(ctx, PreferenceManager.getDefaultSharedPreferences(ctx));
        Configuration.getInstance().setUserAgentValue(BuildConfig.APPLICATION_ID);

        if (ContextCompat.checkSelfPermission(getActivity(),
                Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity() ,Manifest.permission.WRITE_EXTERNAL_STORAGE)) {

                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.

            } else {

                // No explanation needed, we can request the permission.

                ActivityCompat.requestPermissions(getActivity(),
                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        PERMISSION_REQUEST_WRITE_EXTERNAL_STORAGE);

                // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }
        }
        map = (MapView) v.findViewById(R.id.map);
        map.setTileSource(TileSourceFactory.MAPNIK);
        configMap();

        return v;
    }
    private void configMap(){
        map.setBuiltInZoomControls(true);
        map.setMultiTouchControls(true);


        map.setFlingEnabled(true);
        IMapController mapController = map.getController();
        mapController.setZoom(6);
        GeoPoint startPoint = new GeoPoint(50.77623, 6.06937);
        mapController.setCenter(startPoint);

        SharedPreferences sharedPref = this.getActivity().getSharedPreferences("sharedPref", Context.MODE_PRIVATE);
        String server = sharedPref.getString("server", "http://www.hampager.de:8080");
        String user = sharedPref.getString("user", "invalid");
        String password = sharedPref.getString("pass", "invalid");

        fetchJSON(server,user,password);
    }
    private void config(){

        Drawable onlineMarker=getResources().getDrawable(R.mipmap.ic_radiotower_green);
        Drawable offlineMarker=getResources().getDrawable(R.mipmap.ic_radiotower_red);

        int backgroundColor=Color.parseColor("#ffffff");
        ewOverlay=new ItemizedOverlayWithFocus<>(onlineWide,onlineMarker,onlineMarker, backgroundColor,
                new ItemizedIconOverlay.OnItemGestureListener<OverlayItem>() {
                    @Override
                    public boolean onItemSingleTapUp(final int index, final OverlayItem item) {
                        //do something
                        return true;
                    }
                    @Override
                    public boolean onItemLongPress(final int index, final OverlayItem item) {
                        return false;
                    }
                },getContext());
        dwOverlay=new ItemizedOverlayWithFocus<>(offlineWide,offlineMarker,offlineMarker, backgroundColor,
                new ItemizedIconOverlay.OnItemGestureListener<OverlayItem>() {
                    @Override
                    public boolean onItemSingleTapUp(final int index, final OverlayItem item) {
                        //do something
                        return true;
                    }
                    @Override
                    public boolean onItemLongPress(final int index, final OverlayItem item) {
                        return false;
                    }
                },getContext());
        epOverlay=new ItemizedOverlayWithFocus<>(onlinePers,onlineMarker,onlineMarker, backgroundColor,
                new ItemizedIconOverlay.OnItemGestureListener<OverlayItem>() {
                    @Override
                    public boolean onItemSingleTapUp(final int index, final OverlayItem item) {
                        //do something
                        return true;
                    }
                    @Override
                    public boolean onItemLongPress(final int index, final OverlayItem item) {
                        return false;
                    }
                },getContext());
        dpOverlay=new ItemizedOverlayWithFocus<>(offlinePers,offlineMarker,offlineMarker, backgroundColor,
                new ItemizedIconOverlay.OnItemGestureListener<OverlayItem>() {
                    @Override
                    public boolean onItemSingleTapUp(final int index, final OverlayItem item) {
                        //do something
                        return true;
                    }
                    @Override
                    public boolean onItemLongPress(final int index, final OverlayItem item) {
                        return false;
                    }
                },getContext());
        dwOverlay.setEnabled(false);
        epOverlay.setEnabled(false);
        dpOverlay.setEnabled(false);
        ewOverlay.setFocusItemsOnTap(true);
        ItemizedOverlayWithFocus[] ovList={ewOverlay,dwOverlay,epOverlay,dpOverlay};
        fo = new FolderOverlay();
        for(ItemizedOverlayWithFocus t : ovList){
            //t.setDescriptionBoxCornerWidth(32);
            //t.setDescriptionBoxPadding(6);
            //t.setDescriptionMaxWidth(200);

            fo.add(t);
        }

        map.getOverlays().add(fo);

        ewOverlay.setFocusItemsOnTap(true);
    }

    @Override public boolean singleTapConfirmedHelper(GeoPoint p) {
        fo.closeAllInfoWindows();
        return true;
    }

    @Override public boolean longPressHelper(GeoPoint p) {
        //DO NOTHING FOR NOW:
        return false;
    }
    private void fetchJSON(String server, String user, String password) {
        try {
            ServiceGenerator.changeApiBaseUrl(server);
        } catch (java.lang.NullPointerException e) {
            ServiceGenerator.changeApiBaseUrl("http://www.hampager.de:8080");
        }
        HamPagerService service = ServiceGenerator.createService(HamPagerService.class, user, password);
        Call<ArrayList<TransmitterResource>> call;
        call=service.getAllTransmitter();
        call.enqueue(new Callback<ArrayList<TransmitterResource>>() {
            @Override
            public void onResponse(Call<ArrayList<TransmitterResource>> call, Response<ArrayList<TransmitterResource>> response) {
                if (response.isSuccessful()) {
                    Log.i(TAG, "Connection was successful");
                    // tasks available
                    ArrayList<TransmitterResource> data = response.body();
                    for(TransmitterResource t: data){
                        OverlayItem temp = new OverlayItem(t.getName(), getDesc(t), new GeoPoint(t.getLatitude(), t.getLongitude()));
                        if (t.getUsage().equals("WIDERANGE")){
                            if(t.getStatus().equals("ONLINE")){
                                onlineWide.add(onlineWide.size(),temp);
                            }else{
                                offlineWide.add(offlineWide.size(),temp);
                            }
                        }else{
                            if(t.getStatus().equals("ONLINE")){
                                onlinePers.add(onlinePers.size(),temp);
                            }else{
                                offlinePers.add(offlinePers.size(),temp);
                            }
                        }
                    }
                    config();
                } else {
                    Log.e(TAG, "Error " + response.code());
                    Log.e(TAG, response.message());
                    if (response.code() == 401) {
                        SharedPreferences sharedPref = getActivity().getSharedPreferences("sharedPref", Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPref.edit();
                        editor.clear();
                        editor.apply();
                    }
                }
            }

            @Override
            public void onFailure(Call<ArrayList<TransmitterResource>> call, Throwable t) {
                // something went completely wrong (e.g. no internet connection)
                Log.e(TAG, t.getMessage());
            }
        });
    }

    private String getDesc(TransmitterResource TrRe) {
        String s = "";
        String dot = ": ";
        Context res = getActivity();
        s += res.getString(R.string.type);
        s += dot;
        s += TrRe.getUsage();
        s += "\n";
        s += res.getString(R.string.transmission_power);
        s += dot;
        s += Double.toString(TrRe.getPower());
        s += "\n";
        if (TrRe.getTimeSlot().length() > 1) s += res.getString(R.string.timeslots);
        else s += res.getString(R.string.timeslot);
        s += dot;
        s += TrRe.getTimeSlot();
        s += "\n";
        if (TrRe.getOwnerNames().size() > 1) s += res.getString(R.string.owners);
        else s += res.getString(R.string.owner);
        s += dot;
        for (String temp : TrRe.getOwnerNames()) {
            s += temp + " ";
        }
        return s;
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        Log.i(TAG,"Permission");
        if(requestCode==PERMISSION_REQUEST_WRITE_EXTERNAL_STORAGE){
            // If request is cancelled, the result arrays are empty.
            Log.i(TAG,"Permission request");
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // permission was granted
                Log.i(TAG,"Permission granted");
            } else {
                Log.i(TAG,"Permission not granted");
                // permission denied
            }
        }
        //To use more Permissions you should implement a switch
    }
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater){
        this.menu=menu;
        inflater.inflate(R.menu.mapfilter, menu);
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        item.setChecked(!item.isChecked());
        if(menu.getItem(2).isChecked()){
            ewOverlay.setEnabled(menu.getItem(0).isChecked());
            ewOverlay.setFocusItemsOnTap(menu.getItem(0).isChecked());

            dwOverlay.setEnabled(menu.getItem(1).isChecked());
            ewOverlay.setFocusItemsOnTap(menu.getItem(1).isChecked());
        }else {
            //Disable Online&&Offline WiderangeOverlay
            ewOverlay.setEnabled(false);
            ewOverlay.setFocusItemsOnTap(false);
            dwOverlay.setEnabled(false);
            dwOverlay.setFocusItemsOnTap(false);
        }
        if(menu.getItem(3).isChecked()){
            epOverlay.setEnabled(menu.getItem(0).isChecked());
            epOverlay.setFocusItemsOnTap(menu.getItem(0).isChecked());
            dpOverlay.setEnabled(menu.getItem(1).isChecked());
            epOverlay.setFocusItemsOnTap(menu.getItem(1).isChecked());
        }else {
            //Disable Online&&Offline PersOverlay
            epOverlay.setEnabled(false);
            epOverlay.setFocusItemsOnTap(false);
            dpOverlay.setEnabled(false);
            dpOverlay.setFocusItemsOnTap(false);
        }
        map.invalidate();
        InfoWindow.closeAllInfoWindowsOn(map);

        return true;

    }
    @Override
    public void onResume(){
        super.onResume();
        //this will refresh the osmdroid configuration on resuming.
        //if you make changes to the configuration, use
        //Shared Preferences prefs = Preference Manager.getDefault SharedPreferences(this)
        Configuration.getInstance().load(getActivity().getApplicationContext(), PreferenceManager.getDefaultSharedPreferences(getActivity().getApplicationContext()));
    }
    public void onButtonPressed(Uri uri) {
        //Not yet implemented
    }

    /*
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
}