package de.hampager.dapnetmobile.fragments;

import android.Manifest;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
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

import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import org.osmdroid.api.IMapController;
import org.osmdroid.config.Configuration;
import org.osmdroid.events.MapEventsReceiver;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.FolderOverlay;
import org.osmdroid.views.overlay.GroundOverlay2;
import org.osmdroid.views.overlay.MapEventsOverlay;
import org.osmdroid.views.overlay.Marker;
import org.osmdroid.views.overlay.Overlay;
import org.osmdroid.views.overlay.infowindow.InfoWindow;
import org.osmdroid.views.overlay.infowindow.MarkerInfoWindow;

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
    //Member variable needed to prevent garbage collection
    Target target;
    private MapView map;
    private List<TransmitterResource> transmitterList = new ArrayList<>();
    private FolderOverlay onlineWideRangeFolder = new FolderOverlay();
    private FolderOverlay onlinePersonalFolder = new FolderOverlay();
    private FolderOverlay offlineWideRangeFolder = new FolderOverlay();
    private FolderOverlay offlinePersonalFolder = new FolderOverlay();
    private FolderOverlay coverageFolder = new FolderOverlay();
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

    private static Bitmap drawableToBitmap(Drawable drawable) {
        Bitmap bitmap = null;

        if (drawable instanceof BitmapDrawable) {
            BitmapDrawable bitmapDrawable = (BitmapDrawable) drawable;
            if (bitmapDrawable.getBitmap() != null) {
                return bitmapDrawable.getBitmap();
            }
        }

        if (drawable.getIntrinsicWidth() <= 0 || drawable.getIntrinsicHeight() <= 0) {
            bitmap = Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888); // Single color bitmap will be created of 1x1 pixel
        } else {
            bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        }

        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);
        return bitmap;
    }

    private void config(){

        map.getOverlays().add(new MapEventsOverlay(this));
        map.getOverlays().add(coverageFolder);
        Drawable onlineMarker=getResources().getDrawable(R.mipmap.ic_radiotower_green);
        Drawable offlineMarker=getResources().getDrawable(R.mipmap.ic_radiotower_red);

        for (TransmitterResource t : transmitterList) {
            Marker tempMarker = new Marker(map);
            tempMarker.setPosition(new GeoPoint(t.getLatitude(), t.getLongitude()));
            tempMarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
            tempMarker.setSnippet(getDesc(t));
            //map.getOverlays().add(startMarker);
            tempMarker.setTitle(t.getName());
            tempMarker.setInfoWindow(new MarkerInfoWindow(R.layout.custom_info_window, map));
            tempMarker.setOnMarkerClickListener(new Marker.OnMarkerClickListener() {
                @Override
                public boolean onMarkerClick(Marker marker, MapView mapView) {
                    Target target = new Target() {
                        @Override
                        public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                            GroundOverlay2 groundOverlay = new GroundOverlay2();
                            groundOverlay.setTransparency(0.4f);
                            groundOverlay.setImage(bitmap);
                            groundOverlay.setPosition(new GeoPoint(51.6755, 4.64684), new GeoPoint(49.8768, 7.49137));
                            if (!coverageFolder.getItems().contains(groundOverlay)) {
                                coverageFolder.add(groundOverlay);
                            }
                        }

                        @Override
                        public void onBitmapFailed(Drawable errorDrawable) {

                        }

                        @Override
                        public void onPrepareLoad(Drawable placeHolderDrawable) {

                        }
                    };
                    Picasso.with(getContext()).load("https://hampager.de/assets/coverage/" + t.getName().toLowerCase()).resize(1024, 1024).centerCrop().placeholder(R.drawable.loading_coverage).into(target);
                    return false;
                }
            });
            if (t.getStatus().equals("ONLINE")) {
                tempMarker.setIcon(onlineMarker);
                if (t.getUsage().equals("WIDERANGE")) {
                    onlineWideRangeFolder.add(tempMarker);
                } else onlinePersonalFolder.add(tempMarker);
            } else {
                tempMarker.setIcon(offlineMarker);
                if (t.getUsage().equals("WIDERANGE")) offlineWideRangeFolder.add(tempMarker);
                else offlinePersonalFolder.add(tempMarker);
            }
        }

        //fastOverlay();
        map.getOverlays().add(onlineWideRangeFolder);
        final GroundOverlay2[] placeholder = new GroundOverlay2[1];
        target = new Target() {
            @Override
            public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                map.getOverlays().remove(placeholder[0]);
                map.invalidate();
                GroundOverlay2 groundOverlay = new GroundOverlay2();
                groundOverlay.setTransparency(0.4f);
                groundOverlay.setImage(bitmap);
                groundOverlay.setPosition(new GeoPoint(51.6755, 4.64684), new GeoPoint(49.8768, 7.49137));
                map.getOverlays().add(groundOverlay);
                map.invalidate();
            }

            @Override
            public void onBitmapFailed(Drawable errorDrawable) {

            }

            @Override
            public void onPrepareLoad(Drawable placeHolderDrawable) {
                GroundOverlay2 groundOverlayPlaceHolder = new GroundOverlay2();
                groundOverlayPlaceHolder.setImage(drawableToBitmap(placeHolderDrawable));
                groundOverlayPlaceHolder.setPosition(new GeoPoint(51.6755, 4.64684), new GeoPoint(49.8768, 7.49137));
                placeholder[0] = groundOverlayPlaceHolder;
                map.getOverlays().add(groundOverlayPlaceHolder);
            }

        };
        Picasso.with(getContext()).load("https://hampager.de/assets/coverage/db0sda.png").resize(1024, 1024).centerCrop().placeholder(R.drawable.loading_coverage).into(target);
        map.invalidate();
    }


    @Override public boolean singleTapConfirmedHelper(GeoPoint p) {
        onlineWideRangeFolder.closeAllInfoWindows();
        offlineWideRangeFolder.closeAllInfoWindows();
        onlinePersonalFolder.closeAllInfoWindows();
        offlinePersonalFolder.closeAllInfoWindows();
        for (Overlay o : coverageFolder.getItems()) {
            coverageFolder.remove(o);
        }
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
                    //List<TransmitterResource> data = response.body();
                    transmitterList = response.body();
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
        s.append(res.getString(R.string.type)).append(dot).append(TrRe.getUsage()).append("<br/>");
        s.append(res.getString(R.string.transmission_power)).append(dot).append(Double.toString(TrRe.getPower())).append("<br/>");
        if (TrRe.getTimeSlot().length() > 1) s.append(res.getString(R.string.timeslots));
        else s.append(res.getString(R.string.timeslot));
        s.append(dot).append(TrRe.getTimeSlot()).append("<br/>");
        if (TrRe.getOwnerNames().size() > 1) {
            s.append(res.getString(R.string.owners)).append(dot);
            for (String temp : TrRe.getOwnerNames()) {
                s.append(temp).append(",");
            }
        } else
            s.append(res.getString(R.string.owner)).append(dot).append(TrRe.getOwnerNames().get(0));

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
        Menu menu = this.menu;
        boolean onlineEnabled = menu.findItem(R.id.online_filter).isChecked();
        boolean offlineEnabled = menu.findItem(R.id.offline_filter).isChecked();
        boolean wideRangeEnabled = menu.findItem(R.id.widerange_filter).isChecked();
        boolean personalEnabled = menu.findItem(R.id.personal_filter).isChecked();

        if (onlineEnabled) {
            if (wideRangeEnabled) {
                if (!map.getOverlays().contains(onlineWideRangeFolder)) {
                    map.getOverlays().add(onlineWideRangeFolder);
                }
            } else
                map.getOverlays().remove(onlineWideRangeFolder);
            if (personalEnabled) {
                if (!map.getOverlays().contains(onlinePersonalFolder)) {
                    map.getOverlays().add(onlinePersonalFolder);
                }
            } else
                map.getOverlays().remove(onlinePersonalFolder);
        } else {
            map.getOverlays().remove(onlineWideRangeFolder);
            map.getOverlays().remove(onlinePersonalFolder);
        }
        if (offlineEnabled) {
            if (wideRangeEnabled) {
                if (!map.getOverlays().contains(offlineWideRangeFolder))
                    map.getOverlays().add(offlineWideRangeFolder);
            } else
                map.getOverlays().remove(offlineWideRangeFolder);
            if (personalEnabled) {
                if (!map.getOverlays().contains(offlinePersonalFolder))
                    map.getOverlays().add(offlinePersonalFolder);
            } else
                map.getOverlays().remove(offlinePersonalFolder);
        } else {
            map.getOverlays().remove(offlineWideRangeFolder);
            map.getOverlays().remove(offlinePersonalFolder);
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
    private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
        map.
        ImageView bmImage;

        public DownloadImageTask(ImageView bmImage) {
            this.bmImage = bmImage;
        }

        protected Bitmap doInBackground(String... urls) {
            String urldisplay = urls[0];
            Bitmap mIcon11 = null;
            try {
                InputStream in = new java.net.URL(urldisplay).openStream();
                mIcon11 = BitmapFactory.decodeStream(in);
            } catch (Exception e) {
                Log.e("Error", e.getMessage());
                e.printStackTrace();
            }
            return mIcon11;
        }

        protected void onPostExecute(Bitmap result) {
            bmImage.setImageBitmap(result);
        }
    }*/
}
