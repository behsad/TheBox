package com.example.behzad.thebox;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;


public class FavoriteActivity extends BaseActivity {

    RecyclerView recyclerView;
    SwipeRefreshLayout swipe;


    DataAdapter adapter;
    TextView not_found_text;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        FrameLayout content_frame = (FrameLayout) findViewById(R.id.content_frame);
        getLayoutInflater().inflate(R.layout.favorite,content_frame);

        navigationView.getMenu().findItem(R.id.mnu_favorite).setChecked(true);

//--------------------------Change The Toolbar title and font----------------------
        toolbar.setTitle("علاقه مندی ها");
        for (int i = 0; i<toolbar.getChildCount();i++){
            View view = toolbar.getChildAt(i);
            if(view instanceof TextView){
                TextView tv = (TextView) view;
                tv.setTypeface(myfont);
                tv.setTextSize(18);
            }
        }
//------------------------------------End------------------------------------------
        not_found_text = (TextView) findViewById(R.id.txt_favoriteLayout_not_found);
        recyclerView = (RecyclerView) findViewById(R.id.card_favoriteLayout_recycler_view);
        recyclerView.setHasFixedSize(true);

        RecyclerView.LayoutManager lm = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(lm);


        ArrayList<JSONObject> data_list = new ArrayList<JSONObject>();

        adapter = new DataAdapter(getApplicationContext(), data_list) {
            @Override
            public void load_more() {


            }
        };
        recyclerView.setAdapter(adapter);

        new get_bookmark_ad_list().execute();


        swipe = (SwipeRefreshLayout) findViewById(R.id.swipe_favoriteLayout_container);
        swipe.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {

                not_found_text.setVisibility(View.GONE);


                adapter.clear_list();

                new get_bookmark_ad_list().execute();

                swipe.setRefreshing(false);
            }
        });




    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.help_menu, menu);
        return true;
    }



    public class get_bookmark_ad_list extends AsyncTask<Void,Void,String>
    {
        ProgressDialog pd = new ProgressDialog(FavoriteActivity.this);

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            pd.setMessage("در حال دریافت اطلاعات");
            pd.show();

        }

        @Override
        protected String doInBackground(Void... voids) {

            ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();

            JSONObject get_ad_list = new JSONObject();
            try {
                get_ad_list.put("command","get_bookmark_ad_list");
                get_ad_list.put("user_id",settings.getInt("user_id",0));

            } catch (JSONException e) {
                e.printStackTrace();
            }


            nameValuePairs.add(new BasicNameValuePair("myjson",get_ad_list.toString()));

            try {

                HttpClient httpClient = new DefaultHttpClient();
                HttpPost httpPost = new HttpPost("http://192.168.43.38/thebox/command.php");
                httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs, HTTP.UTF_8));

                HttpResponse httpResponse = httpClient.execute(httpPost);
                String response = EntityUtils.toString(httpResponse.getEntity());

                if (response.startsWith("<thebox>") && response.endsWith("</thebox>")) {//response is valid

                    response = response.replace("<thebox>", "").replace("</thebox>", "");

                    final String finalResponse = response;
                    runOnUiThread(new Runnable() {

                        @Override
                        public void run() {

                            try {
                                JSONArray ad_list = new JSONArray(finalResponse);

                                if (ad_list.length() == 0){
                                    not_found_text.setVisibility(View.VISIBLE);
                                }


                                adapter.insert(adapter.getItemCount(),ad_list);

                                //Toast.makeText(getBaseContext(),String.valueOf(ad_list.length()), Toast.LENGTH_SHORT).show();

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    });


                }else {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getBaseContext(), "خطا در دریافت اطلاعات", Toast.LENGTH_SHORT).show();
                        }
                    });

                }

            }catch (Exception e){
                e.printStackTrace();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getBaseContext(), "خطا در دریافت اطلاعات", Toast.LENGTH_SHORT).show();

                    }
                });
            }

            return null;
        }
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            pd.hide();
            pd.dismiss();

        }
    }
}
