package com.example.sbidw.multi_notes;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.method.ScrollingMovementMethod;
import android.util.JsonReader;
import android.util.JsonWriter;
import android.view.Menu;
import android.view.MenuItem;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.Toast;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.StringWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;


public class EditNoteActivity extends AppCompatActivity {
    private EditText Title, Text;
    private Note note;
    private final String TAG ="EditNoteActivity";
    int i;
    private String diffTitle;
    private String diffContent;
    @Override
    protected void onCreate( Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.editlayout);
        Title = findViewById(R.id.title);
        Text = findViewById(R.id.text);
        Text.setMovementMethod(new ScrollingMovementMethod());
        Text.setTextIsSelectable(true);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        Intent intent = getIntent();
        if(intent.hasExtra(Note.class.getName())) {
            note = (Note) intent.getSerializableExtra(Note.class.getName());

        }
        }

    @Override
    protected void onResume() {
        super.onStart();
        if(note!=null) {
            diffContent = note.getMcontent();
            Title.setText(note.getMtitle());
            Title.setSelection(note.getMtitle().length());
            Text.setText(note.getMcontent());
            diffTitle = note.getMcontent();
        }
    }

    @Override
    protected void onPause() {
        note.setMtitle(Title.getText().toString());
        note.setMcontent(Text.getText().toString());
        if(note.getMtitle().equals("")) {
            Toast.makeText(getApplicationContext(), "No title, Activity not saved", Toast.LENGTH_SHORT).show();
            finish();
        }
        else {
            if(!diffContent.equals(Text.getText().toString())) {
                savenotes(note, this);
            }
        }
        super.onPause();
    }


   @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.save_bar, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.save_note:
                if(!(diffTitle.equals(Title.getText().toString())|| diffContent.equals(Text.getText().toString()))) {
                    note.setMtitle(Title.getText().toString());
                    note.setMcontent(Text.getText().toString());

                finish();
                return true;}
                else if(!diffContent.equals(Text.getText().toString())) {
                    savenotes(note,this);
                    finish();
                }
                note.setMtitle(Title.getText().toString());
                note.setMcontent(Text.getText().toString());
                if(note.getMtitle().equals("")) {
                    Toast.makeText(getApplicationContext(), "No Title, Note not saved", Toast.LENGTH_SHORT).show();
                    finish();
                }else {
                    savenotes(note,this);
                    finish();
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }

    }

    @Override
    public void onBackPressed() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(" Note not saved.."+"\n"+"Do you want to save the note?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int which) {
                        EditNoteActivity.this.onSuperBackPressed();
                        if(diffContent.equals(Text.getText().toString())) {
                            finish();
                        }
                        note.setMtitle(Title.getText().toString());
                        note.setMcontent(Text.getText().toString());
                        if(note.getMtitle().equals("")) {
                            Toast.makeText(getApplicationContext(),"No Title, Note not saved", Toast.LENGTH_SHORT).show();
                            finish();
                        }
                        else {
                            savenotes(note, EditNoteActivity.this);
                            finish();
                        }
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                           }
                });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    public void onSuperBackPressed() {
        super.onBackPressed();
    }

     void savenotes(Note note, EditNoteActivity editNoteActivity) {
    try {
        FileOutputStream fileOutputStream = editNoteActivity.getApplicationContext().openFileOutput("File.json", Context.MODE_PRIVATE);
        JsonWriter writer = new JsonWriter(new OutputStreamWriter(fileOutputStream, "UTF-8"));
        writer.setIndent("  ");
        writer.beginObject();
        DateFormat dateFormat = new SimpleDateFormat("EEE MMM d, h:mm a");
        String d = dateFormat.format(Calendar.getInstance().getTime());
        writer.name("ID").value(note.getID());
        writer.name("Time").value(d);
        writer.name("Title").value(note.getMtitle());
        writer.name("Content").value(note.getMcontent());
        writer.endObject();
        writer.close();

        StringWriter stringWriter = new StringWriter();
        writer = new JsonWriter(stringWriter);
        writer.setIndent("  ");
        writer.beginObject();
        Bundle bundle = getIntent().getExtras();
        if(bundle != null) {
            i = bundle.getInt("size");

        }
        writer.name("ID").value(i);
        writer.name("Title").value(note.getMtitle());
        writer.name(("Time")).value(d);
        writer.name("Content").value(note.getMcontent());
        writer.endObject();
        writer.close();
    }
    catch (Exception e) {
        e.getStackTrace();
    }}
         public Note fetchFile(MainActivity mainActivity) {
             Note note;

             note = new Note();
             try {
                 InputStream inputStream = mainActivity.getApplicationContext().openFileInput("File.json");
                 JsonReader reader = new JsonReader(new InputStreamReader(inputStream, "UTF-8"));
                 reader.beginObject();
                 while (reader.hasNext()) {
                     String name = reader.nextName();
                     if (name.equals("ID")) {
                         note.setID(Integer.parseInt(reader.nextString()));
                     } else if (name.equals("Title")) {
                         note.setMtitle(reader.nextString());
                     }
                     else if(name.equals("Time")) {
                         note.setMdateTime(reader.nextString());
                     }
                     else if (name.equals("Content")) {
                         note.setMcontent(reader.nextString());

                     } else {
                         reader.skipValue();
                     }
                 }
                 reader.endObject();

             }  catch (FileNotFoundException e) {
                 Toast.makeText(mainActivity,"Notes not found", Toast.LENGTH_SHORT).show();
             } catch (Exception e) {
                 e.printStackTrace();
             }
             return note;
         }

     }


