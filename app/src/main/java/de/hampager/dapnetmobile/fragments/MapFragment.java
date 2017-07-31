package de.hampager.dapnetmobile.fragments;

import android.Manifest;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.icu.text.IDNA;
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
import android.widget.Toast;

import org.osmdroid.api.IMapController;
import org.osmdroid.config.Configuration;
import org.osmdroid.events.MapEventsReceiver;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.FolderOverlay;
import org.osmdroid.views.overlay.ItemizedIconOverlay;
import org.osmdroid.views.overlay.ItemizedOverlayWithFocus;
import org.osmdroid.views.overlay.MapEventsOverlay;
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
    private MapView map;
    //private OnFragmentInteractionListener mListener;
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
    //MapEventsOverlay mapEventsOverlay = new MapEventsOverlay(getContext(), this);
    FolderOverlay fo;
    private String server;
    private String user;
    private String password;
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
        //setContentView(R.layout.activity_main);
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

        //map.setTilesScaledToDpi(true);
        map.setFlingEnabled(true);
        IMapController mapController = map.getController();
        mapController.setZoom(6);
        GeoPoint startPoint = new GeoPoint(50.77623, 6.06937);
        mapController.setCenter(startPoint);

        SharedPreferences sharedPref = this.getActivity().getSharedPreferences("sharedPref", Context.MODE_PRIVATE);
        server = sharedPref.getString("server", "http://www.hampager.de:8080");
        user = sharedPref.getString("user", "invalid");
        password = sharedPref.getString("pass", "invalid");

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
            t.setDescriptionBoxCornerWidth(32);
            t.setDescriptionBoxPadding(6);
            t.setDescriptionMaxWidth(200);
            //map.getOverlays().add(t);
            fo.add(t);
        }
        map.getOverlays().add(fo);
        //map.getOverlays().add(0,mapEventsOverlay);

        ewOverlay.setFocusItemsOnTap(true);
    }

    @Override public boolean singleTapConfirmedHelper(GeoPoint p) {
        //Toast.makeText(getContext(), "Tapped", Toast.LENGTH_SHORT).show();
        //InfoWindow.closeAllInfoWindowsOn(map);
        Toast.makeText(getContext(),""+map.getOverlays().size(),Toast.LENGTH_SHORT).show();
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
                        OverlayItem temp = new OverlayItem(t.getName(),t.toString(),new GeoPoint(t.getLatitude(),t.getLongitude()));
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
                        //items.add(items.size(),temp);
                    }
                    config();
                } else {
                    //APIError error = ErrorUtils.parseError(response);
                    Log.e(TAG, "Error " + response.code());
                    Log.e(TAG, response.message());
                    //Snackbar.make(getView, "Error! " + response.code() + " " + response.message(), Snackbar.LENGTH_LONG).setAction("Action", null).show();
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
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_REQUEST_WRITE_EXTERNAL_STORAGE: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.

                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater){
        this.menu=menu;
        inflater.inflate(R.menu.mapfilter, menu);
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        item.setChecked(!item.isChecked());
        /*int id = item.getItemId();
        if(id == R.id.online_filter){
            item.setChecked(!item.isChecked());
            Overlay currOv = map.getOverlays().get(map.getOverlays().indexOf(ewOverlay));
            /*if (map.getOverlays().get(0).isEnabled()){
                Log.i(TAG,"Overlay 0 enabled");
                map.getOverlays().get(0).setEnabled(false);
            }else{
                map.getOverlays().get(0).setEnabled(true);
            }*/
            /*if (ewOverlay.isEnabled()){
                Log.i(TAG,"Overlay 0 enabled");
                ewOverlay.setEnabled(false);
                item.setChecked(false);
            }else{
                ewOverlay.setEnabled(true);
                item.setChecked(true);
            }
            
            return true;
        }*/
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
        //Quick Hack used to refresh map
        IMapController mapController = map.getController();
        //mapController.scrollBy(1,1);
        //mapController.scrollBy(-1,-1);
        map.invalidate();
        InfoWindow.closeAllInfoWindowsOn(map);

        return true;

        //return super.onOptionsItemSelected(item);
    }
    public void onResume(){
        super.onResume();
        //this will refresh the osmdroid configuration on resuming.
        //if you make changes to the configuration, use
        //SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        //Configuration.getInstance().save(this, prefs);
        Configuration.getInstance().load(getActivity().getApplicationContext(), PreferenceManager.getDefaultSharedPreferences(getActivity().getApplicationContext()));
    }
    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        /*if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }*/
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        /*if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }*/
    }

    @Override
    public void onDetach() {
        super.onDetach();
        //mListener = null;
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
    /*
    public interface OnFragmentInteractionListener {

        void onFragmentInteraction(Uri uri);
    }*/
}
