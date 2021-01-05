package com.example.projet_v1.Profile;

import android.app.Dialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;

import com.bumptech.glide.Glide;
import com.example.projet_v1.Model.User;
import com.example.projet_v1.R;
import com.example.projet_v1.SimpleCallback;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;

import de.hdodenhof.circleimageview.CircleImageView;

import static android.app.Activity.RESULT_OK;

public class ProfileDialog extends DialogFragment {
    public static final String TAG = "profile_dialog";

    EditText nom;
    EditText mail;
    EditText tel;
    EditText dtn;
    TextView save;

    TextInputLayout nL;
    TextInputLayout tL;
    TextInputLayout dL;

    StorageTask mUploadTask;
    CircleImageView profile_pic;
    MaterialButton choose_pic;

    private Toolbar toolbar;

    User userUpdated = new User();

    private static final int PICK_IMAGE_REQUEST = 1;
    Uri mImageUri;

    public static ProfileDialog display(FragmentManager fragmentManager) {
        ProfileDialog profileDialog = new ProfileDialog();
        profileDialog.show(fragmentManager, TAG);
        return profileDialog;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NORMAL, R.style.AppTheme_FullScreenDialog);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.profile_dialog, container, false);

        toolbar = (Toolbar) view.findViewById(R.id.toolbar);

        profile_pic = view.findViewById(R.id.profile_pic);
        choose_pic = view.findViewById(R.id.choose_pic);

        nom = view.findViewById(R.id.nom);
        mail = view.findViewById(R.id.mail);
        tel = view.findViewById(R.id.tel);
        dtn = view.findViewById(R.id.dtn);
        save = view.findViewById(R.id.save);

        nL = view.findViewById(R.id.nL);
        tL = view.findViewById(R.id.tL);
        dL = view.findViewById(R.id.dL);

        tel.setText("+212");

        ProfileFragment.getInfoCurrentUser(new SimpleCallback<User>() {
            @Override
            public void callback(User user) {
                if(user!=null){
                    if(!user.getUsr_img_url().equals("") && getActivity()!=null){
                        Glide.with(getActivity()).load(user.getUsr_img_url()).into(profile_pic);
                        }
                    if(!user.getUser_nom().equals((""))){
                        nom.setText(user.getUser_nom());
                        }
                    if(!user.getUser_email().equals((""))){
                        mail.setText(user.getUser_email());
                        }
                    if(!user.getUser_tel().equals((""))){
                        tel.setText(user.getUser_tel());
                        }
                    if(!user.getUsr_date_naissance().equals((""))){
                        dtn.setText(user.getUsr_date_naissance());
                        }
                }
            }
        });

        nom.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(getContext()!=null){
                    if(s.length()<3){
                        nL.setErrorEnabled(true);
                        nL.setError(getString(R.string.err_np));
                        save.setTextColor(getContext().getColor(R.color.err));
                        save.setEnabled(false);}
                    else{
                        nL.setErrorEnabled(false);
                        nL.setError(null);
                        save.setTextColor(getContext().getColor(R.color.blanc_c));
                        save.setEnabled(true);}
                }

            }
            @Override
            public void afterTextChanged(Editable s) { }
        });

        tel.addTextChangedListener(new TextWatcher() {
            String pattern = "\\+212\\d{9}";

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(s.toString().equals("")) {
                    tel.setText("+212");
                    tel.setSelection(4);
                }
                if(getContext()!=null){
                    if(!s.toString().matches(pattern)){
                        tL.setErrorEnabled(true);
                        tL.setError(getString(R.string.err_tel));
                        save.setTextColor(getContext().getColor(R.color.err));
                        save.setEnabled(false);}
                    else{
                        tL.setErrorEnabled(false);
                        tL.setError(null);
                        save.setTextColor(getContext().getColor(R.color.blanc_c));
                        save.setEnabled(true);}
                }

            }
            @Override
            public void afterTextChanged(Editable s) { }
        });

        dtn.addTextChangedListener(new TextWatcher() {
            String pattern = "(?:\\d{2}-){2}\\d{4}";

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(getContext()!=null){
                    if(!s.toString().matches(pattern)){
                        dL.setErrorEnabled(true);
                        dL.setError(getString(R.string.err_dtn));
                        save.setTextColor(getContext().getColor(R.color.err));
                        save.setEnabled(false);}
                    else{
                        dL.setErrorEnabled(false);
                        dL.setError(null);
                        save.setTextColor(getContext().getColor(R.color.blanc_c));
                        save.setEnabled(true);}
                }

            }
            @Override
            public void afterTextChanged(Editable s) { }
        });

        choose_pic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openFileChooser();
            }
        });

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                userUpdated.setUser_nom(nom.getText().toString());
                userUpdated.setUser_tel(tel.getText().toString());
                userUpdated.setUsr_date_naissance(dtn.getText().toString());
                uploadimage();
                updateInfoUser();
                getDialog(R.string.parfait,R.string.profile_updated,R.drawable.ic_success).setNegativeButton("OK", null).setCancelable(false).show();
                ProfileDialog.this.dismiss();

            }
        });

        return view;
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ProfileDialog.this.dismiss();
            }
        });
        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                ProfileDialog.this.dismiss();
                return true;
            }
        });
    }

    public MaterialAlertDialogBuilder getDialog(int title, int msg, int icon){
        return new MaterialAlertDialogBuilder(getContext()).setTitle(title).setMessage(msg).setIcon(getResources().getDrawable(icon));
    }

    private void openFileChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    private String getFileExtension(Uri uri) {
        Context applicationContext = getContext();

        ContentResolver cR = null;
        if (applicationContext != null) {
            cR = applicationContext.getContentResolver();
        }
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cR.getType(uri));
    }

    private void uploadimage() {
        StorageReference mStorageRef;

        mStorageRef = FirebaseStorage.getInstance().getReference().child("profile_pictures/");
        if (mImageUri != null) {
            StorageReference fileReference = mStorageRef.child(FirebaseAuth.getInstance().getCurrentUser().getUid() + "." + getFileExtension(mImageUri));
            mUploadTask = fileReference.putFile(mImageUri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            Task<Uri> task = taskSnapshot.getMetadata().getReference().getDownloadUrl();
                            task.addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    String photoLink = uri.toString();
                                    if(FirebaseAuth.getInstance().getCurrentUser() != null){
                                        DatabaseReference dr = FirebaseDatabase.getInstance().getReference().child("users").child(FirebaseAuth.getInstance().getCurrentUser().getUid());
                                        dr.child("usr_img_url").setValue(photoLink);
                                    }
                                }
                            });

                        }


                    });

        }
    }

    public void updateInfoUser(){
        if(FirebaseAuth.getInstance().getCurrentUser() != null){
            DatabaseReference dr = FirebaseDatabase.getInstance().getReference().child("users").child(FirebaseAuth.getInstance().getCurrentUser().getUid());
            dr.child("user_nom").setValue(userUpdated.getUser_nom());
            dr.child("user_tel").setValue(userUpdated.getUser_tel());
            dr.child("usr_date_naissance").setValue(userUpdated.getUsr_date_naissance());
            //dr.child("usr_img_url").setValue(userUpdated.getUsr_img_url());
        }

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK
                && data != null && data.getData() != null) {
            mImageUri = data.getData();
            profile_pic.setImageURI(mImageUri);
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        Dialog dialog = getDialog();
        if (dialog != null) {
            int width = ViewGroup.LayoutParams.MATCH_PARENT;
            int height = ViewGroup.LayoutParams.MATCH_PARENT;
            dialog.getWindow().setLayout(width, height);
            dialog.getWindow().setWindowAnimations(R.style.AppTheme_Slide);

        }
    }


}
