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
    public static final String billsChanged = "com.ap.bindkeeper.prebill.billsChanged";

    private static final String dbname = "prebillDB";
    private static final String itemsTable = "ITEMSTABLE";
    private static final String itemNameRow = "NAME";
    private static final String itemPriceRow = "PRICE";
    private static final String itemProductIdRow = "productId";
    private static final String itemBillId = "billId";

    private static final String productTable = "productTable";
    private static final String productID = "_id";
    private static final String productName = "name";

    private static final String billsTable = "billsTable";
    private static final String billId = "_id";
    private static final String billName = "billName";

    private static final int version = 2;
    Context context;

    public DBHelper(Context context) {
        super(context, dbname, null, version);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // TODO: update the statement, replace itemName to product_id


        db.execSQL("CREATE TABLE  " + itemsTable +
                " ( _id INTEGER PRIMARY KEY, " +
                itemProductIdRow +" INTEGER , " +
                itemPriceRow + " TEXT , " +
                itemBillId + " INTEGER , "+
                "FOREIGN KEY ('"+ itemBillId +"') REFERENCES '"+ productTable +"' ('"+ billId +"'),"+
                "FOREIGN KEY ('"+ itemProductIdRow +"') REFERENCES '"+ billsTable +"' ('"+ productID +"')  );");

        db.execSQL("CREATE TABLE " + productTable + " ( " + productID + " INTEGER PRIMARY KEY, " + productName + " TEXT  );" );
        db.execSQL("CREATE TABLE " + billsTable + " ( " + billId + " INTEGER PRIMARY KEY, " + billName + " TEXT  );" );
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        /*TODO: write the following updates
        */
        if (oldVersion < 2) {



            db.execSQL("CREATE TABLE " + productTable + " ( " + productID + " INTEGER PRIMARY KEY, " + productName + " TEXT  );" );
            db.execSQL("CREATE TABLE " + billsTable + " ( " + billId + " INTEGER PRIMARY KEY, " + billName + " TEXT  );" );
            /*
            ALTER TABLE ITEMSTABLE ADD COLUMN BILL_ID INTEGER DEFAULT 0 REFERENCES BILLSTABLE(_id);
            */
            db.execSQL("ALTER TABLE " + itemsTable + " ADD COLUMN " + itemBillId + " INTEGER DEFAULT 0 REFERENCES "+billsTable+"("+ billId + ");");
            /*
            INSERT INTO producttalbe (name)  SELECT NAME FROM ITEMSTABLE;
            */
            db.execSQL("INSERT INTO " + productTable + "(" + productName + ") SELECT "+ itemNameRow + " FROM " + itemsTable + " ;");
            /*

            ALTER TABLE ITEMSTABLE ADD COLUMN product_id INTEGER DEFAULT 0 REFERENCES producttalbe(_id);
            */
            db.execSQL("ALTER TABLE " + itemsTable + " ADD COLUMN " + itemProductIdRow + " INTEGER DEFAULT 0 REFERENCES " + productTable + "(" + productID + ");");
            /*

            UPDATE ITEMSTABLE SET product_id = (SELECT producttalbe._id FROM producttalbe WHERE ITEMSTABLE.NAME = producttalbe.name);
            */
            db.execSQL("UPDATE " + itemsTable + " SET " + itemProductIdRow + " = ( SELECT " + productTable+"."+productID + " FROM " + productTable + " WHERE " + itemsTable+"."+itemNameRow + " = " + productTable+"."+productID+");");

        }

    }

    public void addBill (String name) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(billName,name);
        db.insert(billsTable, null, cv);
        LocalBroadcastManager.getInstance(context).sendBroadcast(new Intent(billsChanged));
    }

    public Bill getBill (int billId) {
        Bill bill = null;
        SQLiteDatabase db = getReadableDatabase();
        Cursor c = db.rawQuery("SELECT * FROM " + billsTable + " WHERE _id = " + billId, null);
        if (c.getCount() == 1){
            c.moveToNext();
            bill = new Bill(c.getInt(0), c.getString(1));
        }
        return bill;
    }

    public void addItem( Item item, int billId) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(itemNameRow, item.getName());
        cv.put(itemPriceRow, item.getPrice());
        cv.put(itemBillId, billId);
        db.insert(itemsTable, null, cv);

        LocalBroadcastManager.getInstance(context).sendBroadcast(new Intent(dataChanged));

    }

    public ArrayList<Bill> getBills() {
        ArrayList<Bill> bills = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();
        Cursor c = db.rawQuery("SELECT * FROM " + billsTable , null);
        while (c.moveToNext()) {
            bills.add(new Bill(c.getInt(0), c.getString(1)));
        }
        db.close();
        return bills;
    }

    public ArrayList<Item> getItems (int billId) {
        ArrayList<Item> items = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();
        Cursor c = db.rawQuery("SELECT * FROM "+ itemsTable + " WHERE "+ itemBillId +" = " + billId + " ORDER BY _id desc;", null);

        while (c.moveToNext()) {
            items.add(new Item( c.getString(1), c.getFloat(2), c.getInt(0)));
        }
        db.close();
        return items;
    }

    public void delAll (int billId) {
        SQLiteDatabase db = getWritableDatabase();
        db.delete(itemsTable, itemBillId + " = ?", new String[] {""+billId});
        db.delete(billsTable, "_id = ?", new String[] {""+billId});
        db.close();
        LocalBroadcastManager.getInstance(context).sendBroadcast(new Intent(dataChanged));
        LocalBroadcastManager.getInstance(context).sendBroadcast(new Intent(billsChanged));

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
