package sener.blocksatportable.Activities;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import sener.blocksatportable.R;
/**
 * <h1>Activity: MapsActivity</h1>
 * <p>
 * Activity that shows the operation map.
 * </p>
 *
 * @author  Sergio del Horno
 * @version 1.0.0
 * @since   2018-05-18
 */
public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;

    private double coord_lat;
    private double coord_long;

    private int numOfTrains = 0;
    private String[] trains_id = new String[20];
    private double[] trains_lat = new double[20];
    private double[] trains_long = new double[20];


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        coord_lat = getIntent().getDoubleExtra("coord_lat",41.490272);
        coord_long = getIntent().getDoubleExtra("coord_long", 2.107008);
        trains_id = getIntent().getStringArrayExtra("trains_id");
        trains_lat = getIntent().getDoubleArrayExtra("trains_lat");
        trains_long = getIntent().getDoubleArrayExtra("trains_long");
        numOfTrains = getIntent().getIntExtra("numOfTrains",0);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.

        int status = GooglePlayServicesUtil.isGooglePlayServicesAvailable(getApplicationContext());

        if (status == ConnectionResult.SUCCESS)
        {
            SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                    .findFragmentById(R.id.map);
            mapFragment.getMapAsync(this);
        }
        else
        {
            Dialog dialog = GooglePlayServicesUtil.getErrorDialog(status, (Activity)getApplicationContext(), 10);
            dialog.show();
        }
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);

        UiSettings uiSettings = mMap.getUiSettings();
        uiSettings.setZoomControlsEnabled(true);

        // Add a marker in default position and move the camera
        LatLng myPos = new LatLng(coord_lat, coord_long);
        mMap.addMarker(new MarkerOptions().position(myPos).title(getResources().getString(R.string.operator)).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));

        for(int i = 0; i < numOfTrains; i++){
            LatLng newTrain = new LatLng(trains_lat[i], trains_long[i]);
            mMap.addMarker(new MarkerOptions().position(newTrain).title("Tren " + trains_id[i]).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)));
        }

        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(myPos, 16));
    }
}
