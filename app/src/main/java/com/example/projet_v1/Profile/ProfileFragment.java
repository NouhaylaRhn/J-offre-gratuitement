package com.example.projet_v1.Profile;

import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.example.projet_v1.ListOffre.NavigationIconClickListener;
import com.example.projet_v1.Model.User;
import com.example.projet_v1.R;
import com.example.projet_v1.SimpleCallback;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileFragment extends Fragment {
    Button edit;
    TextView nom;
    TextView mail;
    TextView phone;
    TextView date;
    CircleImageView picture;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        // Set up the toolbar
        setUpToolbar(view);

        edit = view.findViewById(R.id.update);
        nom = view.findViewById(R.id.nom);
        mail = view.findViewById(R.id.mail);
        phone = view.findViewById(R.id.phone);
        date = view.findViewById(R.id.date);
        picture = view.findViewById(R.id.profile_image);

        getInfoCurrentUser(new SimpleCallback<User>() {
            @Override
            public void callback(User user) {
                if(user!=null){
                    if(!user.getUsr_img_url().equals("") && getContext() != null){
                        Glide.with(getContext()).load(user.getUsr_img_url()).into(picture);}
                    if(!user.getUser_nom().equals((""))){
                        nom.setText(user.getUser_nom()); }
                    if(!user.getUser_email().equals((""))){
                        mail.setText(user.getUser_email()); }
                    if(!user.getUser_tel().equals((""))){
                        phone.setText(user.getUser_tel()); }
                    if(!user.getUsr_date_naissance().equals((""))){
                        date.setText(user.getUsr_date_naissance()); }
                }
            }
        });

        edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openProfileDialog();
            }
        });

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            view.findViewById(R.id.profile).setBackgroundResource(R.drawable.fragment_round_corners);
        }

        return view;
    }

    private void setUpToolbar(View view) {
        Toolbar toolbar = view.findViewById(R.id.app_bar);
        AppCompatActivity activity = (AppCompatActivity) getActivity();
        if (activity != null) {
            activity.setSupportActionBar(toolbar);
        }
        toolbar.setNavigationOnClickListener(new NavigationIconClickListener(
                getContext(),
                view.findViewById(R.id.profile),
                new AccelerateDecelerateInterpolator(),
                getContext().getResources().getDrawable(R.drawable.project_menu),
                getContext().getResources().getDrawable(R.drawable.icon_close_menu)));
    }

    public void openProfileDialog(){
        if(getActivity()!=null)
            ProfileDialog.display(getActivity().getSupportFragmentManager());
    }

    public static void getInfoCurrentUser(@NonNull final SimpleCallback<User> finishedCallback){
        DatabaseReference dr = FirebaseDatabase.getInstance().getReference().child("users").child(FirebaseAuth.getInstance().getCurrentUser().getUid());
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
