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

import com.example.projet_v1.Model.User;
import com.example.projet_v1.R;
import com.github.ybq.android.spinkit.style.Wave;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;

public class FragmentSignUp extends Fragment {
    private View v;

    private FirebaseAuth mAuth;

    private TextInputLayout nomLay;
    //private TextInputLayout prenomLay;
    private TextInputLayout mailLay;
    private TextInputLayout passLay;
    private EditText nom;
    //private EditText prenom;
    private EditText mail;
    private EditText pass;
    private Button create;
    private User user;

    private Wave mWaveDrawable;

    public FragmentSignUp() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        user = new User();
        mWaveDrawable = new Wave();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.fragment_sign_up,container,false);

        mAuth = FirebaseAuth.getInstance();

        nomLay = v.findViewById(R.id.nL);
        //prenomLay = v.findViewById(R.id.pL);
        mailLay = v.findViewById(R.id.mL);
        passLay = v.findViewById(R.id.psL);

        nom = (EditText)v.findViewById(R.id.nom);
        //prenom = (EditText)v.findViewById(R.id.prenom);
        mail = (EditText)v.findViewById(R.id.mail);
        pass = (EditText)v.findViewById(R.id.pass);
        create = (Button)v.findViewById(R.id.create);

        nom.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(s.length()<3){
                    nomLay.setErrorEnabled(true);
                    nomLay.setError(getString(R.string.err_np));
                    create.setTextColor(getContext().getColor(R.color.err));
                    create.setEnabled(false);}
                else{
                    nomLay.setErrorEnabled(false);
                    nomLay.setError(null);
                    create.setTextColor(getContext().getColor(R.color.colorPrimaryDark));
                    create.setEnabled(true);}
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
                    create.setTextColor(getContext().getColor(R.color.err));
                    create.setEnabled(false);}
                else{
                    passLay.setErrorEnabled(false);
                    passLay.setError(null);
                    create.setTextColor(getContext().getColor(R.color.colorPrimaryDark));
                    create.setEnabled(true);}
            }
            @Override
            public void afterTextChanged(Editable s) { }
        });

        mail.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(!Patterns.EMAIL_ADDRESS.matcher(s).matches()){
                    mailLay.setErrorEnabled(true);
                    mailLay.setError(getString(R.string.err_mail));
                    create.setTextColor(getContext().getColor(R.color.err));
                    create.setEnabled(false);}
                else{
                    mailLay.setErrorEnabled(false);
                    mailLay.setError(null);
                    create.setTextColor(getContext().getColor(R.color.colorPrimaryDark));
                    create.setEnabled(true);}
            }
            @Override
            public void afterTextChanged(Editable s) { }
        });

        create.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mWaveDrawable.setBounds(0,0,100,100);
                mWaveDrawable.setColor(getResources().getColor(R.color.colorPrimaryDark));
                create.setCompoundDrawables(null,null,mWaveDrawable,null);
                mWaveDrawable.start();
                user.setUser_nom(nom.getText().toString());
                user.setUsr_date_naissance("");
                user.setUser_email(mail.getText().toString());
                user.setUser_password(pass.getText().toString());
                createUser();
            }
        });
        return v;
    }

    public void createUser(){
        if(!user.getUser_nom().equals("") && !user.getUser_email().equals("") && !user.getUser_password().equals("")){
            mAuth.createUserWithEmailAndPassword(user.getUser_email(), user.getUser_password())
                .addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            user.setUser_password("");
                            user.setUser_tel("");
                            user.setUsr_date_naissance("");
                            user.setUsr_img_url("");
                            user.add_user_todatabase(mAuth.getCurrentUser().getUid());
                            mAuth.getCurrentUser().sendEmailVerification();
                            getDialog(R.string.parfait,R.string.register_success_msg,R.drawable.ic_success).setNegativeButton("OK", new DialogInterface.OnClickListener() {
                                @RequiresApi(api = Build.VERSION_CODES.M)
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    mAuth.signOut();
                                    init();
                                }
                            }).setCancelable(false).show();
                        }
                        else{
                            try {
                                throw task.getException();
                            } catch (FirebaseAuthUserCollisionException fe){
                                getDialog(R.string.attention,R.string.register_warning_msg,R.drawable.ic_warning).setNegativeButton("OK", new DialogInterface.OnClickListener() {
                                    @RequiresApi(api = Build.VERSION_CODES.M)
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        init();
                                    }
                                }).setOnCancelListener(new DialogInterface.OnCancelListener() {
                                    @RequiresApi(api = Build.VERSION_CODES.M)
                                    @Override
                                    public void onCancel(DialogInterface dialog) {
                                        init();
                                    }
                                }).show();
                            } catch (Exception e) {
                                getDialog(R.string.erreur,R.string.register_error_msg,R.drawable.ic_error).setNegativeButton("OK", new DialogInterface.OnClickListener() {
                                    @RequiresApi(api = Build.VERSION_CODES.M)
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        init();
                                    }
                                }).setOnCancelListener(new DialogInterface.OnCancelListener() {
                                    @RequiresApi(api = Build.VERSION_CODES.M)
                                    @Override
                                    public void onCancel(DialogInterface dialog) {
                                        init();
                                    }
                                }).show();
                            }
                        }
                    }
                });
        }
        else{
            getDialog(R.string.attention,R.string.register_empty_form,R.drawable.ic_attention).setNegativeButton("OK", new DialogInterface.OnClickListener() {
                @RequiresApi(api = Build.VERSION_CODES.M)
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    init();
                }
            }).setOnCancelListener(new DialogInterface.OnCancelListener() {
                @RequiresApi(api = Build.VERSION_CODES.M)
                @Override
                public void onCancel(DialogInterface dialog) {
                    init();
                }
            }).show();
        }

        //mWaveDrawable.stop();
    }

    public MaterialAlertDialogBuilder getDialog(int title,int msg,int icon){
        return new MaterialAlertDialogBuilder(getContext()).setTitle(title).setMessage(msg).setIcon(getResources().getDrawable(icon));
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    public void init(){
        create.setCompoundDrawables(null,null,null,null);
        nom.getText().clear();      nomLay.setErrorEnabled(false);      nomLay.setError(null);
        //prenom.getText().clear();   prenomLay.setErrorEnabled(false);   prenomLay.setError(null);
        mail.getText().clear();     mailLay.setErrorEnabled(false);     mailLay.setError(null);
        pass.getText().clear();     passLay.setErrorEnabled(false);     passLay.setError(null);
        create.setTextColor(getContext().getColor(R.color.colorPrimaryDark));
        create.setEnabled(true);
    }

    public void logOut(){
        mAuth.signOut();
        /*Intent intent = new Intent(ListOffres.this,MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();*/
    }
}
