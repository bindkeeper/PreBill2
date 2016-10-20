package com.ap.bindkeeper.prebill;

/**
 * Created by User on 06/10/2016.
 */
public class Bill {


    String billName;



    int id;


    public Bill(int id, String billName) {
        this.id = id;
        this.billName = billName;
    }

    public String getBillName() {
        return billName;
    }

    public int getId() {
        return id;
    }
    public void setBillName(String billName) {
        this.billName = billName;
    }

}
