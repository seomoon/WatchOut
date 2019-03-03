package com.example.ise.watchout.Main;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.ise.watchout.R;

/**
 * Created by user on 2017-08-17.
 */

public class RetViewHolder extends RecyclerView.ViewHolder {

    TextView value;
    TextView date;

    public RetViewHolder(View itemView) {
        super(itemView);

        value = (TextView)itemView.findViewById(R.id.value);
        date = (TextView)itemView.findViewById(R.id.date);


    }


}