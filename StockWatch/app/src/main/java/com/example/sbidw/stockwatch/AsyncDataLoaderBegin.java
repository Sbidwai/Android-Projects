package com.example.sbidw.stockwatch;

import android.net.Uri;
import android.os.AsyncTask;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;


public class AsyncDataLoaderBegin extends AsyncTask<String,Void,String> {

    private static final String TAG ="" ;
    private MainActivity mainActivity;
    private int count;
    CompanyStock companyStock = new CompanyStock();


    private static String AllStocks = "https://api.iextrading.com/1.0/stock/";

    public AsyncDataLoaderBegin(MainActivity ma , CompanyStock companyStock)
    {
        mainActivity = ma;
        this.companyStock = companyStock;
    }

    @Override
    protected void onPreExecute()
    {
    }

    @Override
    protected void onPostExecute(String sb)
    {
        if (sb == null)
        {
            mainActivity.noDataFound();

        }
        else
        {
            String clean = sb.replaceAll("//", "");
            System.out.print("Sb data after clean:"+clean);
            TempStocks tempStocks = parseJSON(clean);
            mainActivity.SelectSymbolTemp1(tempStocks);
        }
    }

    @Override
    protected String doInBackground(String... params) {


        Uri symbolUri = Uri.parse(AllStocks + companyStock.getSymbol()+ "/quote");
        String urlToUse = symbolUri.toString();
        StringBuilder sb = new StringBuilder();
        try
        {
            URL url = new URL(urlToUse);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            InputStream is = conn.getInputStream();
            BufferedReader reader = new BufferedReader((new InputStreamReader(is)));
            String line;


            while ((line = reader.readLine()) != null)
            {
                sb.append(line);
            }
        }
        catch (Exception e)
        {
            return null;
        }
        return sb.toString();
    }


    private TempStocks parseJSON(String s)
    {
        try
        {
            JSONObject jObjMain = new JSONObject(s);
            count = jObjMain.length();
            System.out.println("New Count=" + count);
            String name = jObjMain.getString("companyName");
            String symbol = jObjMain.getString("symbol");
            String latestPrice = jObjMain.getString("latestPrice");
            String change1 = jObjMain.getString("change");
            String change = jObjMain.getString("change");
            String changePercent = jObjMain.getString("changePercent");
            TempStocks tempStocks = new TempStocks(symbol, name, latestPrice, change1, change, changePercent);
            return tempStocks;
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return null;
    }

}
