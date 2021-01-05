package com.example.projet_v1.ListOffre;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.projet_v1.Adapters.CommentAdapter;
import com.example.projet_v1.MesOffres.ListBenifDialog;
import com.example.projet_v1.Model.Commentaire;
import com.example.projet_v1.Model.Offre;
import com.example.projet_v1.Profile.ProfileDialog;
import com.example.projet_v1.R;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.List;


public class DetailOffreFragment extends Fragment {

    private ImageView imageOffre;
    private TextView Titre, cloture, aucun, comm;
    private TextView Description;
    private MaterialButton addComment, showLocation, nbrBenif, showBenef, deadline;

    private RecyclerView recyclerView;
    private CommentAdapter commentAdapter;

    private int mCommentsPerPage=5;
    private boolean mIsLoading =false;
    private int mTotalItemCount = 0;
    private int mLastVisibleItemPosition = 0;

    String offreUser;

    private Offre offre;
    public DetailOffreFragment(Offre offre,String user) {
        // Required empty public constructor
        this.offre=offre;
        this.offreUser = user;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_detail_offre, container, false);

        imageOffre = (ImageView) view.findViewById(R.id.offre_image_detail);
        Titre = (TextView) view.findViewById(R.id.offre_title_detail);
        Description = (TextView) view.findViewById(R.id.offre_description_detail);
        addComment = view.findViewById(R.id.ajoutcomment);
        deadline = view.findViewById(R.id.dateDeadline);
        nbrBenif = view.findViewById(R.id.benif);
        showLocation = view.findViewById(R.id.viewLocation);
        showBenef = view.findViewById(R.id.showBenif);
        cloture = view.findViewById(R.id.cloture);
        aucun = view.findViewById(R.id.aucun);
        comm = view.findViewById(R.id.comm);

        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
        String strDate= formatter.format(offre.getOffre_deadline());
        deadline.setText("Date limite: "+strDate);
        nbrBenif.setText("Nombre de bénéficiares : "+offre.getOffre_nbr_beneficiaire()+" ("+offre.getOffre_nbr_restant()+" restants)");

        recyclerView = view.findViewById(R.id.listComments);
        commentAdapter = new CommentAdapter(getContext());
        recyclerView.setAdapter(commentAdapter);
        commentAdapter.setIdOffre(offre.getOffre_id());

        final LinearLayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(mLayoutManager);

