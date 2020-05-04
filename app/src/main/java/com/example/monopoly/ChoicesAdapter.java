package com.example.monopoly;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class ChoicesAdapter extends RecyclerView.Adapter<ChoicesAdapter.MyViewHolder>{
    private List<String> choicesList;
    private View.OnClickListener onClickListener;
    private static MyViewHolder myViewHolder;
    public int choice;

    public ChoicesAdapter(List<String> choicesList)
    {
        this.choice = 1;
        this.choicesList = choicesList;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.choice, parent, false);
        myViewHolder = new MyViewHolder(itemView);
        return myViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        String choiceString = choicesList.get(position);
        holder.choiceView.setText(choiceString);
    }

    @Override
    public int getItemCount() {
        return choicesList.size();
    }

    public static MyViewHolder getMyViewHolder() {
        return myViewHolder;
    }

    public int getChoice() {
        return choice;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener
    {
        public TextView choiceView;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            this.choiceView = itemView.findViewById(R.id.choicetext);
        }

        @Override
        public void onClick(View view) {
            choice = getLayoutPosition();
            return;
        }
    }

}
