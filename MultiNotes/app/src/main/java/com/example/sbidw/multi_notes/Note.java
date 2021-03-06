package com.example.sbidw.multi_notes;

import android.annotation.TargetApi;

import java.io.Serializable;
import java.text.DateFormat;
import java.util.Calendar;
@TargetApi(24)
public class Note implements Serializable {
    private String mdateTime;
    private String mtitle;
    private String mcontent;
    private int ID;


    public Note(int id) {

        this.ID= id;
        DateFormat dateFormat = new java.text.SimpleDateFormat("EEE MMM d, h:mm a");
        String d = dateFormat.format(Calendar.getInstance().getTime());
        this.mdateTime = "";
        this.mtitle = "";
        this.mcontent = "";

    }
    public Note() {}


    public String getMdateTime() {
        return mdateTime;
    }

    public String getMtitle() {
        return mtitle;
    }

    public String getMcontent() {
        return mcontent;
    }

    public int getID() {
        return ID;
    }

    public void setMdateTime(String mdateTime) {
        this.mdateTime = mdateTime;
    }

    public void setMtitle(String mtitle) {
        this.mtitle = mtitle;
    }

    public void setMcontent(String mcontent) {
        this.mcontent = mcontent;
    }

    public void setID(int ID) {
        this.ID = ID;
    }

    public String toString(){
        return ID + mdateTime + ": " + mtitle + mcontent;
    }

}

