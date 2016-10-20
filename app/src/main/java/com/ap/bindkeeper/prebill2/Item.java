package com.ap.bindkeeper.prebill2;

/**
 *
 */
public class Item {

    private String name;
    private Float price;
    private int id;

    public Item(String name, Float price, int id) {
        this.id = id;
        this.name = name;
        this.price = price;
    }

    public Item(String name, Float price) {
        this.id = 0;
        this.name = name;
        this.price = price;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Float getPrice() {
        return price;
    }

    public void setPrice(Float price) {
        this.price = price;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
