package com.sbidw.newsgateway;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import java.util.ArrayList;

public class NewsService extends Service {

    private static final String TAG = "NewsService";
    private boolean isworking = true;
    private ReceiveServices receiveServices;
    private ArrayList<Article> articleArrayList = new ArrayList <Article>();

    public NewsService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        receiveServices = new ReceiveServices();
        IntentFilter intentFilter = new IntentFilter(MainActivity.ACTION_MSG_TO_SERVICE);
        registerReceiver(receiveServices, intentFilter);

        new Thread(new Runnable() {
            @Override
            public void run() {

                while (isworking) {
                    while(articleArrayList.isEmpty()){
                        try {
                            Thread.sleep(250);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                    Intent intent = new Intent();
                    intent.setAction(MainActivity.ACTION_NEWS_STORY);
                    intent.putExtra(MainActivity.ARTICLE_LIST, articleArrayList);
                    sendBroadcast(intent);
                    articleArrayList.clear();
                }
                Log.i(TAG, "NewsService was properly stopped");
            }
        }).start();

        return Service.START_STICKY;
    }

    @Override
    public void onDestroy() {
        isworking = false;
        Toast.makeText(this, "NewsService Stopped", Toast.LENGTH_SHORT).show();
    }

    public void ArticlesSetting(ArrayList<Article> articleArrayList){
        this.articleArrayList.clear();
        this.articleArrayList.addAll(articleArrayList);

    }

    class ReceiveServices extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {

            switch (intent.getAction()) {
                case MainActivity.ACTION_MSG_TO_SERVICE:
                    String id ="";
                    String temp="";
                    if (intent.hasExtra(MainActivity.SOURCE_ID)) {
                        id = intent.getStringExtra(MainActivity.SOURCE_ID);
                        temp=id.replaceAll(" ","-");
                    }

                    new NewsArticleDownloader(NewsService.this, temp).execute();
                    break;
            }

        }
    }
}


