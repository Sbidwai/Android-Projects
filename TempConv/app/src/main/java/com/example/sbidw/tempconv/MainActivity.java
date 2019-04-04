package com.example.sbidw.tempconv;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import java.text.DecimalFormat;

public class MainActivity extends AppCompatActivity {
    private double result, temp;
    private RadioButton RadioButton1;
    private RadioButton RadioButton2;
    private EditText editText1;
    private TextView textView1;
    private TextView History;
    private Button button1;
    private String s;
    private String s2 = "";
    private StringBuilder sb;
    final DecimalFormat form = new DecimalFormat("0.0");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        RadioButton1 = findViewById(R.id.radioButton);
        RadioButton2 = findViewById(R.id.radioButton2);
        editText1 = findViewById(R.id.editText);
        textView1 = findViewById(R.id.textView);
        History = findViewById(R.id.textView6);
        button1 = findViewById(R.id.button);
        History.setMovementMethod(new ScrollingMovementMethod());
        sb = new StringBuilder();
        //sb.append("History \n");
        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                s = editText1.getText().toString();
                if (RadioButton1.isChecked() && !s.isEmpty()) {
                    double val = ftoc();
                    textView1.setText(form.format(val));
                    sb.append(s2);
                    sb.append("F to C: " +temp+" -> "+form.format(result) + "\n");
                    History.setText(sb.toString());
                } else if (RadioButton2.isChecked() && !s.isEmpty()) {
                    double val = ctof();
                    textView1.setText(form.format(val));
                    sb.append(s2);
                    sb.append("C to F: " +temp+" -> "+ form.format(result) + "\n");
                    History.setText(sb.toString());
                }
                else {
                    Toast.makeText(MainActivity.this, "Please enter the value", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public double ftoc() {

        temp = Double.parseDouble(s);
        result = (temp - 32.0) / 1.8;
        return result;

    }

    public double ctof() {
        //s = editText1.getText().toString();
        temp = Double.parseDouble(s);
        result = (temp * 1.8) + 32.0;
        return result;

    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {

        outState.putString("HISTORY", History.getText().toString());
        outState.putString("VALUE", textView1.getText().toString());
        outState.putString("Input", s);

        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        // Call super first
        super.onRestoreInstanceState(savedInstanceState);
        s2 = savedInstanceState.getString("HISTORY");
        History.setText(savedInstanceState.getString("HISTORY"));
        textView1.setText(savedInstanceState.getString("VALUE"));
        editText1.setText(savedInstanceState.getString("Input"));

    }
}