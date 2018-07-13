package com.example.behzad.thebox;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
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
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;


public class LoginActivity extends BaseActivity {

    FloatingActionButton fb;
    RecyclerView recyclerView;
    SwipeRefreshLayout swipe;

    TextView txt_location_filter;
    TextView txt_carType_filter;

    EditText mobile_text;
    EditText email_text;
    Button submit_bt;

    EditText activation_code;
    Button submit2_bt;

    String activation_key;

    String mobile;
    String email;

    LinearLayout login_layout;
    LinearLayout activation_layout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);






        FrameLayout content_frame = (FrameLayout) findViewById(R.id.content_frame);
        getLayoutInflater().inflate(R.layout.login,content_frame);

        navigationView.getMenu().findItem(R.id.mnu_account).setChecked(true);

//------------------------Change The Toolbar title and font-------------------
        toolbar.setTitle("ورود به جعبه");
        for (int i = 0; i<toolbar.getChildCount();i++){
            View view = toolbar.getChildAt(i);
            if(view instanceof TextView){
                TextView tv = (TextView) view;
                tv.setTypeface(myfont);
                tv.setTextSize(18);
            }
        }
//------------------------------------End------------------------------------------
        mobile_text = (EditText)findViewById(R.id.edt_login_phoneNumber);
        email_text = (EditText)findViewById(R.id.edt_login_email);
        submit_bt = (Button) findViewById(R.id.btn_login_receive_verifyCode);

        activation_code = (EditText) findViewById(R.id.edt_login_verify);
        submit2_bt = (Button) findViewById(R.id.btn_login_submit_verifyCode);




        submit_bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Boolean is_valid=true;

                    if(!mobile_text.getText().toString().startsWith("09")||mobile_text.getText().toString().length()!=11){
                        is_valid = false;
                    }

                    if (!email_text.getText().toString().trim().equals(""))
                    {
                        if(email_text.getText().toString().indexOf(".")==-1||email_text.getText().toString().indexOf("@")==-1)
                        {
                            is_valid = false;
                        }
                    }

                    if (is_valid){

                        mobile = mobile_text.getText().toString();
                        email = email_text.getText().toString();
                        new send_activation_key().execute();



                    }else {
                        Toast.makeText(getApplicationContext(),"اطلاعات وارد شده صحیح نیست",Toast.LENGTH_LONG).show();
                    }



            }
        });


        submit2_bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (activation_code.getText().toString().length() == 4){

                    activation_key = activation_code.getText().toString();
                    new apply_activation_key().execute();

                }else {
                Toast.makeText(getApplicationContext(),"اطلاعات وارد شده صحیح نیست",Toast.LENGTH_LONG).show();
                }

            }
        });


    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.help_menu, menu);
        return true;
    }


    public class send_activation_key extends AsyncTask<Void,Void,String>
    {
        ProgressDialog pd = new ProgressDialog(LoginActivity.this);

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
                get_ad_list.put("command","send_activation_key");
                get_ad_list.put("mobile",mobile);

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

                    if (response.trim().equals("ok"))
                    {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {

                                login_layout = (LinearLayout)findViewById(R.id.login_layout);
                                activation_layout = (LinearLayout)findViewById(R.id.activation_layout);

                                login_layout.setVisibility(View.GONE);
                                activation_layout.setVisibility(View.VISIBLE);


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
                    final String finalResponse = response;
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





    public class apply_activation_key extends AsyncTask<Void,Void,String>
    {
        ProgressDialog pd = new ProgressDialog(LoginActivity.this);

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
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {


                    }
                });

                get_ad_list.put("command","apply_activation_key");
                get_ad_list.put("mobile",mobile);
                get_ad_list.put("email",email);
                get_ad_list.put("activation_key",activation_key);

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

                    if (!response.trim().equals("error"))
                    {

                        SharedPreferences.Editor editor = settings.edit();
                        editor.putInt("user_id",Integer.parseInt(response));
                        editor.commit();

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(getBaseContext(), "ورود موفقیت آمیز", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(getApplicationContext(),AccountActivity.class);
                                startActivity(intent);

                            }
                        });




                    }else {

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(getBaseContext(), "کد فعال سازی اشتباه است", Toast.LENGTH_SHORT).show();
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
