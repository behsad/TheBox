package com.example.behzad.thebox;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import com.theartofdev.edmodo.cropper.CropImage;

import java.io.File;
import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;


public class NewOrderActivity extends BaseActivity {

    Spinner spn_province;
    Spinner spn_city;
    Spinner spn_carType;
    Spinner spn_priceType;

    TextInputLayout title_layout;
    TextInputLayout description_layout;
    TextInputLayout district_layout;
    TextInputLayout price_layout;

    EditText title_text;
    EditText description_text;
    EditText district_text;
    EditText price_text;

    Button submit_bt;


    ArrayList<ImageView> images = new ArrayList<ImageView>();

    int current_image;
    Uri uri;

    Boolean[] fill_images = new Boolean[3];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        FrameLayout content_frame = (FrameLayout) findViewById(R.id.content_frame);
        getLayoutInflater().inflate(R.layout.new_order,content_frame);

        navigationView.getMenu().findItem(R.id.mnu_newOrder).setChecked(true);


//-------------------------Change The Toolbar title and font---------------
        toolbar.setTitle("ثبت سفارش");
        for (int i = 0; i<toolbar.getChildCount();i++){
            View view = toolbar.getChildAt(i);
            if(view instanceof TextView){
                TextView tv = (TextView) view;
                tv.setTypeface(myfont);
                tv.setTextSize(18);
            }
        }
//------------------------------------End------------------------------------------

//        -------------------Change The Toolbar icon-----------------
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        Drawable arrow = getResources().getDrawable(R.drawable.ic_arrow_forward);
        getSupportActionBar().setHomeAsUpIndicator(arrow);

//        ----------------------change toolbar action----------------

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawer.closeDrawer(Gravity.RIGHT);
                NewOrderActivity.this.finish();
            }
        });

//------------------------------------End------------------------------------------

//-------------------------------- mainInitial------------------------------------
        spn_province = (Spinner) findViewById(R.id.spn_province);
        spn_city = (Spinner) findViewById(R.id.spn_city);
        spn_carType = (Spinner)findViewById(R.id.spn_carType);
        spn_priceType = (Spinner) findViewById(R.id.spn_priceType);

        images.add((ImageView)findViewById(R.id.img_selectImage1));
        images.add((ImageView)findViewById(R.id.img_selectImage2));
        images.add((ImageView)findViewById(R.id.img_selectImage3));


        title_layout= (TextInputLayout) findViewById(R.id.edtLayout_newOrder_title);
        description_layout = (TextInputLayout) findViewById(R.id.edtLayout_newOrder_description);
        district_layout = (TextInputLayout)findViewById(R.id.edtLayout_newOrder_district);
        price_layout = (TextInputLayout)findViewById(R.id.edtLayout_newOrder_price);

        title_text = (EditText) findViewById(R.id.edt_newOrder_title);
        description_text = (EditText)findViewById(R.id.edt_newOrder_description);
        district_text = (EditText)findViewById(R.id.edt_newOrder_district);
        price_text = (EditText) findViewById(R.id.edt_newOrder_price);

        submit_bt = (Button) findViewById(R.id.btn_submit_order);

//------------------------------------End------------------------------------------



// ------------------------------spinner Province-------------------------------
        ArrayAdapter<CharSequence> myadapter = ArrayAdapter.createFromResource(getApplicationContext(), R.array.province, R.layout.row);
        spn_province.setAdapter(myadapter);

        //namayesh shahr bar asas ostan

        spn_province.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {


                ArrayAdapter<CharSequence> myadapter = ArrayAdapter.createFromResource(getApplicationContext(), getResources().getIdentifier("array/city" + spn_province.getSelectedItemPosition(), null, getApplicationContext().getPackageName()), R.layout.row);
                spn_city.setAdapter(myadapter);


            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
//---------------------------------------------------------------------------

// ------------------------ CarType and PriceType Spinners -----------------
        myadapter = ArrayAdapter.createFromResource(getApplicationContext(), R.array.carType, R.layout.row);
        spn_carType.setAdapter(myadapter);

        myadapter = ArrayAdapter.createFromResource(getApplicationContext(), R.array.price_type, R.layout.row);
        spn_priceType.setAdapter(myadapter);
//----------------------------------End-----------------------------------------

//------------------------------Add Image Stuff-----------------------------

        images.get(0).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                current_image = 0;
                if (!fill_images[current_image]) {
                    show_dialog();
                }
                else {
                    show_delete_dialog();
                }
            }
        });
        images.get(1).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                current_image = 1;
                if (!fill_images[current_image]) {
                    show_dialog();
                }
                else {
                    show_delete_dialog();
                }
            }
        });
        images.get(2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                current_image = 2;
                if (!fill_images[current_image]) {
                    show_dialog();
                }
                else {
                    show_delete_dialog();
                }
            }
        });

        Arrays.fill(fill_images,false);
//----------------------------------End-----------------------------------------



