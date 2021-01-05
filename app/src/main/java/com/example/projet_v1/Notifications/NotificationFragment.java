package com.example.projet_v1.Notifications;

import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.projet_v1.Adapters.ListBenifAdapter;
import com.example.projet_v1.Adapters.NotificationAdapter;
import com.example.projet_v1.ListOffre.NavigationIconClickListener;
import com.example.projet_v1.R;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

public class NotificationFragment extends Fragment {
    private RecyclerView recyclerView;
    private NotificationAdapter notificationAdapter;
    MaterialButton nbrNotifications, deleteNotifications;

    private int mNotifsPerPage=5;
    private boolean mIsLoading =false;
    private int mTotalItemCount = 0;
    private int mLastVisibleItemPosition = 0;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onStart() {
        super.onStart();
        DatabaseReference mRefDB = FirebaseDatabase.getInstance().getReference().child("users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("Notifications");
        mRefDB.child("nbr_attente").setValue(0);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_notifications, container, false);

        nbrNotifications = view.findViewById(R.id.nbrnotifications);
        deleteNotifications = view.findViewById(R.id.deletenotifications);

        // Set up the toolbar
        setUpToolbar(view);

        recyclerView = view.findViewById(R.id.recycler_view);
        notificationAdapter = new NotificationAdapter(getContext());
        recyclerView.setAdapter(notificationAdapter);

        final LinearLayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(mLayoutManager);

        downloadListNotifs(null);

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                mTotalItemCount = mLayoutManager.getItemCount();
                mLastVisibleItemPosition = mLayoutManager.findLastVisibleItemPosition();
                if(!mIsLoading && mTotalItemCount<= (mLastVisibleItemPosition+mNotifsPerPage)){
                    downloadListNotifs(notificationAdapter.getLastNotifId());
                    mIsLoading = true;
                }
            }
        });


        DatabaseReference mRef = FirebaseDatabase.getInstance().getReference().child("users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("Notifications");
        mRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.child("nbr_total").getValue() != null){
                    long nbr1 = (long) dataSnapshot.child("nbr_total").getValue();
                    int nbr_total = new Long(nbr1).intValue();

                    if(nbr_total > 0){
                        nbrNotifications.setText(nbr_total+" Notifications");
                        deleteNotifications.setVisibility(View.VISIBLE);
                    }
                    else {
                        nbrNotifications.setText("Pas de notifications pour l'instant");
                        deleteNotifications.setVisibility(View.GONE);
                    }
                }
                notificationAdapter.clear();
                downloadListNotifs(null);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        deleteNotifications.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getDialog(R.string.title_comment,R.string.msg_notif,R.drawable.ic_warning).setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        deleteAllNotifications();
                    }
                }).show();
            }
        });

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            view.findViewById(R.id.notifications).setBackgroundResource(R.drawable.fragment_round_corners);
        }
        return view;
    }

    public void deleteAllNotifications(){
        DatabaseReference mRef = FirebaseDatabase.getInstance().getReference().child("users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("Notifications");
        mRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                for(DataSnapshot dataSnapshot : snapshot.getChildren()) {
                        dataSnapshot.getRef().setValue(null);
                }
                snapshot.getRef().child("nbr_total").setValue(0);
                snapshot.getRef().child("nbr_attente").setValue(0);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public MaterialAlertDialogBuilder getDialog(int title, int msg, int icon){
        return new MaterialAlertDialogBuilder(getContext()).setTitle(title).setMessage(msg).setIcon(getContext().getResources().getDrawable(icon)).setCancelable(true);
    }

    public void downloadListNotifs(String notifId){
        DatabaseReference mDatabase;
        mDatabase = FirebaseDatabase.getInstance().getReference().child("users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("Notifications");
        Query query ;
        if(notifId == null)
            query =mDatabase.orderByKey().limitToLast(mNotifsPerPage);
        else
            query = mDatabase.orderByKey().endAt(notifId).limitToLast(mNotifsPerPage);

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                List<String> listNotifs = new ArrayList<>();
                Iterable<DataSnapshot> children = dataSnapshot.getChildren();
                Iterator<DataSnapshot> iterator = children.iterator();
                DataSnapshot next;
                listNotifs.clear();

                while (iterator.hasNext()){
                    next = (DataSnapshot) iterator.next();
                    if(!notificationAdapter.listNotifs.isEmpty()){
                        if(notificationAdapter.getLastNotifId().equals((String) next.child("idOffre").getValue())) break;
                    }
                    if(next.child("idOffre").getValue()!=null) {
                        String idOffre = (String) next.child("idOffre").getValue();

                        listNotifs.add(idOffre);
                    }
                }
                Log.e("NOTIF","-----------------> "+notificationAdapter.getItemCount());
                Collections.reverse(listNotifs);
                notificationAdapter.addAll(listNotifs);
                mIsLoading = false;
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                mIsLoading = false;
            }
        });
    }

    private void setUpToolbar(View view) {
        Toolbar toolbar = view.findViewById(R.id.app_bar);
        AppCompatActivity activity = (AppCompatActivity) getActivity();
        if (activity != null) {
            activity.setSupportActionBar(toolbar);
        }
        toolbar.setNavigationOnClickListener(new NavigationIconClickListener(
                getContext(),
                view.findViewById(R.id.notifications),
                new AccelerateDecelerateInterpolator(),
                getContext().getResources().getDrawable(R.drawable.project_menu),
                getContext().getResources().getDrawable(R.drawable.icon_close_menu)));
    }
}
