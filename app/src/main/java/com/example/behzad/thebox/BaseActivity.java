package com.example.behzad.thebox;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;


import java.lang.reflect.Field;


public class BaseActivity extends AppCompatActivity {

     Typeface myfont;
     Toolbar toolbar;
     DrawerLayout drawer;
     NavigationView navigationView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

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
                    Intent intent = new Intent(getApplicationContext(),NewOrderActivity.class);
                    startActivity(intent);

                }else if (id == R.id.mnu_userInfo){
//                    Intent intent = new Intent(getApplicationContext(),NewOrderActivity.class);
//                    startActivity(intent);

                }else if (id== R.id.mnu_exit){

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
