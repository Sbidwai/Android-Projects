package com.sbidw.newsgateway;

import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;


public class NewsSourceDownloader extends AsyncTask<String, Integer, String> {

    private static final String TAG = "NewsSourceDownloader";
    private StringBuilder stringBuilder;
    private boolean DataNotFound = false;
    boolean ifNoDataFound = true;
    private MainActivity mainActivity;
    private String category;
    private Uri.Builder builder = null;
    private ArrayList<NewsSource> newsSourceArrayList = new ArrayList <NewsSource>();
    private ArrayList<String> arrayList = new ArrayList <String>();
    private String API_KEY ="05937da4fd6e4af6a401d1dea755e201";
    private String NewsAPI;

    public NewsSourceDownloader(MainActivity mainActivity, String category){
        this.mainActivity = mainActivity;
        if(category.equalsIgnoreCase("all") || category.equalsIgnoreCase("")) {
            this.category = "";
            NewsAPI ="https://newsapi.org/v2/sources?language=en&country=us&apiKey="+API_KEY;

        }
        else
        {
            String s = "https://newsapi.org/v2/sources?country=us&category=";
            String s1 ="&apiKey="+API_KEY;
            NewsAPI = s+category+s1;
            this.category = category;
        }

    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
        for(int j = 0; j< newsSourceArrayList.size(); j++)
        {
            String temp = newsSourceArrayList.get(j).getCategory();
            if(!arrayList.contains(temp))
                arrayList.add(temp);
        }
        mainActivity.setSources(newsSourceArrayList, arrayList);
    }

    @Override
    protected String doInBackground(String... strings) {

        builder = Uri.parse(NewsAPI).buildUpon();
        connectToAPI();
        if(!ifNoDataFound) {
            parseJSON1(stringBuilder.toString());
        }
        return null;
    }


    public void connectToAPI() {

        String urlToUse = builder.build().toString();
        stringBuilder = new StringBuilder();
        try {
            URL url = new URL(urlToUse);

            HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
            if(httpURLConnection.getResponseCode() == HttpURLConnection.HTTP_NOT_FOUND)
            {
                DataNotFound = true;
            }
            else {
                httpURLConnection.setRequestMethod("GET");
                InputStream inputStream = httpURLConnection.getInputStream();
                BufferedReader bufferedReader = new BufferedReader((new InputStreamReader(inputStream)));

                String line = null;
                while ((line = bufferedReader.readLine()) != null) {
                    stringBuilder.append(line).append('\n');
                }
                ifNoDataFound = false;

            }
        }
        catch(FileNotFoundException fe){
            Log.d(TAG, "FileNotFoundException ");
        }
        catch (Exception e) {
            Log.d(TAG, "Exception doInBackground: " + e.getMessage());
        }
    }


    private void parseJSON1(String s) {
        try{
            if(!DataNotFound){
                JSONObject jsonObject = new JSONObject(s);
                JSONArray sources = jsonObject.getJSONArray("sources");
                for(int i=0;i<sources.length();i++){
                    JSONObject jsonObject1 = (JSONObject) sources.get(i);
                    NewsSource newsSource = new NewsSource();
                    newsSource.setId(jsonObject1.getString("id"));
                    newsSource.setCategory(jsonObject1.getString("category"));
                    newsSource.setName(jsonObject1.getString("name"));
                    newsSource.setUrl(jsonObject1.getString("url"));
                    newsSourceArrayList.add(newsSource);
                }
            }
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }
}
