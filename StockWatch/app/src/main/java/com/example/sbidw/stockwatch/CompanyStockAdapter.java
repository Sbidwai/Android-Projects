package com.example.sbidw.stockwatch;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import java.util.List;

public class CompanyStockAdapter extends RecyclerView.Adapter<StockHolder>
{
    private List<CompanyStock> companyStockList;
    private MainActivity mainAct;

    public CompanyStockAdapter(List<CompanyStock> empList, MainActivity ma)
    {
        this.companyStockList = empList;
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
    public void onBindViewHolder(StockHolder holder, int i)
    {
        CompanyStock companyStock = companyStockList.get(i);
        holder.symbol.setText(companyStock.getSymbol());
        holder.name.setText(companyStock.getName());
    }

    @Override
    public int getItemCount()
    {    return companyStockList.size();    }
}
