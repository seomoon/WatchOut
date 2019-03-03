package com.example.ise.watchout.Main;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.ise.watchout.R;

import java.util.ArrayList;

/**
 * Created by user on 2017-08-17.
 */

public class RetAdapter extends RecyclerView.Adapter<RetViewHolder> {

    ArrayList<Ret> rDatas;
    View.OnClickListener recyclerclickListener;

    private View itemView;
    private ViewGroup parent;


    public RetAdapter(ArrayList<Ret> rDatas) {
        this.rDatas = rDatas;
    }
    public RetAdapter(ArrayList<Ret> rDatas, View.OnClickListener clickListener) {
        this.rDatas = rDatas;
        this.recyclerclickListener = clickListener;
    }


    @Override
    public RetViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        this.parent = parent;
        // 뷰홀더 패턴을 생성하는 메소드.
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.listview_item, parent, false);

        if( this.recyclerclickListener != null )
            itemView.setOnClickListener(recyclerclickListener);

        RetViewHolder viewHolder = new RetViewHolder(itemView);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RetViewHolder holder, final int position) {

        //리싸이클뷰에 항목을 뿌려주는 메소드.


        //Glide.with(parent.getContext()).load(mDatas.get(position).movie_image).into(holder.getMovieImageView());
        holder.value.setText(rDatas.get(position).content);
        holder.date.setText(String.valueOf(rDatas.get(position).date + " " + rDatas.get(position).time));


    }

    @Override
    public int getItemCount() {
        return (rDatas != null) ? rDatas.size() : 0;
    }
}
