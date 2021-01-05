package com.example.projet_v1.Adapters;

import android.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.projet_v1.Model.Commentaire;
import com.example.projet_v1.R;

import java.util.ArrayList;
import java.util.Arrays;


public class Adapter1 extends RecyclerView.Adapter<Adapter1.MyViewHolder> {



    private  final    ArrayList Commentaires = new ArrayList<>(Arrays.asList(
            //new Commentaire(R.drawable.jj ,"Nouhayla", "Je suis intéréssée" )
    ));



    @Override
    public int getItemCount() {

        return Commentaires.size();
    }

    @Override
    //crée la vu d'une cellule
    // parent pour créer la vu et int pour spécifier le type de la cellule si on a plusieurrs type (orgnaisation differts)
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        //pour créer un laouyt depuis un XML
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.item, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    //appliquer ne donnée à une vue
    public void onBindViewHolder(MyViewHolder holder, int position) {

        Commentaire Etab= (Commentaire) Commentaires.get(position);

        //System.out.println("Vname =" + Etab.getName());
        System.out.println("position=" + position);



    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        private final TextView label;
        private final TextView name;
        private final ImageView img;

        //  private Pair<String, String> currentPair;
        private Commentaire currentEtab;


        public MyViewHolder(final View itemView) {
            super(itemView);

            label = itemView.findViewById(R.id.label);
            name = itemView.findViewById(R.id.showcomment);

            img=  itemView.findViewById(R.id.img);



            /*itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    new AlertDialog.Builder(itemView.getContext())
                            .setTitle(currentEtab.getName())
                            .show();
                }
            });*/
        }

        public void display(Commentaire Etab) {
            currentEtab = Etab;
            /*name.setText(Etab.getName());
            label.setText(Etab.getName());
            img.setImageResource(Etab.getImg());*/

        }
    }






}
