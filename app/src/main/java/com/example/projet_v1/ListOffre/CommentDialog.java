package com.example.projet_v1.ListOffre;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;


import androidx.annotation.RequiresApi;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;

import com.bumptech.glide.Glide;
import com.example.projet_v1.Model.Commentaire;
import com.example.projet_v1.Model.Offre;
import com.example.projet_v1.Model.User;
import com.example.projet_v1.Profile.ProfileFragment;
import com.example.projet_v1.R;
import com.example.projet_v1.SimpleCallback;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;

import de.hdodenhof.circleimageview.CircleImageView;

public class CommentDialog extends DialogFragment {
    public static final String TAG = "comment_dialog";
    EditText commentaire;
    CircleImageView usrImg;
    MaterialButton addComment, annuler;
    TextView titreOffre, nameUser;
    Offre offre;

    public static CommentDialog display(FragmentManager fragmentManager, Offre offre) {
        CommentDialog commentDialog = new CommentDialog();
        commentDialog.offre = offre;
        commentDialog.show(fragmentManager,TAG);
        return commentDialog;
    }

    @Override
    public void onStart() {
        super.onStart();
        Dialog dialog = getDialog();
        if (dialog != null) {
            int width = ViewGroup.LayoutParams.MATCH_PARENT;
            int height = ViewGroup.LayoutParams.WRAP_CONTENT;
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            dialog.getWindow().setLayout(width, height);
            dialog.getWindow().setWindowAnimations(R.style.AppTheme_Slide);
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.comment_dialog, container, false);

        commentaire = view.findViewById(R.id.commentaire);
        usrImg = view.findViewById(R.id.userImg);
        addComment = view.findViewById(R.id.ajoutercomm);
        annuler = view.findViewById(R.id.annuler);
        titreOffre = view.findViewById(R.id.titreOffre);
        nameUser = view.findViewById(R.id.nameUser);

        titreOffre.setText(offre.getOffre_titre());

        ProfileFragment.getInfoCurrentUser(new SimpleCallback<User>() {
            @Override
            public void callback(User user) {
                if(user!=null){
                    if(!user.getUsr_img_url().equals("") && getActivity()!=null){
                        Glide.with(getActivity()).load(user.getUsr_img_url()).into(usrImg);
                    }
                    if(!user.getUser_nom().equals((""))){
                        nameUser.setText(user.getUser_nom());
                    }
                }
            }
        });

        annuler.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getDialog().dismiss();
            }
        });

        addComment.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onClick(View v) {
                Commentaire c = new Commentaire(FirebaseAuth.getInstance().getCurrentUser().getUid(),offre.getOffre_id(),commentaire.getText().toString());
                c.setIdOffreur(offre.getUserID());
                c.addNewComment();
                getDialog().dismiss();
            }
        });

        return view;
    }
}
