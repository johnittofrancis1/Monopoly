package com.example.monopoly;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.LinkedList;

public class PlayerAdapter extends BaseAdapter {
    public LinkedList<Player> players;
    Context context;

    public PlayerAdapter(Context context,LinkedList<Player> players) {
        this.context = context;
        this.players = players;
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    @Override
    public int getViewTypeCount() {
        return getCount();
    }
    @Override
    public int getCount() {
        return players.size();
    }

    @Override
    public Object getItem(int i) {
        return players.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        view = inflater.inflate(R.layout.player,null,true);

        TextView name = view.findViewById(R.id.playertext);
        TextView value = view.findViewById(R.id.value);

        Player player = players.get(i);
        name.setText((i+1)+player.getName());

        int totValue = player.getMoney() + player.getTotalPropertyValue() + player.getTotalBuildingValue() - player.getMortgagedValue();
        value.setText("Value: "+totValue);
        return view;
    }

}
