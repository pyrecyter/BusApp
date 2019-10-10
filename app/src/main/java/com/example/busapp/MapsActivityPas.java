package com.example.busapp;

import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static android.content.Context.LOCATION_SERVICE;

public class MapsActivityPas extends Fragment implements OnMapReadyCallback {

    private GoogleMap mMap;
    private LocationManager locationManager;
    private List<Marker> buses;
    private Marker flag;

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.activity_maps_pas, container, false);

        buses = new ArrayList<>();
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Users");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    if (ds.child("type").getValue(String.class).matches("Driver")) {
                        if (ds.child("live").getValue(Boolean.class)) {
                            if (ds.child("routeNumber").getValue(String.class).matches(((PasHome) getActivity()).getRoute())) {
                                LatLng latLng = new LatLng(ds.child("latitude").getValue(Double.class), ds.child("longitude").getValue(Double.class));
                                boolean check = false;
                                if (!buses.isEmpty()) {
                                    for (Marker f : buses) {
                                        if (f.getTitle().matches(ds.child("numberPlate").getValue(String.class))) {
                                            f.setPosition(latLng);
                                            check = true;
                                        }
                                    }
                                }
                                if (!check)
                                    buses.add(mMap.addMarker(new MarkerOptions().position(latLng).title(ds.child("numberPlate").getValue(String.class)).icon(bitmapDescriptorFromVectorbus(view.getContext(), R.mipmap.ic_marker_bus))));
                            } else {
                                if (!buses.isEmpty()) {
                                    for (Marker f : buses) {
                                        if (ds.child("numberPlate").getValue(String.class) != null)
                                            if (f.getTitle().matches(ds.child("numberPlate").getValue(String.class))) {
                                                buses.remove(f);
                                                f.remove();
                                            }
                                    }
                                }
                            }
                        } else {
                            if (!buses.isEmpty()) {
                                for (Marker f : buses) {
                                    if (ds.child("numberPlate").getValue(String.class) != null)
                                        if (f.getTitle().matches(ds.child("numberPlate").getValue(String.class))) {
                                            buses.remove(f);
                                            f.remove();
                                        }
                                }
                            }
                        }
                    }
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        locationManager = (LocationManager) getContext().getSystemService(LOCATION_SERVICE);
        if (inflater.getContext().checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && inflater.getContext().checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    Activity#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for Activity#requestPermissions for more details.
            ActivityCompat.requestPermissions(getActivity(),
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    2);
        } else {
            if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, new LocationListener() {
                    @Override
                    public void onLocationChanged(Location location) {

                        double latitude = location.getLatitude();
                        double longitude = location.getLongitude();
                        LatLng latLng = new LatLng(latitude, longitude);
                        Geocoder geocoder = new Geocoder(inflater.getContext().getApplicationContext());
                        try {
                            List<Address> addressList = geocoder.getFromLocation(latitude, longitude, 1);
                            String tag = addressList.get(0).getLocality() + " ";
                            tag += addressList.get(0).getCountryName();
                            if (flag != null)
                                flag.setPosition(latLng);
                            else {
                                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15f));
                                flag = mMap.addMarker(new MarkerOptions().position(latLng).title(tag).icon(bitmapDescriptorFromVector(view.getContext(), R.mipmap.ic_marker_pas)));
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                    }

                    @Override
                    public void onStatusChanged(String s, int i, Bundle bundle) {

                    }

                    @Override
                    public void onProviderEnabled(String s) {

                    }

                    @Override
                    public void onProviderDisabled(String s) {
                        Toast.makeText(getContext(), "GPS Disabled", Toast.LENGTH_LONG).show();
                    }
                });
            }
        }
        return view;
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
    }

    private BitmapDescriptor bitmapDescriptorFromVector(Context context, @DrawableRes int vectorDrawableResourceId) {
        Drawable background = ContextCompat.getDrawable(context, R.drawable.ic_pin_red);
        background.setBounds(0, 0, background.getIntrinsicWidth() + 40, background.getIntrinsicHeight() + 40);
        Drawable vectorDrawable = ContextCompat.getDrawable(context, vectorDrawableResourceId);
        vectorDrawable.setBounds(20, 10, vectorDrawable.getIntrinsicWidth() - 120, vectorDrawable.getIntrinsicHeight() - 120);
        Bitmap bitmap = Bitmap.createBitmap(background.getIntrinsicWidth() + 40, background.getIntrinsicHeight() + 40, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        background.draw(canvas);
        vectorDrawable.draw(canvas);
        return BitmapDescriptorFactory.fromBitmap(bitmap);
    }


    private BitmapDescriptor bitmapDescriptorFromVectorbus(Context context, @DrawableRes int vectorDrawableResourceId) {
        Drawable background = ContextCompat.getDrawable(context, R.drawable.ic_pin_red);
        background.setBounds(0, 0, background.getIntrinsicWidth() + 40, background.getIntrinsicHeight() + 40);
        Drawable vectorDrawable = ContextCompat.getDrawable(context, vectorDrawableResourceId);
        vectorDrawable.setBounds(20, 10, vectorDrawable.getIntrinsicWidth() - 120, vectorDrawable.getIntrinsicHeight() - 120);
        Bitmap bitmap = Bitmap.createBitmap(background.getIntrinsicWidth() + 40, background.getIntrinsicHeight() + 40, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        background.draw(canvas);
        vectorDrawable.draw(canvas);
        return BitmapDescriptorFactory.fromBitmap(bitmap);
    }
}