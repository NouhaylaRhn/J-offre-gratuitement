package com.example.projet_v1.Conversation;

import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.projet_v1.Adapters.UserAdapter;
import com.example.projet_v1.ListOffre.NavigationIconClickListener;
import com.example.projet_v1.Model.User;
import com.example.projet_v1.R;
import com.example.projet_v1.SimpleCallback;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class ConversationFragment extends Fragment {
    private static final String TAG = "ConversationFragment";
    private RecyclerView recyclerView;
    private UserAdapter userAdapter;
    private List<User> mUsers;
    private String imageurl;
    EditText search_users;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_conversation, container, false);
        recyclerView = view.findViewById(R.id.recycler_view_conv);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));


        // Set up the toolbar
        setUpToolbar(view);
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            view.findViewById(R.id.conversation).setBackgroundResource(R.drawable.fragment_round_corners);
        }

        getAllUsers(new SimpleCallback<List<User>>() {
            @Override
            public void callback(List<User> data) {
                if (!data.isEmpty()) {
                    userAdapter = new UserAdapter(getContext(), data);
                    recyclerView.setAdapter(userAdapter);
                    Log.e(TAG, data.toString());
                }
            }
        });







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
                view.findViewById(R.id.conversation),
                new AccelerateDecelerateInterpolator(),
                getContext().getResources().getDrawable(R.drawable.project_menu),
                getContext().getResources().getDrawable(R.drawable.icon_close_menu)));

    }


    public void getAllUsers(@NonNull final SimpleCallback<List<User>> finishedCallback) {
        final FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("users");

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                List<User> users = new ArrayList<>();
                users.clear();
                try {
                    for (DataSnapshot ds : dataSnapshot.getChildren()) {
                        User user = new User();
                        String nom = (String) ds.child("user_nom").getValue();
                        String pic = (String) ds.child("usr_img_url").getValue();
                        String id = (String) ds.child("user_id").getValue();
                        user.setUsr_img_url(pic);
                        user.setUser_nom(nom);
                        user.setUser_id(id);
                        assert firebaseUser != null;
                        if (!user.getUser_id().equals(firebaseUser.getUid())) {
                            users.add(user);
                            Log.e("ReadUser1", "*******************" + user.getUser_nom());
                        }
                    }
                    finishedCallback.callback(users);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
