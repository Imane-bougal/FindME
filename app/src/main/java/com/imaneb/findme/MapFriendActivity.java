package com.imaneb.findme;

import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.telephony.TelephonyManager;

import com.android.volley.NetworkResponse;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.imaneb.findme.ui.main.MainActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MapFriendActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    RequestQueue requestQueue;
    String insertUrl = "http://192.168.42.6:8080/positions/save";
    String FindUserByImeiUrl = "http://192.168.42.6:8080/users/findByImei/";
    String FindFriendByImeiUrl = "http://192.168.42.6:8080/positions/findRecentByImei/";
    String userId = "";
    double lonF = 0.0;
    double latF = 0.0;
    String FriendImei="";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map_friend);
        requestQueue = Volley.newRequestQueue(getApplicationContext());
        getIntent();
        findUserIdByImei();
        Bundle extras = getIntent().getExtras();
        FriendImei = extras.getString("Fimei");
        findUserIdByImei();
        FindFriendByImei(FriendImei);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    private void FindFriendByImei(String friendImei) {
        String URL = FindFriendByImeiUrl+friendImei;
        JsonObjectRequest req = new JsonObjectRequest(URL,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        System.out.println("******catched Friend**********");
                        System.out.println(response);
                        if (response.length()==0){
                            // redirecting to connection :
                            Intent i = new Intent(MapFriendActivity.this, MainActivity.class);
                            startActivity(i);
                        }
                        else {
                            try {
                                latF = Double.parseDouble(response.get("latitude").toString());
                                lonF = Double.parseDouble(response.get("longitude").toString());
                                System.out.println("******Friend coord. : **********"+latF + " "+ lonF);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                NetworkResponse response = error.networkResponse;
                System.out.println("****Catch not working************");
                if (error instanceof ServerError && response != null) {
                    try {
                        String res = new String(response.data,
                                HttpHeaderParser.parseCharset(response.headers, "utf-8"));
                        // Now you can use any deserializer to make sense of data
                        JSONObject obj = new JSONObject(res);
                    } catch (UnsupportedEncodingException e1) {
                        // Couldn't properly decode data to string
                        e1.printStackTrace();
                    } catch (JSONException e2) {
                        // returned data is not JSONObject?
                        e2.printStackTrace();
                    }
                }
            }
        }
        );
        // add the request object to the queue to be executed
        requestQueue.add(req);
    }

    private void findUserIdByImei() {
        TelephonyManager telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        String currenImei = telephonyManager.getDeviceId();
        String URL = FindUserByImeiUrl+currenImei;
        JsonObjectRequest req = new JsonObjectRequest(URL,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        System.out.println("******catched Json**********");
                        System.out.println(response);
                        if (response.length()==0){
                            // redirecting to connection :
                            Intent i = new Intent(MapFriendActivity.this, MainActivity.class);
                            startActivity(i);
                        }
                        else {
                            try {
                                userId = response.get("id").toString();
                                System.out.println("******User ID : **********"+userId);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                NetworkResponse response = error.networkResponse;
                System.out.println("****Catch not working************");
                if (error instanceof ServerError && response != null) {
                    try {
                        String res = new String(response.data,
                                HttpHeaderParser.parseCharset(response.headers, "utf-8"));
                        // Now you can use any deserializer to make sense of data
                        JSONObject obj = new JSONObject(res);
                    } catch (UnsupportedEncodingException e1) {
                        // Couldn't properly decode data to string
                        e1.printStackTrace();
                    } catch (JSONException e2) {
                        // returned data is not JSONObject?
                        e2.printStackTrace();
                    }
                }
            }
        }
        );
        // add the request object to the queue to be executed
        requestQueue.add(req);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
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
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 60000, 150, new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                LatLng user = new LatLng(location.getLatitude(), location.getLongitude());
                mMap.addMarker(new MarkerOptions().position( user ).title("you are here"));
                mMap.moveCamera(CameraUpdateFactory.newLatLng(user));
                FindFriendByImei(FriendImei);
                LatLng friend = new LatLng(latF , lonF);
                mMap.addMarker(new MarkerOptions().position( friend ).title("your Friend here"));
                mMap.moveCamera(CameraUpdateFactory.newLatLng(friend));

                addPosition(location.getLatitude(), location.getLongitude());

            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {

            }

            @Override
            public void onProviderEnabled(String provider) {
            }

            @Override
            public void onProviderDisabled(String provider) {
            }
        });
    }
    void addPosition(final double lat, final double lon) {

        // Post params to be sent to the server
        TelephonyManager telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        JSONObject params = new JSONObject();
        JSONObject user = new JSONObject();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try{
            params.put("latitude", lat + "");
            params.put("longitude", lon + "");
            params.put("date", sdf.format(new Date()));
            params.put("imei", telephonyManager.getDeviceId());
            System.out.println(" ****** User ID adding position : ********** "+userId);
            user.put("id",Integer.parseInt(userId)+"");
            params.put("user",user);
            System.out.println("******* JSON OBJECT SENT TO POSITION ******* "+params);
        } catch (JSONException e) {
            e.printStackTrace();
        }


        JsonObjectRequest req = new JsonObjectRequest(insertUrl,params,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        System.out.println("************ sending position working *************");
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                NetworkResponse response = error.networkResponse;
                System.out.println("************ sending position is not working ************* "+error);

                if (error instanceof ServerError && response != null) {
                    try {
                        String res = new String(response.data,
                                HttpHeaderParser.parseCharset(response.headers, "utf-8"));
                        // Now you can use any deserializer to make sense of data
                        JSONObject obj = new JSONObject(res);
                    } catch (UnsupportedEncodingException e1) {
                        // Couldn't properly decode data to string
                        e1.printStackTrace();
                    } catch (JSONException e2) {
                        // returned data is not JSONObject?
                        e2.printStackTrace();
                    }
                }
            }
        }
        );

        // add the request object to the queue to be executed
        requestQueue.add(req);
    }
}