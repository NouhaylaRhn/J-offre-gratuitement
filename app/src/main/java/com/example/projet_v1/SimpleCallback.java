package com.example.projet_v1;

/*
** Cette On utilise cette des interface dans le cas d'extraction des données depuis l'inner-class vers l'outer-class
** Utilisée surtout dans les listeners de FirebaseDatabase
 */
public interface SimpleCallback<T> {
    void callback(T data);
}
