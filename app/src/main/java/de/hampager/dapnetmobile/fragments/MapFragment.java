package de.hampager.dapnetmobile.fragments;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
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
import org.osmdroid.bonuspack.clustering.RadiusMarkerClusterer;
import org.osmdroid.config.Configuration;
import org.osmdroid.events.MapEventsReceiver;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.FolderOverlay;
import org.osmdroid.views.overlay.MapEventsOverlay;
import org.osmdroid.views.overlay.Marker;
import org.osmdroid.views.overlay.infowindow.InfoWindow;
import org.osmdroid.views.overlay.infowindow.MarkerInfoWindow;
import org.osmdroid.views.overlay.simplefastpoint.LabelledGeoPoint;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import de.hampager.dap4j.DAPNET;
import de.hampager.dap4j.DapnetSingleton;
import de.hampager.dap4j.callbacks.DapnetListener;
import de.hampager.dap4j.callbacks.DapnetResponse;
import de.hampager.dap4j.models.Transmitter;
import de.hampager.dapnetmobile.BuildConfig;
import de.hampager.dapnetmobile.R;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * { MapFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link MapFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MapFragment extends Fragment implements MapEventsReceiver {
    private static final String TAG = "MapFragment";
    static final int PERMISSION_REQUEST_WRITE_EXTERNAL_STORAGE = 1;

    public static final String BR = "<br/>";

    private static final GeoPoint START_POINT_DEFAULT = new GeoPoint(50.77623, 6.06937);

    Menu menu;
    private MapView map;
    private List<Transmitter> transmitterList = new ArrayList<>();

    private RadiusMarkerClusterer onWClusterer, onPClusterer, ofWClusterer, ofPClusterer;
    private FolderOverlay onlineWideRangeFolder = new FolderOverlay();
    private FolderOverlay onlinePersonalFolder = new FolderOverlay();
    private FolderOverlay offlineWideRangeFolder = new FolderOverlay();
    private FolderOverlay offlinePersonalFolder = new FolderOverlay();

    OnWelcomeFragmentListener mListener;

    /** Required public constructor */
    public MapFragment() { /* empty */ }

    /**
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
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_map, container, false);
        Context ctx = Objects.requireNonNull(getActivity()).getApplicationContext();

        //important! set your user agent to prevent getting banned from the osm servers
        Configuration.getInstance().load(ctx, PreferenceManager.getDefaultSharedPreferences(ctx));
        Configuration.getInstance().setUserAgentValue(BuildConfig.APPLICATION_ID);

        if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {

            // TODO: Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
            }
            else {
                // No explanation needed, we can request the permission.
                // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an app-defined int constant.
                // The callback method gets the result of the request.
                ActivityCompat.requestPermissions(getActivity(),
                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        PERMISSION_REQUEST_WRITE_EXTERNAL_STORAGE);
            }
        }
        map = v.findViewById(R.id.map);
        map.setTileSource(TileSourceFactory.MAPNIK);
        map.setBuiltInZoomControls(false); // disable overlay zoom controls for now
        map.setMultiTouchControls(true);
        map.setFlingEnabled(true);
        map.setMinZoomLevel(2.5);
        IMapController mapController = map.getController();
        mapController.setZoom(5.0);

        // Set center start point
        String localeString = Locale.getDefault().toString();
        GeoPoint startPoint;
        // TODO: check other locations (Locale/language- NZ/Aus, Chn/Jpn/Thai)
        if (localeString.equals(Locale.US.toString())) {
            startPoint = new LabelledGeoPoint(39.50,-98.35);
        }
        else {
            startPoint = START_POINT_DEFAULT; // Europe
        }
        mapController.setCenter(startPoint);

        try {
            onWClusterer = new RadiusMarkerClusterer(Objects.requireNonNull(getContext()));
            onPClusterer = new RadiusMarkerClusterer(getContext());
            ofWClusterer = new RadiusMarkerClusterer(getContext());
            ofPClusterer = new RadiusMarkerClusterer(getContext());
            onlineWideRangeFolder.add(onWClusterer);
            onlinePersonalFolder.add(onPClusterer);
            offlineWideRangeFolder.add(ofWClusterer);
            offlinePersonalFolder.add(ofPClusterer);
            fetchJSON();
        }
        catch (Exception e) {
            Log.e(TAG,"Context missing?");
        }
        return v;
    }

    private void config() {
        try {
            map.getOverlays().add(new MapEventsOverlay(this));
            Drawable onlineWiderangeMarker = getResources().getDrawable(R.mipmap.ic_radiotower_green);
            Drawable offlineWiderangeMarker = getResources().getDrawable(R.mipmap.ic_radiotower_red);
            Drawable onlinePersonalMarker = getResources().getDrawable(R.drawable.transmitter_personal_online);
            Drawable offlinePersonalMarker = getResources().getDrawable(R.drawable.transmitter_personal_offline);
            for (Transmitter t : transmitterList) {
                Marker tempMarker = new Marker(map);
                tempMarker.setPosition(new GeoPoint(t.getLatitude(), t.getLongitude()));
                tempMarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
                tempMarker.setSnippet(getDesc(t));
                tempMarker.setTitle(t.getName());
                tempMarker.setInfoWindow(new MarkerInfoWindow(R.layout.custom_info_window, map));
                if (t.getStatus().equals("ONLINE")) {
                    if (t.getUsage().equals("WIDERANGE")) {
                        tempMarker.setIcon(onlineWiderangeMarker);
                        onWClusterer.add(tempMarker);
                    } else {
                        tempMarker.setIcon(onlinePersonalMarker);
                        onPClusterer.add(tempMarker);
                    }
                }
                else {
                    if (t.getUsage().equals("WIDERANGE")) {
                        tempMarker.setIcon(offlineWiderangeMarker);
                        ofWClusterer.add(tempMarker);
                    } else {
                        tempMarker.setIcon(offlinePersonalMarker);
                        ofPClusterer.add(tempMarker);
                    }
                }
            }
            map.getOverlays().add(onlineWideRangeFolder);
            map.invalidate();
        }
        catch (Exception e) {
            Log.e(TAG,"Context missing?");
        }
    }

    @Override
    public boolean singleTapConfirmedHelper(GeoPoint p) {
        onlineWideRangeFolder.closeAllInfoWindows();
        offlineWideRangeFolder.closeAllInfoWindows();
        onlinePersonalFolder.closeAllInfoWindows();
        offlinePersonalFolder.closeAllInfoWindows();
        return true;
    }

    @Override
    public boolean longPressHelper(GeoPoint p) {
        return false;
    }

    private void fetchJSON() {
        DAPNET dapnet = DapnetSingleton.getInstance().getDapnet();
        dapnet.getAllTransmitters(new DapnetListener<List<Transmitter>>() {
            @Override
            public void onResponse(DapnetResponse<List<Transmitter>> dapnetResponse) {
                if (dapnetResponse.isSuccessful()) {
                    Log.i(TAG, "Connection was successful.");
                    // tasks available
                    transmitterList = dapnetResponse.body();
                    config();
                } else {
                    Log.e(TAG, "Error.");
                    //TODO: implement .code,.message
                }
            }

            @Override
            public void onFailure(Throwable throwable) {
                // something went completely wrong (e.g. no internet connection)
                Log.e(TAG, throwable.getMessage());
            }
        });
    }

    /**
     * Creates a String from the selected Transmitter node.
     *
     * @param transmitter  The selected Transmitter node
     * @return The generated String
     */
    private String getDesc(Transmitter transmitter) {
        StringBuilder s = new StringBuilder();
        String dot = ": ";
        Context res = getContext();
        if (res != null) {
            s.append(res.getString(R.string.type))
                    .append(dot)
                    .append(transmitter.getUsage())
                    .append(BR)
                    .append(res.getString(R.string.transmission_power))
                    .append(dot)
                    .append(transmitter.getPower())
                    .append(BR);
            s.append((transmitter.getTimeSlot().length() > 1) ? res.getString(R.string.timeslots) : res.getString(R.string.timeslot))
                    .append(dot)
                    .append(transmitter.getTimeSlot())
                    .append(BR);
            if (transmitter.getOwnerNames().size() > 1) {
                s.append(res.getString(R.string.owners)).append(dot);
                for (String temp : transmitter.getOwnerNames()) {
                    s.append(temp).append(",");
                }
            }
            else {
                s.append(res.getString(R.string.owner)).append(dot).append(transmitter.getOwnerNames().get(0));
            }
        }
        return s.toString();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        Log.i(TAG, "Permission");

        // To use more Permissions you should implement a switch
        if (requestCode == PERMISSION_REQUEST_WRITE_EXTERNAL_STORAGE) {
            // If request is cancelled, the result arrays are empty.
            Log.i(TAG, "Permission request");
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // permission was granted
                Log.i(TAG, "Permission granted");
            } else {
                // permission denied
                Log.i(TAG, "Permission not granted");
            }
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        this.menu = menu;
        inflater.inflate(R.menu.mapfilter, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        item.setChecked(!item.isChecked());
        boolean onlineEnabled = menu.findItem(R.id.online_filter).isChecked();
        boolean offlineEnabled = menu.findItem(R.id.offline_filter).isChecked();
        boolean wideRangeEnabled = menu.findItem(R.id.widerange_filter).isChecked();
        boolean personalEnabled = menu.findItem(R.id.personal_filter).isChecked();

        if (onlineEnabled) {
            mapOnlineEnabledCheck(wideRangeEnabled, personalEnabled);
        } else {
            map.getOverlays().remove(onlineWideRangeFolder);
            map.getOverlays().remove(onlinePersonalFolder);
        }

        if (offlineEnabled) {
            mapOfflineEnabledCheck(wideRangeEnabled,personalEnabled);
        } else {
            map.getOverlays().remove(offlineWideRangeFolder);
            map.getOverlays().remove(offlinePersonalFolder);
        }

        // To communicate with MainActivity and WelcomeFragment;
        // MainActivity responds through OnWelcomeFragmentListener.setMapFull(boolean)
        // to call WelcomeFragment.setMapFull(boolean).
        mListener.setMapFull(menu.findItem(R.id.full_filter).isChecked());

        map.invalidate();
        InfoWindow.closeAllInfoWindowsOn(map);
        return true;
    }

    private void mapOnlineEnabledCheck(boolean wideRangeEnabled, boolean personalEnabled){
        if (wideRangeEnabled) {
            if (!map.getOverlays().contains(onlineWideRangeFolder)) {
                map.getOverlays().add(onlineWideRangeFolder);
            }
        }
        else {
            map.getOverlays().remove(onlineWideRangeFolder);
        }
        if (personalEnabled) {
            if (!map.getOverlays().contains(onlinePersonalFolder)) {
                map.getOverlays().add(onlinePersonalFolder);
            }
        }
        else {
            map.getOverlays().remove(onlinePersonalFolder);
        }
    }

    private void mapOfflineEnabledCheck(boolean wideRangeEnabled, boolean personalEnabled){
        if (wideRangeEnabled) {
            if (!map.getOverlays().contains(offlineWideRangeFolder)) {
                map.getOverlays().add(offlineWideRangeFolder);
            }
        }
        else {
            map.getOverlays().remove(offlineWideRangeFolder);
        }
        if (personalEnabled) {
            if (!map.getOverlays().contains(offlinePersonalFolder)) {
                map.getOverlays().add(offlinePersonalFolder);
            }
        }
        else {
            map.getOverlays().remove(offlinePersonalFolder);
        }
    }

    /** Refreshes the osmdroid configuration on resuming. */
    @Override
    public void onResume() {
        super.onResume();
        // if you make changes to the configuration, use:
        // SharedPreferences prefs = Preference Manager.getDefaultSharedPreferences(this);
        Configuration.getInstance().load(Objects.requireNonNull(getActivity()).getApplicationContext(),
                PreferenceManager.getDefaultSharedPreferences(getActivity().getApplicationContext()));
    }

    public void onButtonPressed(Uri uri) { /* Not yet implemented */ }

    /** Listener for MapFragment.onOptionsItemSelected(MenuItem). */
    public interface OnWelcomeFragmentListener {
        /**
         *  To communicate between MapFragment and MainActivity+WelcomeFragment;
         *  MainActivity responds through OnWelcomeFragmentListener.setMapFull(boolean)
         *  to call WelcomeFragment.setMapFull(boolean).
         *
         * @param fullscreenChecked  Flag to set WelcomeFragment map to fullscreen
         * @return fullscreenChecked
         */
        boolean setMapFull(boolean fullscreenChecked);
    }

    // region for listener
    @Override
    public void onStart() {
        super.onStart();
        try {
            mListener = (OnWelcomeFragmentListener) getActivity();
        }
        catch (ClassCastException cce) {
            Log.e(TAG, cce.getMessage());
            //throw new ClassCastException(getActivity().toString() + " must implement FragmentInteractionListener.");
        }
        catch (NullPointerException npe) {
            Log.e(TAG, npe.getMessage());
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }
    // endregion for listener

}
