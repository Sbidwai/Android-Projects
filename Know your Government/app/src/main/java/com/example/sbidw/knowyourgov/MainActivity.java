package com.example.sbidw.knowyourgov;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.provider.Settings;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.InputFilter;
import android.text.InputType;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.StringTokenizer;

import static android.R.drawable.ic_dialog_alert;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, View.OnLongClickListener {

    private ArrayList<Person> personList = new ArrayList<>();
    private RecyclerView recyclerView;
    private OfficialAdapter officialAdapter;
    private Person person;
    String zipCode = "";
    private static final String TAG = "MainActivity";
    private static final int NEW = 1;
    private Locator locator;
    private String location = null;
    Person person1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        recyclerView = findViewById(R.id.recylerView);
        officialAdapter = new OfficialAdapter(personList, this);
        recyclerView.setAdapter(officialAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        recyclerView.setHasFixedSize(true);

        int result = 0;
        result = networkCheckOnAddButton(this);

        if (result != 1) {
            locator = new Locator(this);
        }

    }

    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_official, menu);
        return true;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public boolean onOptionsItemSelected(MenuItem menuItem) {

        switch (menuItem.getItemId()) {
            case R.id.menuHelp: {
                Intent intent = new Intent(MainActivity.this, About.class);
                startActivityForResult(intent, NEW);
                return true;
            }

            case R.id.menuSearch: {
                loadCivicDownloader();
            }
            default:
                return super.onOptionsItemSelected(menuItem);
        }
    }

    public int networkCheckOnAddButton(Context context) {

        int result = 0;
        ConnectivityManager connectivityManager =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnectedOrConnecting()) {

        } else {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            Intent intent = getIntent();
            builder.setTitle("No Network Connection");
            builder.setIcon(ic_dialog_alert);
            builder.setMessage("Data cannot be accesses/loaded without an Internet connection");
            AlertDialog dialog = builder.create();
            dialog.show();
            result = 1;
        }
        return result;
    }

    @Override
    public void onClick(View v) {
        int position = recyclerView.getChildLayoutPosition(v);
        person1 = personList.get(position);
        Intent intent = new Intent(MainActivity.this, PersonActivity.class);
        intent.putExtra("infoSent", person1);
        intent.putExtra("officialPosition", position);
        intent.putExtra("location", location);
        startActivityForResult(intent, NEW);
    }

    @Override
    public boolean onLongClick(View v) {
        Toast.makeText(this, " Long View has been clicked", Toast.LENGTH_LONG).show();
        return false;
    }


    public void setData(double lat, double lon) {

        Log.d(TAG, "setData: Lat: " + lat + ", Lon: " + lon);
        String address = doAddress(lat, lon);
        StringTokenizer stringTokenizer = new StringTokenizer(address, " ");
        String zipCodeAuto = null;
        while (stringTokenizer.hasMoreTokens()) {
            zipCodeAuto = stringTokenizer.nextToken();
        }
        ((TextView) findViewById(R.id.postalAddress)).setText(address);
        person = new Person();
        person.setZipcode(zipCodeAuto);
        new CivicInfoDownloader(MainActivity.this).execute(person);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        Log.d(TAG, "onRequestPermissionsResult: CALL: " + permissions.length);
        Log.d(TAG, "onRequestPermissionsResult: PERM RESULT RECEIVED");
        if (requestCode == 5) {
            Log.d(TAG, "onRequestPermissionsResult: permissions.length: " + permissions.length);
            for (int i = 0; i < permissions.length; i++) {
                if (permissions[i].equals(Manifest.permission.ACCESS_FINE_LOCATION)) {
                    if (grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                        Log.d(TAG, "onRequestPermissionsResult: HAS PERM");
                        locator.setUpLocationManager();
                        locator.determineLocation();
                    } else {
                        Toast.makeText(this, "Location permission was denied - cannot determine address", Toast.LENGTH_LONG).show();
                        Log.d(TAG, "onRequestPermissionsResult: NO PERM");
                    }
                }
            }
        }
        Log.d(TAG, "onRequestPermissionsResult: Exiting onRequestPermissionsResult");
    }


    private String doAddress(double latitude, double longitude) {
        Log.d(TAG, "doAddress: Lat: " + latitude + ", Lon: " + longitude);
        List<Address> addresses = null;
        for (int times = 0; times < 3; times++) {
            Geocoder geocoder = new Geocoder(this, Locale.getDefault());
            try {
                Log.d(TAG, "doAddress: Getting address now");
                addresses = geocoder.getFromLocation(latitude, longitude, 1);
                StringBuilder stringBuilder = new StringBuilder();
                for (Address address : addresses) {
                    Log.d(TAG, "doLocation: " + address);

                    for (int i = 0; i < 1; i++)
                        stringBuilder.append(address.getAddressLine(1));
                }
                return stringBuilder.toString();
            } catch (IOException e) {
                Log.d(TAG, "doAddress: " + e.getMessage());
                return null;
            }
        }
        return null;
    }

    public void noLocationAvailable() {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Enable Location");
        builder.setMessage("Enable location services for a precise search?");
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(intent);
                }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }


    @Override
    protected void onDestroy() {
        locator.shutdown();
        super.onDestroy();
    }

    public void loadCivicDownloader() {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        //builder.setTitle("Search Location");
        builder.setMessage("Enter a City, State or a Zip Code:");
        final EditText zipcode = new EditText(this);
        zipcode.setInputType(InputType.TYPE_CLASS_TEXT);
        zipcode.setFilters(new InputFilter[]{new InputFilter.AllCaps()});
        zipcode.setGravity(Gravity.CENTER);
        builder.setView(zipcode);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                person = new Person();
                MainActivity.this.zipCode = zipcode.getText().toString();
                person.setZipcode(MainActivity.this.zipCode);
                new CivicInfoDownloader(MainActivity.this).execute(person);
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }


    public void setPersonList(HashMap<String, ArrayList<Person>> hmap) {

        HashMap<String, ArrayList<Person>> objectList;

        objectList = hmap;
        personList.clear();
        for (Map.Entry<String, ArrayList<Person>> map : objectList.entrySet()) {
            location = map.getKey();
            ((TextView) findViewById(R.id.postalAddress)).setText(map.getKey());
            personList.addAll(map.getValue());
        }
        officialAdapter.notifyDataSetChanged();
    }
}
