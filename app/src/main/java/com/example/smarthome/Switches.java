package com.example.smarthome;

public class Switches {
    private String name; // название
    private int imgResource;
    private boolean condition;

    public Switches(String name, int image, boolean condition){
        this.name=name;
        this.imgResource=image;
        this.condition =condition;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getFlagResource() {
        return this.imgResource;
    }

    public void setFlagResource(int flagResource) {
        this.imgResource = imgResource;
    }

    public boolean getCondition() { return this.condition; }
}
