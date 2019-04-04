package com.example.sbidw.stockwatch;

import java.io.Serializable;

public class TempStocks implements Serializable {

    private String symbol;
    private String name;
    private String value;
    private String change1;
    private String change;
    private String changepercent;


    public TempStocks(String sym, String nm, String vle, String ud, String cha, String chap)
    {
        symbol=sym;
        name=nm;
        value=vle;
        change1 =ud;
        change=cha;
        changepercent =chap;
    }

    public String getSymbol()
    {return symbol;}

    public String getName()
    {return name;}

    public String getValue()
    {return value;}

    public String getChange1()
    {return change1;}

    public String getChange()
    {return change;}

    public String getChangepercent()
    {return changepercent;}
}
