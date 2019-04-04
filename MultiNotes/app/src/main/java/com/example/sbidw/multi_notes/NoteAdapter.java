package com.example.sbidw.multi_notes;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

public class NoteAdapter extends RecyclerView.Adapter<NoteHolder> {

    private static final String TAG = "NoteAdapter";
    private List<Note> notes;
    private MainActivity mainAct;


    public NoteAdapter(List<Note> notes, MainActivity mainActivity) {
        this.notes = notes;
        mainAct = mainActivity;
    }


    @Override
    public NoteHolder onCreateViewHolder( final ViewGroup viewGroup, int i) {
        View noteview = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.notesview, viewGroup, false);
        noteview.setOnClickListener(mainAct);
        noteview.setOnLongClickListener(mainAct);
        return new NoteHolder(noteview);
    }

    @Override
    public void onBindViewHolder( NoteHolder noteHolder, int position) {
        Note note = notes.get(position);
        noteHolder.mtitle.setText(note.getMtitle());
        noteHolder.mdateTime.setText(note.getMdateTime());
        noteHolder.mcontent.setText(String_Length(note.getMcontent(), 80));
    }

    private String String_Length(String s, int l) {
        if(!TextUtils.isEmpty(s)) {
            if(s.length() > l) {
                return s.substring(0, l)+"...";
            }
        }
        return s;
    }

    @Override
    public int getItemCount() {
        return notes.size();
    }
}
