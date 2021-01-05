package com.example.projet_v1;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.os.Bundle;

import com.example.projet_v1.Authentification.FragmentSignIn;
import com.example.projet_v1.Authentification.FragmentSignUp;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity {
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private ViewPagerAdapter viewPagerAdapter;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAuth = FirebaseAuth.getInstance();

        if(mAuth.getCurrentUser() != null){
            Intent intent =new Intent(MainActivity.this, MainActivity2.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        }

        tabLayout = (TabLayout) findViewById(R.id.tabloyout_id);
        viewPager = (ViewPager) findViewById(R.id.viewpager_id);
        viewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager());

        //add fragment here
        viewPagerAdapter.addFragment(new FragmentSignIn(),"Sign IN");
        viewPagerAdapter.addFragment(new FragmentSignUp(),"Sign UP");

        viewPager.setAdapter(viewPagerAdapter);
        tabLayout.setupWithViewPager(viewPager);

        //charger les icons dans les titres des fragements
        tabLayout.getTabAt(0).setIcon(R.drawable.ic_signin);
        tabLayout.getTabAt(1).setIcon(R.drawable.ic_signup);
    }
}
