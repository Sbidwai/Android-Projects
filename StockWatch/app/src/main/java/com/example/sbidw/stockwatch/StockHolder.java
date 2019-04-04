package com.example.sbidw.stockwatch;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

public class StockHolder extends RecyclerView.ViewHolder
{
    public TextView symbol;
    public TextView name;
    public TextView value;
    public ImageView imageView;
    public TextView change;
    public TextView changepercent;

    public StockHolder(View view)
    {
        super(view);
        symbol = (TextView) view.findViewById(R.id.sSymbol);
        name = (TextView) view.findViewById(R.id.sName);
        value = (TextView) view.findViewById(R.id.sValue);
        imageView = (ImageView) view.findViewById(R.id.sUpDown);
        change = (TextView) view.findViewById(R.id.sChange);
        changepercent = (TextView) view.findViewById(R.id.sChangeP);
    }
}
