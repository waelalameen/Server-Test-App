package com.app_mo.servertest;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.app_mo.servertest.viewHolders.MainViewHolder;
import com.app_mo.servertest.viewHolders.ProgressBarViewHolder;
import com.squareup.picasso.Picasso;

import java.util.List;

public class Adapter extends RecyclerView.Adapter {
    private List<Model> list;
    private Context context;
    private static final int VIEW_TYPE1 = 1;
    private static final int VIEW_TYPE2 = 2;

    public Adapter(Context context, List<Model> list) {
        this.list = list;
        this.context = context;
    }

    @Override
    public int getItemViewType(int position) {
        return (list.get(position).getName() != null ? VIEW_TYPE1 : VIEW_TYPE2);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder vh;
        View view;

        if (viewType == VIEW_TYPE1) {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.main_list, parent, false);
            vh = new MainViewHolder(view);
        } else {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.progressbar_layout, parent, false);
            vh = new MainViewHolder(view);
        }
        return vh;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        Model model = list.get(position);

        if (holder instanceof MainViewHolder) {
            if (model.getImage() != null) {
                if (!model.getImage().equals("")) {
                    Picasso.with(context).load(model.getImage()).error(R.drawable.photo).into(((MainViewHolder) holder).productImg);
                    ((MainViewHolder) holder).productName.setText(model.getName());
                    ((MainViewHolder) holder).productPrice.setText(model.getPrice());
                }
            }
        } else {
            ((ProgressBarViewHolder) holder).progressBar.setIndeterminate(true);
            ((ProgressBarViewHolder) holder).progressBar.setProgress(100);
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }
}
