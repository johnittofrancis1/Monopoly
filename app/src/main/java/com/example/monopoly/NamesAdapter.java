package com.example.monopoly;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;

import java.util.ArrayList;

public class NamesAdapter extends BaseAdapter {
    private Context context;
    public static ArrayList<EditTextModel> editTextModelArrayList;

    public NamesAdapter(Context context,ArrayList<EditTextModel> editTextModelArrayList) {
        this.context = context;
        NamesAdapter.editTextModelArrayList = editTextModelArrayList;
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
        return editTextModelArrayList.size();
    }

    @Override
    public Object getItem(int i) {
        return editTextModelArrayList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(final int position, View view, ViewGroup viewGroup) {
        final ViewHolder holder;

        if(view == null)
        {
            holder = new ViewHolder();
            LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.edittext,null,true);
            holder.editText = view.findViewById(R.id.name);
            view.setTag(holder);
            holder.editText.setHint("Player "+(position+1)+" Name");
        }
        else
        {
            holder = (ViewHolder)view.getTag();
        }

        holder.editText.setHint("Player "+(position+1)+" Name");
        holder.editText.setText(editTextModelArrayList.get(position).getEditTextValue());
        holder.editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                editTextModelArrayList.get(position).setEditTextValue(holder.editText.getText().toString());
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        return view;
    }

    private class ViewHolder
    {
        protected EditText editText;
    }
}
