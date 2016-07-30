package com.ap.bindkeeper.prebill;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteStatement;
import android.support.v4.content.LocalBroadcastManager;

import java.util.ArrayList;

/**
 *
 */
public class DBHelper extends SQLiteOpenHelper {
    public static final String dataChanged = "com.ap.bindkeeper.prebill.dataChanged";

    private static final String dbname = "prebillDB";
    private static final String itemsTable = "ITEMSTABLE";
    private static final String itemNameRow = "NAME";
    private static final String itemPriceRow = "PRICE";

    private static final int version = 1;
    Context context;

    public DBHelper(Context context) {
        super(context, dbname, null, version);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE  " + itemsTable + " ( _id INTEGER PRIMARY KEY, " + itemNameRow + " TEXT , " +  itemPriceRow + " TEXT );");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public void addItem( Item item) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(itemNameRow, item.getName());
        cv.put(itemPriceRow, item.getPrice());
        db.insert(itemsTable, null, cv);

        LocalBroadcastManager.getInstance(context).sendBroadcast(new Intent(dataChanged));

    }

    public ArrayList<Item> getItems () {
        ArrayList<Item> items = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();
        Cursor c = db.rawQuery("SELECT * FROM "+ itemsTable + " ORDER BY _id desc;", null);

        while (c.moveToNext()) {
            items.add(new Item( c.getString(1), c.getFloat(2), c.getInt(0)));
        }
        db.close();
        return items;
    }

    public void delAll () {
        SQLiteDatabase db = getWritableDatabase();
        db.delete(itemsTable, null, null);
        db.close();
        LocalBroadcastManager.getInstance(context).sendBroadcast(new Intent(dataChanged));
    }


    public void updateName (int id ,String newName) {
        SQLiteDatabase db =getWritableDatabase();
        String sqlString = "UPDATE " + itemsTable + " SET " + itemNameRow + "= ? WHERE _id=? ;";
        SQLiteStatement sql = db.compileStatement(sqlString);
        sql.bindString(1, newName);
        sql.bindString(2, ""+id);
        sql.execute();
        LocalBroadcastManager.getInstance(context).sendBroadcast(new Intent(dataChanged));
        db.close();
    }

    public void updatePrice (int id ,String newPrice) {
        SQLiteDatabase db =getWritableDatabase();
        String sqlString = "UPDATE " + itemsTable + " SET " + itemPriceRow + "= ? WHERE _id=? ;";
        SQLiteStatement sql = db.compileStatement(sqlString);
        sql.bindString(1, newPrice);
        sql.bindString(2, ""+id);
        sql.execute();
        LocalBroadcastManager.getInstance(context).sendBroadcast(new Intent(dataChanged));
        db.close();
    }


    public void updateItem (int id ,String newName, String newPrice) {
        SQLiteDatabase db =getWritableDatabase();
        String sqlString = "UPDATE " + itemsTable + " SET " + itemNameRow + "= ? ," + itemPriceRow + "=?" +" WHERE _id=? ;";
        SQLiteStatement sql = db.compileStatement(sqlString);
        sql.bindString(1, newName);
        sql.bindString(2, newPrice);
        sql.bindString(3, ""+id);
        sql.execute();
        LocalBroadcastManager.getInstance(context).sendBroadcast(new Intent(dataChanged));
        db.close();
    }


    public void removeItem(int id) {
        SQLiteDatabase db =getWritableDatabase();
        db.delete(itemsTable,"_id=?", new String[] {""+id});
        LocalBroadcastManager.getInstance(context).sendBroadcast(new Intent(dataChanged));
        db.close();
    }
}
