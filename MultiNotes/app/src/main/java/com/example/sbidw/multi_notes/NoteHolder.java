package com.example.sbidw.multi_notes;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

public class NoteHolder extends RecyclerView.ViewHolder {

    public TextView mcontent;
   public TextView mtitle;
   public TextView mdateTime;

    public NoteHolder(View view) {
        super(view);
        mcontent = view.findViewById(R.id.desc);
        mtitle = view.findViewById(R.id.note_title);
        mdateTime = view.findViewById(R.id.note_date);
    }
}
