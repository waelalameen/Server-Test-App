package com.app_mo.servertest;

import com.google.gson.annotations.SerializedName;

public class Model {
    @SerializedName("name")
    private String name;
    @SerializedName("img")
    private String image;
    @SerializedName("price")
    private String  price;

    public Model(String name, String image, String price) {
        this.name = name;
        this.image = image;
        this.price = price;
    }

    public String getName() {
        return name;
    }

    String getImage() {
        return image;
    }

    String getPrice() {
        return price;
    }
}
