package com.example.projet_v1.Model;

public class Categorie_offre {

    private int categorie_id;
    private String categorie_label;

    public Categorie_offre(int categorie_id, String categorie_label) {
        this.categorie_id = categorie_id;
        this.categorie_label = categorie_label;
    }

    public int getCategorie_id() {
        return categorie_id;
    }

    public void setCategorie_id(int categorie_id) {
        this.categorie_id = categorie_id;
    }

    public String getCategorie_label() {
        return categorie_label;
    }

    public void setCategorie_label(String categorie_label) {
        this.categorie_label = categorie_label;
    }
}
