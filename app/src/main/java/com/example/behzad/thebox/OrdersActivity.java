package com.example.behzad.thebox;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.support.v7.widget.SearchView;

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
import java.util.Arrays;




public class OrdersActivity extends BaseActivity {

    FloatingActionButton fb;
    RecyclerView recyclerView;
    SwipeRefreshLayout swipe;

    TextView location_filter;
    TextView car_type_filter;

    TextView not_found_text;

    DataAdapter adapter;
    int last_ad_id = 0;

    String search_key = "";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        FrameLayout content_frame = (FrameLayout) findViewById(R.id.content_frame);
        getLayoutInflater().inflate(R.layout.orders,content_frame);

        navigationView.getMenu().findItem(R.id.mnu_orders).setChecked(true);

        fb = (FloatingActionButton)findViewById(R.id.btn_floatBtn);
        fb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //check user login
                if (settings.getInt("user_id",0)!=0){

                    Intent intent = new Intent(getApplicationContext(),NewOrderActivity.class);
                    startActivity(intent);
                }else {
                    Toast.makeText(getBaseContext(),"برای سفارش دادن باید عضو شوید",Toast.LENGTH_LONG).show();
                    Intent intent = new Intent(getApplicationContext(),LoginActivity.class);
                    startActivity(intent);
                }
            }
        });

        not_found_text = (TextView) findViewById(R.id.txt_not_found);

        recyclerView = (RecyclerView) findViewById(R.id.card_recycler_view);
        recyclerView.setHasFixedSize(true);

        RecyclerView.LayoutManager lm = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(lm);


        ArrayList<JSONObject> data_list = new ArrayList<JSONObject>();

        adapter = new DataAdapter(getApplicationContext(), data_list) {
            @Override
            public void load_more() {
                if (last_ad_id != -1) {
                    new get_ad_list().execute();
                }

            }
        };
        recyclerView.setAdapter(adapter);

        new get_ad_list().execute();




//------------------refresh the list with SwipeRefreshLayout----------------------
        swipe = (SwipeRefreshLayout) findViewById(R.id.swipe_container);
        swipe.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {

                not_found_text.setVisibility(View.GONE);

                last_ad_id = 0;

                adapter.clear_list();

                new get_ad_list().execute();

                swipe.setRefreshing(false);
            }
        });


        location_filter = (TextView) findViewById(R.id.txt_location_filter);
        car_type_filter = (TextView) findViewById(R.id.txt_carType_filter);

        String[] province_list = getResources().getStringArray(R.array.province);
        String[] car_type_list = getResources().getStringArray(R.array.carType);
        province_list[0] = "همه استان ها";
        car_type_list[0] = "همه وسایل";

        location_filter.setText(province_list[settings.getInt("location_filter",0)]);
        car_type_filter.setText(car_type_list[settings.getInt("car_type_filter",0)]);


        location_filter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final ArrayList<String> list = new ArrayList<String>(Arrays.asList(getResources().getStringArray(R.array.province)));
                list.set(0,"همه استان ها");
                AlertDialog.Builder builder = new AlertDialog.Builder(OrdersActivity.this);
                builder.setAdapter(new ArrayAdapter<String>(OrdersActivity.this,R.layout.row,R.id.txt_mytxt,list), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {


                        if (!location_filter.getText().toString().equals(list.get(i))){

                            location_filter.setText(list.get(i));

                            SharedPreferences.Editor editor = settings.edit();
                            editor.putInt("location_filter",i);
                            editor.commit();

                            //refresh the list
                            last_ad_id = 0;
                            adapter.clear_list();

                            not_found_text.setVisibility(View.GONE);

                            new get_ad_list().execute();
                        }

                    }
                });
                builder.show();
            }
        });

        car_type_filter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final ArrayList<String> list = new ArrayList<String>(Arrays.asList(getResources().getStringArray(R.array.carType)));
                list.set(0,"همه وسایل");


                AlertDialog.Builder builder = new AlertDialog.Builder(OrdersActivity.this);
                builder.setAdapter(new ArrayAdapter<String>(OrdersActivity.this,R.layout.row,R.id.txt_mytxt,list), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if (!car_type_filter.getText().toString().equals(list.get(i))){

                            car_type_filter.setText(list.get(i));
                            SharedPreferences.Editor editor = settings.edit();
                            editor.putInt("car_type_filter",i);
                            editor.commit();

                            //refresh the list
                            last_ad_id = 0;
                            adapter.clear_list();

                            not_found_text.setVisibility(View.GONE);

                            new get_ad_list().execute();
                        }

                    }
                });
                builder.show();
            }
        });


    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.search_menu, menu);

        MenuItem searchItem = menu.findItem(R.id.btn_ActionBar_search);

        //change search text and font
        if(searchItem != null){
            SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
            TextView searchText = (TextView)searchView.findViewById(android.support.v7.appcompat.R.id.search_src_text);
            searchText.setTypeface(myfont);

            searchView.setQueryHint("جستجو");

            searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                @Override
                public boolean onQueryTextSubmit(String query) {

                    search_key = query;

                    last_ad_id = 0;
                    adapter.clear_list();
                    new get_ad_list().execute();

                    return false;
                }

                @Override
                public boolean onQueryTextChange(String newText) {

                    if (newText.equals(""))
                    {
                        search_key = "";
                        last_ad_id = 0;
                        adapter.clear_list();
                        new get_ad_list().execute();

                        not_found_text.setVisibility(View.GONE);
                    }
                    return false;
                }
            });
        }

        return true;
    }




    public class get_ad_list extends AsyncTask<Void,Void,String>
    {
        ProgressDialog pd = new ProgressDialog(OrdersActivity.this);

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
                get_ad_list.put("command","get_ad_list");
                get_ad_list.put("location_filter",settings.getInt("location_filter",0));
                get_ad_list.put("car_type_filter",settings.getInt("car_type_filter",0));
                get_ad_list.put("search_key",search_key);
                get_ad_list.put("last_ad_id",last_ad_id);

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

                                if (ad_list.length() == 0 && last_ad_id == 0){
                                    not_found_text.setVisibility(View.VISIBLE);
                                }

                                if (ad_list.length() != 0){
                                last_ad_id = ad_list.getJSONObject(ad_list.length()-1).getInt("id");

                                    if (ad_list.length() != 10){
                                        last_ad_id = -1;
                                    }

                                }
                                else {
                                    last_ad_id = -1;
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
                            Toast.makeText(getBaseContext(), "خطا در در دریافت اطلاعات", Toast.LENGTH_SHORT).show();
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
