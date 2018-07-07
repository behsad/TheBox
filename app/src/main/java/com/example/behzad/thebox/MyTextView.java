package com.example.behzad.thebox;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.TextView;

import org.w3c.dom.Text;

public class MyTextView extends android.support.v7.widget.AppCompatTextView {

    public  MyTextView(Context context, AttributeSet attrs, int defStyle){
        super(context,attrs,defStyle);
        init();
    }

    public MyTextView (Context context, AttributeSet attrs){
        super(context, attrs);
        init();
    }
    public MyTextView(Context context){
        super(context);
        init();
    }


    private void init(){
        if(!isInEditMode()){
            Typeface typeface = Typeface.createFromAsset(getContext().getAssets(),"myfont.ttf");
            this.setTypeface(typeface);
        }
    }
}
