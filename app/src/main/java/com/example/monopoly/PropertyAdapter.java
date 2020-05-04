package com.example.monopoly;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class PropertyAdapter extends RecyclerView.Adapter<PropertyAdapter.MyViewHolder> {

    private List<Property> propertyList;
    CustomItemClickListener listener;

    public PropertyAdapter(List<Property> propertyList, CustomItemClickListener listener)
    {
        this.propertyList = propertyList;
        this.listener = listener;
    }
    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.property, parent, false);
        final MyViewHolder myViewHolder = new MyViewHolder(itemView);
        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.onItemClick(view,myViewHolder.getLayoutPosition());
            }
        });
        return myViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        Property property = propertyList.get(position);
        holder.name.setText(property.getCityName());
        holder.price.setText("Price: $"+property.getPrice());
        holder.mortgage.setText("Mortgage: $"+property.getMortgage());
        holder.propColor.setBackgroundColor(property.getColorId());
    }

    @Override
    public int getItemCount() {
        return propertyList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView name,price,mortgage;
        public ImageView propColor;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            name = itemView.findViewById(R.id.propertyname);
            price = itemView.findViewById(R.id.price);
            mortgage = itemView.findViewById(R.id.mortgage);
            propColor = itemView.findViewById(R.id.propcolor);
        }
    }
}
