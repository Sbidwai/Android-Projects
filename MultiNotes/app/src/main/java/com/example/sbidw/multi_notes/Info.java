package com.example.sbidw.multi_notes;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

public class Info extends AppCompatActivity {
    TextView appname, version, Name;

    @Override
    protected void onCreate( Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.infofile);
        appname = findViewById(R.id.titleinfo);
        version = findViewById(R.id.Version);
        Name = findViewById(R.id.name1);



    }
}
