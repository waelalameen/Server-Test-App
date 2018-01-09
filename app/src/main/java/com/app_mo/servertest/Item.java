package com.app_mo.servertest;

public class Item {
    private String title, content;

    public Item(String title, String content) {
        this.title = title;
        this.content = content;
    }

    public String getTitle() {
        return title;
    }

    public String getContent() {
        return content;
    }
}
