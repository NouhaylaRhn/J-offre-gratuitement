package com.example.projet_v1.Model;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Date;

public class User {

    private String user_id;
    private String user_nom;
    private String user_prenom;
    private String user_email;
    private String user_password;
    private String user_tel;
    private String user_sex;
    private String usr_date_naissance;
    private String usr_img_url;

    public static DatabaseReference databaseUsers = FirebaseDatabase.getInstance().getReference("users");

    public User(){

    }


    public User(String User_id, String user_nom, String user_prenom, String user_email, String user_password, String user_tel, String user_sex, String usr_date_naissance, String imageURL, String usr_img_url) {
        user_id = User_id;
        this.user_id = user_id;
        this.user_nom = user_nom;
        this.user_prenom = user_prenom;
        this.user_email = user_email;
        this.user_password = user_password;
        this.user_tel = user_tel;
        this.user_sex = user_sex;
        this.usr_date_naissance = usr_date_naissance;
        this.usr_img_url = usr_img_url;
    }

    public String getUsr_img_url() {
        return usr_img_url;
    }

    public void setUsr_img_url(String usr_img_url) {
        this.usr_img_url = usr_img_url;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getUser_nom() {
        return user_nom;
    }

    public void setUser_nom(String user_nom) {
        this.user_nom = user_nom;
    }

    public String getUser_prenom() {
        return user_prenom;
    }

    public void setUser_prenom(String user_prenom) {
        this.user_prenom = user_prenom;
    }

    public String getUser_email() {
        return user_email;
    }

    public void setUser_email(String user_email) {
        this.user_email = user_email;
    }

    public String getUser_password() {
        return user_password;
    }

    public void setUser_password(String user_password) {
        this.user_password = user_password;
    }

    public String getUser_tel() {
        return user_tel;
    }

    public void setUser_tel(String user_tel) {
        this.user_tel = user_tel;
    }

    public String getUser_sex() {
        return user_sex;
    }

    public void setUser_sex(String user_sex) {
        this.user_sex = user_sex;
    }


    public String getUsr_date_naissance() {
        return usr_date_naissance;
    }

    public void setUsr_date_naissance(String usr_date_naissance) {
        this.usr_date_naissance = usr_date_naissance;
    }



    public void add_user_todatabase(String id){
        //String id = databaseUsers.push().getKey();
        this.setUser_id(id);
        databaseUsers.child(id).setValue(this);
        databaseUsers.child(id).child("Notifications").child("nbr_total").setValue(0);
        databaseUsers.child(id).child("Notifications").child("nbr_attente").setValue(0);
        databaseUsers.child(id).child("Notifications").child("offre").setValue(0);
    }

    @Override
    public String toString() {
        return "User{" +
                "user_id='" + user_id + '\'' +
                ", user_nom='" + user_nom + '\'' +
                ", usr_img_url='" + usr_img_url + '\'' +
                '}';
    }
}
