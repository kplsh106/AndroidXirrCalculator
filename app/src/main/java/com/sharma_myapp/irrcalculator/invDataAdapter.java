package com.sharma_myapp.irrcalculator;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;


import java.util.ArrayList;
import java.util.List;

public class invDataAdapter extends ArrayAdapter<invData> {

    ArrayList<invData> item;

    public invDataAdapter(@NonNull Context context, int resource, ArrayList<invData> item ) {
        super(context, resource, (List<invData>) item);
        this.item = item;
        Log.v("insideadapter","inside adapter constructor");
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        int phraseIndex = position;
        if(convertView == null) {
            Log.v("insideadapter","inside the if part of getView");
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.list,
                    parent, false);
        }

        EditText dateEditText = convertView.findViewById(R.id.date);
        EditText amountEditText = convertView.findViewById(R.id.amount);
        TextView snoTextView=convertView.findViewById(R.id.sno);

        dateEditText.setText(item.get(position).getInDay());

        double amt=item.get(position).getAmount();
        String strAmount=String.valueOf(amt);
        amountEditText.setText(strAmount);

        int sno=item.get(position).sno;
        String strSno=String.valueOf(sno);
        snoTextView.setText(strSno);
        return convertView;
    }
}
