package com.example.sbidw.stockwatch;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.InputFilter;
import android.text.InputType;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, View.OnLongClickListener {

    private static final String TAG = "";
    private ArrayList<CompanyStock> companyStockList = new ArrayList<>();
    private  ArrayList<TempStocks> tempStocksList = new ArrayList<>();
    private RecyclerView recyclerView;
    private SwipeRefreshLayout refreshLayout;
    MenuItem item;
    private String searchSymbol;
    private String symbolSelected;
    private DatabaseHandler databaseHandler;
    private static String marketwatch = "http://www.marketwatch.com/investing/stock/";
    private CompanyStockAdapter companyStockAdapter;
    private TempStocksAdapter tempStocksAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        item = (MenuItem) findViewById(R.id.add);
        recyclerView = (RecyclerView) findViewById(R.id.recycler);
        companyStockAdapter = new CompanyStockAdapter(companyStockList, this);
        tempStocksAdapter = new TempStocksAdapter(tempStocksList, this);
        recyclerView.setAdapter(companyStockAdapter);
        recyclerView.setAdapter(tempStocksAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        refreshLayout = (SwipeRefreshLayout) findViewById(R.id.swiper);
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh()
            {
                doRefresh();
            }
        });

        databaseHandler = new DatabaseHandler(this);
        ArrayList<CompanyStock> list = databaseHandler.loadStocks();
        companyStockList.addAll(list);

        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnectedOrConnecting())
        {
            for (int i = 0; i < companyStockList.size(); i++)
            {
                CompanyStock stock = companyStockList.get(i);
                System.out.println("Inside MainActivity: Loading database:" + stock.getSymbol());
                System.out.println("Inside MainActivity: Loading database:" + stock.getName());
                DownloadStocks(stock);
            }
        }
        else
        {
            AlertDialog alertDialog = new AlertDialog.Builder(MainActivity.this).create();
            alertDialog.setTitle("No Network Connection");
            alertDialog.setMessage("Stocks Cannot Be Updated Without A Network Connection");
            alertDialog.show();
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        getMenuInflater().inflate(R.menu.add, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId())
        {
            case R.id.add:
                ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
                NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
                if (networkInfo != null && networkInfo.isConnectedOrConnecting())
                {
                    AlertDialog.Builder builder = new AlertDialog.Builder(this);
                    final EditText editText = new EditText(this);
                    editText.setInputType(InputType.TYPE_CLASS_TEXT);
                    editText.setGravity(Gravity.CENTER_HORIZONTAL);
                    builder.setView(editText);
                    editText.setFilters(new InputFilter[] {new InputFilter.AllCaps()});
                    builder.setPositiveButton("OK", new DialogInterface.OnClickListener()
                    {
                        public void onClick(DialogInterface dialog, int id)
                        {
                            String symbol = editText.getText().toString().trim().replaceAll(", ", ",");
                            searchSymbol = symbol;
                            if(symbol.equals(""))
                            {
                                AlertDialog alertDialog = new AlertDialog.Builder(MainActivity.this).create();
                                alertDialog.setTitle("Please Enter a Stock symbol");
                                alertDialog.setMessage("No Stock symbol found");
                                alertDialog.show();
                            }
                            else
                            {
                                searchSymbol(symbol);
                            }
                        }
                    });
                    builder.setNegativeButton("CANCEL", new DialogInterface.OnClickListener()
                    {
                        public void onClick(DialogInterface dialog, int id)
                        {
                        }
                    });

                    builder.setMessage("Please enter a Stock Symbol:");
                    builder.setTitle("Stock Selection");
                    AlertDialog dialog = builder.create();
                    dialog.show();

                }
                else
                {
                    AlertDialog alertDialog = new AlertDialog.Builder(MainActivity.this).create();
                    alertDialog.setTitle("No Network Connection");
                    alertDialog.setMessage("Stocks Cannot Be Added Without A Network Connection");
                    alertDialog.show();
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void searchSymbol(String sym)
    {
        AsyncLoaderTask asyncLoaderTask = new AsyncLoaderTask(this);
        asyncLoaderTask.execute(sym);
    }

    public void noSymbolFound()
    {
        AlertDialog alertDialog = new AlertDialog.Builder(MainActivity.this).create();
        alertDialog.setTitle("Symbol Not Found: " + searchSymbol);
        alertDialog.setMessage("No data for "+ searchSymbol +" symbol");
        alertDialog.show();
    }

    public void noDataFound()
    {
        AlertDialog alertDialog = new AlertDialog.Builder(MainActivity.this).create();
        alertDialog.setTitle("No Data Found: " + symbolSelected);
        alertDialog.setMessage("There is no data for "+ symbolSelected +" symbol");
        alertDialog.show();
    }


    private void doRefresh()
    {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnectedOrConnecting())
        {
            ArrayList<TempStocks> copy = (ArrayList<TempStocks>) tempStocksList.clone();
            tempStocksList.clear();
            for(int i=0;i<copy.size();i++) {
                TempStocks tempStocks = copy.get(i);
                DownloadMoreStocks(tempStocks);
            }
        }
        else
        {
            AlertDialog alertDialog = new AlertDialog.Builder(MainActivity.this).create();
            alertDialog.setTitle("No Network Connection");
            alertDialog.setMessage("Stocks Cannot Be Updated Without A Network Connection");
            alertDialog.show();
        }
        refreshLayout.setRefreshing(false);
    }


    @Override
    public void onClick(View v)
    {
        int pos = recyclerView.getChildLayoutPosition(v);
        TempStocks tempStocks = tempStocksList.get(pos);
        String symbol = tempStocks.getSymbol();
        String url = marketwatch + symbol;
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse(url));
        startActivity(intent);
    }

    @Override
    public boolean onLongClick(View v)
    {
        final int pos = recyclerView.getChildLayoutPosition(v);
        TempStocks tempStocks = tempStocksList.get(pos);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setPositiveButton("Delete", new DialogInterface.OnClickListener()
        {
            public void onClick(DialogInterface dialog, int id)
            {
                databaseHandler.deleteStock(tempStocksList.get(pos).getSymbol());
                tempStocksList.remove(pos);
                tempStocksAdapter.notifyDataSetChanged();
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener()
        {
            public void onClick(DialogInterface dialog, int id)
            {
            }
        });
        builder.setIcon(R.drawable.ic_delete_black_48dp);

        builder.setMessage("Delete Stock Symbol " + tempStocksList.get(pos).getSymbol() + "?");
        builder.setTitle("Delete Stock");
        AlertDialog dialog = builder.create();
        dialog.show();
        return true;
    }

    public void selectSymbol(final HashMap<String, String> sList)
    {
        int size = sList.size();
        Map<String, String> stockMaps = sList;
        CompanyStock companyStock = null;
        if (size == 1) {
        for(Map.Entry<String, String> entry:sList.entrySet()) {
            companyStock = new CompanyStock(entry.getKey(), entry.getValue());

        }
        DownloadData(companyStock);
        }
        else if(size==0) {
            AlertDialog alertDialog = new AlertDialog.Builder(MainActivity.this).create();
            alertDialog.setTitle("Symbol Not Found: " + searchSymbol);
            alertDialog.setMessage("Data for stock symbol"+ searchSymbol +" symbol");
            alertDialog.show();
        }
        else
            {
            final CharSequence[] charSequences = new CharSequence[size];
           int i = 0;
                for(Map.Entry<String, String> entry:sList.entrySet()) {
                    if(i<=size) {
                        companyStock = new CompanyStock(entry.getKey(), entry.getValue());
                    charSequences[i] = companyStock.getSymbol() + "-"+companyStock.getName();
                    i++;
                    }


                }
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Make a selection");

            builder.setItems(charSequences, new DialogInterface.OnClickListener()
            {
                public void onClick(DialogInterface dialog, int which)
                {
                    String dataLoad = (String) charSequences[which];
                    String[] data = dataLoad.split("-");
                    String symbol = data[0];
                    String name = data[1];
                    int i;
                    for (i = 0; i < sList.size(); i++)
                    {
                        if(charSequences[i] == dataLoad)
                            break;
                    }
                    CompanyStock temp = new CompanyStock(symbol, name);
                    DownloadData(temp);
                }
            });

            builder.setNegativeButton("Nevermind", new DialogInterface.OnClickListener()
            {
                public void onClick(DialogInterface dialog, int id)
                {   }
            });
            AlertDialog dialog = builder.create();
            dialog.show();
        }
    }



    public void selectSymbolTemp(TempStocks tempStocks)
    {
        boolean b;
        CompanyStock companyStock = new CompanyStock(tempStocks.getSymbol(),tempStocks.getName());
        b = databaseHandler.CheckIsDataAlreadyInDBorNot(companyStock.getSymbol());
        if(b == false) {
            databaseHandler.addStock(companyStock);
            tempStocksList.add(tempStocks);
            Collections.sort(tempStocksList, new Comparator<TempStocks>()
            {
                @Override
                public int compare(TempStocks lhs, TempStocks rhs)
                {
                    return lhs.getSymbol().compareTo(rhs.getSymbol());
                }
            });
            tempStocksAdapter.notifyDataSetChanged();
        }
        else
        {
            AlertDialog alertDialog = new AlertDialog.Builder(MainActivity.this).create();
            alertDialog.setIcon(R.drawable.ic_warning_black_48dp);
            alertDialog.setTitle("Duplicate Stock: " + companyStock.getSymbol() );
            alertDialog.setMessage("Stock Symbol: "+ companyStock.getSymbol() +" is already displayed");
            alertDialog.show();
        }

    }


    private void DownloadData(CompanyStock companyStock){
        String symbol = companyStock.getSymbol();
        String name = companyStock.getName();
        symbolSelected = companyStock.getSymbol();
        AsyncDataLoader asyncDataLoader = new AsyncDataLoader(this);
        asyncDataLoader.execute(symbol ,name);
    }

    private void DownloadStocks(CompanyStock companyStock)
    {
        AsyncDataLoaderBegin asyncDataLoaderBegin = new AsyncDataLoaderBegin(this,companyStock);
        asyncDataLoaderBegin.execute();
    }

    private void DownloadMoreStocks(TempStocks tempStocks)
    {
        String symbol = tempStocks.getSymbol();
        String name = tempStocks.getName();
        AsyncDataLoaderRefresh alt = new AsyncDataLoaderRefresh(this,new CompanyStock(symbol,name));
        alt.execute();
    }

    public void SelectSymbolTemp1(TempStocks tempStocks) {

        tempStocksList.add(tempStocks);
        Collections.sort(tempStocksList, new Comparator<TempStocks>() {
            @Override
            public int compare(TempStocks lhs, TempStocks rhs) {
                return lhs.getSymbol().compareTo(rhs.getSymbol());
            }
        });
        tempStocksAdapter.notifyDataSetChanged();

    }
    public void SelectSymbolTemp2(TempStocks tempStocks) {

        tempStocksList.add(tempStocks);
        Collections.sort(tempStocksList, new Comparator<TempStocks>()
        {
            @Override
            public int compare(TempStocks lhs, TempStocks rhs)
            {
                return lhs.getSymbol().compareTo(rhs.getSymbol());
            }
        });
        tempStocksAdapter.notifyDataSetChanged();
    }
}

