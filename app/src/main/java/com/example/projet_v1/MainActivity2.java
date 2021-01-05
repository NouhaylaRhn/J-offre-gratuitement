package com.example.projet_v1;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.example.projet_v1.ChatBot.ChatBotDialog;
import com.example.projet_v1.Conversation.ConversationFragment;
import com.example.projet_v1.ListOffre.NavigationHost;
import com.example.projet_v1.ListOffre.OffreGridFragment;
import com.example.projet_v1.MesOffres.AjoutOffreFragment;
import com.example.projet_v1.MesOffres.LocationOffreFragment;
import com.example.projet_v1.MesOffres.MesOffresFragment;
import com.example.projet_v1.MesOffres.PostesFacebookFragment;
import com.example.projet_v1.Notifications.NotificationFragment;
import com.example.projet_v1.Profile.ProfileFragment;
import com.example.projet_v1.Statistiques.StatistiquesFragment;
import com.facebook.AccessToken;
import com.facebook.login.LoginManager;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MainActivity2 extends AppCompatActivity implements NavigationHost {
    private FloatingActionButton chatBotBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_d_offres);

        if (savedInstanceState == null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .add(R.id.container, new OffreGridFragment())
                    .commit();
        }

        //chatbot
        chatBotBtn = findViewById(R.id.chatbotBtn);
        chatBotBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openDialogChatbot();
            }
        });

        backdropListener();
    }

    @Override
    public void navigateTo(Fragment fragment, boolean addToBackstack) {
        FragmentTransaction transaction =
                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.container, fragment);

        if (addToBackstack) {
            transaction.addToBackStack(null);
        }
        transaction.commit();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.project_toolbar_menu, menu);
        return true;
    }

    public void openDialogChatbot(){
        ChatBotDialog.display(getSupportFragmentManager());
    }



    public void backdropListener(){
        Button Acceuil = (Button) findViewById(R.id.acceuil);
        Button MesOffres = (Button) findViewById(R.id.mesoffres);
        Button AjoutOffre = (Button) findViewById(R.id.ajoutoffre);
        Button MonCompte = (Button) findViewById(R.id.idmoncompte);
        Button Deconnexion = (Button)findViewById(R.id.idsedeconnecter);
        Button PostesFacebook = (Button) findViewById(R.id.idpostesfacebook);
        Button Notifications = (Button) findViewById(R.id.idnotifs);
        final Button nbrNotifs = (Button) findViewById(R.id.nbrNotifs);


        Button LocationOffres = (Button) findViewById(R.id.locationoffres);
        Button Statistiques = (Button) findViewById(R.id.statistiques);

        Statistiques.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getSupportFragmentManager().beginTransaction().replace(R.id.container, new StatistiquesFragment()).commit();
            }
        });

        Acceuil.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getSupportFragmentManager().beginTransaction().replace(R.id.container, new OffreGridFragment()).commit();
            }
        });

        MesOffres.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getSupportFragmentManager().beginTransaction().replace(R.id.container, new MesOffresFragment()).commit();
            }
        });

        AjoutOffre.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getSupportFragmentManager().beginTransaction().replace(R.id.container, new AjoutOffreFragment()).commit();
            }
        });

        Notifications.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getSupportFragmentManager().beginTransaction().replace(R.id.container, new NotificationFragment()).commit();
            }
        });

        MonCompte.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getSupportFragmentManager().beginTransaction().replace(R.id.container, new ProfileFragment()).commit();
            }
        });

        LocationOffres.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getSupportFragmentManager().beginTransaction().replace(R.id.container, new LocationOffreFragment()).commit();
            }
        });

        Deconnexion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(),"deconnexion",Toast.LENGTH_LONG).show();
                FirebaseAuth.getInstance().signOut();
                LoginManager.getInstance().logOut();
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            }
        });

        //Afficher les postes facebook uniquement lorsqu'on se connecte avec facebook
        if(AccessToken.getCurrentAccessToken() == null){
            ViewGroup layout = (ViewGroup) PostesFacebook.getParent();
            if(layout != null)  layout.removeView(PostesFacebook);
        }


        PostesFacebook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getSupportFragmentManager().beginTransaction().replace(R.id.container, new PostesFacebookFragment()).commit();
            }
        });

        //mise à jours du nombre de notifications non lues
        DatabaseReference mRef = FirebaseDatabase.getInstance().getReference().child("users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("Notifications").child("nbr_attente");
        mRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.getValue() != null){
                    long nbr = (long) dataSnapshot.getValue();
                    int nbr_attente = new Long(nbr).intValue();
                    if(nbr_attente > 0){
                        nbrNotifs.setVisibility(View.VISIBLE);
                        nbrNotifs.setText(""+nbr_attente);
                    }
                    else nbrNotifs.setVisibility(View.INVISIBLE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
