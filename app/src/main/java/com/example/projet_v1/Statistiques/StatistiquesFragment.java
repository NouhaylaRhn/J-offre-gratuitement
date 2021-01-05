package com.example.projet_v1.Statistiques;

import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import com.example.projet_v1.ListOffre.NavigationIconClickListener;
import com.example.projet_v1.R;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class StatistiquesFragment extends Fragment {
    TextView nombre_total_benif;
    TextView nombre_total_offres;
    PieChart chartB;
    PieChart chartO;
    String name_benif[]={"Autre", "Education", "Emploi", "info", "Médicament", "Vêtement"};
    long nbr_benif[]=new long[6];
    long nbr_offres[]=new long[6];
    long nbr_total_benif;
    long nbr_total_offres;
    int colors[]={Color.rgb(193, 37, 82), Color.rgb(255, 102, 0), Color.rgb(245, 199, 0),
                   Color.rgb(106, 150, 31), Color.rgb(179, 100, 53),Color.rgb(0, 100, 160)};

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_statistiques, container, false);

        nombre_total_benif=view.findViewById(R.id.nombre_total_benif) ;
        nombre_total_offres=view.findViewById(R.id.nombre_total_offres);

        chartB = (PieChart) view.findViewById(R.id.statistiques);
        chartO = (PieChart) view.findViewById(R.id.statistiques_offres);

        DownloadStatistics();

        // Set up the toolbar
        setUpToolbar(view);
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            view.findViewById(R.id.statistics).setBackgroundResource(R.drawable.fragment_round_corners);
        }

        //Set up Graph:

        return view;
    }

    private void setupPieChartBenif() {
        List<PieEntry> pieEntries = new ArrayList<>();
        for(int i=0; i<name_benif.length; i++){
            pieEntries.add(new PieEntry(nbr_benif[i], name_benif[i]));
        }

        PieDataSet dataSet = new PieDataSet(pieEntries, " ");
        dataSet.setColors(ColorTemplate.createColors(colors));
        PieData data = new PieData(dataSet);
        chartB.setData(data);
        chartB.animateY(1000);
        chartB.getDescription().setText("Statistiques des bénéficiaires");
        chartB.invalidate();
    }
    private void setupPieChartOffres() {
        List<PieEntry> pieEntries = new ArrayList<>();
        for(int i=0; i<name_benif.length; i++){
            pieEntries.add(new PieEntry(nbr_offres[i], name_benif[i]));
        }

        PieDataSet dataSet = new PieDataSet(pieEntries, " ");
        dataSet.setColors(ColorTemplate.createColors(colors));
        PieData data = new PieData(dataSet);
        chartO.setData(data);
        chartO.animateY(1000);
        chartO.getDescription().setText("Statistiques des offres");
        chartO.invalidate();
    }



    private void setUpToolbar(View view) {
        Toolbar toolbar = view.findViewById(R.id.app_bar);
        AppCompatActivity activity = (AppCompatActivity) getActivity();
        if (activity != null) {
            activity.setSupportActionBar(toolbar);
        }
        toolbar.setNavigationOnClickListener(new NavigationIconClickListener(
                getContext(),
                view.findViewById(R.id.statistics),
                new AccelerateDecelerateInterpolator(),
                getContext().getResources().getDrawable(R.drawable.project_menu),
                getContext().getResources().getDrawable(R.drawable.icon_close_menu)));

    }



    public void DownloadStatistics(){

        DatabaseReference mDatabase;
        mDatabase = FirebaseDatabase.getInstance().getReference().child("Statistiques");
        Query query = mDatabase.orderByKey();

        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                //Iterable<DataSnapshot> children = dataSnapshot.getChildren();
                nbr_benif[0] = (long) dataSnapshot.child("nbr_benif_autre").getValue();
                nbr_benif[1] = (long) dataSnapshot.child("nbr_benif_education").getValue();
                nbr_benif[2] = (long) dataSnapshot.child("nbr_benif_emploi").getValue();
                nbr_benif[3] = (long) dataSnapshot.child("nbr_benif_info").getValue();
                nbr_benif[4] = (long) dataSnapshot.child("nbr_benif_medicament").getValue();
                nbr_benif[5] = (long) dataSnapshot.child("nbr_benif_vetement").getValue();
                nbr_total_benif= (long) dataSnapshot.child("nbr_benif").getValue();


                nbr_offres[0]=(long) dataSnapshot.child("nbr_offre_autre").getValue();
                nbr_offres[1]=(long) dataSnapshot.child("nbr_offre_education").getValue();
                nbr_offres[2]=(long) dataSnapshot.child("nbr_offre_emploi").getValue();
                nbr_offres[3]=(long) dataSnapshot.child("nbr_offre_info").getValue();
                nbr_offres[4]=(long) dataSnapshot.child("nbr_offre_medicament").getValue();
                nbr_offres[5]=(long) dataSnapshot.child("nbr_offre_vetement").getValue();
                nbr_total_offres= (long) dataSnapshot.child("nbr_total").getValue();

                nombre_total_benif.setText("Nombre total : "+String.valueOf(nbr_total_benif));
                nombre_total_offres.setText("Nombre total : "+String.valueOf(nbr_total_offres));

                setupPieChartBenif();
                setupPieChartOffres();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }


}
