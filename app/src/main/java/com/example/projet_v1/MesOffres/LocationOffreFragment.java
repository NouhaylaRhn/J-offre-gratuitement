package com.example.projet_v1.MesOffres;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.ScrollView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import com.example.projet_v1.ListOffre.NavigationIconClickListener;
import com.example.projet_v1.Model.Offre;
import com.example.projet_v1.R;
import com.example.projet_v1.SimpleCallback;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Random;


public class LocationOffreFragment extends Fragment {
    private GoogleMap mMap;
    ScrollView sv;

    int PERMISSION_ID = 44;
    FusedLocationProviderClient mFusedLocationProviderClient;
    LatLng currentLocation;

    protected LocationManager locationManager;



    private LocationCallback mLocationCallback = new LocationCallback(){
        @Override
        public void onLocationResult(LocationResult locationResult) {
            Location mLastLocation = locationResult.getLastLocation();
            currentLocation = new LatLng(mLastLocation.getLatitude(),mLastLocation.getLongitude());
            mMap.addMarker(new MarkerOptions().position(currentLocation).title("Location courrante"));
            //mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLocation,12));
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(getActivity());

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_locationoffres, container, false);

        // Set up the toolbar
        setUpToolbar(view);

        sv = view.findViewById(R.id.locationoffres);

        if(mMap == null){
            SupportMapFragment map_frg = (WorkAroundMapFragment)getChildFragmentManager().findFragmentById(R.id.loc_off);
            map_frg.getMapAsync(new OnMapReadyCallback() {
                @Override
                public void onMapReady(GoogleMap googleMap) {
                    mMap = googleMap;
                    mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                    mMap.getUiSettings().setZoomControlsEnabled(true);

                    ((WorkAroundMapFragment) getChildFragmentManager().findFragmentById(R.id.loc_off))
                            .setListener(new WorkAroundMapFragment.OnTouchListener() {
                                @Override
                                public void onTouch() {
                                    sv.requestDisallowInterceptTouchEvent(true);
                                }
                            });

                }
            });
        }

        getLastLocation();


        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            view.findViewById(R.id.locationoffres).setBackgroundResource(R.drawable.fragment_round_corners);
        }
        return view;
    }

    private void setUpToolbar(View view) {
        Toolbar toolbar = view.findViewById(R.id.app_bar);
        AppCompatActivity activity = (AppCompatActivity) getActivity();
        if (activity != null) {
            activity.setSupportActionBar(toolbar);
        }
        toolbar.setNavigationOnClickListener(new NavigationIconClickListener(
                getContext(),
                view.findViewById(R.id.locationoffres),
                new AccelerateDecelerateInterpolator(),
                getContext().getResources().getDrawable(R.drawable.project_menu),
                getContext().getResources().getDrawable(R.drawable.icon_close_menu)));
    }

    @SuppressLint("MissingPermission")
    private void getLastLocation(){
        if (checkPermissions()) {
            if (isLocationEnabled()) {
                mFusedLocationProviderClient.getLastLocation().addOnCompleteListener(
                        new OnCompleteListener<Location>() {
                            @Override
                            public void onComplete(@NonNull Task<Location> task) {
                                Location location = task.getResult();
                                if (location == null) {
                                    requestNewLocationData();
                                } else {
                                    final LatLng loc = new LatLng(location.getLatitude(), location.getLongitude());

                                    CircleOptions circleOptions = new CircleOptions();
                                    circleOptions.center(loc);
                                    circleOptions.radius(11000);
                                    circleOptions.strokeColor(Color.BLUE);
                                    circleOptions.fillColor(0x30394F57);
                                    circleOptions.strokeWidth(2);
                                    mMap.addCircle(circleOptions);

                                    getOffres(new SimpleCallback<List<Offre>>() {
                                        @Override
                                        public void callback(List<Offre> data) {
                                            loadOffresOnMap(data,loc);
                                        }
                                    });
                                    mMap.addMarker(new MarkerOptions().position(loc).title("Localisation courrante"));
                                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(loc,11));
                                }
                            }
                        }
                );
            } else {
                Toast.makeText(getActivity(), "Activer la localisation", Toast.LENGTH_LONG).show();
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(intent);
            }
        } else {
            requestPermissions();
        }
    }



        @SuppressLint("MissingPermission")
    public void requestNewLocationData(){
        LocationRequest mLocationRequest = new LocationRequest();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setInterval(0);
        mLocationRequest.setFastestInterval(0);
        mLocationRequest.setNumUpdates(1);

        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(getActivity());
        mFusedLocationProviderClient.requestLocationUpdates(
                mLocationRequest,mLocationCallback, Looper.myLooper()
        );
    }

    public boolean checkPermissions(){
        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            return true;
        }
        return false;
    }

    public void requestPermissions(){
        ActivityCompat.requestPermissions(getActivity(),new String[]{Manifest.permission.ACCESS_COARSE_LOCATION,Manifest.permission.ACCESS_FINE_LOCATION},PERMISSION_ID);
    }

    public boolean isLocationEnabled(){
        LocationManager locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
    }



    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if(requestCode == PERMISSION_ID){
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                getLastLocation();
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if(checkPermissions())  getLastLocation();
    }

    public void loadOffresOnMap(List<Offre> offres,LatLng locoffre){
        for(Offre o : offres){
            LatLng loc;
            loc = new LatLng(o.getLatitude(), o.getLogitude());
            if(Math.abs(locoffre.latitude - loc.latitude) <= 0.1 && Math.abs(locoffre.longitude - loc.longitude)<=0.1){
                mMap.addMarker(new MarkerOptions().position(loc).title("Catégorie : "+o.getOffre_categorie()).snippet(o.getOffre_titre())
                        .icon(BitmapDescriptorFactory.defaultMarker(randomColorMarker())));

            }

        }
    }

    public float randomColorMarker() {
        List<Float> colors = Arrays.asList(BitmapDescriptorFactory.HUE_AZURE,BitmapDescriptorFactory.HUE_BLUE,BitmapDescriptorFactory.HUE_CYAN,
                BitmapDescriptorFactory.HUE_GREEN,BitmapDescriptorFactory.HUE_MAGENTA,BitmapDescriptorFactory.HUE_ORANGE);
        Random rand = new Random();
        float clr = colors.get(rand.nextInt(colors.size()));
        return clr;
    }

    public void getOffres(@NonNull final SimpleCallback<List<Offre>> finishedCallback){
        DatabaseReference mDatabase;
        mDatabase = FirebaseDatabase.getInstance().getReference().child("Offre");
        Query query = mDatabase.orderByChild("enable").equalTo(true);
        query.addValueEventListener(new ValueEventListener() {
            List<Offre> offres= new ArrayList<>();
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                offres.clear();
                Iterable<DataSnapshot> children = dataSnapshot.getChildren();
                Iterator<DataSnapshot> iterator = children.iterator();

                while (iterator.hasNext()){
                    DataSnapshot next = (DataSnapshot) iterator.next();
                    if(next.child("latitude").exists() && next.child("longitude").exists()){
                        String offre_titre = (String) next.child("offre_titre").getValue();
                        String offre_cat=(String) next.child("offre_categorie").getValue();
                        double lat = (double) next.child("latitude").getValue();
                        double lon = (double) next.child("longitude").getValue();

                        Offre offre = new Offre();
                        offre.setOffre_titre(offre_titre);
                        offre.setOffre_categorie(offre_cat);
                        offre.setLatitude(lat);
                        offre.setLogitude(lon);
                        offres.add(offre);
                    }

                }
                finishedCallback.callback(offres);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


}
