package com.abusharp.maritimeborder;

import android.Manifest;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.location.Location;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.Toast;

import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.firebase.geofire.GeoQuery;
import com.firebase.geofire.GeoQueryEventListener;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolygonOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    Location currentLocation;
    Timer timer;
    TimerTask timerTask;
    FusedLocationProviderClient fusedLocationProviderClient;
    private static final int REQUEST_CODE = 101;
    double latitude,longitude,height;
    FirebaseAuth firebaseAuth;
    GeoFire geofire;
    FirebaseUser User;
    MarkerOptions markerOptions;
    LatLng MyLocation;
    DatabaseReference databaseReference;
    GoogleMap map;
    Bitmap smallMarker;
    Marker marker;
    LatLng zone0  , zone1  , zone2  , zone3  , zone4  , zone5  , zone6  , zone7  , zone8  , zone9 , zone10  , zone11  , zone12, zone13, zone14  , zone15  , zone16  , zone17, zone18  , zone19  , zone20  , zone21  ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        fetchLastLocation();
        try {
            User = getIntent().getParcelableExtra("user");
            firebaseAuth = FirebaseAuth.getInstance();
            databaseReference = FirebaseDatabase.getInstance().getReference();
            geofire=new GeoFire(databaseReference);
           // Toast.makeText(getApplicationContext(), ""+UserId, Toast.LENGTH_SHORT).show();
        }
        catch (Exception e){
            Toast.makeText(getApplicationContext(), ""+e, Toast.LENGTH_SHORT).show();
        }
        BitmapDrawable bitmapdraw=(BitmapDrawable)getResources().getDrawable(R.drawable.boat_ico_down);
        Bitmap b=bitmapdraw.getBitmap();
        smallMarker = Bitmap.createScaledBitmap(b, 50, 100, false);
    }

    private void fetchLastLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]
                    {Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_CODE);
            return;
        }
        Task<Location> task = fusedLocationProviderClient.getLastLocation();

        task.addOnSuccessListener(new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                if (location != null) {
                    currentLocation = location;
                    timer = new Timer();
                    final Map<String,Object> update = new HashMap<>();
                    latitude = currentLocation.getLatitude();
                   // longitude = currentLocation.getLongitude();
                    timerTask = new TimerTask() {
                        @Override
                        public void run() {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    latitude = latitude-0.005;
                                    longitude = currentLocation.getLongitude();
                                    height = currentLocation.getAltitude();
                                    marker.remove();

                                    MyLocation = new LatLng(latitude,longitude);
                                    markerOptions = new MarkerOptions().position(MyLocation).title("Hello").icon(BitmapDescriptorFactory.fromBitmap(smallMarker));
                                    marker=map.addMarker(markerOptions);
                                        update.put("location" , Arrays.asList(latitude,longitude,height));
                                        databaseReference.child("Location").child(User.getUid()).setValue(update);
                                   // Toast.makeText(getApplicationContext(), latitude + " " + longitude + " " + height, Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    };
                    timer.scheduleAtFixedRate(timerTask,0,100);
                    SupportMapFragment supportMapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
                    supportMapFragment.getMapAsync(MapsActivity.this);
                }
            }
        });
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;
        CameraUpdate center= CameraUpdateFactory.newLatLng(new LatLng(currentLocation.getLatitude(),currentLocation.getLongitude()));
        CameraUpdate zoom=CameraUpdateFactory.zoomTo(16);
        googleMap.moveCamera(center);
        googleMap.animateCamera(zoom);
        googleMap.getUiSettings().setMyLocationButtonEnabled(true);
        googleMap.getUiSettings().setZoomControlsEnabled(true);
        googleMap.getUiSettings().setZoomGesturesEnabled(true);
        MyLocation = new LatLng(latitude,longitude);
        markerOptions = new MarkerOptions().position(MyLocation).title("Hello");
        marker=map.addMarker(markerOptions);

        if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            //googleMap.setMyLocationEnabled(true);
        }
        zone0 = new LatLng(6.513520000000002, 78.20343000000003);
        zone1 = new LatLng(7.505090405032396, 78.93279525527112);
        zone2 = new LatLng(7.888208000000006, 78.76167299999997);
        zone3 = new LatLng(8.203335000000019, 78.89488200000005);
        zone4 = new LatLng(8.370146000000018, 78.92320599999994);
        zone5 = new LatLng(8.52041900000002, 79.07838800000002);
        zone6 = new LatLng(8.620059000000015, 79.21670399999994);
        zone7 = new LatLng(8.666688000000004, 79.30335000000002);
        zone8 = new LatLng(8.885556000000005, 79.48416700000007);
        zone9 = new LatLng(8.998177999999998, 79.52144599999997);
        zone10 = new LatLng(9.10000000000002, 79.53333299999997);
        zone11 = new LatLng(9.216667000000013, 79.53333299999997);
        zone12 = new LatLng(9.363333, 79.378333);
        zone13 = new LatLng(9.669631, 79.37690700000007);
        zone14 = new LatLng(9.950000000000017, 79.58333300000004);
        zone15 = new LatLng(10.083332999999994, 80.04999999999995);
        zone16 = new LatLng(10.085556000000006, 80.08333300000004);
        zone17 = new LatLng(10.134443999999995, 80.151389);
        zone18 = new LatLng(10.121001576872644, 80.69660202116143);
        zone19 = new LatLng(10.174405019527528, 80.83376910053562);
        zone20 = new LatLng(10.68333300000002, 81.03472199999999);
        zone21 = new LatLng(11.035278000000032, 81.93333299999995);
        googleMap.addPolygon(new PolygonOptions()
                .add(zone0,zone1,zone2,zone3,zone4,zone5,zone6,zone7,zone8,zone9,zone10,zone11,zone12,zone13,zone14,zone15,zone16,zone17,zone18,zone19,zone20,zone21)
                .strokeColor(Color.BLUE)
                .fillColor(0x220000FF)
                .strokeWidth(5.0F));


      /* googleMap.addCircle(new CircleOptions()
                .center(MyLocation)
                .radius(10)
                .strokeColor(Color.BLUE)
                .fillColor(0x220000FF)
                .strokeWidth(5.0F));

        GeoQuery geoQuery = geofire.queryAtLocation(new GeoLocation(MyLocation.latitude,MyLocation.longitude),0.010F);
        geoQuery.addGeoQueryEventListener(new GeoQueryEventListener() {
            @Override
            public void onKeyEntered(String key, GeoLocation location) {
                sendNotification("Alert : ",String.format("If you want to live you have to turn your boat otherwise you will die"));
            }

            @Override
            public void onKeyExited(String key) {
                sendNotification("Alert : ",String.format("Now you are in safe zone, Start fishing"));
            }

            @Override
            public void onKeyMoved(String key, GeoLocation location) {

            }

            @Override
            public void onGeoQueryReady() {

            }

            @Override
            public void onGeoQueryError(DatabaseError error) {

            }
        });*/
    }
    private void sendNotification(String title, String content) {
        Notification.Builder builder = new Notification.Builder(this)
                .setSmallIcon(R.mipmap.boat).setContentTitle(title)
                .setContentText(content);
        NotificationManager manager = (NotificationManager)this.getSystemService(Context.NOTIFICATION_SERVICE);
        Intent intent = new Intent(this,MapsActivity.class);
        PendingIntent contentIntent = PendingIntent.getActivity(this,0,intent,PendingIntent.FLAG_IMMUTABLE);
        builder.setContentIntent(contentIntent);
        Notification notification = builder.build();
        notification.flags |= Notification.FLAG_AUTO_CANCEL;
        notification.defaults |= Notification.DEFAULT_SOUND;
        manager.notify(new Random().nextInt(),notification);
    }

  /*  private void getAddress(double latitude, double longitude) {

        Geocoder geocoder=new Geocoder(getApplication(), Locale.getDefault());
        try
        {
            List<Address> addresses = geocoder.getFromLocation(latitude, longitude, 1);
            Address obj = addresses.get(0);
            String add = obj.getAddressLine(0);
            add = add + "\n" + obj.getCountryName();


            NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this);
            mBuilder.setSmallIcon(R.mipmap.boat);
            mBuilder.setContentTitle("You Are Here");
            mBuilder.setContentText(add);

            Intent notificationIntent = new Intent(this, MapsActivity.class);
            PendingIntent contentIntent = PendingIntent.getActivity(this, 0, notificationIntent,
                    PendingIntent.FLAG_UPDATE_CURRENT);
            //Notification.Builder builder;
            mBuilder.setContentIntent(contentIntent);

            NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            mNotificationManager.notify(1,mBuilder.build());

        }
        catch (IOException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }*/

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        switch (requestCode) {
            case REQUEST_CODE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    fetchLastLocation();
                }
        }
    }
}

