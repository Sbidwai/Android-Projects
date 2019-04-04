package com.example.sbidw.multi_notes;

import android.os.AsyncTask;
import android.util.JsonReader;
import android.widget.Toast;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class AsyncLoader extends AsyncTask<String, Integer, String> {

    private MainActivity mainAc;
    public AsyncLoader(MainActivity mainAct) {
        mainAc = mainAct;
    }

    @Override
    protected void onPreExecute() {
        Toast.makeText(mainAc, "Showing notes ...", Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onPostExecute(String s) {
        ArrayList<Note> note = new ArrayList<>();
        mainAc.modifyData(note);
    }

    @Override
    protected String doInBackground(String... strings) {
        String jobj = null;
        List<Note> note_list;
        try {
            note_list = getNotes(mainAc);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return jobj;
    }

    public List<Note> getNotes(MainActivity mainAc) throws IOException {
        InputStream inputStream = mainAc.getApplicationContext().openFileInput("notes.json");
        JsonReader reader = new JsonReader(new InputStreamReader(inputStream, "UTF-8"));
        try {
            return getArray(reader);
        } finally {
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
            String name = reader.nextName();
            if (name.equals("ID")) {
                note.setID(Integer.parseInt(reader.nextString()));
            } else if (name.equals("Title")) {
                note.setMtitle(reader.nextString());
            } else if (name.equals("Time")) {
                note.setMdateTime(reader.nextString());
            } else if (name.equals("Content")) {
                note.setMcontent(reader.nextString());

            } else {
                reader.skipValue();
            }
        }
        reader.endObject();
        if (note.getID() == 999) {
            return null;
        }
        return note;
    }
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
                    note.setMdateTime(reader.nextString());
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
        } catch (IOException e) {
            e.printStackTrace();
        }
        return note;
    }

}
