package e.adwaya.iconnect2;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.content.Context;
import android.os.Bundle;
import android.location.LocationManager;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

public class MainActivity extends AppCompatActivity {


     static Location currentLocation;               // Stores the current location of the user
    Button useCurrentLocation;                      // Button for using current location
    Button useOwnLocation;                          // Using a current location
    TextView display;                               // Display current location data

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        useCurrentLocation= (Button) findViewById(R.id.currentLocationButton);
        useOwnLocation=(Button)findViewById(R.id.personalAddressButton);
        display=(TextView)findViewById(R.id.displayTest);
        if(MapsActivity.finalLatLng!=null)
        {
            display.setText(MapsActivity.finalLatLng.latitude+","+ MapsActivity.finalLatLng.longitude+"");
        }
        // Set up criteria
        useCurrentLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                currentLocation=getLastLocation();
                if(currentLocation!=null)
                display.setText(currentLocation.getLatitude()+"," + currentLocation.getLongitude());

            }
        });

        useOwnLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                currentLocation=getLastLocation();
                if(currentLocation!=null) {
                    Intent startMap = new Intent(getApplicationContext(), MapsActivity.class);
                    startActivity(startMap);
                }
            }
        });

    }
    private Location getLastLocation()
    {
        boolean permissionOn=false;
        Location returnLocation = null;
        //if at least Marshmallow, need to ask user's permission to get GPS data
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            //if permission is not yet granted, ask for it
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 0);
                if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    //if permission still not granted, tell user app will not work without it
                    Toast.makeText(this, "Need GPS permissions for app to function", Toast.LENGTH_LONG);
                }
                //once permission is granted, set up location listener
                //updating every UPDATE_INTERVAL milliseconds, regardless of distance change
                else
                    permissionOn=true;
            } else
                permissionOn=true;
        } else {
            permissionOn=true;
        }


        if(permissionOn) {

            LocationManager lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            List<String> providers = lm.getProviders(true);

        /* Loop over the array backwards, and if you get an accurate location, then break out the loop*/


            for (int i = providers.size() - 1; i >= 0; i--) {
                returnLocation = lm.getLastKnownLocation(providers.get(i));
                if (returnLocation != null) break;
            }
        }
            return returnLocation;

    }
    private double distanceFinder(Location location1, Location location2)
    {
        return location1.distanceTo(location2);
    }
}
