package com.app_mo.servertest.viewHolders;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ProgressBar;

import com.app_mo.servertest.R;

public class ProgressBarViewHolder extends RecyclerView.ViewHolder {
    public ProgressBar progressBar;

    public ProgressBarViewHolder(View itemView) {
        super(itemView);

        progressBar = (ProgressBar) itemView.findViewById(R.id.progress_bar);
    }
}
