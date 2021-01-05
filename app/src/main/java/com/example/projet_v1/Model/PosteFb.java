package com.example.projet_v1.Model;

import androidx.annotation.Nullable;

public class PosteFb {
    public String message;
    public String updated_time;
    public String id_poste;

    public PosteFb(){
    }
    public PosteFb(String message, String updated_time, String id_poste) {
        this.message = message;
        this.updated_time = updated_time;
        this.id_poste = id_poste;
    }


    public String getMessage() {
        return message;
    }

    public String getUpdated_time() {
        return updated_time;
    }

    public String getId_poste() {
        return id_poste;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setUpdated_time(String updated_time) {
        this.updated_time = updated_time;
    }

    public void setId_poste(String id_poste) {
        this.id_poste = id_poste;
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        PosteFb a =(PosteFb) obj;
        return id_poste.matches(a.getId_poste());
    }

    @Override
    public String toString() {
        return "PosteFb{" +
                "message='" + message + '\'' +
                ", updated_time='" + updated_time + '\'' +
                ", id_poste='" + id_poste + '\'' +
                '}';
    }
}
