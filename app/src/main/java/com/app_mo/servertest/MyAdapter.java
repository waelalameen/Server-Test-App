package com.app_mo.servertest;

import android.content.Context;
import android.graphics.Color;
import android.os.Handler;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.MyHolder> {
    private Context context;
    private List<Item> itemList;
    private List<Item> itemsPendingRemoval;
    private boolean unDo;
    private Handler handler;
    private Map<Item, Runnable> pendingRunnableMap;
    private static final int REMOVAL_TIMEOUT = 3000;
    private RecyclerView mRecycler;

    MyAdapter(Context context, List<Item> itemList, RecyclerView mRecycler) {
        this.context = context;
        this.itemList = itemList;
        this.mRecycler = mRecycler;
        itemsPendingRemoval = new ArrayList<>();
        handler = new Handler();
        pendingRunnableMap = new HashMap<>();
        setUnDo(false);
    }
    @Override
    public MyHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.another_layout, parent, false);
        return new MyHolder(view);
    }

    @Override
    public void onBindViewHolder(MyHolder holder, int position) {
        final Item item = itemList.get(position);
        holder.title.setText(item.getTitle());
        holder.content.setText(item.getContent());

        if (itemsPendingRemoval.contains(item)) {
            holder.itemView.setBackgroundColor(Color.RED);
            holder.title.setVisibility(View.GONE);
            holder.content.setVisibility(View.GONE);

            Snackbar snackbar = Snackbar.make(mRecycler, "Item Removed", Snackbar.LENGTH_LONG)
                    .setAction("UNDO", new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            //setUnDo(true);
                            Runnable pendingRemovalRunnable = pendingRunnableMap.get(item);
                            pendingRunnableMap.remove(item);

                            if (pendingRemovalRunnable != null) {
                                handler.removeCallbacks(pendingRemovalRunnable);
                            }

                            itemsPendingRemoval.remove(item);
                            notifyItemChanged(itemList.indexOf(item));
                            mRecycler.scrollToPosition(itemList.indexOf(item));
                        }
                    });

            snackbar.show();

        } else {
            holder.itemView.setBackgroundColor(Color.WHITE);
            holder.title.setVisibility(View.VISIBLE);
            holder.content.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }

    private void setUnDo(boolean unDo) {
        this.unDo = unDo;
    }

    public boolean isUnDo() {
        return this.unDo;
    }

    public void pendingRemoval(int position) {
        final Item item = itemList.get(position);

        if (!itemsPendingRemoval.contains(item)) {
            itemsPendingRemoval.add(item);
            notifyItemChanged(position);

            Runnable pendingRemovalRunnable = new Runnable() {
                @Override
                public void run() {
                    remove(itemList.indexOf(item));
                }
            };

            handler.postDelayed(pendingRemovalRunnable, REMOVAL_TIMEOUT);
            pendingRunnableMap.put(item, pendingRemovalRunnable);
        }
    }

    public void remove(int position) {
        Item item = itemList.get(position);

        if (itemsPendingRemoval.contains(item)) {
            itemsPendingRemoval.remove(item);
        }

        if (itemList.contains(item)) {
            itemList.remove(position);
            notifyItemRemoved(position);
        }
    }

    public boolean isPendingRemoval(int position) {
        Item item = itemList.get(position);
        return itemsPendingRemoval.contains(item);
    }

    class MyHolder extends RecyclerView.ViewHolder {
        TextView title, content;

        public MyHolder(View itemView) {
            super(itemView);
            title = (TextView) itemView.findViewById(R.id.title);
            content = (TextView) itemView.findViewById(R.id.content);
        }
    }
}
