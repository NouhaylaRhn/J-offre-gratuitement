package com.example.projet_v1.ListOffre;

import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;

import com.example.projet_v1.Model.Offre;
import com.example.projet_v1.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import static com.facebook.FacebookSdk.getApplicationContext;

public class OffreGridFragment extends Fragment {
    private DatabaseReference mDatabase;
    final List<Offre> listnewsData= new ArrayList<Offre>();
    private RecyclerView recyclerView;
    private OffreCardRecyclerViewAdapter adapter;
    private int mTotalItemCount = 0;
    private int mLastVisibleItemPosition = 0;
    private boolean mIsLoading =false;
    private int mPostsPerPage=6;
    SwipeRefreshLayout swipeLayout;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_offre_grid, container, false);

        // Set up the toolbar
        setUpToolbar(view);

        swipeLayout = view.findViewById(R.id.offre_grid);

        //set up the RecyclerView
        recyclerView = view.findViewById(R.id.recycler_view);
        adapter = new OffreCardRecyclerViewAdapter(getApplicationContext());
        recyclerView.setAdapter(adapter);
        recyclerView.setHasFixedSize(true);
        final GridLayoutManager mLayoutManager = new GridLayoutManager(getContext(),1, GridLayoutManager.VERTICAL,false);
        recyclerView.setLayoutManager(mLayoutManager);

        //-----------------------------------------------------------------------//
        downloadList(null);
        //-----------------------------------------------------------------------///

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                mTotalItemCount = mLayoutManager.getItemCount();
                mLastVisibleItemPosition = mLayoutManager.findLastVisibleItemPosition();
                if(!mIsLoading && mTotalItemCount<= (mLastVisibleItemPosition+mPostsPerPage)){
                    Log.e("ICI","--------------> ici");
                    downloadList(adapter.getLastOffreId());
                    mIsLoading = true;
                }
            }
        });

        swipeLayout.setColorSchemeColors(
                getResources().getColor(android.R.color.holo_blue_bright),
                getResources().getColor(android.R.color.holo_green_light),
                getResources().getColor(android.R.color.holo_orange_light),
                getResources().getColor(android.R.color.holo_red_light)
        );

        swipeLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                adapter.clear();
                new Handler().postDelayed(new Runnable() {
                    @Override public void run() {
                        swipeLayout.setRefreshing(false);
                    }
                }, 2000);
            }
        });

        int largePadding = getResources().getDimensionPixelSize(R.dimen.project_offre_grid_spacing);
        int smallPadding = getResources().getDimensionPixelSize(R.dimen.project_offre_grid_spacing_small);
        recyclerView.addItemDecoration(new OffreGridItemDecoration(largePadding,smallPadding));

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            view.findViewById(R.id.offre_grid).setBackgroundResource(R.drawable.fragment_round_corners);
        }

        //-----------------------------------------------------------------------//

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
                view.findViewById(R.id.offre_grid),
                new AccelerateDecelerateInterpolator(),
                getContext().getResources().getDrawable(R.drawable.project_menu),
                getContext().getResources().getDrawable(R.drawable.icon_close_menu)));

    }




    public void downloadList(String offreID){

        Date aujourdhui = new Date();
        //add data and view it

        DatabaseReference mDatabase;
        mDatabase = FirebaseDatabase.getInstance().getReference().child("Offre");
        Query query ;
        if(offreID == null)
            query =mDatabase.orderByKey().limitToLast(mPostsPerPage);
        else
            query = mDatabase.orderByKey().endAt(offreID).limitToLast(mPostsPerPage);


        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                //listnewsData.clear();
                List<Offre> listeOffre = new ArrayList<>();
                Iterable<DataSnapshot> children = dataSnapshot.getChildren();
                Iterator<DataSnapshot> iterator = children.iterator();
                DataSnapshot next;

                while (iterator.hasNext()){
                    next = (DataSnapshot) iterator.next();
                    if(!adapter.offreList.isEmpty()){
                        if(adapter.getLastOffreId().equals((String) next.child("offre_id").getValue())) continue;
                    }
                    //and nbr restant>0
                    if(next.child("latitude").getValue()!=null && next.child("longitude").getValue()!=null && next.child("offre_nbr_beneficiaire").getValue()!=null && next.child("offre_nbr_restant").getValue()!=null && next.child("enable").getValue()!=null && (boolean) next.child("enable").getValue()){
                        String offre_titre = (String) next.child("offre_titre").getValue();
                        String offre_description=(String) next.child("offre_description").getValue();
                        long nbr = (long) next.child("offre_nbr_beneficiaire").getValue();
                        int offre_nbr_beneficiaire = new Long(nbr).intValue();
                        long nbr1= (long) next.child("offre_nbr_restant").getValue();
                        int offre_nbr_restant = new Long(nbr1).intValue();
                        String offre_categorie = (String)next.child("offre_categorie").getValue();
                        String offre_deadline=(String) next.child("offre_deadline").getValue();
                        boolean enable= (boolean) next.child("enable").getValue();
                        //Adresse adresse = (Adresse) next.child("offre_adresse").getValue();
                        String mImageUrl=(String) next.child("mImageUrl").getValue();
                        String userID = (String) next.child("userID").getValue();
                        String offreID = (String) next.child("offre_id").getValue();

                        double lat = (double) next.child("latitude").getValue();
                        double lon = (double) next.child("longitude").getValue();

                        Date aujourdhui = new Date(offre_deadline);

                        Offre offre = new Offre();
                        offre.setmImageUrl(mImageUrl);
                        offre.setOffre_titre(offre_titre);
                        offre.setOffre_description(offre_description);
                        offre.setOffre_categorie(offre_categorie);
                        offre.setUserID(userID);
                        offre.setOffre_id(offreID);

                        //System.out.println(aujourdhui);
                        //Log.e("deadline","date limite : "+aujourdhui.toString());
                        offre.setEnable(enable);
                        offre.setOffre_nbr_beneficiaire(offre_nbr_beneficiaire);
                        offre.setOffre_nbr_restant(offre_nbr_restant);
                        offre.setOffre_deadline(aujourdhui);
                        offre.setLatitude(lat);
                        offre.setLogitude(lon);

                        Date now = new Date();
                        boolean test = now.before(aujourdhui);
                        //Log.e("DATE","--------> titre = "+offre.getOffre_titre()+"--------------> cond = "+(test && offre_nbr_restant != 0)+"--------------> test = "+test+"--------------> nbr_restant = "+(offre_nbr_restant));
                        if(test && offre_nbr_restant != 0){
                            listeOffre.add(offre);
                            Log.e("ADD","--------------> added");
                        }
                    }
                }
                Collections.reverse(listeOffre);
                /*OffreCardRecyclerViewAdapter adapter =
                recyclerView.setAdapter(adapter);*/
                adapter.addAll(listeOffre);
                //adapter.notifyDataSetChanged();
                Log.e("Total items : ","---"+adapter.getItemCount());
                mIsLoading = false;
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                mIsLoading = false;
            }
        });
    }
}
