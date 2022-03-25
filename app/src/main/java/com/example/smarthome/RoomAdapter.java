package com.example.smarthome;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;

import java.util.List;

public class RoomAdapter extends ArrayAdapter<Room> {
    private LayoutInflater inflater;
    private int layout;
    private List<Room> rooms;


    public RoomAdapter(Context context, int resource, List<Room> rooms) {
        super(context, resource, rooms);
        this.rooms = rooms;
        this.layout = resource;
        this.inflater = LayoutInflater.from(context);

    }

    public View getView(int position, View convertView, ViewGroup parent) {
        View view = inflater.inflate(this.layout, parent, false); // из созданного объекта View получаем элементы ImageView и TextView по id.

        ImageView FoodView = (ImageView) view.findViewById(R.id.imageView_RoomPicture);
        TextView nameView = (TextView) view.findViewById(R.id.textView_RoomName);
        FoodView.setFocusable(false);

        Room room = rooms.get(position); // полученные элементы ImageView и TextView наполняем из полученного по позиции объекта State

        FoodView.setImageResource(room.getPicture());
        nameView.setText(room.getRoom_name());

        return view; // возвращаем view
    }
}