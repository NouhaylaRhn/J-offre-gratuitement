package com.example.projet_v1.Model;

import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Commentaire {
    private String idComment;
    private String idUser;
    private String idOffre;
    private String comment;
    private String date;
    String idOffreur;

    public Commentaire(String idUser, String idOffre, String comment) {
        this.idUser = idUser;
        this.idOffre = idOffre;
        this.comment = comment;
    }

    public String getIdUser() {
        return idUser;
    }

    public void setIdUser(String idUser) {
        this.idUser = idUser;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getIdOffre() {
        return idOffre;
    }

    public void setIdOffre(String idOffre) {
        this.idOffre = idOffre;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getIdComment() {
        return idComment;
    }

    public void setIdComment(String idComment) {
        this.idComment = idComment;
    }

    public String getIdOffreur() {
        return idOffreur;
    }

    public void setIdOffreur(String idOffreur) {
        this.idOffreur = idOffreur;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void addNewComment(){
        DatabaseReference mRef1;
        DatabaseReference mRef2;

        mRef1 = FirebaseDatabase.getInstance().getReference().child("users").child(idOffreur).child("Notifications");
        mRef1.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.child("nbr_attente").exists() && dataSnapshot.child("nbr_total").exists()){
                    long nbrAttente = dataSnapshot.child("nbr_attente").getValue(Long.class);
                    nbrAttente = nbrAttente+1;
                    dataSnapshot.getRef().child("nbr_attente").setValue(nbrAttente);
                    long nbrTotal = dataSnapshot.child("nbr_total").getValue(Long.class);
                    nbrTotal = nbrTotal +1;
                    dataSnapshot.getRef().child("nbr_total").setValue(nbrTotal);

                    //String idO = dataSnapshot.getRef().push().getKey();
                    Log.e("OFFRE","-----------> id offre"+idOffre);
                    if(!FirebaseAuth.getInstance().getCurrentUser().getUid().equals(idOffreur))    dataSnapshot.getRef().child(idOffre).child("idOffre").setValue(idOffre);
                    setIdOffre(null);
                    setIdOffreur(null);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        mRef2 = FirebaseDatabase.getInstance().getReference().child("Offre").child(getIdOffre()).child("Commentaires");
        String id = mRef2.push().getKey();
        setIdComment(id);
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy - HH:mm");
        LocalDateTime now = LocalDateTime.now();
        setDate(dtf.format(now));
        mRef2.child(id).setValue(this);

        mRef2.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.child("nbr_total").exists()){
                    //mise à jour du nbr total des commentaires
                    long nbrTotal = dataSnapshot.child("nbr_total").getValue(Long.class);
                    nbrTotal = nbrTotal+1;
                    dataSnapshot.getRef().child("nbr_total").setValue(nbrTotal);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }
}
