package com.example.projet_v1.Adapters;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Build;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.projet_v1.Model.Commentaire;
import com.example.projet_v1.Model.User;
import com.example.projet_v1.R;
import com.example.projet_v1.SimpleCallback;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class CommentAdapter extends RecyclerView.Adapter<CommentViewHolder>{
    Context context;
    public List<Commentaire> commentaires;
    public List<String> listUsers;
    boolean isOffreUser = false, isCloture = false;
    String idOffre = "";
    private int lp = -1;

    public CommentAdapter(Context context){
        this.context = context;
        commentaires = new ArrayList<>();
        listUsers = new ArrayList<>();
    }

    private void setAnimation(View itemView, int position) {
        if (position > lp) {
            Animation animation = AnimationUtils.loadAnimation(context, R.anim.fade_container_transition);
            itemView.startAnimation(animation);
            lp = position;
        }
    }

    public boolean isCloture() {
        return isCloture;
    }

    public void setCloture(boolean cloture) {
        isCloture = cloture;
    }

    public boolean isOffreUser() {
        return isOffreUser;
    }

    public void setOffreUser(boolean offreUser) {
        isOffreUser = offreUser;
    }

    public String getIdOffre() {
        return idOffre;
    }

    public void setIdOffre(String idOffre) {
        this.idOffre = idOffre;
    }

    @NonNull
    @Override
    public CommentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View layoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.comment_item,parent,false);
        return new CommentViewHolder(layoutView);
    }

    @Override
    public void onBindViewHolder(@NonNull final CommentViewHolder holder, int position) {
        setAnimation(holder.itemView,position);
        if(commentaires != null && position < commentaires.size() && commentaires.get(position).getComment()!=null) {
            final Commentaire commentaire = commentaires.get(position);

            if(commentaire.getIdUser().equals(FirebaseAuth.getInstance().getCurrentUser().getUid()))
                holder.itemView.setBackgroundColor(context.getResources().getColor(R.color.light));

            holder.comment.setText(commentaire.getComment());
            holder.date.setText(commentaire.getDate());

            if(isCloture){
                holder.choixBenif.setEnabled(false);
            }

            if (isOffreUser){
                holder.choixBenif.setVisibility(View.VISIBLE);
            }

            holder.choixBenif.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    getDialog(R.string.title_comment,R.string.msg_comment,R.drawable.ic_warning).setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            if(!isCloture && !ExistBenif(commentaire.getIdUser()))  UpdateOffreAndSendNotificationsToUser(commentaire);
                        }
                    }).show();
                }
            });

            getInfoUser(new SimpleCallback<User>() {
                @Override
                public void callback(User user) {
                    if(user!=null){
                        if(!user.getUsr_img_url().equals("")){
                            Glide.with(context.getApplicationContext()).load(user.getUsr_img_url()).thumbnail(0.1f).diskCacheStrategy(DiskCacheStrategy.ALL).into(holder.imgUser);}
                        if(!user.getUser_nom().equals((""))){
                            holder.username.setText(user.getUser_nom()); }
                        if(!user.getUser_id().equals("")){
                            holder.idUser.setText(user.getUser_id());
                            if(user.getUser_id().equals(FirebaseAuth.getInstance().getCurrentUser().getUid())){
                                holder.choixBenif.setVisibility(View.GONE);
                            }
                            if(ExistBenif(user.getUser_id())){
                                holder.choixBenif.setText("Vous avez déja choisi ce bénéficiaire !");
                                holder.choixBenif.setTextColor(context.getResources().getColor(R.color.lightGreen));
                                holder.choixBenif.setEnabled(false);
                            }
                        }

                    }
                }
            },commentaire.getIdUser());
        }
    }

    public void UpdateOffreAndSendNotificationsToUser(final Commentaire commentaire){
        DatabaseReference mRef = FirebaseDatabase.getInstance().getReference().child("Offre").child(idOffre);
        DatabaseReference mRef1 = FirebaseDatabase.getInstance().getReference().child("users").child(commentaire.getIdUser()).child("Notifications");
                    /*String id = mRef.push().getKey();
                    mRef.child("Benificiaires").child(id).child("idUser").setValue(commentaire.getIdUser());*/

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
                    dataSnapshot.getRef().child(idOffre).child("idOffre").setValue(idOffre);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        mRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.child("offre_nbr_restant").exists()){
                    long nbrRestant = dataSnapshot.child("offre_nbr_restant").getValue(Long.class);
                    nbrRestant = nbrRestant-1;

                    dataSnapshot.getRef().child("offre_nbr_restant").setValue(nbrRestant);
                    //String id = dataSnapshot.getRef().push().getKey();
                    dataSnapshot.getRef().child("Benificiaires").child(commentaire.getIdUser()).child("idUser").setValue(commentaire.getIdUser());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) { }
        });
        //clear();
    }

    public MaterialAlertDialogBuilder getDialog(int title, int msg, int icon){
        return new MaterialAlertDialogBuilder(context).setTitle(title).setMessage(msg).setIcon(context.getResources().getDrawable(icon)).setCancelable(true);
    }

    @Override
    public int getItemCount() {
        return commentaires.size();
    }


    public void addAll(List<Commentaire> comments){
        int initialSize = commentaires.size();
        commentaires.addAll(comments);
        notifyItemRangeInserted(initialSize,comments.size());
    }

    public void addAllBenif(List<String> benifs){
        listUsers.addAll(benifs);
    }

    public void clearBenifs(){
        listUsers.clear();
    }

    public void clear(){
        commentaires.clear();
        notifyDataSetChanged();
    }

    public boolean ExistBenif(String idUser){
        Log.e("BENIF : ", "test exit benef  ="+listUsers.contains(idUser));
        return listUsers.contains(idUser);
    }

    public String getLastCommentId(){
        if (!commentaires.isEmpty())
            return commentaires.get(commentaires.size() - 1).getIdComment();
        return null;
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
                user.setUser_id(id);
                user.setUser_nom(nom);
                user.setUsr_img_url(pic);
                finishedCallback.callback(user);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }
}

class CommentViewHolder extends RecyclerView.ViewHolder {
    CircleImageView imgUser;
    TextView username, idUser;
    TextView comment;
    TextView date;
    MaterialButton choixBenif;

    public CommentViewHolder(@NonNull View itemView) {
        super(itemView);
        idUser = itemView.findViewById(R.id.idUser);
        imgUser = itemView.findViewById(R.id.user_img);
        username = itemView.findViewById(R.id.nameUser);
        comment = itemView.findViewById(R.id.commentUser);
        date = itemView.findViewById(R.id.dateComment);
        choixBenif = itemView.findViewById(R.id.chooseBenif);
    }
}
