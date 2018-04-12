package de.hampager.dapnetmobile.fragments;

import android.Manifest;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Paint;
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
import android.widget.Toast;

import org.osmdroid.api.IGeoPoint;
import org.osmdroid.api.IMapController;
import org.osmdroid.config.Configuration;
import org.osmdroid.events.MapEventsReceiver;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.FolderOverlay;
import org.osmdroid.views.overlay.infowindow.InfoWindow;
import org.osmdroid.views.overlay.simplefastpoint.LabelledGeoPoint;
import org.osmdroid.views.overlay.simplefastpoint.SimpleFastPointOverlay;
import org.osmdroid.views.overlay.simplefastpoint.SimpleFastPointOverlayOptions;
import org.osmdroid.views.overlay.simplefastpoint.SimplePointTheme;

import java.util.ArrayList;
import java.util.List;

import de.hampager.dapnetmobile.BuildConfig;
import de.hampager.dapnetmobile.R;
import de.hampager.dapnetmobile.api.HamPagerService;
import de.hampager.dapnetmobile.api.ServiceGenerator;
import de.hampager.dapnetmobile.api.TransmitterResource;
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
    Menu menu;
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
        FolderOverlay onlineWideRangeFolder = new FolderOverlay();
        FolderOverlay onlinePersonalFolder = new FolderOverlay();
        FolderOverlay offlineWideRangeFolder = new FolderOverlay();
        FolderOverlay offlinePersonalFolder = new FolderOverlay();


        map.getOverlays().add(onlineWideRangeFolder);

    }

    private void fastOverlay() {
        // create 10k labelled points
        // in most cases, there will be no problems of displaying >100k points, feel free to try
        List<IGeoPoint> points = new ArrayList<>();
        for (int i = 0; i < 10000; i++) {
            points.add(new LabelledGeoPoint(37 + Math.random() * 5, -8 + Math.random() * 5
                    , "Point #" + i));
        }

        // wrap them in a theme
        SimplePointTheme pt = new SimplePointTheme(points, true);

        // create label style
        Paint textStyle = new Paint();
        textStyle.setStyle(Paint.Style.FILL);
        textStyle.setColor(Color.parseColor("#0000ff"));
        textStyle.setTextAlign(Paint.Align.CENTER);
        textStyle.setTextSize(24);

        // set some visual options for the overlay
        // we use here MAXIMUM_OPTIMIZATION algorithm, which works well with >100k points
        SimpleFastPointOverlayOptions opt = SimpleFastPointOverlayOptions.getDefaultStyle()
                .setAlgorithm(SimpleFastPointOverlayOptions.RenderingAlgorithm.MAXIMUM_OPTIMIZATION)
                .setRadius(7).setIsClickable(true).setCellSize(15).setTextStyle(textStyle);

        // create the overlay with the theme
        final SimpleFastPointOverlay sfpo = new SimpleFastPointOverlay(pt, opt);

        // onClick callback
        sfpo.setOnClickListener(new SimpleFastPointOverlay.OnClickListener() {
            @Override
            public void onClick(SimpleFastPointOverlay.PointAdapter points, Integer point) {
                Toast.makeText(map.getContext()
                        , "You clicked " + ((LabelledGeoPoint) points.get(point)).getLabel()
                        , Toast.LENGTH_SHORT).show();
            }
        });

        // add overlay
        map.getOverlays().add(sfpo);
    }
    @Override public boolean singleTapConfirmedHelper(GeoPoint p) {
        //TODO: close Windows?
        //fo.closeAllInfoWindows();
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
        StringBuilder s = new StringBuilder();
        String dot = ": ";
        Context res = getContext();
        s.append(res.getString(R.string.type));
        s.append(dot);
        s.append(TrRe.getUsage());
        s.append("\n");
        s.append(res.getString(R.string.transmission_power));
        s.append(dot);
        s.append(Double.toString(TrRe.getPower()));
        s.append("\n");
        if (TrRe.getTimeSlot().length() > 1) s.append(res.getString(R.string.timeslots));
        else s.append(res.getString(R.string.timeslot));
        s.append(dot);
        s.append(TrRe.getTimeSlot());
        s.append("\n");
        if (TrRe.getOwnerNames().size() > 1) s.append(res.getString(R.string.owners));
        else s.append(res.getString(R.string.owner));
        s.append(dot);
        for (String temp : TrRe.getOwnerNames()) {
            s.append(temp).append(" ");
        }
        return s.toString();
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