package com.example.voyage.models;

public class Expense {
    private String type;
    private String name;
    private int amount;

    public Expense(String type, String name, int amount) {
        this.type = type;
        this.name = name;
        this.amount = amount;
    }

    public String getType() {
        return type;
    }

    public String getName() {
        return name;
    }

    public int getAmount() {
        return amount;
    }
}