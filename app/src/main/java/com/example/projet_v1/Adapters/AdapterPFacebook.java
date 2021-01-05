package com.example.projet_v1.Adapters;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.projet_v1.MainActivity;
import com.example.projet_v1.Model.Chat;
import com.example.projet_v1.Model.PosteFb;
import com.example.projet_v1.R;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class AdapterPFacebook extends RecyclerView.Adapter<AdapterPFacebook.MyViewHolder> {

    private Context context;
    private URL url;
    public Context getContext() {
        return context;
    }

    private final Activity act;
    //private ArrayList<String> messagelist=new ArrayList<>();
    //private ArrayList<String> updated_timelist=new ArrayList<>();
    //private ArrayList<String> id_postelist=new ArrayList<>();
    private List<PosteFb> posteFbs=new ArrayList<>();




    public AdapterPFacebook(Activity act,List<PosteFb> posteFbs ) {
        this.act=act;
        this.posteFbs=posteFbs;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{
        public TextView message;
        public  TextView updated_time;
        public  TextView id_poste;
        private PosteFb currentPoste;



        public MyViewHolder(final View itemView){
            super(itemView);
            message= itemView.findViewById(R.id.idmessage);
            updated_time = itemView.findViewById(R.id.idupdated_time);
            id_poste = itemView.findViewById(R.id.idposte);


            context=itemView.getContext();
        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    /*
                    		String path = "aaaaa_bbbbb";
		String segments[] = path.split("_");
		String document = segments[segments.length - 1];
		System.out.println(document);
                     */
                    String segments[] = currentPoste.getId_poste().split("_");
                    String id_poste = segments[segments.length - 1];
                    //url = new URL("https://mobile.facebook.com/groups/" + currentPoste.getId_poste());
                    url = new URL("https://mobile.facebook.com/groups/232872551135487?view=permalink&id=" + id_poste);
                    Intent browse = new Intent( Intent.ACTION_VIEW , Uri.parse(String.valueOf(url)) );
                    context.startActivity(browse);

                } catch (MalformedURLException e) {
                    e.printStackTrace();
                }
            }
        });
        }

        public void display(PosteFb posteFb) {
            currentPoste = posteFb;
            message.setText(posteFb.getMessage());
            updated_time.setText(posteFb.getUpdated_time());
            id_poste.setText(posteFb.getId_poste());
        }
    }



    @NonNull
    @Override
    public AdapterPFacebook.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //pour créer un laouyt depuis un XML
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.item_post_facebook, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AdapterPFacebook.MyViewHolder holder, int position) {
        PosteFb posteFb= (PosteFb) posteFbs.get(position);
        //String message= (String) messagelist.get(position);
        //String updated_time= (String) updated_timelist.get(position);
        //String id_poste= (String) id_postelist.get(position);

        //holder.id_poste.setText(id_postelist.get(position));
        //holder.updated_time.setText(updated_timelist.get(position));
        //holder.message.setText(messagelist.get(position));
        holder.display(posteFb);
    }


    @Override
    public int getItemCount() {
        return posteFbs.size();
    }

    /*
    public View getView(int position, View view, ViewGroup parent){

    }
     */

}
