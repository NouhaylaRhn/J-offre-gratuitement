package com.example.projet_v1.ListOffre;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;
import com.example.projet_v1.Model.Offre;
import com.example.projet_v1.R;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.button.MaterialButton;

import org.jetbrains.annotations.NotNull;

public class LocationOffreDialog extends DialogFragment implements OnMapReadyCallback{
    public static final String TAG = "location_offre_dialog";
    private GoogleMap mMap;
    MaterialButton retour;
    Offre offre;
    SupportMapFragment mapFragment;

    public static LocationOffreDialog display(FragmentManager fragmentManager, Offre offre) {
        LocationOffreDialog locationOffreDialog = new LocationOffreDialog();
        locationOffreDialog.offre = offre;
        locationOffreDialog.show(fragmentManager,TAG);
        return locationOffreDialog;
    }

    @Override
    public void onStart() {
        super.onStart();
        Dialog dialog = getDialog();
        if (dialog != null) {
            int width = ViewGroup.LayoutParams.MATCH_PARENT;
            int height = ViewGroup.LayoutParams.WRAP_CONTENT;
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            dialog.getWindow().setLayout(width, height);
            dialog.getWindow().setWindowAnimations(R.style.AppTheme_Slide);
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.location_dialog, container, false);
        retour = view.findViewById(R.id.retour);

        mapFragment = (SupportMapFragment) getFragmentManager().findFragmentById(R.id.loc_offre);
        mapFragment.getMapAsync(this);

        retour.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getDialog().dismiss();
            }
        });

        return view;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        if(mMap == null) {
            mMap = googleMap;
            mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
            mMap.getUiSettings().setZoomControlsEnabled(true);

            LatLng loc = new LatLng(offre.getLatitude(), offre.getLogitude());

            mMap.addMarker(new MarkerOptions().position(loc).title("Catégorie: " + offre.getOffre_categorie()).snippet(offre.getOffre_titre()));
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(loc, 15));
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        SupportMapFragment f = (SupportMapFragment) getFragmentManager().findFragmentById(R.id.loc_offre);
        if (f != null)
            getFragmentManager().beginTransaction().remove(f).commit();
    }
}
