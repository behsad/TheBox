package com.example.behzad.thebox;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;


import java.lang.reflect.Field;


public class BaseActivity extends AppCompatActivity {

     Typeface myfont;
     Toolbar toolbar;
     DrawerLayout drawer;
     NavigationView navigationView;

     SharedPreferences settings;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        settings = PreferenceManager.getDefaultSharedPreferences(this);
        //for test
        SharedPreferences.Editor editor = settings.edit();
//        editor.putInt("user_id",55);
//        editor.commit();

//       ---------------------- mainInitial------------------------------------
        toolbar = (Toolbar) findViewById(R.id.my_toolbar);
        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        navigationView = (NavigationView) findViewById(R.id.nav_view);


//        Change The Toolbar title and font
        toolbar.setTitle("جعبه");
        for (int i = 0; i<toolbar.getChildCount();i++){
            View view = toolbar.getChildAt(i);
            if(view instanceof TextView){
                TextView tv = (TextView) view;
                tv.setTypeface(myfont);
                tv.setTextSize(24);
            }


//        --------------------Set Font--------------------------------------
        myfont = Typeface.createFromAsset(getAssets(), "myfont.ttf");
        try {
            Field st = Typeface.class.getDeclaredField("MONOSPACE");
            st.setAccessible(true);
            try {
                st.set(null, myfont);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        }


//        -------------------------navigationViews Stuff----------------------------------------
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id = item.getItemId();

                if (id == R.id.mnu_orders) {
                    Intent intent = new Intent(getApplicationContext(),OrdersActivity.class);
                    startActivity(intent);

                }else if (id == R.id.mnu_newOrder){

                    //check user login
                    if (settings.getInt("user_id",0)!=0){

                    Intent intent = new Intent(getApplicationContext(),NewOrderActivity.class);
                    startActivity(intent);
                    }else {
                        Toast.makeText(getBaseContext(),"برای سفارش دادن باید عضو شوید",Toast.LENGTH_LONG).show();
                        Intent intent = new Intent(getApplicationContext(),LoginActivity.class);
                        startActivity(intent);
                    }


                }else if (id == R.id.mnu_account){
                    if (settings.getInt("user_id",0)!=0){
                        Intent intent = new Intent(getApplicationContext(),AccountActivity.class);
                        startActivity(intent);

                    }else {
                        Intent intent = new Intent(getApplicationContext(),LoginActivity.class);
                        startActivity(intent);
                    }


                }else if (id== R.id.mnu_favorite){
                    if (settings.getInt("user_id",0)!=0){

                        Intent intent = new Intent(getApplicationContext(),FavoriteActivity.class);
                        startActivity(intent);
                    }else {
                        Toast.makeText(getBaseContext(),"برای مشاهده نشان شده ها باید عضو شوید",Toast.LENGTH_LONG).show();
                        Intent intent = new Intent(getApplicationContext(),LoginActivity.class);
                        startActivity(intent);
                    }

                }else if (id== R.id.mnu_share){

                    //share app with optional choose
                    Intent intent = new Intent(Intent.ACTION_SEND);
                    intent.setType("text/plain");
                    intent.putExtra(Intent.EXTRA_SUBJECT,"جعبه");
                    intent.putExtra(Intent.EXTRA_TEXT,"با نصب جعبه میتونید محموله خود را به هر جای  ایران ارسال کنید\n\n لینک دانلود :\n example.com");

                    startActivity(Intent.createChooser(intent,"یک برنامه را انتخاب کنید"));


                }
                drawer.closeDrawer(Gravity.RIGHT);
                return true;
            }
        });

//        --------------------Tool Bar-------------------------------------------
        setSupportActionBar(toolbar);

        toolbar.setNavigationIcon(R.drawable.myicon_menu);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (drawer.isDrawerOpen(Gravity.RIGHT))
                    drawer.closeDrawer(Gravity.RIGHT);
                else
                    drawer.openDrawer(Gravity.RIGHT);
            }
        });

        }


    }






}
