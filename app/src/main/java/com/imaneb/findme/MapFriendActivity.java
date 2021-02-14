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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;
import com.imaneb.findme.ui.main.MainActivity;
import com.imaneb.findme.utils.Constants;
import com.koalap.geofirestore.GeoFire;
import com.koalap.geofirestore.GeoLocation;
import com.koalap.geofirestore.LocationCallback;

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
    String FriendUid="";
    FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    FirebaseFirestore FirebaseInstance = FirebaseFirestore.getInstance();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map_friend);
        requestQueue = Volley.newRequestQueue(getApplicationContext());
        getIntent();
        Bundle extras = getIntent().getExtras();
        FriendUid = extras.getString("FUid");
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
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
                findFriend(FriendUid);

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

    private void findFriend(String Uid){
        CollectionReference geoFirestoreRef = FirebaseFirestore.getInstance().collection(Constants.USERS_NODE);
        GeoFire geoFirestore = new GeoFire(geoFirestoreRef);
        geoFirestore.getLocation( Uid, new LocationCallback() {
            @Override
            public void onLocationResult(String key, GeoLocation location) {
                System.out.println("Location + "+location.latitude+"  "+location.longitude);
                LatLng sydney = new LatLng(location.latitude, location.longitude);
                mMap.addMarker(new MarkerOptions().position(sydney).title("your friend"));
                mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
            }

            @Override
            public void onCancelled(Exception exception) {

            }


        } );

    }

    public void updatePosition(double lat, double lon) {
        CollectionReference geoFirestoreRef = FirebaseInstance.collection(Constants.USERS_NODE);
        GeoFire geoFirestore = new GeoFire(geoFirestoreRef);
        GeoLocation point = new GeoLocation(lat, lon);
        geoFirestore.setLocation(firebaseAuth.getCurrentUser().getUid(), point);


    }

}