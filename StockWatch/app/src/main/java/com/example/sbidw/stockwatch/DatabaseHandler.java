package com.example.sbidw.stockwatch;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import java.util.ArrayList;

public class DatabaseHandler extends SQLiteOpenHelper {
    private static final String TAG = "DatabaseHandler";

    private static final int DATABASE_VERSION = 1;

    private static final String DATABASE_NAME = "StockAppDB";
    private static final String TABLE_NAME = "StockWatchTable";
    private static final String SYMBOL = "StockSymbol";
    private static final String COMPANY = "CompanyName";

    private static final String SQL_CREATE_TABLE =
            "CREATE TABLE " + TABLE_NAME + " (" +
                    SYMBOL + " TEXT not null unique," +
                    COMPANY + " TEXT not null)";

    private SQLiteDatabase database;

    public DatabaseHandler(Context context)
    {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        database = getWritableDatabase();

    }

    @Override
    public void onCreate(SQLiteDatabase db)
    {
        db.execSQL(SQL_CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
    {    }




    public boolean CheckIsDataAlreadyInDBorNot(String fieldValue)
    {
        String Query = "Select * FROM " + TABLE_NAME + " WHERE " + SYMBOL + " =?";
        Cursor cursor = database.rawQuery(Query, new String[] {fieldValue});
        if(cursor.getCount() <= 0)
        {
            cursor.close();
            return false;
        }
        cursor.close();
        return true;
    }

    public void addStock(CompanyStock companyStock)
    {
        ContentValues values = new ContentValues();
        values.put(SYMBOL, companyStock.getSymbol());
        values.put(COMPANY, companyStock.getName());
        database.insert(TABLE_NAME, null, values);
    }

    public void deleteStock(String symbol)
    {
        database.delete(TABLE_NAME, SYMBOL + " = ?", new String[]{symbol});
    }

    public ArrayList<CompanyStock> loadStocks()
    {
        ArrayList<CompanyStock> companyStocks = new ArrayList<>();
        Cursor cursor = database.query(
                TABLE_NAME,
                new String[]{SYMBOL, COMPANY},
                null,
                null,
                null,
                null,
                null);
        if (cursor != null) {
            cursor.moveToFirst();
            for (int i = 0; i < cursor.getCount(); i++) {
                String symbol = cursor.getString(0);
                String company = cursor.getString(1);
                companyStocks.add(new CompanyStock(symbol, company));
                cursor.moveToNext();
            }
            cursor.close();
        }

        return companyStocks;
    }
}
