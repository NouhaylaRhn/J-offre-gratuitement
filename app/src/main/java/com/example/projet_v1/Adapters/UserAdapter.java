package com.example.projet_v1.Adapters;


import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.example.projet_v1.Model.User;
import com.example.projet_v1.R;
import com.example.projet_v1.messageActivity;

import java.util.ArrayList;
import java.util.List;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.ViewHolder> {

    private Context mContext;
    private List<User> mUsers;

    public UserAdapter(Context mContext , List<User> mUsers) {
        this.mContext=mContext;
        this.mUsers= mUsers;
    }


    public class ViewHolder extends RecyclerView.ViewHolder{
        public TextView username;
        public  ImageView profile_image;


        public ViewHolder(View itemView){
            super(itemView);
            username= itemView.findViewById(R.id.username);
            profile_image = itemView.findViewById(R.id.profile_image);

        }
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.user_item , parent, false);
        return new ViewHolder(view);

    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        final User user = mUsers.get(position);
        holder.username.setText(user.getUser_nom());
        if (user.getUsr_img_url().equals("default")) {
            holder.profile_image.setImageResource(R.mipmap.ic_launcher);

        } else {
            Glide.with(mContext).load(user.getUsr_img_url()).into(holder.profile_image);
        }


        holder.itemView.setOnClickListener(new View.OnClickListener() {


            @Override
            public void onClick(View v) {

                Intent intent = new Intent(mContext, messageActivity.class);
                intent.putExtra("userid", user.getUser_id());
                mContext.startActivity(intent);

            }
        });




    }
    @Override
    public int getItemCount(){
        return mUsers.size();
    }

    public void filterList(ArrayList<User> filteredList) {
        mUsers = filteredList;
        notifyDataSetChanged();
    }
}


