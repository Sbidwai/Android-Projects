package com.example.sbidw.stockwatch;

import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;


public class TempStocksAdapter extends RecyclerView.Adapter<StockHolder>
{
    private List<TempStocks> stockList;
    private MainActivity mainAct;


    public TempStocksAdapter(List<TempStocks> empList, MainActivity ma)
    {
        this.stockList = empList;
        mainAct = ma;
    }

    @Override
    public StockHolder onCreateViewHolder(final ViewGroup parent, int viewType)
    {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.stock_list, parent, false);
        itemView.setOnClickListener(mainAct);
        itemView.setOnLongClickListener(mainAct);
        return new StockHolder(itemView);
    }

    @Override
    public void onBindViewHolder(StockHolder holder, int position)
    {
        TempStocks tempStocks = stockList.get(position);
        Double v = Double.parseDouble(tempStocks.getChange());
        if(v > 0)
        {
            holder.symbol.setText(tempStocks.getSymbol());
            holder.symbol.setTextColor(Color.parseColor("#3CE522"));
            holder.name.setText(tempStocks.getName());
            holder.name.setTextColor(Color.parseColor("#3CE522"));
            holder.value.setText(tempStocks.getValue());
            holder.value.setTextColor(Color.parseColor("#3CE522"));
            holder.imageView.setImageResource(R.drawable.ic_arrow_drop_up);
            holder.change.setText(tempStocks.getChange());
            holder.change.setTextColor(Color.parseColor("#3CE522"));
            holder.changepercent.setText("(" + tempStocks.getChangepercent() + "%" + ")");
            holder.changepercent.setTextColor(Color.parseColor("#3CE522"));
        }
        else
        {
            holder.symbol.setText(tempStocks.getSymbol());
            holder.symbol.setTextColor(Color.parseColor("#FD0504"));
            holder.name.setText(tempStocks.getName());
            holder.name.setTextColor(Color.parseColor("#FD0504"));
            holder.value.setText(tempStocks.getValue());
            holder.value.setTextColor(Color.parseColor("#FD0504"));
            holder.imageView.setImageResource(R.drawable.ic_arrow_drop_down);
            holder.change.setText(tempStocks.getChange());
            holder.change.setTextColor(Color.parseColor("#FD0504"));
            holder.changepercent.setText("(" + tempStocks.getChangepercent() + "%" + ")");
            holder.changepercent.setTextColor(Color.parseColor("#FD0504"));
        }
    }

    @Override
    public int getItemCount()
    {
        return stockList.size();
    }
}
