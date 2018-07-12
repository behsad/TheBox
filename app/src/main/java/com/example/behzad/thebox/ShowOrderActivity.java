package com.example.behzad.thebox;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.daimajia.slider.library.SliderLayout;
import com.daimajia.slider.library.SliderTypes.DefaultSliderView;

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

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Locale;


public class ShowOrderActivity extends BaseActivity {

    FloatingActionButton fb;
    RecyclerView recyclerView;
    SwipeRefreshLayout swipe;

    TextView txt_location_filter;
    TextView txt_carType_filter;

    SliderLayout slider;

    FloatingActionButton call_bt;

    JSONObject ad;

    TextView ad_title;
    TextView ad_price;
    TextView ad_description;
    TextView ad_car_type;
    TextView ad_location;

    Menu menu;

    JSONObject user_contact_details;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        FrameLayout content_frame = (FrameLayout) findViewById(R.id.content_frame);
        getLayoutInflater().inflate(R.layout.show_order, content_frame);

        //receiving data from order activity
        try {
            //bookmark data
            ad = new JSONObject(getIntent().getStringExtra("ad"));


        } catch (JSONException e) {
            e.printStackTrace();
        }




        ad_title = (TextView) findViewById(R.id.txt_showOrder_title);
        ad_price = (TextView) findViewById(R.id.txt_showOrder_price);
        ad_location = (TextView) findViewById(R.id.txt_showOrder_location);
        ad_car_type  = (TextView) findViewById(R.id.txt_showOrder_carType);
        ad_description = (TextView) findViewById(R.id.txt_showOrder_description);


        try {
            ad_title.setText(ad.getString("title"));
            ad_description.setText(ad.getString("description"));




            if(ad.getInt("price_type")==0)
            {
                ad_price.setText("");

            }else if(ad.getInt("price_type")==1)
            {
                ad_price.setText("قیمت توافقی");

            }else if(ad.getInt("price_type")==2)
            {
                //set US number format 20000 -> 20,000
                NumberFormat numberFormat =  NumberFormat.getNumberInstance(Locale.US);
                ad_price.setText(numberFormat.format(ad.getInt("price")) + " تومان ");
            }



            String[] province_list = getResources().getStringArray(R.array.province);
            ad_location.setText(province_list[ad.getInt("province")]);

            ArrayAdapter<CharSequence> myadapter = ArrayAdapter.createFromResource(getApplicationContext(), getApplicationContext().getResources().getIdentifier("array/city" + ad.getInt("province"), null, getApplicationContext().getPackageName()), R.layout.row);
            ad_location.setText(ad_location.getText()+" - "+myadapter.getItem(ad.getInt("city"))+" , " + ad.getString("district"));



            String[] car_type_list = getResources().getStringArray(R.array.carType);
            ad_car_type.setText(car_type_list[ad.getInt("car_type")]);




        } catch (JSONException e) {
            e.printStackTrace();
        }

        toolbar.setTitle("");

        //------------------------ Change The Toolbar icon-----------------
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        Drawable arrow = getResources().getDrawable(R.drawable.ic_arrow_forward);
        getSupportActionBar().setHomeAsUpIndicator(arrow);

//---------------------------- change toolbar action-----------------------

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawer.closeDrawer(Gravity.RIGHT);
                ShowOrderActivity.this.finish();
            }
        });
//------------------------------------End------------------------------------------

