package com.example.projet_v1.ListOffre;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;

import android.widget.Button;
import android.widget.Toast;

import com.example.projet_v1.ChatBot.ChatBotDialog;
import com.example.projet_v1.MainActivity;
import com.example.projet_v1.Model.PosteFb;
import com.example.projet_v1.R;
import com.facebook.AccessToken;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.HttpMethod;
import com.facebook.login.LoginManager;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import static ai.api.util.VoiceActivityDetector.TAG;

public class ListDOffres extends AppCompatActivity implements NavigationHost {
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




       // this.navigateTo(new OffreGridFragment(), false); // Navigate to the next Fragment

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
        Button MonCompte = (Button) findViewById(R.id.idmoncompte);
        Button Deconnexion = (Button)findViewById(R.id.idsedeconnecter);
        Button PostesFacebook = (Button) findViewById(R.id.idpostesfacebook);


        MonCompte.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Toast.makeText(getApplicationContext(),"Mon Compte",Toast.LENGTH_LONG).show();
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
    }
}
