package com.app_mo.servertest.viewHolders;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.app_mo.servertest.R;

public class MainViewHolder extends RecyclerView.ViewHolder {
    public ImageView productImg;
    public TextView productName, productPrice;

    public MainViewHolder(View itemView) {
        super(itemView);

        productImg = (ImageView) itemView.findViewById(R.id.product_img);
        productName = (TextView) itemView.findViewById(R.id.product_name);
        productPrice = (TextView) itemView.findViewById(R.id.product_price);
    }
}
