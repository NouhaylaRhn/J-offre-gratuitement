package com.example.projet_v1.MesOffres;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.webkit.MimeTypeMap;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.example.projet_v1.ListOffre.NavigationIconClickListener;
import com.example.projet_v1.Model.Offre;
import com.example.projet_v1.R;
import com.github.ybq.android.spinkit.style.Wave;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import static android.app.Activity.RESULT_OK;
import static com.facebook.FacebookSdk.getApplicationContext;

public class AjoutOffreFragment extends Fragment {
    private static String TAG = "AjoutOffre";
    private static final int PICK_IMAGE_REQUEST = 1;

    private GoogleMap mMap;
    ScrollView sv;
    private SearchView searchView;

    AutoCompleteTextView auto ;
    LatLng latLng;
    MaterialButton addOffre ;
    MaterialButton resetOffre ;

    TextInputLayout deadline;
    private TextView mDisplayDate;
    private DatePickerDialog.OnDateSetListener mDateSetListener;

    StorageTask mUploadTask;

    EditText Titre;
    EditText Descritption;
    EditText NombreBenificiaires;

    Button mChooseImage;
    ImageView mImageView;
    ProgressBar mProgressBar;
    Uri mImageUri;

    Offre offre;
    private Wave mWaveDrawable;
    int PERMISSION_ID = 44;
    FusedLocationProviderClient mFusedLocationProviderClient;
    Address address = null;

    private LocationCallback mLocationCallback = new LocationCallback(){
        @Override
        public void onLocationResult(LocationResult locationResult) {
            Location mLastLocation = locationResult.getLastLocation();
            latLng = new LatLng(mLastLocation.getLatitude(),mLastLocation.getLongitude());
            mMap.addMarker(new MarkerOptions().position(latLng).title("Location courrante"));
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng,15));
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

