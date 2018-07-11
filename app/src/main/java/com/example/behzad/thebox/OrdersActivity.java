package com.example.behzad.thebox;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.View;
import android.widget.ArrayAdapter;
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
import java.util.Arrays;


public class OrdersActivity extends BaseActivity {

    FloatingActionButton fb;
    RecyclerView recyclerView;
    SwipeRefreshLayout swipe;

    TextView location_filter;
    TextView car_type_filter;

    DataAdapter adapter;


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

        recyclerView = (RecyclerView) findViewById(R.id.card_recycler_view);
        recyclerView.setHasFixedSize(true);

        RecyclerView.LayoutManager lm = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(lm);

        new get_ad_list().execute();


        /*
        final ArrayList<Ad> data_list = new ArrayList<Ad>();
        Ad temp_ad = new Ad();
        temp_ad.ad_title = "عنوان آگهی";
        temp_ad.ad_location = "رشت";
        temp_ad.ad_price = "12 تومان";
        temp_ad.ad_image = "http://www.upsara.com/images/5lbx_logoo2.png";

        data_list.add(temp_ad);
        data_list.add(temp_ad);
        data_list.add(temp_ad);
        data_list.add(temp_ad);
        data_list.add(temp_ad);
        data_list.add(temp_ad);
        data_list.add(temp_ad);

        final DataAdapter adapter = new DataAdapter(getApplicationContext(),data_list);
        recyclerView.setAdapter(adapter);


        swipe = (SwipeRefreshLayout) findViewById(R.id.swipe_container);
        swipe.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {

                ArrayList<Ad> data_list = new ArrayList<Ad>();
                Ad temp_ad = new Ad();
                temp_ad.ad_title = "عنوان آگهی دوم";
                temp_ad.ad_location = "تهران";
                temp_ad.ad_price = "100 تومان";
                temp_ad.ad_image = "http://www.upsara.com/images/5lbx_logoo2.png";

                adapter.insert(adapter.getItemCount(),temp_ad);



                swipe.setRefreshing(false);
            }
        });

        */

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

                        location_filter.setText(list.get(i));

                        SharedPreferences.Editor editor = settings.edit();
                        editor.putInt("location_filter",i);
                        editor.commit();


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

                        car_type_filter.setText(list.get(i));
                        SharedPreferences.Editor editor = settings.edit();
                        editor.putInt("car_type_filter",i);
                        editor.commit();



                    }
                });
                builder.show();
            }
        });


    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.search_menu, menu);
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
                get_ad_list.put("last_ad_id",0);
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

                                ArrayList<JSONObject> data_list = new ArrayList<JSONObject>();

                                for (int i = 0;i <ad_list.length();i++)
                                {
                                    data_list.add(ad_list.getJSONObject(i));
                                }

                                adapter = new DataAdapter(getApplicationContext(),data_list);
                                recyclerView.setAdapter(adapter);
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
