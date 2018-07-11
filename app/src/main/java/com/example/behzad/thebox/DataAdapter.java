package com.example.behzad.thebox;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Locale;

public class DataAdapter extends RecyclerView.Adapter<DataAdapter.ViewHolder> {

    ArrayList<JSONObject> data_list;
    Context context;

    public DataAdapter(Context context, ArrayList<JSONObject> data_list)
    {
    this.context = context;
    this.data_list = data_list;

    }

    @NonNull
    @Override
    public DataAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.order_row,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DataAdapter.ViewHolder holder, int position) {

        try {
            holder.txt_orderTitle.setText(data_list.get(position).getString("title"));


            String[] province_list = context.getResources().getStringArray(R.array.province);
            holder.txt_orderLocation.setText(province_list[data_list.get(position).getInt("province")]);

            ArrayAdapter<CharSequence> myadapter = ArrayAdapter.createFromResource(context, context.getResources().getIdentifier("array/city" + data_list.get(position).getInt("province"), null, context.getPackageName()), R.layout.row);
            holder.txt_orderLocation.setText(holder.txt_orderLocation.getText()+" - "+myadapter.getItem(data_list.get(position).getInt("city")));


            if(data_list.get(position).getInt("price_type")==0)
            {
                holder.txt_orderPrice.setText("");

            }else if(data_list.get(position).getInt("price_type")==1)
            {
                holder.txt_orderPrice.setText("قیمت توافقی");

            }else if(data_list.get(position).getInt("price_type")==2)
            {
                //set US number format 20000 -> 20,000
                NumberFormat numberFormat =  NumberFormat.getNumberInstance(Locale.US);
                holder.txt_orderPrice.setText(numberFormat.format(data_list.get(position).getInt("price")) + " تومان ");
            }

            if(!data_list.get(position).getString("image1").equals("")){

                Picasso.with(context).load("http://192.168.43.38/thebox/"+data_list.get(position).getString("image1")).resize(128,128).into(holder.img_preview);
            }
            else {
                if(!data_list.get(position).getString("image2").equals("")){

                    Picasso.with(context).load("http://192.168.43.38/thebox/"+data_list.get(position).getString("image2")).resize(128,128).into(holder.img_preview);
                }
                else {
                    if(!data_list.get(position).getString("image3").equals("")){

                        Picasso.with(context).load("http://192.168.43.38/thebox/"+data_list.get(position).getString("image3")).resize(128,128).into(holder.img_preview);
                    }
                }
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
        //holder.txt_orderLocation.setText(data_list.get(position).ad_location);
        //holder.txt_orderPrice.setText(data_list.get(position).ad_price);

        //Picasso.with(context).load(data_list.get(position).ad_image).resize(128,128).into(holder.img_preview);

/*
        if(position >= getItemCount()-1){
           new Handler().postDelayed(new Runnable() {
               @Override
               public void run() {
                   Ad temp_ad = new Ad();
                   temp_ad.ad_title = "عنوان آگهی سوم";
                   temp_ad.ad_location = "قزوین";
                   temp_ad.ad_price = "65 تومان";
                   temp_ad.ad_image = "https://findicons.com/files/icons/1675/sketchy/128/box.png";
                   insert(getItemCount(),temp_ad);
               }
           },1);
        }
*/
        holder.cardV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(context,ShowOrderActivity.class);
                //--chon context dar yek class dige hast bayad in code ro ezafe bokonim
                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                //--
                context.startActivity(i);
            }
        });

    }

    @Override
    public int getItemCount() {
        return data_list.size();
    }

    public void insert (int position, JSONObject data){
        data_list.add(position,data);
        notifyItemInserted(position);
    }


    public class ViewHolder extends RecyclerView.ViewHolder{

        TextView txt_orderTitle;
        TextView txt_orderPrice;
        TextView txt_orderLocation;
        ImageView img_preview;

        CardView cardV;

        public ViewHolder(View itemView) {
            super(itemView);

            txt_orderTitle = (TextView)itemView.findViewById(R.id.txt_order_title);
            txt_orderPrice = (TextView)itemView.findViewById(R.id.txt_order_price);
            txt_orderLocation = (TextView)itemView.findViewById(R.id.txt_order_location);
            img_preview = (ImageView) itemView.findViewById(R.id.img_preview);
            cardV = (CardView) itemView.findViewById(R.id.cardV);
        }
    }
}
