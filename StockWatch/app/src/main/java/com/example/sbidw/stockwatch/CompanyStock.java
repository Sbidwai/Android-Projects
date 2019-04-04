package com.example.sbidw.stockwatch;

import java.io.Serializable;


public class CompanyStock implements Serializable
{
    private String symbol;
    private String name;
    public CompanyStock()
    {    }

    public CompanyStock(String sym, String nm)
    {
        symbol = sym;
        name= nm;
    }

    public String getSymbol()
    {return symbol;}

    public String getName()
    {return name;}


}
