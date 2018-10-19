package e.adwaya.iconnect2;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.location.Location;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.location.places.AutocompletePrediction;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceBuffer;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

//import e.adwaya.iconnect2.models.PlaceInfo;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, GoogleApiClient.OnConnectionFailedListener {

    private GoogleMap mMap;
    AutoCompleteTextView addressText;
    private PlaceAutocompleteAdapter adapter;
    private GoogleApiClient mGoogleApiClient;
    private static final float DEFAULT_ZOOM = 15f;
    private static final LatLngBounds LAT_LNG_BOUNDS = new LatLngBounds(
            new LatLng(-40, -168), new LatLng(71, 136));
   // private PlaceInfo mPlace;
    static LatLng placeLtG;
    static LatLng finalLatLng;
    private Marker originalMarker;
    private Button confirmButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        confirmButton=(Button)findViewById(R.id.confirmButton);
        addressText=(AutoCompleteTextView) findViewById(R.id.addressText);
        mGoogleApiClient = new GoogleApiClient
                .Builder(this)
                .addApi(Places.GEO_DATA_API)
                .addApi(Places.PLACE_DETECTION_API)
                .enableAutoManage(this, this)
                .build();
        addressText.setOnItemClickListener(autoCompleteListen);

        adapter=new PlaceAutocompleteAdapter(this,mGoogleApiClient, LAT_LNG_BOUNDS,null);
        addressText.setAdapter(adapter);
        confirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(placeLtG!=null)
                {
                    finalLatLng=placeLtG;
                }
                else
                {
                    finalLatLng=new LatLng(MainActivity.currentLocation.getLatitude(),MainActivity.currentLocation.getLongitude());
                }
                Intent whereNext=new Intent(getApplicationContext(),MainActivity.class);
                startActivity(whereNext);
            }
        });
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        Location here=MainActivity.currentLocation;

        // Add a marker in Sydney and move the camera
        LatLng hereNow = new LatLng(here.getLatitude(), here.getLongitude());

        originalMarker=mMap.addMarker(new MarkerOptions().position(hereNow).title("You are here currently"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(hereNow));
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(hereNow,19),1000,null);
      //  mMap.setMapType(mMap.MAP_TYPE_SATELLITE);
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }
    private void hideSoftKeyboard(){
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    /*
        --------------------------- google places API autocomplete suggestions@Credit- Youtube Channel: CodingWithMitch -----------------
     */

    private AdapterView.OnItemClickListener autoCompleteListen = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
            hideSoftKeyboard();
            originalMarker.remove();
            Log.d("DARN"," CONTROL GOT HERE");
            final AutocompletePrediction item = adapter.getItem(i);
            final String placeId = item.getPlaceId();

            PendingResult<PlaceBuffer> placeResult = Places.GeoDataApi
                    .getPlaceById(mGoogleApiClient, placeId);
            placeResult.setResultCallback(mUpdatePlaceDetailsCallback);
        }
    };

    private ResultCallback<PlaceBuffer> mUpdatePlaceDetailsCallback = new ResultCallback<PlaceBuffer>() {
        @Override
        public void onResult(@NonNull PlaceBuffer places) {
            if(!places.getStatus().isSuccess()){

                places.release();
                return;
            }
            final Place place = places.get(0);
            placeLtG=place.getLatLng();
            mMap.addMarker(new MarkerOptions().position(placeLtG).title("You are here currently"));
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(placeLtG,19),2000,null);
            places.release();
        }
    };
}
