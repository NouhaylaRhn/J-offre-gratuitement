package com.example.projet_v1.Model;

import android.util.Log;

import androidx.annotation.NonNull;

import com.example.projet_v1.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.IgnoreExtraProperties;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

@IgnoreExtraProperties
public class Offre implements Serializable {

    private String offre_id;
    public  String offre_titre;
    private String offre_description;
    public String offre_categorie;
    public int offre_nbr_beneficiaire;
    private int offre_nbr_restant;
    private Date offre_deadline;
    private boolean enable;
    private String mImageUrl;
    private double latitude;
    private double logitude;
    private String userID;


    public String getOffre_categorie() {
        return offre_categorie;
    }

    public void setOffre_categorie(String offre_categorie) {
        this.offre_categorie = offre_categorie;
    }

    public Offre() {

    }

    public Offre(String offre_id, String offre_titre, String offre_description, Date offre_deadline, int offre_nbr_beneficiaire, boolean enable) {
        this.offre_id = offre_id;

        this.offre_titre = offre_titre;
        this.offre_description = offre_description;
        this.offre_deadline = offre_deadline;
        this.offre_nbr_beneficiaire = offre_nbr_beneficiaire;
        this.enable = enable;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLogitude() {
        return logitude;
    }

    public void setLogitude(double logitude) {
        this.logitude = logitude;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public int getOffre_nbr_restant() {
        return offre_nbr_restant;
    }

    public void setOffre_nbr_restant(int offre_nbr_restant) {
        this.offre_nbr_restant = offre_nbr_restant;
    }

    public String getOffre_id() {
        return offre_id;
    }

    public void setOffre_id(String offre_id) {
        this.offre_id = offre_id;
    }



    public String getOffre_titre() {
        return offre_titre;
    }

    public void setOffre_titre(String offre_titre) {
        this.offre_titre = offre_titre;
    }

    public String getOffre_description() {
        return offre_description;
    }

    public void setOffre_description(String offre_description) {
        this.offre_description = offre_description;
    }

    public Date getOffre_deadline() {
        return offre_deadline;
    }

    public void setOffre_deadline(Date offre_deadline) {
        this.offre_deadline = offre_deadline;
    }

    public int getOffre_nbr_beneficiaire() {
        return offre_nbr_beneficiaire;
    }

    public void setOffre_nbr_beneficiaire(int offre_nbr_beneficiaire) {
        this.offre_nbr_beneficiaire = offre_nbr_beneficiaire;
    }

    public boolean isEnable() {
        return enable;
    }

    public void setEnable(boolean enable) {
        this.enable = enable;
    }

    public String getmImageUrl() {
        return mImageUrl;
    }

    public void setmImageUrl(String mImageUrl) {
        this.mImageUrl = mImageUrl;
    }


    public static ArrayList<Offre> initOffreList(){

        final ArrayList<Offre> listnewsData= new ArrayList<Offre>();

        Date aujourdhui = new Date();
        //add data and view it


        DatabaseReference mDatabase;
        mDatabase = FirebaseDatabase.getInstance().getReference().child("Offre");
        Query query = mDatabase.orderByKey();

        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Iterable<DataSnapshot> children = dataSnapshot.getChildren();
                Iterator<DataSnapshot> iterator = children.iterator();

                while (iterator.hasNext()){
                    DataSnapshot next = (DataSnapshot) iterator.next();

                    String offre_titre = (String) next.child("offre_titre").getValue();
                    String offre_description=(String) next.child("offre_description").getValue();
                    long nbr = (long) next.child("offre_nbr_beneficiaire").getValue();
                    int offre_nbr_beneficiaire = new Long(nbr).intValue();
                    //Date offre_deadline=(String) next.child("offre_deadline").getValue();
                    // boolean enable=(String) next.child("enable").getValue();

                    System.out.println("Value "+offre_titre + " " + nbr);


                    String mImageUrl=(String) next.child("mImageUrl").getValue();
                    Date aujourdhui = new Date();
                    listnewsData.add(new Offre(1+"",offre_titre,offre_description,aujourdhui,offre_nbr_beneficiaire,true));
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        return listnewsData;
    }

    @Override
    public String toString() {
        return "Offre{" +
                "offre_id='" + offre_id + '\'' +
                ", offre_titre='" + offre_titre + '\'' +
                ", offre_description='" + offre_description + '\'' +
                ", offre_categorie='" + offre_categorie + '\'' +
                ", offre_nbr_beneficiaire=" + offre_nbr_beneficiaire +
                ", offre_nbr_restant=" + offre_nbr_restant +
                ", offre_deadline=" + offre_deadline +
                ", enable=" + enable +
                ", mImageUrl='" + mImageUrl + '\'' +
                ", latitude=" + latitude +
                ", logitude=" + logitude +
                ", userID='" + userID + '\'' +
                '}';
    }

    public void writeNewOffre(){
        DatabaseReference mDatabase;
        mDatabase = FirebaseDatabase.getInstance().getReference().child("Offre");

        /*
        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()) {
                    maxid=dataSnapshot.getChildrenCount();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        */

        //this.setOffre_id((int) (maxid+1));
        String OffreId = mDatabase.push().getKey();
        mDatabase.child(OffreId).child("offre_id").setValue(OffreId);
        mDatabase.child(OffreId).child("offre_titre").setValue(this.offre_titre);
        mDatabase.child(OffreId).child("offre_description").setValue(this.offre_description);
        mDatabase.child(OffreId).child("userID").setValue(this.userID);
        mDatabase.child(OffreId).child("offre_categorie").setValue(this.offre_categorie);
        mDatabase.child(OffreId).child("offre_nbr_beneficiaire").setValue(this.offre_nbr_beneficiaire);
        mDatabase.child(OffreId).child("offre_nbr_restant").setValue(this.offre_nbr_beneficiaire);
        mDatabase.child(OffreId).child("offre_deadline").setValue(this.offre_deadline.toString());
        mDatabase.child(OffreId).child("enable").setValue(true);
        mDatabase.child(OffreId).child("mImageUrl").setValue(this.mImageUrl);
        mDatabase.child(OffreId).child("latitude").setValue(this.latitude);
        mDatabase.child(OffreId).child("longitude").setValue(this.logitude);
        mDatabase.child(OffreId).child("Commentaires").child("nbr_total").setValue(0);
        mDatabase.child(OffreId).child("Benificiaires").child("nbr").setValue(0);

        updateStatistiques();
    }

    public void updateStatistiques(){
        DatabaseReference mRef = FirebaseDatabase.getInstance().getReference().child("Statistiques");
        mRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.child("nbr_total").exists() && dataSnapshot.child("nbr_benif").exists() && dataSnapshot.child("nbr_offre_vetement").exists() &&dataSnapshot.child("nbr_benif_vetement").exists() &&
                        dataSnapshot.child("nbr_offre_medicament").exists() &&dataSnapshot.child("nbr_benif_medicament").exists() && dataSnapshot.child("nbr_offre_info").exists() &&dataSnapshot.child("nbr_benif_info").exists() &&
                        dataSnapshot.child("nbr_offre_emploi").exists() &&dataSnapshot.child("nbr_benif_emploi").exists() && dataSnapshot.child("nbr_offre_education").exists() &&dataSnapshot.child("nbr_benif_education").exists() &&
                        dataSnapshot.child("nbr_offre_autre").exists() &&dataSnapshot.child("nbr_benif_autre").exists()){
                    //mise à jours du nbr total des offres
                    long nbrTotal = dataSnapshot.child("nbr_total").getValue(Long.class);
                    nbrTotal = nbrTotal+1;
                    dataSnapshot.getRef().child("nbr_total").setValue(nbrTotal);

                    //màj des nbr de beneficiaires
                    long nbrBenif = dataSnapshot.child("nbr_benif").getValue(Long.class);
                    nbrBenif = nbrBenif +offre_nbr_beneficiaire;
                    dataSnapshot.getRef().child("nbr_benif").setValue(nbrBenif);

                    //màj des statistiques selon les catégories
                    switch (offre_categorie){
                        case "Vetement":
                            long nbrOffreVet = dataSnapshot.child("nbr_offre_vetement").getValue(Long.class);
                            nbrOffreVet = nbrOffreVet+1;
                            dataSnapshot.getRef().child("nbr_offre_vetement").setValue(nbrOffreVet);
                            long nbrBenifVet = dataSnapshot.child("nbr_benif_vetement").getValue(Long.class);
                            nbrBenifVet = nbrBenifVet +offre_nbr_beneficiaire;
                            dataSnapshot.getRef().child("nbr_benif_vetement").setValue(nbrBenifVet);
                            break;

                        case "medicaments":
                            long nbrOffreMedic = dataSnapshot.child("nbr_offre_medicament").getValue(Long.class);
                            nbrOffreMedic = nbrOffreMedic+1;
                            dataSnapshot.getRef().child("nbr_offre_medicament").setValue(nbrOffreMedic);
                            long nbrBenifMedic = dataSnapshot.child("nbr_benif_medicament").getValue(Long.class);
                            nbrBenifMedic = nbrBenifMedic +offre_nbr_beneficiaire;
                            dataSnapshot.getRef().child("nbr_benif_medicament").setValue(nbrBenifMedic);
                            break;

                        case "Informatique et multimedia":
                            long nbrOffreInfo = dataSnapshot.child("nbr_offre_info").getValue(Long.class);
                            nbrOffreInfo = nbrOffreInfo+1;
                            dataSnapshot.getRef().child("nbr_offre_info").setValue(nbrOffreInfo);
                            long nbrBenifInfo = dataSnapshot.child("nbr_benif_info").getValue(Long.class);
                            nbrBenifInfo = nbrBenifInfo +offre_nbr_beneficiaire;
                            dataSnapshot.getRef().child("nbr_benif_info").setValue(nbrBenifInfo);
                            break;

                        case "Emploi":
                            long nbrOffreEmp = dataSnapshot.child("nbr_offre_emploi").getValue(Long.class);
                            nbrOffreEmp = nbrOffreEmp+1;
                            dataSnapshot.getRef().child("nbr_offre_emploi").setValue(nbrOffreEmp);
                            long nbrBenifEmp = dataSnapshot.child("nbr_benif_emploi").getValue(Long.class);
                            nbrBenifEmp = nbrBenifEmp +offre_nbr_beneficiaire;
                            dataSnapshot.getRef().child("nbr_benif_emploi").setValue(nbrBenifEmp);
                            break;

                        case "Education":
                            long nbrOffreEdu = dataSnapshot.child("nbr_offre_education").getValue(Long.class);
                            nbrOffreEdu = nbrOffreEdu+1;
                            dataSnapshot.getRef().child("nbr_offre_education").setValue(nbrOffreEdu);
                            long nbrBenifEdu = dataSnapshot.child("nbr_benif_education").getValue(Long.class);
                            nbrBenifEdu = nbrBenifEdu +offre_nbr_beneficiaire;
                            dataSnapshot.getRef().child("nbr_benif_education").setValue(nbrBenifEdu);
                            break;

                        case "Autres":
                            long nbrOffreAutre = dataSnapshot.child("nbr_offre_autre").getValue(Long.class);
                            nbrOffreAutre = nbrOffreAutre+1;
                            dataSnapshot.getRef().child("nbr_offre_autre").setValue(nbrOffreAutre);
                            long nbrBenifAutre = dataSnapshot.child("nbr_benif_autre").getValue(Long.class);
                            nbrBenifAutre = nbrBenifAutre +offre_nbr_beneficiaire;
                            dataSnapshot.getRef().child("nbr_benif_autre").setValue(nbrBenifAutre);
                            break;
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
