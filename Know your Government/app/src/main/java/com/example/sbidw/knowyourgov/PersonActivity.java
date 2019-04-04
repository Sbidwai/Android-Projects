package com.example.sbidw.knowyourgov;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.util.Linkify;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.HashMap;

import static android.R.drawable.ic_dialog_alert;

public class PersonActivity extends AppCompatActivity {


    Person infoSent = new Person();
    private TextView location;
    private TextView desig;
    private TextView namePerson;
    private TextView party;
    private TextView address;

    private TextView phone;
    private TextView email;
    private TextView website;

    private ImageButton gPlusButton;
    private ImageButton fbButton;
    private ImageButton YouTubeButton;
    private ImageButton TwitterButton;
    private ImageView view;
    private String add = null;

    private static final int NEW = 1;

    HashMap<String, String> hashMapChannels = new HashMap<String, String>();

    private static final String TAG = "PersonActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.person_activity);
        Intent intent = getIntent();

        infoSent = (Person) intent.getSerializableExtra("infoSent");
        add = (String) intent.getSerializableExtra("location");
        ConstraintLayout constraintLayout = findViewById(R.id.constraintId);


        if (infoSent.getParty().equals("Republican")) {
            constraintLayout.setBackgroundColor(Color.RED);
        } else if (infoSent.getParty().equals("Democratic")) {
            constraintLayout.setBackgroundColor(Color.BLUE);
        } else {
            constraintLayout.setBackgroundColor(Color.BLACK);
        }

