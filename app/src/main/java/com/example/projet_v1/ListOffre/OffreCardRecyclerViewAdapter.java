package com.example.projet_v1.ListOffre;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.projet_v1.Model.Offre;
import com.example.projet_v1.Model.User;
import com.example.projet_v1.R;
import com.example.projet_v1.SimpleCallback;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class OffreCardRecyclerViewAdapter extends RecyclerView.Adapter<OffreCardViewHolder> {

    private Context context;
    public List<Offre> offreList;
    boolean isOffreUser = false;
    private int lp = -1;
    //private ImageRequester

    public OffreCardRecyclerViewAdapter(Context context){
        //this.offreList = offreList;
        this.offreList = new ArrayList<>();
        this.context=context;
    }

    private void setAnimation(View itemView, int position) {
        if (position > lp) {
            Animation animation = AnimationUtils.loadAnimation(context, R.anim.fade_container_transition);
            itemView.startAnimation(animation);
            lp = position;
        }
    }

    public boolean isOffreUser() {
        return isOffreUser;
    }

    public void setOffreUser(boolean offreUser) {
        isOffreUser = offreUser;
    }

    @NonNull
    @Override
    public OffreCardViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View layoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.project_offre_card,parent,false);
        return new OffreCardViewHolder(layoutView);
    }

    @Override
    public void onBindViewHolder(@NonNull final OffreCardViewHolder holder, int position) {
        if(offreList != null && position < offreList.size() && offreList.get(position).getOffre_titre()!=null){
            setAnimation(holder.itemView,position);
            final Offre offre = offreList.get(position);
            //holder.offreImage.setImageResource(offre.getOffre_icon());

            Picasso.get().load(offre.getmImageUrl()).fit().centerCrop().into(holder.offreImage);
            //holder.offreImage.setImageURI(Uri.parse(offre.getmImageUrl()));
            holder.offreTitle.setText(offre.getOffre_titre());
            holder.offreDescription.setText(" "+offre.getOffre_description());
            holder.offreCategorie.setText(offre.getOffre_categorie());

            getInfoUser(new SimpleCallback<User>() {
                @Override
                public void callback(User user) {
                    if(user!=null){
                        if(!user.getUsr_img_url().equals("")){
                            Glide.with(context).load(user.getUsr_img_url()).thumbnail(0.1f).diskCacheStrategy(DiskCacheStrategy.ALL).into(holder.pic);}
                        if(!user.getUser_nom().equals((""))){
                            holder.user_offre.setText(user.getUser_nom()); }
                    }
                }
            },offre.getUserID());

            holder.detail.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(v.getContext(),offre.getOffre_titre()+" detail",Toast.LENGTH_LONG).show();
                    Intent intent = new Intent(v.getContext(),DetailOffre.class);
                    intent.putExtra("offre_detail",offre);
                    if(isOffreUser())   intent.putExtra("offre_user","true");
                    else    intent.putExtra("offre_user","false");
                    v.getContext().startActivity(intent);

                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return offreList.size();
    }

    public void addAll(List<Offre> newOffres){
        int initialSize = offreList.size();
        offreList.addAll(newOffres);
        notifyItemRangeInserted(initialSize,newOffres.size());
    }

    public void clear(){
        offreList.clear();
        notifyDataSetChanged();
    }

    public String getLastOffreId(){
        if (!offreList.isEmpty())
            return offreList.get(offreList.size() - 1).getOffre_id();
        return null;
    }

    public void getInfoUser(@NonNull final SimpleCallback<User> finishedCallback, String userID){
        DatabaseReference dr = FirebaseDatabase.getInstance().getReference().child("users").child(userID);
        dr.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User user = new User();
                String nom = (String)dataSnapshot.child("user_nom").getValue();
                String mail =(String)dataSnapshot.child("user_email").getValue();
                String tel = (String)dataSnapshot.child("user_tel").getValue();
                String dtn = (String)dataSnapshot.child("usr_date_naissance").getValue();
                String pic = (String)dataSnapshot.child("usr_img_url").getValue();
                user.setUser_nom(nom);
                user.setUser_email(mail);
                user.setUser_tel(tel);
                user.setUsr_date_naissance(dtn);
                user.setUsr_img_url(pic);
                //Log.e("nom","--------------------------"+user.getUsr_img_url()+"-------------");
                finishedCallback.callback(user);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
