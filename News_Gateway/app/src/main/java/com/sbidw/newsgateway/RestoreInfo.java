package com.sbidw.newsgateway;

import java.io.Serializable;
import java.util.ArrayList;

public class RestoreInfo implements Serializable {
    private ArrayList<NewsSource> newsSourceArrayList = new ArrayList<NewsSource>();
    private ArrayList<Article> articleArrayList = new ArrayList <Article>();
    private ArrayList<String> stringArrayList = new ArrayList <String>();
    private int curr_source;
    private int curr_article;

    public ArrayList <NewsSource> getNewsSourceArrayList() {
        return newsSourceArrayList;
    }

    public void setNewsSourceArrayList(ArrayList <NewsSource> newsSourceArrayList) {
        this.newsSourceArrayList = newsSourceArrayList;
    }

    public ArrayList <Article> getArticleArrayList() {
        return articleArrayList;
    }

    public void setArticleArrayList(ArrayList <Article> articleArrayList) {
        this.articleArrayList = articleArrayList;
    }

    public ArrayList <String> getStringArrayList() {
        return stringArrayList;
    }

    public void setStringArrayList(ArrayList <String> stringArrayList) {
        this.stringArrayList = stringArrayList;
    }

    public int getCurr_source() {
        return curr_source;
    }

    public void setCurr_source(int curr_source) {
        this.curr_source = curr_source;
    }

    public int getCurr_article() {
        return curr_article;
    }

    public void setCurr_article(int curr_article) {
        this.curr_article = curr_article;
    }
}