//--------------------------- Set price Edit Text Visible and Invisible --------------------
        spn_priceType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int i, long l) {

                if(spn_priceType.getSelectedItemPosition()==2){
                    price_layout.setVisibility(View.VISIBLE);
                }
                else {
                    price_layout.setVisibility(View.GONE);
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
//----------------------------------End-----------------------------------------

//----------------------------- Set Focus on Title EditText --------------------

        title_text.requestFocus();

//----------------------------------End-----------------------------------------

        submit_bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Boolean is_validate = true;

            //validation inputs
                //title validation
                if(title_text.getText().toString().trim().length()>=4){
                    title_layout.setErrorEnabled(false);

                }else {
                    title_layout.setError("عنوان آگهی باید حداقل 4 حرف باشد");
                    title_layout.setErrorEnabled(true);
                    is_validate=false;
                }

                //description validation
                if(description_text.getText().toString().trim().length()>=10) {
                    description_layout.setErrorEnabled(false);


                }else {
                    description_layout.setError("توضیحات آگهی باید حداقل 10 حرف باشد");
                    description_layout.setErrorEnabled(true);
                    is_validate=false;
                }

                //district validation
                if(district_text.getText().toString().trim().length()>=10) {
                    district_layout.setErrorEnabled(false);


                }else {
                    district_layout.setError("آدرس آگهی باید حداقل 4 حرف باشد");
                    district_layout.setErrorEnabled(true);
                    is_validate=false;
                }

                //car type Spinner validation
                if(spn_carType.getSelectedItemPosition()==0){

                    ((TextView)spn_carType.getSelectedView()).setError("");

                }
                //province Spinner validation
                if(spn_province.getSelectedItemPosition()==0){

                    ((TextView)spn_province.getSelectedView()).setError("");

                }
                //city Spinner validation
                if(spn_city.getSelectedItemPosition()==0){

                    ((TextView)spn_city.getSelectedView()).setError("");

                }
                //price type Spinner validation
                if(spn_priceType.getSelectedItemPosition()==0){

                    ((TextView)spn_priceType.getSelectedView()).setError("");

                }
                if(spn_priceType.getSelectedItemPosition()== 2){

                    if(price_text.getText().toString().trim().length() == 0 || Integer.parseInt(price_text.getText().toString())<=0){

                        price_layout.setError("قیمت وارد شده اشتباه است");
                        price_layout.setErrorEnabled(true);
                        is_validate=false;
                    }
                    else {
                        price_layout.setErrorEnabled(false);
                    }

                }

                else {
                    price_layout.setErrorEnabled(true);
                }
            }
        });



    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.help_menu, menu);
        return true;
    }


    public void show_dialog(){

        //get Storage and camera permissions
        if (Build.VERSION.SDK_INT >= 23) {
            if (checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {

                //File write logic here
            } else {
                ActivityCompat.requestPermissions(NewOrderActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
            }
        }

        if (ContextCompat.checkSelfPermission(NewOrderActivity.this, Manifest.permission.CAMERA)
                == PackageManager.PERMISSION_DENIED){
            ActivityCompat.requestPermissions(NewOrderActivity.this, new String[] {Manifest.permission.CAMERA}, 1);
        }



        //----------------------------------End-----------------------------------------

        final ArrayList<String> list = new ArrayList<String>();
        list.add("انتخاب عکس از گالری");
        list.add("گرفتن عکس از دوربین");

        AlertDialog.Builder builder = new AlertDialog.Builder(NewOrderActivity.this);
        builder.setAdapter(new ArrayAdapter<String>(NewOrderActivity.this,R.layout.row,R.id.txt_mytxt,list), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {


                if(i==0){
                    //gallery
//                    StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
//                    StrictMode.setVmPolicy(builder.build());
                    Intent gallery_intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    startActivityForResult(Intent.createChooser(gallery_intent,"لطفا یک عکس را انتخاب کنید"),2);
                }
                else {
                    //camera
//                    StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
//                    StrictMode.setVmPolicy(builder.build());






//                    Intent camera_intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//                    File file = new File(Environment.getExternalStorageDirectory(),"file"+String.valueOf(System.currentTimeMillis())+".jpg");
//                    uri = Uri.fromFile(file);
//                    camera_intent.putExtra(MediaStore.EXTRA_OUTPUT,uri);
//                    camera_intent.putExtra("return-data",true);
//                    startActivityForResult(camera_intent,1);

                }


            }
        });
        builder.show();

    }
//----------------------------- SHOW DELETE DIALOG FOR IMAGE-----------------------------
    public void show_delete_dialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(NewOrderActivity.this);

        builder.setMessage("حذف کردن عکس؟");
        builder.setPositiveButton("بله", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                images.get(current_image).setImageResource(R.drawable.ic_add_image);
                fill_images[current_image]=false;

            }
        });
        builder.setNegativeButton("نه", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

//----------------------------------------End----------------------------------------------

    public void onActivityResult(int requestCode, int resultCode, Intent data){
        if (requestCode == 1 && resultCode == RESULT_OK){
            //camera

            //set image size by CropImage library
            CropImage.activity(uri).setAspectRatio(1,1).setRequestedSize(512,512).start(this);
        }
        else if(requestCode == 2 && resultCode == RESULT_OK){
            //gallery
            uri=data.getData();
            CropImage.activity(uri).setAspectRatio(1,1).setRequestedSize(512,512).start(this);
        }
        else if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE&&resultCode == RESULT_OK){
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            Uri resultUri =result.getUri();
            images.get(current_image).setImageURI(resultUri);
            fill_images[current_image]=true;
        }
    }


}
