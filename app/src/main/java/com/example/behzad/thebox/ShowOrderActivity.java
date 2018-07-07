package com.example.behzad.thebox;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
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

import com.daimajia.slider.library.SliderLayout;
import com.daimajia.slider.library.SliderTypes.DefaultSliderView;

import java.util.ArrayList;


public class ShowOrderActivity extends BaseActivity {

    FloatingActionButton fb;
    RecyclerView recyclerView;
    SwipeRefreshLayout swipe;

    TextView txt_location_filter;
    TextView txt_carType_filter;

    SliderLayout slider;

    FloatingActionButton call_bt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        FrameLayout content_frame = (FrameLayout) findViewById(R.id.content_frame);
        getLayoutInflater().inflate(R.layout.show_order,content_frame);


        toolbar.setTitle("");

        //        -------------------Change The Toolbar icon-----------------
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        Drawable arrow = getResources().getDrawable(R.drawable.ic_arrow_forward);
        getSupportActionBar().setHomeAsUpIndicator(arrow);

//        ----------------------change toolbar action----------------

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawer.closeDrawer(Gravity.RIGHT);
                ShowOrderActivity.this.finish();
            }
        });
//------------------------------------End------------------------------------------

//-----------------------------Setup Slider--------------------------------------------
        slider = (SliderLayout)findViewById(R.id.slider);

        DefaultSliderView sliderView = new DefaultSliderView(this);
        sliderView.image("http://www.upsara.com/images/ps4f_logoo.png");
        slider.addSlider(sliderView);
        slider.stopAutoCycle();


        call_bt = (FloatingActionButton) findViewById(R.id.btn_float_Call);
        call_bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final ArrayList<String> list = new ArrayList<String>();
                list.add("ارسال ایمیل");
                list.add("تماس تلفنی");
                list.add("ارسال پیامک");

                AlertDialog.Builder builder = new AlertDialog.Builder(ShowOrderActivity.this);
                builder.setAdapter(new ArrayAdapter<String>(ShowOrderActivity.this,R.layout.row,R.id.txt_mytxt,list), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {


                    }
                });
                builder.show();

            }
        });




    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.like_share_menu, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        if(item.getItemId()==R.id.share){



        }else if(item.getItemId()==R.id.bookmark){

            item.setIcon(R.drawable.ic_filled_star);

        }

        return super.onOptionsItemSelected(item);
    }
}
