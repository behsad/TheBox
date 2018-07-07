package com.example.behzad.thebox;

import android.content.DialogInterface;
import android.content.Intent;
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

import java.util.ArrayList;
import java.util.Arrays;


public class OrdersActivity extends BaseActivity {

    FloatingActionButton fb;
    RecyclerView recyclerView;
    SwipeRefreshLayout swipe;

    TextView txt_location_filter;
    TextView txt_carType_filter;

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
                Intent intent = new Intent(OrdersActivity.this,NewOrderActivity.class);
                startActivity(intent);
            }
        });

        recyclerView = (RecyclerView) findViewById(R.id.card_recycler_view);
        recyclerView.setHasFixedSize(true);

        RecyclerView.LayoutManager lm = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(lm);

        ArrayList<Ad> data_list = new ArrayList<Ad>();
        Ad temp_ad = new Ad();
        temp_ad.ad_title = "عنوان آگهی";
        temp_ad.ad_location = "رشت";
        temp_ad.ad_price = "12 تومان";
        temp_ad.ad_image = "https://findicons.com/files/icons/1675/sketchy/128/box.png";

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
                temp_ad.ad_image = "https://findicons.com/files/icons/1675/sketchy/128/box.png";

                adapter.insert(adapter.getItemCount(),temp_ad);



                swipe.setRefreshing(false);
            }
        });

        txt_location_filter = (TextView) findViewById(R.id.txt_location_filter);
        txt_carType_filter = (TextView) findViewById(R.id.txt_carType_filter);

        txt_location_filter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final ArrayList<String> list = new ArrayList<String>(Arrays.asList(getResources().getStringArray(R.array.province)));
                list.set(0,"همه استان ها");
                AlertDialog.Builder builder = new AlertDialog.Builder(OrdersActivity.this);
                builder.setAdapter(new ArrayAdapter<String>(OrdersActivity.this,R.layout.row,R.id.txt_mytxt,list), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                        txt_location_filter.setText(list.get(i));


                    }
                });
                builder.show();
            }
        });
        txt_carType_filter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final ArrayList<String> list = new ArrayList<String>(Arrays.asList(getResources().getStringArray(R.array.carType)));
                list.set(0,"همه وسایل");


                AlertDialog.Builder builder = new AlertDialog.Builder(OrdersActivity.this);
                builder.setAdapter(new ArrayAdapter<String>(OrdersActivity.this,R.layout.row,R.id.txt_mytxt,list), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                        txt_carType_filter.setText(list.get(i));


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
}