        mWaveDrawable = new Wave();

        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(getActivity());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_ajoutoffre, container, false);

        // Set up the toolbar
        setUpToolbar(view);

        offre = new Offre();

        auto = view.findViewById(R.id.cat);
        sv = view.findViewById(R.id.ajoutoffre);
        searchView = view.findViewById(R.id.sv_location);
        addOffre = view.findViewById(R.id.ajouteroffre);
        resetOffre = view.findViewById(R.id.annuler);
        mDisplayDate = view.findViewById(R.id.ajoutdeadline);

        Titre = view.findViewById(R.id.ajouttitre);
        Descritption = view.findViewById(R.id.ajoutdesc);
        NombreBenificiaires = view.findViewById(R.id.nbr_benif);

        mChooseImage = view.findViewById(R.id.choose_image);
        mImageView = view.findViewById(R.id.image_view_ajoutOffe);
        mProgressBar = view.findViewById(R.id.progress_bar);
        deadline = view.findViewById(R.id.deadline);

        ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(),R.layout.categorie_item,getResources().getStringArray(R.array.category_array));
        auto.setAdapter(adapter);

        mChooseImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openFileChooser();
            }
        });

        deadline.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar cal = Calendar.getInstance();
                int year = cal.get(Calendar.YEAR);
                int month = cal.get(Calendar.MONTH);
                int day = cal.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog dialog = new DatePickerDialog(
                        getActivity(), android.R.style.Theme_Holo_Light_Dialog_MinWidth,
                        mDateSetListener, year, month, day);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog.show();
            }
        });

        mDisplayDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar cal = Calendar.getInstance();
                int year = cal.get(Calendar.YEAR);
                int month = cal.get(Calendar.MONTH);
                int day = cal.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog dialog = new DatePickerDialog(
                        getActivity(), android.R.style.Theme_Holo_Light_Dialog_MinWidth,
                        mDateSetListener, year, month, day);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog.show();
            }
        });

        mDateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                //Log.d(TAG, "OnDateSet : date : " + year + "/" + month + "/" + dayOfMonth);
                String date = year + "/" + (month+1) + "/" + dayOfMonth;
                mDisplayDate.setText(date);
            }
        };

        resetOffre.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                init();
            }
        });

        addOffre.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mUploadTask != null && mUploadTask.isInProgress()) {
                    Toast.makeText(getActivity(), "Upload in progress", Toast.LENGTH_SHORT).show();
                } else {
                    if(!auto.getText().toString().equals("") && !Titre.getText().toString().equals("") && !Descritption.getText().toString().equals("")
                            && !NombreBenificiaires.getText().toString().equals("") && !mDisplayDate.getText().toString().equals("") && latLng!=null){
                        mWaveDrawable.setBounds(0,0,100,100);
                        mWaveDrawable.setColor(getResources().getColor(R.color.colorPrimaryDark));
                        addOffre.setCompoundDrawables(null,null,mWaveDrawable,null);
                        mWaveDrawable.start();

                        offre.setUserID(FirebaseAuth.getInstance().getCurrentUser().getUid());
                        offre.setOffre_categorie(auto.getText().toString());
                        offre.setOffre_titre(Titre.getText().toString());
                        offre.setOffre_description(Descritption.getText().toString());
                        offre.setOffre_nbr_beneficiaire(Integer.parseInt(NombreBenificiaires.getText().toString()));
                        offre.setLatitude(latLng.latitude);
                        offre.setLogitude(latLng.longitude);
                        //offre.setOffre_adresse(getArd_lat_long());
                        String string = mDisplayDate.getText().toString();
                        DateFormat format = new SimpleDateFormat("yyyy/MM/dd");
                        Date date = null;
                        try {
                            date = format.parse(string);
                            offre.setOffre_deadline(date);
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                        UploadimageAndSaveOffre();
                    }
                    else {
                        getDialog(R.string.attention,R.string.ajout_offre_empty,R.drawable.ic_attention).show();
                    }
                }
            }
        });

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                String location = searchView.getQuery().toString();
                List<Address> addressList = null;

                if (location != null || !location.equals("")){
                    Geocoder geocoder = new Geocoder(getActivity());
                    try {
                        addressList = geocoder.getFromLocationName(location,1);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    if (addressList.size()>0) address = addressList.get(0);

                    else{
                        Toast.makeText(getApplicationContext(), "Impossible de trouver cette location, veuillez entrez une position correcte",
                                Toast.LENGTH_SHORT).show();
                        return false;
                    }
                    mMap.clear();
                    latLng = new LatLng(address.getLatitude(),address.getLongitude());
                    mMap.addMarker(new MarkerOptions().position(latLng).title(location));
                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng,15));
                }
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });

        if(mMap == null){
            SupportMapFragment map_frg = (WorkAroundMapFragment)getChildFragmentManager().findFragmentById(R.id.map_frg);
            map_frg.getMapAsync(new OnMapReadyCallback() {
                @Override
                public void onMapReady(GoogleMap googleMap) {
                    mMap = googleMap;
                    mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                    mMap.getUiSettings().setZoomControlsEnabled(true);

                    /*LatLng maroc = new LatLng(31.791702,-7.092620 );
                    mMap.addMarker(new MarkerOptions().position(maroc).title("Marker in Morocco"));
                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(maroc,5));*/

                    ((WorkAroundMapFragment) getChildFragmentManager().findFragmentById(R.id.map_frg))
                            .setListener(new WorkAroundMapFragment.OnTouchListener() {
                                @Override
                                public void onTouch() {
                                    sv.requestDisallowInterceptTouchEvent(true);
                                }
                            });
                    mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {

                        @Override
                        public void onMapClick(LatLng latlng1) {
                            mMap.clear();
                            latLng=latlng1;
                            mMap.addMarker(new MarkerOptions()
                                    .position(latlng1).title("Localisation de l'offre")
                                    .icon(BitmapDescriptorFactory
                                            .defaultMarker(BitmapDescriptorFactory.HUE_RED)));

                        }
                    });
                }
            });

        }
        if(mMap!=null){

        }


        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            view.findViewById(R.id.ajoutoffre).setBackgroundResource(R.drawable.fragment_round_corners);
        }
        return view;
    }

    public MaterialAlertDialogBuilder getDialog(final int title, int msg, int icon){
        return new MaterialAlertDialogBuilder(getContext()).setTitle(title).setMessage(msg).setIcon(getResources().getDrawable(icon))
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @RequiresApi(api = Build.VERSION_CODES.M)
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        FragmentTransaction ft = getFragmentManager().beginTransaction();
                        ft.detach(AjoutOffreFragment.this).attach(AjoutOffreFragment.this).commit();
                    }
                }).setCancelable(false);
    }

    private void setUpToolbar(View view) {
        Toolbar toolbar = view.findViewById(R.id.app_bar);
        AppCompatActivity activity = (AppCompatActivity) getActivity();
        if (activity != null) {
            activity.setSupportActionBar(toolbar);
        }
        toolbar.setNavigationOnClickListener(new NavigationIconClickListener(
                getContext(),
                view.findViewById(R.id.ajoutoffre),
                new AccelerateDecelerateInterpolator(),
                getContext().getResources().getDrawable(R.drawable.project_menu),
                getContext().getResources().getDrawable(R.drawable.icon_close_menu)));
    }

    private void openFileChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK
                && data != null && data.getData() != null) {
            mImageUri = data.getData();
            mImageView.setImageURI(mImageUri);
        }
    }

    private String getFileExtension(Uri uri) {
        Context applicationContext = getContext();

        ContentResolver cR = applicationContext.getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cR.getType(uri));
    }

    private void UploadimageAndSaveOffre() {
        StorageReference mStorageRef;

        mStorageRef = FirebaseStorage.getInstance().getReference().child("images/");
        if (mImageUri != null) {
            StorageReference fileReference = mStorageRef.child(System.currentTimeMillis() + "." + getFileExtension(mImageUri));
            mUploadTask = fileReference.putFile(mImageUri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            Handler handler = new Handler();
                            handler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    mProgressBar.setProgress(0);

                                }
                            }, 500);
                            Toast.makeText(getActivity(), "Ajout réussi", Toast.LENGTH_LONG).show();
                            //offre.setmImageUrl(taskSnapshot.getUploadSessionUri().toString());
                            Task<Uri> task = taskSnapshot.getMetadata().getReference().getDownloadUrl();
                            task.addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    String photoLink = uri.toString();
                                    offre.setmImageUrl(photoLink);
                                    offre.writeNewOffre();
                                    getDialog(R.string.parfait,R.string.ajout_offre_succes,R.drawable.ic_success).show();
                                }
                            });
                            mWaveDrawable.stop();
                            init();

                        }


                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_SHORT).show();
                            mWaveDrawable.stop();
                        }
                    }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(@NonNull UploadTask.TaskSnapshot taskSnapshot) {
                            double progress = (100.0 * taskSnapshot.getBytesTransferred() / taskSnapshot.getTotalByteCount());
                            mProgressBar.setProgress((int) progress);

                        }
                    });

        } else {
            Toast.makeText(getActivity(), "No file selected", Toast.LENGTH_SHORT).show();
        }
    }

    public void init(){
        auto.setText("");
        Titre.setText("");
        Descritption.setText("");
        NombreBenificiaires.setText("");
        mDisplayDate.setText("");
        latLng=null;
        searchView.setQuery("",false);
        mImageView.setImageResource(R.color.browser_actions_title_color);
        mWaveDrawable.stop();
        sv.scrollTo(5,10);
        mMap.clear();
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
                                    LatLng loc = new LatLng(location.getLatitude(), location.getLongitude());
                                    mMap.addMarker(new MarkerOptions().position(loc).title("Location courrante"));
                                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(loc,12));
                                }
                            }
                        }
                );
            } else {
                Toast.makeText(getActivity(), "Activer la location", Toast.LENGTH_LONG).show();
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
}
