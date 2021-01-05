package com.example.projet_v1.Adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.projet_v1.ListOffre.DetailOffre;
import com.example.projet_v1.Model.Offre;
import com.example.projet_v1.R;
import com.example.projet_v1.SimpleCallback;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class NotificationAdapter extends RecyclerView.Adapter<NotifViewHolder>{
    Context context;
    public List<String> listNotifs;
    private int lp = -1;

    public NotificationAdapter(Context context){
        this.context = context;
        listNotifs = new ArrayList<>();
    }

    @NonNull
    @Override
    public NotifViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View layoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.notif_item,parent,false);
        return new NotifViewHolder(layoutView);
    }

    @Override
    public void onBindViewHolder(@NonNull final NotifViewHolder holder, int position) {
        final String offreId = listNotifs.get(position);
        setAnimation(holder.itemView,position);
        getInfoOffre(new SimpleCallback<Offre>() {
            @Override
            public void callback(final Offre offre) {
                if(offre!=null){
                    if(!offre.getOffre_titre().equals("") && !offre.getOffre_id().equals("")){
                        if(offre.getUserID().equals(FirebaseAuth.getInstance().getCurrentUser().getUid())) {
                            holder.notifTxt.setText("Un nouveau commentaire est ajouté à votre offre ("+offre.getOffre_titre()+")");

                            if(offre.getUserID().equals(FirebaseAuth.getInstance().getCurrentUser().getUid())) holder.itemView.setBackgroundColor(context.getResources().getColor(R.color.light));

                            holder.detailNotif.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Intent intent = new Intent(v.getContext(), DetailOffre.class);
                                    intent.putExtra("offre_detail",offre);
                                    intent.putExtra("offre_user","true");
                                    context.startActivity(intent);
                                }
                            });
                        }
                        else {
                            holder.notifTxt.setText("Vous etes marqué comme bénéficiaire pour l'offre : "+offre.getOffre_titre());
                            holder.detailNotif.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Intent intent = new Intent(v.getContext(), DetailOffre.class);
                                    intent.putExtra("offre_detail",offre);
                                    intent.putExtra("offre_user","false");
                                    context.startActivity(intent);
                                }
                            });
                        }
                    }
                }
            }
        }, offreId);
    }

    public String getLastNotifId(){
        if (!listNotifs.isEmpty())
            return listNotifs.get(listNotifs.size() - 1);
        return null;
    }

    @Override
    public int getItemCount() {
        return listNotifs.size();
    }

    public void addAll(List<String> benefs){
        int initialSize = listNotifs.size();
        listNotifs.addAll(benefs);
        notifyItemRangeInserted(initialSize,benefs.size());
    }

    public void clear(){
        listNotifs.clear();
        notifyDataSetChanged();
    }

    private void setAnimation(View itemView, int position) {
        if (position > lp) {
            Animation animation = AnimationUtils.loadAnimation(context, R.anim.fade_container_transition);
            itemView.startAnimation(animation);
            lp = position;
        }
    }

    public void getInfoOffre(@NonNull final SimpleCallback<Offre> finishedCallback, String offreID){
        DatabaseReference dr = FirebaseDatabase.getInstance().getReference().child("Offre").child(offreID);
        dr.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.child("latitude").getValue()!=null && dataSnapshot.child("longitude").getValue()!=null &&
                        dataSnapshot.child("offre_nbr_beneficiaire").getValue()!=null && dataSnapshot.child("offre_nbr_restant").getValue()!=null &&
                        dataSnapshot.child("enable").getValue()!=null && (boolean) dataSnapshot.child("enable").getValue()) {

                    String offre_titre = (String) dataSnapshot.child("offre_titre").getValue();
                    String offre_description=(String) dataSnapshot.child("offre_description").getValue();
                    long nbr = (long) dataSnapshot.child("offre_nbr_beneficiaire").getValue();
                    int offre_nbr_beneficiaire = new Long(nbr).intValue();
                    long nbr1= (long) dataSnapshot.child("offre_nbr_restant").getValue();
                    int offre_nbr_restant = new Long(nbr1).intValue();
                    String offre_categorie = (String)dataSnapshot.child("offre_categorie").getValue();
                    String offre_deadline=(String) dataSnapshot.child("offre_deadline").getValue();
                    boolean enable= (boolean) dataSnapshot.child("enable").getValue();

                    String mImageUrl=(String) dataSnapshot.child("mImageUrl").getValue();
                    String userID = (String) dataSnapshot.child("userID").getValue();
                    String offreID = (String) dataSnapshot.child("offre_id").getValue();

                    double lat = (double) dataSnapshot.child("latitude").getValue();
                    double lon = (double) dataSnapshot.child("longitude").getValue();

                    Date aujourdhui = new Date(offre_deadline);

                    Offre offre = new Offre();
                    offre.setmImageUrl(mImageUrl);
                    offre.setOffre_titre(offre_titre);
                    offre.setOffre_description(offre_description);
                    offre.setOffre_categorie(offre_categorie);
                    offre.setUserID(userID);
                    offre.setOffre_id(offreID);
                    offre.setEnable(enable);
                    offre.setOffre_nbr_beneficiaire(offre_nbr_beneficiaire);
                    offre.setOffre_nbr_restant(offre_nbr_restant);
                    offre.setOffre_deadline(aujourdhui);
                    offre.setLatitude(lat);
                    offre.setLogitude(lon);

                    finishedCallback.callback(offre);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }
}

class NotifViewHolder extends RecyclerView.ViewHolder {
    TextView notifTxt;
    MaterialButton detailNotif;

    public NotifViewHolder(@NonNull View itemView) {
        super(itemView);
        notifTxt = itemView.findViewById(R.id.notiftxt);
        detailNotif = itemView.findViewById(R.id.detailnotif);
    }
}
