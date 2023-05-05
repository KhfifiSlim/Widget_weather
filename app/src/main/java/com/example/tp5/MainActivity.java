package com.example.tp5;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.telecom.Call;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {
    LocationManager locationManager0;
    TextView tvCity, tvState, tvCountry, tvPin, tvLocality;

    public String cityName2;
    private LocationListener listener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

//        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
//                && ActivityCompat.checkSelfPermission(getApplicationContext(),
//                android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
//            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION,
//                    android.Manifest.permission.ACCESS_COARSE_LOCATION}, 101);
//        }

        tvCity = findViewById(R.id.tvCity);
        tvState = findViewById(R.id.tvState);
        tvCountry = findViewById(R.id.tvCountry);
        tvPin = findViewById(R.id.tvPin);
        tvLocality = findViewById(R.id.tvLocality);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            return;
        }
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        String provider = locationManager.getBestProvider(new Criteria(), true);


        //locationManager0 = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        listener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                //  tvCity.setText("slim");

            }

            // Other methods of LocationListener
        };
        if (provider != null) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }
            Location location = locationManager.getLastKnownLocation(provider);

            locationManager.requestLocationUpdates(provider, 300000, 0, listener);

            if (location == null) {
                locationManager.requestSingleUpdate(provider, listener, null);

                return;
            }
            double lat = location.getLatitude();
            double lng = location.getLongitude();

            if (!Geocoder.isPresent()) {
                Toast.makeText(getApplicationContext(), "Les coordonnées sont indisponibles", Toast.LENGTH_LONG).show();
                return;
            }

            Geocoder geocoder = new Geocoder(this);

            try {
                List<Address> addressList = geocoder.getFromLocation(lat, lng, 10);
                //  String cityName = addressList.get(0).getAddressLine(0);
                //   String stateName = addressList.get(0).getAddressLine(1);
                // String countryName = addressList.get(0).getAddressLine(2);
                // Toast.makeText(getApplicationContext(), String.valueOf(addressList.get(0).getAdminArea()),Toast.LENGTH_LONG).show();
                //  Toast.makeText(getApplicationContext(), addressList.get(0).getAdminArea(),Toast.LENGTH_LONG).show();

                if (addressList == null) {
                    Toast.makeText(getApplicationContext(), "Les coordonnées sont indisponibles", Toast.LENGTH_LONG).show();
                    return;
                }


                cityName2 = addressList.get(0).getAdminArea();
                 Toast.makeText(getApplicationContext(), cityName2,Toast.LENGTH_LONG).show();


            } catch (Exception e) {

                e.printStackTrace();
            }

            tvCity.setText(cityName2);
            SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString("cityName", cityName2);
            editor.apply();

        } else {
            Toast.makeText(getApplicationContext(), "La localisation est désactivée, veuillez l'activer ou bien chercher une autre ville!!!", Toast.LENGTH_LONG).show();

        }         locationEnabled();
        //getLocation();

    }

    private void locationEnabled() {
        LocationManager lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        boolean gps_enabled = false;
        boolean network_enabled = false;
        try {
            gps_enabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            network_enabled = lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (!gps_enabled && !network_enabled) {
            new AlertDialog.Builder(MainActivity.this)
                    .setTitle("Enable GPS Service")
                    .setMessage("We need your GPS location to show Near Places around you.")
                    .setCancelable(false)
                    .setPositiveButton("Enable", new
                            DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                                    startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                                }
                            })
                    .setNegativeButton("Cancel", null)
                    .show();
        }
    }

    void getLocation() {
        try {
         //   locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        //    locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 3000, 5, (LocationListener) this);



        } catch (SecurityException e) {
            e.printStackTrace();
        }
    }

    private void getAddressFromLocation(double latitude, double longitude, Context context) {
        Geocoder geocoder = new Geocoder(context, Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocation(latitude, longitude, 10);
            if (addresses != null && addresses.size() > 0) {
                String cityName = addresses.get(0).getLocality();

                cityName2=cityName;
                tvCity.setText(cityName);
                 Toast.makeText(MainActivity.this, addresses.get(0).getAdminArea(), Toast.LENGTH_SHORT).show();


                // Use cityName for further operations
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}