//-----------------------------Setup Slider--------------------------------------------
        slider = (SliderLayout) findViewById(R.id.slider);

        try {

            if (ad.getString("image1").trim().equals("") && ad.getString("image2").trim().equals("") && ad.getString("image3").trim().equals("")) {
                DefaultSliderView sliderView = new DefaultSliderView(this);
                sliderView.image("http://www.upsara.com/images/ps4f_logoo.png");
            } else {


                if (!ad.getString("image1").trim().equals("")) {
                    DefaultSliderView sliderView = new DefaultSliderView(this);
                    sliderView.image("http://192.168.43.38/thebox/" + ad.getString("image1").trim());
                    slider.addSlider(sliderView);
                }
                if (!ad.getString("image2").trim().equals("")) {
                    DefaultSliderView sliderView = new DefaultSliderView(this);
                    sliderView.image("http://192.168.43.38/thebox/" + ad.getString("image2").trim());
                    slider.addSlider(sliderView);
                }
                if (!ad.getString("image3").trim().equals("")) {
                    DefaultSliderView sliderView = new DefaultSliderView(this);
                    sliderView.image("http://192.168.43.38/thebox/" + ad.getString("image3").trim());
                    slider.addSlider(sliderView);
                }

            }
        } catch (Exception e) {

        }

        slider.stopAutoCycle();


        call_bt = (FloatingActionButton) findViewById(R.id.btn_float_Call);
        call_bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                new get_contact_details().execute();

            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.like_share_menu, menu);

        this.menu=menu;

        try {
            if (ad.getBoolean("bookmark")){
                menu.findItem(R.id.bookmark).setIcon(R.drawable.ic_filled_star);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.share) {
            Intent intent = new Intent(Intent.ACTION_SEND);
            intent.setType("text/plain");
            try {
                intent.putExtra(Intent.EXTRA_SUBJECT,ad.getString("title"));
                intent.putExtra(Intent.EXTRA_TEXT,ad.getString("title")+" \n" + "با نصب جعبه میتونی این آگهی ببینی\n\n لینک دانلود :\n example.com");
            } catch (JSONException e) {
                e.printStackTrace();
            }

            startActivity(Intent.createChooser(intent,"یک برنامه را انتخاب کنید"));


        } if (item.getItemId() == R.id.bookmark) {
            if (settings.getInt("user_id",0)==0){
                Toast.makeText(getApplicationContext(),"لطفا وارد حساب کاربری خود شوید",Toast.LENGTH_LONG);
            }else
            {
                new bookmark_ad().execute();
            }

            item.setIcon(R.drawable.ic_filled_star);

        }

        return super.onOptionsItemSelected(item);
    }


    public class bookmark_ad extends AsyncTask<Void,Void,String>
    {
        ProgressDialog pd = new ProgressDialog(ShowOrderActivity.this);

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
                get_ad_list.put("command","bookmark_ad");
                get_ad_list.put("user_id",settings.getInt("user_id",0));
                get_ad_list.put("ad_id",ad.getInt("id"));

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

                    if(response.equals("ok")){

                        if(ad.getBoolean("bookmark"))
                        {
                            ad.put("bookmark",false);

                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(getBaseContext(), "سفارش از علاقه مندی ها حذف شد", Toast.LENGTH_SHORT).show();
                                    menu.findItem(R.id.bookmark).setIcon(R.drawable.ic_blank_star);
                                }
                            });



                        }else {
                            ad.put("bookmark",true);

                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(getBaseContext(), "سفارش به علاقه مندی ها اضافه شد", Toast.LENGTH_SHORT).show();
                                    menu.findItem(R.id.bookmark).setIcon(R.drawable.ic_filled_star);
                                }
                            });



                        }

                    }else {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(getBaseContext(), "خطا در دریافت اطلاعات", Toast.LENGTH_SHORT).show();
                            }
                        });

                    }

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



    public class get_contact_details extends AsyncTask<Void,Void,String>
    {
        ProgressDialog pd = new ProgressDialog(ShowOrderActivity.this);

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
                get_ad_list.put("command","get_contact_details");
                get_ad_list.put("user_id",ad.getInt("user_id"));

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

                    if(!response.equals("error"))
                    {
                        final String finalResponse = response;
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                String email="";
                                String mobile="";

                                try {
                                    user_contact_details = new JSONObject(finalResponse);

                                    if (!user_contact_details.getString("email").equals(""))
                                    {
                                        email = user_contact_details.getString("email");
                                    }
                                    mobile = user_contact_details.getString("mobile");
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }

                                final ArrayList<String> list = new ArrayList<String>();

                                list.add("تماس تلفنی : "+mobile);
                                list.add("ارسال پیامک : "+mobile);
                                if(!email.equals("")){
                                    list.add("ارسال ایمیل : "+email);
                                }

                                AlertDialog.Builder builder = new AlertDialog.Builder(ShowOrderActivity.this);
                                builder.setAdapter(new ArrayAdapter<String>(ShowOrderActivity.this, R.layout.row, R.id.txt_mytxt, list), new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {

                                        if(i==0){//call
                                            try {

                                                //TODO : fix call permission
                                                Intent callIntent = new Intent(Intent.ACTION_CALL);
                                                callIntent.setData(Uri.parse("tel:"+user_contact_details.getString("mobile")));
                                                startActivity(callIntent);

                                            }catch (Exception e){

                                            }
                                        }else if (i==1){//sms
                                            try {
                                                Intent smsIntent = new Intent(Intent.ACTION_SENDTO);
                                                smsIntent.setData(Uri.parse("sms:"+user_contact_details.getString("mobile")));
                                                startActivity(smsIntent);
                                            } catch (JSONException e1) {
                                                e1.printStackTrace();
                                            }


                                        }else if (i==2){//email
                                            try {
                                                Intent emailIntent = new Intent(Intent.ACTION_SENDTO);
                                                emailIntent.setData(Uri.parse("mailto:"+user_contact_details.getString("email")));
                                                startActivity(emailIntent);
                                            } catch (JSONException e1) {
                                                e1.printStackTrace();
                                            }

                                        }

                                    }
                                });
                                builder.show();
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
