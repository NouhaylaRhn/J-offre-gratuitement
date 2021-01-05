package com.example.projet_v1.MesOffres;

import android.app.Dialog;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.projet_v1.Adapters.CommentAdapter;
import com.example.projet_v1.Adapters.ListBenifAdapter;
import com.example.projet_v1.Model.Commentaire;
import com.example.projet_v1.R;
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

public class ListBenifDialog extends DialogFragment {
    public static final String TAG = "listBenif_dialog";
    String idOffre;

    private RecyclerView recyclerView;
    private ListBenifAdapter listBenifAdapter;

    private int mBenefsPerPage=5;
    private boolean mIsLoading =false;
    private int mTotalItemCount = 0;
    private int mLastVisibleItemPosition = 0;

    private Toolbar toolbar;

    public static ListBenifDialog display(FragmentManager fragmentManager, String idOffre) {
        ListBenifDialog listBenifDialog = new ListBenifDialog();
        listBenifDialog.idOffre = idOffre;
        listBenifDialog.show(fragmentManager, TAG);
        return listBenifDialog;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NORMAL, R.style.AppTheme_FullScreenDialog);
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.list_benif_dialog, container, false);

        toolbar = (Toolbar) view.findViewById(R.id.toolbar);

        recyclerView = view.findViewById(R.id.recycler_view);
        listBenifAdapter = new ListBenifAdapter(getContext());
        recyclerView.setAdapter(listBenifAdapter);

        final LinearLayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(mLayoutManager);

        downloadListBenefs(null);

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                mTotalItemCount = mLayoutManager.getItemCount();
                mLastVisibleItemPosition = mLayoutManager.findLastVisibleItemPosition();
                if(!mIsLoading && mTotalItemCount<= (mLastVisibleItemPosition+mBenefsPerPage)){
                    downloadListBenefs(listBenifAdapter.getLastBenefId());
                    mIsLoading = true;
                }
            }
        });

        return view;
    }

    public void downloadListBenefs(String benefId){
        DatabaseReference mDatabase;
        mDatabase = FirebaseDatabase.getInstance().getReference().child("Offre").child(idOffre).child("Benificiaires");
        Query query ;
        if(benefId == null)
            query =mDatabase.orderByKey().limitToLast(mBenefsPerPage);
        else
            query = mDatabase.orderByKey().endAt(benefId).limitToLast(mBenefsPerPage);

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                List<String> listUsers = new ArrayList<>();
                Iterable<DataSnapshot> children = dataSnapshot.getChildren();
                Iterator<DataSnapshot> iterator = children.iterator();
                DataSnapshot next;
                listUsers.clear();

                while (iterator.hasNext()){
                    next = (DataSnapshot) iterator.next();
                    if(!listBenifAdapter.listUsers.isEmpty()){
                        if(listBenifAdapter.getLastBenefId().equals((String) next.child("idUser").getValue())) break;
                    }
                    if(next.child("idUser").getValue()!=null) {
                        String idUser = (String) next.child("idUser").getValue();

                        listUsers.add(idUser);
                    }
                }
                Collections.reverse(listUsers);
                listBenifAdapter.addAll(listUsers);
                mIsLoading = false;
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                mIsLoading = false;
            }
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ListBenifDialog.this.dismiss();
            }
        });
        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                ListBenifDialog.this.dismiss();
                return true;
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        Dialog dialog = getDialog();
        if (dialog != null) {
            int width = ViewGroup.LayoutParams.MATCH_PARENT;
            int height = ViewGroup.LayoutParams.MATCH_PARENT;
            dialog.getWindow().setLayout(width, height);
            dialog.getWindow().setWindowAnimations(R.style.AppTheme_Slide);

        }
    }
}
