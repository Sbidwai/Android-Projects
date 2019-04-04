package com.sbidw.newsgateway;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;

import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private DrawerLayout drawerLayout;
    private ListView listView;
    private ActionBarDrawerToggle actionBarDrawerToggle;
    private boolean isServiceRunning = false;
    static final String ACTION_MSG_TO_SERVICE = "ACTION_MSG_TO_SERVICE";
    static final String ACTION_NEWS_STORY = "ACTION_NEWS_STORY";
    static final String ARTICLE_LIST = "ARTICLE_LIST";
    static final String SOURCE_ID = "SOURCE_ID";
    private ArrayList<String> sourceList = new ArrayList <String>();
    private ArrayList<String> categoryList = new ArrayList <String>();
    private ArrayList<NewsSource> arrayList = new ArrayList <NewsSource>();
    private ArrayList<Article> articles = new ArrayList <Article>();
    private HashMap<String, NewsSource> sourceHashMap = new HashMap<>();
    private Menu menu;
    private NewsReceiver newsReceiver;
    private String string;
    private ArrayAdapter arrayAdapter;
    private PageAdapter pageAdapter;
    private List <Fragment> fragmentList;
    private ViewPager viewPager;
    private boolean aBoolean;
    private int srcPtr;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);


        if(!isServiceRunning &&  savedInstanceState == null) {
            Intent intent = new Intent(MainActivity.this, NewsService.class);
            startService(intent);
            isServiceRunning = true;
        }

        newsReceiver = new NewsReceiver();
        IntentFilter intentFilter = new IntentFilter(MainActivity.ACTION_NEWS_STORY);
        registerReceiver(newsReceiver, intentFilter);

        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        listView = (ListView) findViewById(R.id.left_drawer);


        listView.setOnItemClickListener(
                new ListView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        viewPager.setBackgroundResource(0);
                        srcPtr = position;
                        selectItem(position);
                    }
                }
        );

        actionBarDrawerToggle = new ActionBarDrawerToggle(
                this,
                drawerLayout,
                R.string.drawer_open,
                R.string.drawer_close
        );

        arrayAdapter = new ArrayAdapter<>(this, R.layout.list_element, sourceList);
        listView.setAdapter(arrayAdapter);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        fragmentList = new ArrayList<>();

        pageAdapter = new PageAdapter(getSupportFragmentManager());
        viewPager = findViewById(R.id.viewPager);
        viewPager.setAdapter(pageAdapter);

        if (sourceHashMap.isEmpty() && savedInstanceState == null )
            new NewsSourceDownloader(this, "").execute();

    }


    @Override
    public void onConfigurationChanged(Configuration configuration) {
        super.onConfigurationChanged(configuration);
        actionBarDrawerToggle.onConfigurationChanged(configuration);
    }

    public boolean onOptionsItemSelected(MenuItem menuItem) {

        if (actionBarDrawerToggle.onOptionsItemSelected(menuItem)) {
            return true;
        }

        new NewsSourceDownloader(this, menuItem.getTitle().toString()).execute();
        drawerLayout.openDrawer(listView);
        return super.onOptionsItemSelected(menuItem);
    }

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        actionBarDrawerToggle.syncState();
    }

    private void selectItem(int position) {
        string = sourceList.get(position);
        Intent intent = new Intent(MainActivity.ACTION_MSG_TO_SERVICE);
        intent.putExtra(SOURCE_ID, string);
        sendBroadcast(intent);
        drawerLayout.closeDrawer(listView);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.action_menu, menu);
        this.menu =menu;
        if(aBoolean){
            this.menu.add("All");
            for (String s : categoryList)
                this.menu.add(s);
        }
        return true;
    }

    public void setSources(ArrayList<NewsSource> newsSourceList, ArrayList<String> categoryList)
    {
        sourceHashMap.clear();
        this.sourceList.clear();
        arrayList.clear();
        arrayList.addAll(newsSourceList);

        for(int i = 0; i< newsSourceList.size(); i++){
            this.sourceList.add(newsSourceList.get(i).getName());
            sourceHashMap.put(newsSourceList.get(i).getName(), (NewsSource) newsSourceList.get(i));
        }

        if(!menu.hasVisibleItems()) {
            this.categoryList.clear();
            this.categoryList =categoryList;
            menu.add("All");
            Collections.sort(categoryList);
            for (String s : categoryList)
                menu.add(s);
        }

        arrayAdapter.notifyDataSetChanged();

    }
    private void reDoContents(ArrayList<Article> articleArrayList) {

        setTitle(string);
        for (int i = 0; i < pageAdapter.getCount(); i++)
            pageAdapter.notifyChangeInPosition(i);

        fragmentList.clear();

        for (int i = 0; i < articleArrayList.size(); i++) {
            Article article = articleArrayList.get(i);

            fragmentList.add(ArticleContent.newInstance(articleArrayList.get(i), i, articleArrayList.size()));
        }

        pageAdapter.notifyDataSetChanged();
        viewPager.setCurrentItem(0);
        this.articles = articleArrayList;
    }

    @Override
    protected void onDestroy() {
        unregisterReceiver(newsReceiver);
        Intent intent = new Intent(MainActivity.this, NewsReceiver.class);
        stopService(intent);
        super.onDestroy();
    }
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        RestoreInfo restoreInfo = new RestoreInfo();
        restoreInfo.setStringArrayList(categoryList);
        restoreInfo.setNewsSourceArrayList(arrayList);
        restoreInfo.setCurr_article(viewPager.getCurrentItem());
        restoreInfo.setCurr_source(srcPtr);
        restoreInfo.setArticleArrayList(articles);
        outState.putSerializable("state", restoreInfo);
        super.onSaveInstanceState(outState);
    }
    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        RestoreInfo restoreInfo1 = (RestoreInfo)savedInstanceState.getSerializable("state");
        aBoolean = true;
        articles = restoreInfo1.getArticleArrayList();
        categoryList = restoreInfo1.getStringArrayList();
        arrayList = restoreInfo1.getNewsSourceArrayList();
        for(int i = 0; i< arrayList.size(); i++){
            sourceList.add(arrayList.get(i).getName());
            sourceHashMap.put(arrayList.get(i).getName(), (NewsSource) arrayList.get(i));
        }
        listView.clearChoices();
        arrayAdapter.notifyDataSetChanged();
        listView.setOnItemClickListener(

                new ListView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        viewPager.setBackgroundResource(0);
                        srcPtr = position;
                        selectItem(position);

                    }
                }
        );
        setTitle("News Gateway");

    }
    class NewsReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            switch (intent.getAction()) {
                case ACTION_NEWS_STORY:
                    ArrayList<Article> articleArrayList;
                    if (intent.hasExtra(ARTICLE_LIST)) {
                        articleArrayList = (ArrayList <Article>) intent.getSerializableExtra(ARTICLE_LIST);
                        reDoContents(articleArrayList);
                    }
                    break;
            }
        }
    }
    private class PageAdapter extends FragmentPagerAdapter {
        private long id = 0;


        public PageAdapter(FragmentManager fragmentManager) {
            super(fragmentManager);
        }

        @Override
        public int getItemPosition(@NonNull Object object) {
            return POSITION_NONE;
        }

        @Override
        public Fragment getItem(int position) {
            return fragmentList.get(position);
        }

        @Override
        public int getCount() {
            return fragmentList.size();
        }

        @Override
        public long getItemId(int position) {
            return id + position;
        }

        public void notifyChangeInPosition(int n) {
            id += getCount() + n;
        }
    }
}

