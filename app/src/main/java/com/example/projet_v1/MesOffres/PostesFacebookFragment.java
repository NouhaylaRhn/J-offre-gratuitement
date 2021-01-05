package com.example.projet_v1.MesOffres;

import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.projet_v1.Adapters.AdapterPFacebook;
import com.example.projet_v1.Listeners.EndlessRecyclerOnScrollListener;
import com.example.projet_v1.ListOffre.NavigationIconClickListener;
import com.example.projet_v1.Model.PosteFb;
import com.example.projet_v1.R;
import com.facebook.AccessToken;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.HttpMethod;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class PostesFacebookFragment extends Fragment {

    RecyclerView listPostesFacebook;
    AdapterPFacebook mAdaper;

    final List<PosteFb> postes=new ArrayList<>();
    final String[] afterString = {""};  // will contain the next page cursor
    final String[] beforeString = {"n"};  // will contain the next page cursor
    final Boolean[] noData = {false};   // stop when there is no after cursor
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_postesfacebook, container, false);

        listPostesFacebook= view.findViewById(R.id.listPostesFacebook);

        // Set up the toolbar
        setUpToolbar(view);

        LinearLayoutManager mLayoutManager = new LinearLayoutManager(getActivity());

        listPostesFacebook.setLayoutManager(mLayoutManager);
        mAdaper = new AdapterPFacebook(getActivity() ,postes);
        listPostesFacebook.setAdapter(mAdaper);

        loadPostes();

        listPostesFacebook.setOnScrollListener(new EndlessRecyclerOnScrollListener(mLayoutManager) {
            @Override
            public void onLoadMore() {
                        loadPostes();
                        //Log.e("load more","------------------------------");
            }
        });

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            view.findViewById(R.id.postesfacebook).setBackgroundResource(R.drawable.fragment_round_corners);
        }

        return view;
    }

    public void loadPostes(){
        if(beforeString[0].hashCode()==afterString[0].hashCode()) noData[0]=true;
        if(!noData[0]==true){
            Bundle params = new Bundle();
            if(!afterString[0].equals("")) params.putString("previous", afterString[0]);
            else
            params.putString("limit", "5");
            new GraphRequest(
                    AccessToken.getCurrentAccessToken(), "/232872551135487/feed", params, HttpMethod.GET,
                    new GraphRequest.Callback() {
                        public boolean PosteExist(PosteFb p){
                            for(PosteFb pf : postes){
                                if(pf.getId_poste().equals(p.getId_poste())) return true;
                            }
                            return false;
                        }
                        public void onCompleted(GraphResponse response) {
                            JSONObject jobj = null;
                            try {
                                jobj = new JSONObject(response.getRawResponse());
                                JSONArray data = jobj.getJSONArray("data");
                                for (int i = 0; i < data.length(); i++) {
                                    if(!data.getJSONObject(i).isNull("message")){
                                        PosteFb poste = new PosteFb();
                                        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
                                        Date date = dateFormat.parse(data.getJSONObject(i).getString("updated_time"));
                                        dateFormat = new SimpleDateFormat("dd-MM-yyyy");
                                        String formatedDate = dateFormat.format(date);
                                        poste.setMessage(data.getJSONObject(i).getString("message"));
                                        poste.setUpdated_time(formatedDate);
                                        poste.setId_poste(data.getJSONObject(i).getString("id"));
                                        if(!PosteExist(poste)) postes.add(poste);
                                    }
                                }
                                mAdaper.notifyDataSetChanged();

                                if(!jobj.isNull("paging")) {
                                    JSONObject paging = jobj.getJSONObject("paging");
                                        if (!paging.isNull("next")){
                                            beforeString[0] = afterString[0];
                                            afterString[0] = paging.getString("next");
                                        }

                                        else
                                            noData[0] = true;
                                    }


                                else
                                    noData[0] = true;
                            } catch (JSONException | ParseException e) {
                                e.printStackTrace();
                            }
                        }
                    }
            ).executeAsync();
        }

    }

    private void setUpToolbar(View view) {
        Toolbar toolbar = view.findViewById(R.id.app_bar);
        AppCompatActivity activity = (AppCompatActivity) getActivity();
        if (activity != null) {
            activity.setSupportActionBar(toolbar);
        }
        toolbar.setNavigationOnClickListener(new NavigationIconClickListener(
                getContext(),
                view.findViewById(R.id.postesfacebook),
                new AccelerateDecelerateInterpolator(),
                getContext().getResources().getDrawable(R.drawable.project_menu),
                getContext().getResources().getDrawable(R.drawable.icon_close_menu)));
    }
}
