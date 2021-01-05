package com.example.projet_v1.ListOffre;

import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.toolbox.NetworkImageView;
import com.example.projet_v1.R;

import de.hdodenhof.circleimageview.CircleImageView;

public class OffreCardViewHolder extends RecyclerView.ViewHolder{

    public ImageView offreImage;
    public TextView offreTitle;
    public TextView offreDescription;
    public TextView offreCategorie;
    public Button detail;
    public CircleImageView pic;
    public TextView user_offre;


    public OffreCardViewHolder(@NonNull View itemView) {
        super(itemView);
        offreImage = itemView.findViewById(R.id.offre_image_list);
        offreTitle = itemView.findViewById(R.id.offre_title_list);
        offreDescription = itemView.findViewById(R.id.offre_description);
        offreCategorie = itemView.findViewById(R.id.offre_categorie);
        detail = itemView.findViewById(R.id.action1);
        pic = itemView.findViewById(R.id.image_offreur);
        user_offre = itemView.findViewById(R.id.userName);
    }
}