        downloadListComments(null);

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                mTotalItemCount = mLayoutManager.getItemCount();
                mLastVisibleItemPosition = mLayoutManager.findLastVisibleItemPosition();
                if(!mIsLoading && mTotalItemCount<= (mLastVisibleItemPosition+mCommentsPerPage)){
                    downloadListComments(commentAdapter.getLastCommentId());
                    mIsLoading = true;
                }
            }
        });

        LinearLayout ll = view.findViewById(R.id.forMesOffres);
        if(offreUser.equals("true")){
            ll.setVisibility(View.VISIBLE);
            commentAdapter.setOffreUser(true);
        }
        else{
            ll.setVisibility(View.GONE);
        }

        FirebaseDatabase.getInstance().getReference().child("Offre").child(offre.getOffre_id()).child("Commentaires").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                commentAdapter.clear();

                if(dataSnapshot.child("nbr_total").getValue() != null){
                    long nbr1= (long) dataSnapshot.child("nbr_total").getValue();
                    int nbrTotal = new Long(nbr1).intValue();
                    if(nbrTotal == 0) {
                        aucun.setVisibility(View.VISIBLE);
                        comm.setText("Commentaires");
                    }
                    else {
                        comm.setText("Commentaires ("+nbrTotal+")");
                        aucun.setVisibility(View.GONE);
                    }
                }

                downloadListComments(null);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        Picasso.get().load(offre.getmImageUrl()).fit().centerCrop().into(imageOffre);
        Titre.setText(offre.getOffre_titre());
        Description.setText(offre.getOffre_description());

        showLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LocationOffreDialog.display(getFragmentManager(),offre);
            }
        });

        addComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CommentDialog.display(getFragmentManager(), offre);
            }
        });

        DatabaseReference mRef;
        mRef = FirebaseDatabase.getInstance().getReference().child("Offre").child(offre.getOffre_id());
        mRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.child("offre_nbr_beneficiaire").getValue()!=null && dataSnapshot.child("offre_nbr_restant").getValue()!=null){
                    long nbr = (long) dataSnapshot.child("offre_nbr_beneficiaire").getValue();
                    int offre_nbr_beneficiaire = new Long(nbr).intValue();
                    long nbr1= (long) dataSnapshot.child("offre_nbr_restant").getValue();
                    int offre_nbr_restant = new Long(nbr1).intValue();
                    nbrBenif.setText("Nombre de bénéficiares : "+offre_nbr_beneficiaire+" ("+offre_nbr_restant+" restants)");
                    Date deadlineD = new Date(String.valueOf(offre.getOffre_deadline()));
                    Date now = new Date();
                    //Nombre de benef est atteint
                    if(offre_nbr_restant <1){
                        commentAdapter.setCloture(true);
                    }
                    else commentAdapter.setCloture(false);
                    //Offre Cloturée
                    if(offre_nbr_restant>0 && now.before(deadlineD)){
                        addComment.setEnabled(true);
                        cloture.setVisibility(View.GONE);
                    }
                    else{
                        addComment.setEnabled(false);
                        cloture.setVisibility(View.VISIBLE);
                    }
                }
                if(commentAdapter.isOffreUser()){
                    commentAdapter.clear();
                    downloadListComments(null);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) { }
        });

        mRef.child("Benificiaires").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                commentAdapter.clearBenifs();
                List<String> listUsers = new ArrayList<>();
                Iterable<DataSnapshot> children = dataSnapshot.getChildren();
                Iterator<DataSnapshot> iterator = children.iterator();
                DataSnapshot next;

                while (iterator.hasNext()){
                    next = (DataSnapshot) iterator.next();
                    if(next.child("idUser").getValue() != null){
                        String user_id = (String) next.child("idUser").getValue();
                        listUsers.add(user_id);
                        Log.e("BenifUser : ","----------------->"+user_id);
                    }
                }
                commentAdapter.addAllBenif(listUsers);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        showBenef.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openListBenefsDialog();
            }
        });

        return view;
    }

    public void downloadListComments(String commentId){
        DatabaseReference mDatabase;
        mDatabase = FirebaseDatabase.getInstance().getReference().child("Offre").child(offre.getOffre_id()).child("Commentaires");
        Query query ;
        if(commentId == null)
            query =mDatabase.orderByKey().limitToLast(mCommentsPerPage);
        else
            query = mDatabase.orderByKey().endAt(commentId).limitToLast(mCommentsPerPage);

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                List<Commentaire> listComments = new ArrayList<>();
                Iterable<DataSnapshot> children = dataSnapshot.getChildren();
                Iterator<DataSnapshot> iterator = children.iterator();
                DataSnapshot next;
                listComments.clear();

                while (iterator.hasNext()){
                    next = (DataSnapshot) iterator.next();
                    if(!commentAdapter.commentaires.isEmpty()){
                        if(commentAdapter.getLastCommentId().equals((String) next.child("idComment").getValue())) break;
                    }
                    if(next.child("comment").getValue()!=null) {
                        String comment = (String) next.child("comment").getValue();
                        String date = (String) next.child("date").getValue();
                        String idComment = (String) next.child("idComment").getValue();
                        String idUser = (String) next.child("idUser").getValue();

                        Commentaire c = new Commentaire(idUser, null, comment);
                        c.setDate(date);
                        c.setIdComment(idComment);

                        listComments.add(c);
                    }
                }
                Collections.reverse(listComments);
                commentAdapter.addAll(listComments);
                mIsLoading = false;
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                mIsLoading = false;
            }
        });
    }

    public void openListBenefsDialog(){
        if(getActivity()!=null)
            ListBenifDialog.display(getActivity().getSupportFragmentManager(),offre.getOffre_id());
    }
}
