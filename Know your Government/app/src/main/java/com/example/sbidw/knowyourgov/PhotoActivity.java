package com.example.sbidw.knowyourgov;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import static android.R.drawable.ic_dialog_alert;

public class PhotoActivity extends AppCompatActivity {

    Person infoSent = new Person();
    String add = null;
    private TextView photoLoc;
    private TextView photoDesig;
    private TextView photoName;
    private ImageView imageviewbig;
    private String TAG = "PhotoActivity";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.photo_activity);
        Intent intent = getIntent();

        infoSent = (Person)intent.getSerializableExtra("infoSent");
        add = (String)intent.getSerializableExtra("location");
        ConstraintLayout constraintLayout = findViewById(R.id.photoConst);

        if(infoSent.getParty().equals("Republican")) {
            constraintLayout.setBackgroundColor(Color.RED);
        }
        else if(infoSent.getParty().equals("Democratic"))
        {
            constraintLayout.setBackgroundColor(Color.BLUE);
        }
        else
        {
            constraintLayout.setBackgroundColor(Color.BLACK);
        }


        if(infoSent != null) {

            photoLoc = findViewById(R.id.photoLocation);
            photoLoc.setBackgroundColor(getResources().getColor(R.color.back_purple));
            photoLoc.setText(add);


            photoDesig = findViewById(R.id.photoDesignation);
            photoDesig.setText(infoSent.getPersonDesignation());

            photoName = findViewById(R.id.photoName);
            photoName.setText(infoSent.getPersonName());

        }


        int result = 0;
        result = networkCheckOnAddButton(this);

        if(result == 1)
        {
            imageviewbig = findViewById(R.id.imageViewBig);
            imageviewbig.setImageResource(R.drawable.placeholder);

        }
        else {

            if (!infoSent.getPhotourls().equals("NoPhoto")) {
                imageviewbig = findViewById(R.id.imageViewBig);
                loadImage(infoSent.getPhotourls());
            } else {
                imageviewbig = findViewById(R.id.imageViewBig);
                imageviewbig.setImageResource(R.drawable.missingimage);
            }

        }

    }

    @Override
    protected void onResume(){
        super.onResume();
    }


    @Override
    protected  void onPause(){
        super.onPause();
    }




    public int networkCheckOnAddButton(Context context) {

        int result = 0;
        ConnectivityManager connectivityManager =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = connectivityManager.getActiveNetworkInfo();

        if (netInfo != null && netInfo.isConnectedOrConnecting()) {
        } else {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            Intent intent = new Intent();
            builder.setTitle("No Network Connection");

            builder.setIcon(ic_dialog_alert);
            builder.setMessage("Data cannot be accesses/loaded without an Internet connection");
            AlertDialog dialog = builder.create();
            dialog.show();
            result = 1;

        }
        return result;
    }

    private void loadImage(final String imageURL) {

        Picasso picasso = new Picasso.Builder(this)
                .listener(new Picasso.Listener() {
                    @Override
                    public void onImageLoadFailed(Picasso picasso, Uri uri, Exception exception) {
                        Log.d(TAG, "onImageLoadFailed: ");
                        picasso.load(R.drawable.brokenimage)
                                .into(imageviewbig);
                    }
                })
                .build();

        picasso.load(imageURL)
                .error(R.drawable.brokenimage)
                .placeholder(R.drawable.placeholder)
                .into(imageviewbig);
    }
}
