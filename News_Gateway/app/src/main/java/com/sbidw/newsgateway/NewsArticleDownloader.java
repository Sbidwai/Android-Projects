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
public class NewsArticleDownloader extends AsyncTask<String, Integer, String> {
    private static final String TAG = "NewsArticleDownloader";
    private String id;
    private NewsService newsService;
    private String API_KEY ="05937da4fd6e4af6a401d1dea755e201";
    private String ARTICLE_QUERY_1 = "https://newsapi.org/v2/top-headlines?sources=";
    private String ARTICLE_QUERY_2 = "&apiKey="+API_KEY;
    private Uri.Builder builderURL = null;
    private StringBuilder stringBuilder;
    private boolean ifDataFound =false;
    boolean ifNoDataFound =true;
    private ArrayList<Article> arrayList = new ArrayList <Article>();

    public NewsArticleDownloader(NewsService newsService, String id){
        this.id = id;
        this.newsService = newsService;
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
        newsService.ArticlesSetting(arrayList);
    }

    @Override
    protected String doInBackground(String... strings) {
        String query ="";

        query = ARTICLE_QUERY_1+ id +ARTICLE_QUERY_2;

        builderURL = Uri.parse(query).buildUpon();
        connectToAPI();
        if(!ifNoDataFound) {
            parseJSON(stringBuilder.toString());
        }
        return null;
    }
    public void connectToAPI() {

        String urlToUse = builderURL.build().toString();
        stringBuilder = new StringBuilder();
        try {
            URL url = new URL(urlToUse);

            HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
            if(httpURLConnection.getResponseCode() == HttpURLConnection.HTTP_NOT_FOUND)
            {
                ifDataFound =true;
            }
            else {
                httpURLConnection.setRequestMethod("GET");
                InputStream inputStream = httpURLConnection.getInputStream();
                BufferedReader bufferedReader = new BufferedReader((new InputStreamReader(inputStream)));

                String line=null;
                while ((line = bufferedReader.readLine()) != null) {
                    stringBuilder.append(line).append('\n');
                }
                ifNoDataFound =false;

            }
        }
        catch(FileNotFoundException fe){
            Log.d(TAG, "FileNotFoundException ");
        }
        catch (Exception e) {
            Log.d(TAG, "Exception doInBackground: " + e.getMessage());
        }
    }
    private void parseJSON(String s) {
        try{
            if(!ifDataFound){
                JSONObject jsonObject = new JSONObject(s);
                JSONArray articles = jsonObject.getJSONArray("articles");
                for(int i=0;i<articles.length();i++){
                    JSONObject jsonObject1 = (JSONObject) articles.get(i);
                    Article article = new Article();
                    article.setAuthor(jsonObject1.getString("author"));
                    article.setDescription(jsonObject1.getString("description"));
                    article.setPublishedAt(jsonObject1.getString("publishedAt"));
                    article.setTitle(jsonObject1.getString("title"));
                    article.setUrlToImage(jsonObject1.getString("urlToImage"));
                    article.setUrl(jsonObject1.getString("url"));
                    arrayList.add(article);
                }
            }
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }
}