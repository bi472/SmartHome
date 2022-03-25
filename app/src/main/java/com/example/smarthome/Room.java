package com.example.smarthome;

import java.util.ArrayList;
import java.util.List;

public class Room {
    private String room_name;
    private int picture;

    public Room(String room_name, int picture){
        this.room_name = room_name;
        this.picture = picture;
    }


    public String getRoom_name() {
        return room_name;
    }

    public void setRoom_name(String room_name) {
        this.room_name = room_name;
    }

    public int getPicture() {
        return picture;
    }

    public void setPicture(int picture) {
        this.picture = picture;
    }


}
