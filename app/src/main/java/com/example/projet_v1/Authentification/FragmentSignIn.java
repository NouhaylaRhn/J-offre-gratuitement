package com.example.projet_v1.Authentification;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.projet_v1.ListOffre.ListDOffres;
import com.example.projet_v1.MainActivity2;
import com.example.projet_v1.Model.User;
import com.example.projet_v1.R;
import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginBehavior;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.facebook.login.widget.ToolTipPopup;
import com.github.ybq.android.spinkit.style.Circle;
import com.github.ybq.android.spinkit.style.DoubleBounce;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthEmailException;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserInfo;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Arrays;

import static java.lang.Boolean.TRUE;

public class FragmentSignIn extends Fragment {
    private View v;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private TextInputLayout mailLay;
    private TextInputLayout passLay;
    private EditText mail;
    private EditText pass;
    private TextView att;
    private TextView mdp_oublie;
    private Button connect;
    DoubleBounce mDoubleBounce;
    Circle mCircleDrawable;

    //facebook
    private LoginButton mFacebookLoginButton;
    private CallbackManager mFacebookCallbackManager;

    public FragmentSignIn() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAuth = FirebaseAuth.getInstance();
        mDoubleBounce = new DoubleBounce();
        mCircleDrawable = new Circle();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.fragment_sign_in,container,false);

        mailLay = v.findViewById(R.id.mL);
        passLay = v.findViewById(R.id.psL);

        mail = (EditText)v.findViewById(R.id.mail);
        pass = (EditText)v.findViewById(R.id.pass);

        connect = v.findViewById(R.id.connect);
        mdp_oublie = v.findViewById(R.id.mdp_ou);

        att =v.findViewById(R.id.att);
        att.setVisibility(View.INVISIBLE);

        mFacebookLoginButton = v.findViewById(R.id.log_fb);

        mail.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(!Patterns.EMAIL_ADDRESS.matcher(s).matches()){
                    mailLay.setErrorEnabled(true);
                    mailLay.setError(getString(R.string.err_mail));
                    connect.setTextColor(getContext().getColor(R.color.err));
                    connect.setEnabled(false);}
                else{
                    mailLay.setErrorEnabled(false);
                    mailLay.setError(null);
                    connect.setTextColor(getContext().getColor(R.color.colorPrimaryDark));
                    connect.setEnabled(true);}
            }
            @Override
            public void afterTextChanged(Editable s) { }
        });

        pass.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(s.length()<8){
                    passLay.setErrorEnabled(true);
                    passLay.setError(getString(R.string.err_pass));
                    connect.setTextColor(getContext().getColor(R.color.err));
                    connect.setEnabled(false);}
                else{
                    passLay.setErrorEnabled(false);
                    passLay.setError(null);
                    connect.setTextColor(getContext().getColor(R.color.colorPrimaryDark));
                    connect.setEnabled(true);}
            }
            @Override
            public void afterTextChanged(Editable s) { }
        });

        //EmailAndPasswordAuthentication
        connect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDoubleBounce.setBounds(0,0,100,100);
                mDoubleBounce.setColor(getResources().getColor(R.color.colorPrimaryDark));
                connect.setCompoundDrawables(null,null,mDoubleBounce,null);
                mDoubleBounce.start();
                LoginWithEmailAndPassword();
            }
        });

        //mot de passe oublié, envoyé un mail
        mdp_oublie.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ResetPassword();
            }
        });



        //Facebook Authentication
        mFacebookLoginButton.setPermissions(Arrays.asList("email","public_profile"));
        mFacebookCallbackManager = CallbackManager.Factory.create();
        mFacebookLoginButton.setFragment(this);
        mFacebookLoginButton.setLoginText("Se connecter avec Facebook");
        mFacebookLoginButton.setLoginBehavior(LoginBehavior.WEB_VIEW_ONLY);
        mFacebookLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickAtt();
            }
        });
        mFacebookLoginButton.registerCallback(mFacebookCallbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                //onClickAtt();
                handleFacebookAccessToken(loginResult.getAccessToken());
            }

            @Override
            public void onCancel() {
                onClickAttCancel();
            }

            @Override
            public void onError(FacebookException error) {

            }

        });

        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {

            }
        };

        return v;
    }

    public void onClickAtt(){
        att.setVisibility(View.VISIBLE);
        mFacebookLoginButton.setVisibility(View.GONE);
        mFacebookLoginButton.setEnabled(false);
        mCircleDrawable.setBounds(0,0,60,60);
        mCircleDrawable.setColor(getResources().getColor(R.color.blanc_c));
        att.setCompoundDrawables(getResources().getDrawable(R.drawable.facebook_icon),null,mCircleDrawable,null);
        att.setBackgroundColor(getResources().getColor(R.color.colorPrimaryDark));
        mCircleDrawable.start();
    }

    public void onClickAttCancel(){
        att.setVisibility(View.GONE);
        mFacebookLoginButton.setVisibility(View.VISIBLE);
        mFacebookLoginButton.setEnabled(true);
        mCircleDrawable.stop();
    }

    public void handleFacebookAccessToken(AccessToken token){
        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        mAuth.signInWithCredential(credential).addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    mDoubleBounce.stop();
                    //ici
                    DatabaseReference dr =FirebaseDatabase.getInstance().getReference().child("users").child(mAuth.getCurrentUser().getUid());
                    dr.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if(dataSnapshot.getValue()==null){
                                User us = new User();
                                FirebaseUser user = mAuth.getCurrentUser();
                                for(UserInfo profile: user.getProviderData()){
                                    if(FacebookAuthProvider.PROVIDER_ID.equals(profile.getProviderId())){
                                        //Toast.makeText(getActivity(),"******************************userId : "+profile.,Toast.LENGTH_LONG).show();
                                        us.setUser_nom(profile.getDisplayName());
                                        us.setUser_email(profile.getEmail());
                                        us.setUsr_date_naissance("");
                                        us.setUser_tel("");
                                        us.setUsr_img_url("https://graph.facebook.com/"+profile.getUid()+"/picture?type=normal");
                                        break;
                                    }
                                }
                                us.add_user_todatabase(mAuth.getCurrentUser().getUid());
                                //Toast.makeText(getActivity(),"*************** User null ***************",Toast.LENGTH_LONG).show();
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });

                    Intent intent =new Intent(getActivity(), MainActivity2.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                }
                else{
                    try {
                        throw task.getException();
                    } catch (FirebaseAuthInvalidCredentialsException ce){
                        getDialog(R.string.attention,R.string.login_warning_msg,R.drawable.ic_warning).show();
                    } catch (Exception e) {
                        getDialog(R.string.erreur,R.string.login_error_msg,R.drawable.ic_error).show();
                    }
                }
            }
        });
    }

    public MaterialAlertDialogBuilder getDialog(int title, int msg, int icon){
        return new MaterialAlertDialogBuilder(getContext()).setTitle(title).setMessage(msg).setIcon(getResources().getDrawable(icon))
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @RequiresApi(api = Build.VERSION_CODES.M)
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        init();
                    }
                }).setCancelable(false);
    }

    public void LoginWithEmailAndPassword(){
        if(mail.getText().toString().equals("") || pass.getText().toString().equals("")){
            getDialog(R.string.attention,R.string.mail_pass_empty,R.drawable.ic_attention).show();
        }
        else{
            mAuth.signInWithEmailAndPassword(mail.getText().toString(),pass.getText().toString())
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if(task.isSuccessful()){
                                if(mAuth.getCurrentUser().isEmailVerified()){
                                //if(TRUE){
                                    mDoubleBounce.stop();
                                    Intent intent =new Intent(getActivity(), MainActivity2.class);
                                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                    startActivity(intent);
                                }
                                else{
                                    mDoubleBounce.stop();
                                    mAuth.signOut();
                                    getDialog(R.string.attention,R.string.mail_not_verified,R.drawable.ic_warning).setNegativeButton("Renvoyer le mail de vérification", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            mAuth.getCurrentUser().sendEmailVerification();
                                            getDialog(R.string.parfait,R.string.mail_verif_env,R.drawable.ic_success).show();
                                            mAuth.signOut();
                                        }
                                    }).show();
                                }
                            }
                            else{
                                try {
                                    throw task.getException();
                                } catch (FirebaseAuthInvalidUserException fe){
                                    getDialog(R.string.attention,R.string.login_warning_msg,R.drawable.ic_warning).show();
                                } catch (Exception e) {
                                    getDialog(R.string.erreur,R.string.login_error_msg,R.drawable.ic_error).show();
                                }
                            }
                        }
                    });
        }
    }

    public void ResetPassword(){
        if(mail.getText().toString().equals((""))){
            getDialog(R.string.attention,R.string.reset_mail_att,R.drawable.ic_attention).show();
        }
        else{
            mAuth.sendPasswordResetEmail(mail.getText().toString())
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()){
                                getDialog(R.string.parfait,R.string.reset_suc,R.drawable.ic_success).show();
                            }
                            else getDialog(R.string.erreur,R.string.reset_err,R.drawable.ic_error).show();
                        }
                    });
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    public void init(){
        connect.setCompoundDrawables(null,null,null,null);
        mail.getText().clear();     mailLay.setErrorEnabled(false);     mailLay.setError(null);
        pass.getText().clear();     passLay.setErrorEnabled(false);     passLay.setError(null);
        connect.setTextColor(getContext().getColor(R.color.colorPrimaryDark));
        connect.setEnabled(true);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        mFacebookCallbackManager.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        mAuth.removeAuthStateListener(mAuthListener);
    }
}
