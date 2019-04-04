package com.example.sbidw.multi_notes;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.JsonReader;
import android.util.JsonWriter;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, View.OnLongClickListener{
    private RecyclerView recycler;
    public List<Note> notes = new ArrayList<>();
    private NoteAdapter mAdapter;
    private int NoteSize = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        recycler = findViewById(R.id.recycle);

        mAdapter = new NoteAdapter(notes, this);
        recycler.setAdapter(mAdapter);
        recycler.setLayoutManager(new LinearLayoutManager(this));
        new AsyncLoader(this).execute();
        notes.clear();
        try {
            notes.addAll(getNotes(this));
        }
        catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        Note note = fetchFile(this);
        if (note.getID() != 999) {
            int flag = 0;
            for (int i = 0; i < notes.size(); i++) {
                if (note.getID() == notes.get(i).getID()) {
                    notes.get(i).setMtitle(note.getMtitle());
                    notes.get(i).setMdateTime(note.getMdateTime());
                    notes.get(i).setMcontent(note.getMcontent());
                    flag = 1;
                    break;
                }
            }
            if (flag == 0) {
                if (note.getID() != 0) {
                    notes.add(0, note);
                }
            }
            setNotes(notes, this);
        }
        recycler.setAdapter(mAdapter);
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.options_menu1, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case R.id.info:
                Intent intent1 = new Intent(MainActivity.this, Info.class );
                startActivity(intent1);
                return true;
            case R.id.add:
                NoteSize++;
                Note addNote = new Note(NoteSize);
                Intent intent = new Intent(MainActivity.this, EditNoteActivity.class );
                intent.putExtra(Note.class.getName(), addNote);
                intent.putExtra("size", NoteSize);
                startActivity(intent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }

    }
    @Override
    public void onClick(View v) {
        int NoteNo = recycler.getChildLayoutPosition(v);
        Note num = notes.get(NoteNo);
        notes.remove(NoteNo);
        notes.add(0, num);
        Intent intent = new Intent(MainActivity.this, EditNoteActivity.class);
        intent.putExtra(Note.class.getName(), num);
        startActivity(intent);

    }

    @Override
    public boolean onLongClick(final View v) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this).setTitle("Delete").setMessage("Delete Note?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        int NoteNo = recycler.getChildLayoutPosition(v);
                        Toast.makeText(v.getContext(), "note is deleted", Toast.LENGTH_SHORT).show();
                        notes.remove(NoteNo);
                        setNotes(notes, MainActivity.this);
                        recycler.setAdapter(mAdapter);
                    }
                })
                .setNegativeButton("No", null).setCancelable(false);
            builder.show();
        return true;
    }
    public void modifyData(ArrayList<Note> note) {

        recycler.setAdapter(mAdapter);
    }

    private List<Note> getNotes(MainActivity mainActivity) throws IOException {
        InputStream inputStream = mainActivity.getApplicationContext().openFileInput("notes.json");
        JsonReader reader = new JsonReader(new InputStreamReader(inputStream, "UTF-8"));
        try {
            return getArray(reader);
        }
        finally {
            reader.close();
        }
    }

    private List<Note> getArray(JsonReader reader) throws IOException {
        List<Note> msgArray = new ArrayList<Note>();
        reader.beginArray();
        while (reader.hasNext()) {
            msgArray.add(get_Msg(reader));
        }
        reader.endArray();
        return msgArray;
    }

    private Note get_Msg(JsonReader reader) throws IOException {
        Note note = new Note(999);
        reader.beginObject();
        while (reader.hasNext()) {
            String name =reader.nextName();
            if (name.equals("ID")) {
                note.setID(Integer.parseInt(reader.nextString()));
            }
            else if(name.equals("Title")) {
                note.setMtitle(reader.nextString());
            }
            else if(name.equals("Time")) {
                note.setMdateTime(reader.nextString());
            }
            else if (name.equals("Content")) {
                note.setMcontent(reader.nextString());

            }
            else {
                reader.skipValue();
            }
        }
        reader.endObject();
        if(note.getID() == 999) {
            return null;
        }
        return note;
    }

    private Note fetchFile(MainActivity mainActivity) {
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

    }
     catch (FileNotFoundException e) {
        Toast.makeText(mainActivity,"Notes not found", Toast.LENGTH_SHORT).show();
    } catch (Exception e) {
        e.printStackTrace();
    }
        return note;
    }
    private void setNotes(List<Note> notes, MainActivity mainActivity) {
    try {
        FileOutputStream fileOutputStream = mainActivity.getApplicationContext().openFileOutput("notes.json", Context.MODE_PRIVATE);
        JsonWriter writer = new JsonWriter(new OutputStreamWriter(fileOutputStream, "UTF-8"));
        writer.setIndent("  ");
        setArray(writer, notes);
        writer.close();
    }catch (Exception e) {
        e.getStackTrace();
    }
    }

    private void setArray(JsonWriter writer, List<Note> notes) throws IOException {
        writer.beginArray();
        for (Note note :notes) {
            setMsg(writer, note);
        }
        writer.endArray();
    }

    private void setMsg(JsonWriter writer, Note note) throws IOException {
        writer.beginObject();
        writer.name("ID").value(note.getID());
        writer.name("Title").value(note.getMtitle());
        writer.name("Time").value(note.getMdateTime());
        writer.name("Content").value(note.getMcontent());
        writer.endObject();
    }
    }


