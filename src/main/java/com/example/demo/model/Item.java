package com.example.demo.model;

public class Item {
    private final String key;
    private final String value;

    public Item(String key, String value) {
        this.key = key;
        this.value = value;
    }

    public String getKey() {
        return key;
    }

    public String getValue() {
        return value;
    }

    @Override
    public String toString() {
        return "Item{" +
                "key='" + key + '\'' +
                ", value='" + value + '\'' +
                '}';
    }
}