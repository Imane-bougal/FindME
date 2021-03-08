package com.imaneb.findme;

import android.Manifest;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.SeekBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.fragment.app.FragmentActivity;

import com.android.volley.NetworkResponse;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.RequestManager;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.Query;
import com.imaneb.findme.data.model.User;
import com.imaneb.findme.notifications.NotificationView;
import com.imaneb.findme.utils.Constants;
import com.koalap.geofirestore.GeoFire;
import com.koalap.geofirestore.GeoLocation;
import com.koalap.geofirestore.GeoQuery;
import com.koalap.geofirestore.GeoQueryEventListener;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Transformation;


import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Array;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import jp.wasabeef.glide.transformations.RoundedCornersTransformation;


public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    RequestQueue requestQueue;
    String insertUrl = "http://192.168.42.6:8080/positions/save";
    String FindByImeiUrl = "http://192.168.42.6:8080/users/findByImei/";
    String userId = "";
    FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    FirebaseFirestore FirebaseInstance = FirebaseFirestore.getInstance();
    String currentUid;


    @Inject
    RequestManager requestManager;
    public String getCurrentUid() {
        return firebaseAuth.getCurrentUser().getUid();
    }

    public void updatePosition(double lat, double lon) {
        CollectionReference geoFirestoreRef = FirebaseInstance.collection(Constants.USERS_NODE);
        GeoFire geoFirestore = new GeoFire(geoFirestoreRef);
        GeoLocation point = new GeoLocation(lat, lon);
        geoFirestore.setLocation(firebaseAuth.getCurrentUser().getUid(), point);

        DocumentReference reference = FirebaseInstance.collection(Constants.POSITIONS_NODE).document(currentUid);
        reference.update("positions", FieldValue.arrayUnion(point)).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                System.out.println("Error inserting to positions : " + e);
            }
        }).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) { }
                });

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
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(sydney,13));
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
    private void getNearFriends(double lat ,double lon){
        CollectionReference geoFirestoreRef = FirebaseInstance.collection(Constants.USERS_NODE);
        GeoFire geoFirestore = new GeoFire(geoFirestoreRef);
        GeoQuery geoQuery = geoFirestore.queryAtLocation(new GeoLocation(lat, lon), 100);
        geoQuery.addGeoQueryEventListener(new GeoQueryEventListener() {

            @Override
            public void onKeyEntered(String key, GeoLocation location) {
                System.out.println(String.format("Key %s entered the search area at [%f,%f]",key, location.latitude, location.longitude));
                DocumentReference docRef = FirebaseInstance.collection(Constants.USERS_NODE).document(key);
                docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            if (document.exists()) {
                                User user = document.toObject(User.class);
                                try {
                                    addMarker(user);
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                                addNotification();
                            } else {

                            }
                        } else {

                        }
                    }
                });


            }

            @Override
            public void onKeyExited(String key) {

            }

            @Override
            public void onKeyMoved(String key, GeoLocation location) {

            }

            @Override
            public void onGeoQueryReady() {

            }

            @Override
            public void onGeoQueryError(Exception error) {

            }
        });
    }
    private void addNotification() {
        System.out.println("adding notifications");
        NotificationCompat.Builder builder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(R.drawable.logo) //set icon for notification
                        .setContentTitle("Notifications Example") //set title of notification
                        .setContentText("This is a notification message")//this is notification message
                        .setAutoCancel(true) // makes auto cancel of notification
                        .setPriority(NotificationCompat.PRIORITY_MAX); //set priority of notification


        Intent notificationIntent = new Intent(this, NotificationView.class);
        notificationIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        //notification message will get at NotificationView
        notificationIntent.putExtra("message", "This is a notification message");

        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent,
                PendingIntent.FLAG_UPDATE_CURRENT);
        builder.setContentIntent(pendingIntent);

        // Add as notification
        NotificationManagerCompat  manager = NotificationManagerCompat.from(this);;
        manager.notify(1, builder.build());
    }
    private void addMarker(User user) throws IOException {
        PicasoMarker target;
        LatLng sydney = new LatLng(user.getL().getLatitude(), user.getL().getLongitude());
        MarkerOptions markerOne = new MarkerOptions().position(sydney).title(user.getDisplayName());
        target = new PicasoMarker(mMap.addMarker(markerOne));
        Picasso.get().load(user.getImage()).noFade()
                .resize(100, 100)
                .transform(new CircleTransform())
                .onlyScaleDown().into(target);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(sydney,13));
    }


}