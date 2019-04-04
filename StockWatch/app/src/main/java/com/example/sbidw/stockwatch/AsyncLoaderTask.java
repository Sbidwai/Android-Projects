package com.example.sbidw.stockwatch;
import android.net.Uri;
import android.os.AsyncTask;
import org.json.JSONArray;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;


public class AsyncLoaderTask extends AsyncTask<String, Void, String> {

    private MainActivity mainActivity;
    private String search;

    private static String stockSymbols = "https://api.iextrading.com/1.0/ref-data/symbols";
    public AsyncLoaderTask(MainActivity ma)
    {
        mainActivity = ma;
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
            mainActivity.noSymbolFound();

        }
        else
        {
            //ArrayList<CompanyStock> companyStockArrayList = parseJSON(sb);
            HashMap<String, String> stockString = parseJSON(sb);
            mainActivity.selectSymbol(stockString);
        }
    }

    @Override
    protected String doInBackground(String... params)
    {
        search = params[0];
        Uri symbolUri = Uri.parse(stockSymbols);

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
                sb.append(line).append('\n');
            }
        }
        catch (Exception e)
        {
            return null;
        }
        return sb.toString();
    }


    private HashMap<String, String> parseJSON(String s)
    {
        ArrayList<CompanyStock> companyStockArrayList = new ArrayList<>();
        HashMap<String, String> stockMap = new HashMap<>();
        try
        {
            JSONArray jsonObject = new JSONArray(s);
            for (int i = 0; i < jsonObject.length(); i++)
            {
                JSONObject jStock = (JSONObject) jsonObject.get(i);
                if(jStock.getString("symbol").startsWith(search))
                {
                    String name = jStock.getString("name");
                    String symbol = jStock.getString("symbol");
                    companyStockArrayList.add(
                            new CompanyStock(symbol, name));
                    stockMap.put(symbol, name);
                }
            }
            return stockMap;
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return null;
    }

}
