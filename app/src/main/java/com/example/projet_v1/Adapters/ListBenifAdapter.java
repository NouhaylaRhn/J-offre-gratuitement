package com.example.projet_v1.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.projet_v1.Model.User;
import com.example.projet_v1.R;
import com.example.projet_v1.SimpleCallback;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class ListBenifAdapter extends RecyclerView.Adapter<BenefViewHolder>{
    Context context;
    public List<String> listUsers;

    public ListBenifAdapter(Context context){
        this.context = context;
        listUsers = new ArrayList<>();
    }

    @NonNull
    @Override
    public BenefViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View layoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.benif_item,parent,false);
        return new BenefViewHolder(layoutView);
    }

    @Override
    public void onBindViewHolder(@NonNull final BenefViewHolder holder, int position) {
        String userId = listUsers.get(position);

        getInfoUser(new SimpleCallback<User>() {
            @Override
            public void callback(User user) {
                if(user!=null){
                    if(!user.getUsr_img_url().equals("")){
                        Glide.with(context.getApplicationContext()).load(user.getUsr_img_url()).thumbnail(0.1f).diskCacheStrategy(DiskCacheStrategy.ALL).into(holder.imgUser);}

                    if(!user.getUser_nom().equals((""))){
                        holder.username.setText(user.getUser_nom()); }

                    if(!user.getUsr_date_naissance().equals((""))){
                        holder.dtn.setText("Date de naissance : "+user.getUsr_date_naissance()); }

                    if(!user.getUser_tel().equals((""))){
                        holder.num.setText("Numéro de téléphone : "+user.getUser_tel()); }
                }
            }
        },userId);
    }

    @Override
    public int getItemCount() {
        return listUsers.size();
    }

    public void addAll(List<String> benefs){
        int initialSize = listUsers.size();
        listUsers.addAll(benefs);
        notifyItemRangeInserted(initialSize,benefs.size());
    }

    public String getLastBenefId(){
        if (!listUsers.isEmpty())
            return listUsers.get(listUsers.size() - 1);
        return null;
    }

    public void clear(){
        listUsers.clear();
        notifyDataSetChanged();
    }

    public void getInfoUser(@NonNull final SimpleCallback<User> finishedCallback, String userID){
        DatabaseReference dr = FirebaseDatabase.getInstance().getReference().child("users").child(userID);
        dr.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User user = new User();
                String id = (String)dataSnapshot.child("user_id").getValue();
                String nom = (String)dataSnapshot.child("user_nom").getValue();
                String pic = (String)dataSnapshot.child("usr_img_url").getValue();
                String num = (String)dataSnapshot.child("user_tel").getValue();
                String dtn = (String)dataSnapshot.child("usr_date_naissance").getValue();
                user.setUser_id(id);
                user.setUsr_date_naissance(dtn);
                user.setUser_nom(nom);
                user.setUsr_img_url(pic);
                user.setUser_tel(num);
                finishedCallback.callback(user);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }
}

class BenefViewHolder extends RecyclerView.ViewHolder {
    CircleImageView imgUser;
    TextView username, dtn, num;

    public BenefViewHolder(@NonNull View itemView) {
        super(itemView);
        imgUser = itemView.findViewById(R.id.user_img);
        username = itemView.findViewById(R.id.nameUser);
        dtn = itemView.findViewById(R.id.dtn);
        num = itemView.findViewById(R.id.num);
    }
}