        if (infoSent != null) {

            location = findViewById(R.id.location);
            location.setBackgroundColor(getResources().getColor(R.color.back_purple));
            location.setText(add);

            desig = findViewById(R.id.officialDesignation);
            desig.setText(infoSent.getPersonDesignation());

            namePerson = findViewById(R.id.officialName);
            namePerson.setText(infoSent.getPersonName());

            party = findViewById(R.id.party);
            party.setText("(" + infoSent.getParty() + ")");

            address = findViewById(R.id.address1);
            address.setText(String.format("%s %s %s, %s %s", infoSent.getLineOne(), infoSent.getLineTwo(), infoSent.getCity(), infoSent.getState(), infoSent.getZipcode()));
            address.setLinkTextColor(Color.WHITE);

            Linkify.addLinks(((TextView) findViewById(R.id.address1)), Linkify.MAP_ADDRESSES);

            phone = findViewById(R.id.phone);
            phone.setText(infoSent.getPhone());
            phone.setLinkTextColor(Color.WHITE);
            Linkify.addLinks(((TextView) findViewById(R.id.phone)), Linkify.PHONE_NUMBERS);


            email = findViewById(R.id.email);
            if (infoSent.getEmail() == null) {
                email.setText("No data provided");
            } else {
                email.setText(infoSent.getEmail());
                email.setLinkTextColor(Color.WHITE);
                Linkify.addLinks(((TextView) findViewById(R.id.email)), Linkify.EMAIL_ADDRESSES);
            }
            website = findViewById(R.id.website);
            if (infoSent.getWebsite() == null) {
                website.setText("No data provided");
            } else {
                website.setText(infoSent.getWebsite());
                website.setLinkTextColor(Color.WHITE);
                Linkify.addLinks(((TextView) findViewById(R.id.website)), Linkify.WEB_URLS);
            }


            gPlusButton = findViewById(R.id.googleplus);
            fbButton = findViewById(R.id.facebook);
            YouTubeButton = findViewById(R.id.youtube);
            TwitterButton = findViewById(R.id.twitter);

            hashMapChannels = infoSent.getChannels();

            try {
                if (hashMapChannels.get("GooglePlus").equals("90000")) {
                    gPlusButton.setVisibility(View.INVISIBLE);
                }

                if (hashMapChannels.get("Facebook").equals("90000")) {
                    fbButton.setVisibility(View.INVISIBLE);
                }

                if (hashMapChannels.get("Twitter").equals("90000")) {
                    TwitterButton.setVisibility(View.INVISIBLE);
                }

                if (hashMapChannels.get("YouTube").equals("90000")) {
                    YouTubeButton.setVisibility(View.INVISIBLE);
                }
            }catch (Exception e){

            }

                int result = 0;
                result = networkCheckOnAddButton(this);
                if (result == 1) {
                    view = findViewById(R.id.imageSmall);
                    view.setImageResource(R.drawable.placeholder);
                } else {
                    if (!infoSent.getPhotourls().equals("NoPhoto")) {
                        view = findViewById(R.id.imageSmall);
                        loadImage(infoSent.getPhotourls());
                    } else {
                        view = findViewById(R.id.imageSmall);
                        view.setImageResource(R.drawable.missingimage);
                    }
                }
                view.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View view) {
                        openPhotoActivity(view);

                    }

                });
            }
        }


        @Override
        protected void onResume () {
            super.onResume();
        }


        @Override
        protected void onPause () {
            super.onPause();
        }

    public void openPhotoActivity(View view) {
        Intent intent = new Intent(PersonActivity.this, PhotoActivity.class);
        intent.putExtra("infoSent", infoSent);
        intent.putExtra("location", add);
        startActivityForResult(intent, NEW);
    }

    public int networkCheckOnAddButton(Context context) {

        int result = 0;
        ConnectivityManager connectivityManager =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnectedOrConnecting()) {
        } else {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("No Network Connection");
            builder.setIcon(ic_dialog_alert);
            builder.setMessage("Data cannot be accesses/loaded without an Internet connection");
            AlertDialog dialog = builder.create();
            dialog.show();
            result = 1;

        }
        return result;
    }

    public void facebookClicked(View v) {
        String FACEBOOK_URL = "https://www.facebook.com/" + infoSent.getChannels().get("Facebook");
        String urlToUse;

        PackageManager packageManager = getPackageManager();
        try {
            int versionCode = packageManager.getPackageInfo("com.facebook.katana", 0).versionCode;
            if (versionCode >= 3002850) {
                urlToUse = "fb://facewebmodal/f?href=" + FACEBOOK_URL;

            } else {
                urlToUse = "fb://page/" + infoSent.getChannels().get("Facebook");
            }
        } catch (PackageManager.NameNotFoundException e) {

            urlToUse = FACEBOOK_URL;
        }
        Intent facebookIntent = new Intent(Intent.ACTION_VIEW);
        facebookIntent.setData(Uri.parse(urlToUse));
        startActivity(facebookIntent);

    }

    public void twitterClicked(View v) {
        Intent intent = null;
        String name = infoSent.getChannels().get("Twitter");
        try {
            getPackageManager().getPackageInfo("com.twitter.android", 0);
            intent = new Intent(Intent.ACTION_VIEW, Uri.parse("twitter://user?screen_name=" + name));
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        } catch (Exception e) {
            intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://twitter.com/" + name));
        }
        startActivity(intent);
    }

    public void googlePlusClicked(View v) {
        String name = infoSent.getChannels().get("GooglePlus");
        Intent intent = null;
        try {
            intent = new Intent(Intent.ACTION_VIEW);
            intent.setClassName("com.google.android.apps.plus", "com.google.android.apps.plus.phone.UrlGatewayActivity");
            intent.putExtra("customAppUri", name);
            startActivity(intent);
        } catch (ActivityNotFoundException e) {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://plus.google.com/" + name)));
        }
    }

    public void youTubeClicked(View v) {
        String name = infoSent.getChannels().get("YouTube");
        Intent intent = null;
        try {
            intent = new Intent(Intent.ACTION_VIEW);
            intent.setPackage("com.google.android.youtube");
            intent.setData(Uri.parse("https://www.youtube.com/" + name));
            startActivity(intent);
        } catch (ActivityNotFoundException e) {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.youtube.com/" + name)));
        }
    }


    private void loadImage(final String imageURL) {


        Picasso picasso = new Picasso.Builder(this)
                .listener(new Picasso.Listener() {
                    @Override
                    public void onImageLoadFailed(Picasso picasso, Uri uri, Exception exception) {
                        Log.d(TAG, "onImageLoadFailed: ");
                        picasso.load(R.drawable.brokenimage)
                                .into(view);
                    }
                })
                .build();

        picasso.load(imageURL)
                .error(R.drawable.brokenimage)
                .placeholder(R.drawable.placeholder)
                .into(view);
    }
}