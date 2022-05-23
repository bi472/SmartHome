package com.example.smarthome;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class SwitchesAdapter extends RecyclerView.Adapter<SwitchesAdapter.ViewHolder>{

    interface OnSwitchClickListener {
        void onSwitchClick(Switches switches, int position);
    }

    private final OnSwitchClickListener onClickListener;

    private final LayoutInflater inflater;
    private final List<Switches> list_switches;

    SwitchesAdapter(OnSwitchClickListener onClickListener, Context context, List<Switches> list_switches) {
        this.onClickListener = onClickListener;
        this.list_switches = list_switches;
        this.inflater = LayoutInflater.from(context);
    }
    @Override
    public SwitchesAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.switch_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(SwitchesAdapter.ViewHolder holder, @SuppressLint("RecyclerView") final int position) {
        final Switches state = list_switches.get(position);
        holder.imageView.setImageResource(state.getFlagResource());
        holder.nameView.setText(state.getName());
        holder.aSwitch.setChecked(state.getCondition());
        holder.aSwitch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onClickListener.onSwitchClick(state, position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return list_switches.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        final ImageView imageView;
        final TextView nameView;
        Switch aSwitch;
        ViewHolder(View view){
            super(view);
            aSwitch = view.findViewById(R.id.switch_condition);
            imageView = view.findViewById(R.id.imageview);
            nameView = view.findViewById(R.id.name);
        }
    }
}
