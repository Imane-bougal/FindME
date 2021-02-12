package com.imaneb.findme;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;

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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.Query;
import com.imaneb.findme.utils.Constants;

import org.imperiumlabs.geofirestore.GeoFirestore;
import org.imperiumlabs.geofirestore.GeoQuery;
import org.imperiumlabs.geofirestore.listeners.GeoQueryDataEventListener;
import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    RequestQueue requestQueue;
    String insertUrl = "http://192.168.42.6:8080/positions/save";
    String FindByImeiUrl = "http://192.168.42.6:8080/users/findByImei/";
    String userId = "";
    FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    FirebaseFirestore FirebaseInstance = FirebaseFirestore.getInstance();
    String currentUid;

    public String getCurrentUid() {
        return firebaseAuth.getCurrentUser().getUid();
    }

    public void updatePosition(double lat, double lon) {
        CollectionReference geoFirestoreRef = FirebaseInstance.collection(Constants.USERS_NODE);
        GeoFirestore geoFirestore = new GeoFirestore(geoFirestoreRef);
        GeoPoint point = new GeoPoint(lat, lon);
        geoFirestore.setLocation(firebaseAuth.getCurrentUser().getUid(), point);


    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        currentUid = getCurrentUid();

        requestQueue = Volley.newRequestQueue(getApplicationContext());

        TelephonyManager telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        String currenImei = telephonyManager.getDeviceId();
        String URL = FindByImeiUrl + currenImei;
        /*
        JsonObjectRequest req = new JsonObjectRequest(URL,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        System.out.println("******catched Json**********");
                        System.out.println(response);
                        if (response.length()==0){
                            // redirecting to connection :
                            Intent i = new Intent(MapsActivity.this, MainActivity.class);
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
        requestQueue.add(req);*/
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
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
                LatLng sydney = new LatLng(location.getLatitude(), location.getLongitude());
                mMap.addMarker(new MarkerOptions().position(sydney).title("you are here"));
                mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));

                //addPosition(location.getLatitude(), location.getLongitude());
                updatePosition(location.getLatitude(), location.getLongitude());
                getNearFriends(location.getLatitude(), location.getLongitude());


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
    Map<String,Object> stringIntegerMap=new HashMap<>();
    private void getNearFriends(double lat ,double lon){
        CollectionReference geoFirestoreRef = FirebaseInstance.collection(Constants.USERS_NODE);
        GeoFirestore geoFirestore = new GeoFirestore(geoFirestoreRef);
        GeoQuery geoQuery = geoFirestore.queryAtLocation(new GeoPoint(lat, lon), 100);
        geoQuery.addGeoQueryDataEventListener(new GeoQueryDataEventListener() {
            @Override
            public void onGeoQueryReady() {

            }

            @Override
            public void onGeoQueryError(@NotNull Exception e) {

            }

            @Override
            public void onDocumentMoved(@NotNull DocumentSnapshot documentSnapshot, @NotNull GeoPoint geoPoint) {
                System.out.println(String.format("Key %s event Moved the search area at [%f,%f]", documentSnapshot.getId(), geoPoint.getLatitude(), geoPoint.getLongitude()));


            }

            @Override
            public void onDocumentExited(@NotNull DocumentSnapshot documentSnapshot) {
                System.out.println(String.format("Key %s event exit the search area at [%f,%f]", documentSnapshot.getId()));

            }

            @Override
            public void onDocumentEntered(@NotNull DocumentSnapshot documentSnapshot, @NotNull GeoPoint geoPoint) {
                System.out.println(String.format("Key %s entered the search area at [%f,%f]", documentSnapshot.getId(), geoPoint.getLatitude(), geoPoint.getLongitude()));
                addMarker(geoPoint.getLatitude(), geoPoint.getLongitude());
            }

            @Override
            public void onDocumentChanged(@NotNull DocumentSnapshot documentSnapshot, @NotNull GeoPoint geoPoint) {
                System.out.println(String.format("Key %s event changed the search area at [%f,%f]", documentSnapshot.getId(), geoPoint.getLatitude(), geoPoint.getLongitude()));

            }
        });
    }
    private void addMarker(Double lat,Double lon){
        LatLng sydney = new LatLng(lat, lon);
        mMap.addMarker(new MarkerOptions().position(sydney));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
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