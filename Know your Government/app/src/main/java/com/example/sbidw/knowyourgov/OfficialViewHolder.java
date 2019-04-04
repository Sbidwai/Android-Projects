package com.example.sbidw.knowyourgov;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;



public class OfficialViewHolder extends RecyclerView.ViewHolder {

    public TextView officialDesignation;
    public TextView officialName;


    public OfficialViewHolder(View view) {
        super(view);

        officialDesignation = view.findViewById(R.id.personDesignation);
        officialName = view.findViewById(R.id.personName);


    }
}
