package com.example.behzad.thebox;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;


public class FavoriteActivity extends BaseActivity {

    FloatingActionButton fb;
    RecyclerView recyclerView;
    SwipeRefreshLayout swipe;

    TextView txt_location_filter;
    TextView txt_carType_filter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        FrameLayout content_frame = (FrameLayout) findViewById(R.id.content_frame);
        getLayoutInflater().inflate(R.layout.account,content_frame);

        navigationView.getMenu().findItem(R.id.mnu_favorite).setChecked(true);

//        --------------Change The Toolbar title and font---------------
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



    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.help_menu, menu);
        return true;
    }
}
