package com.app_mo.servertest;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.View;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Another extends AppCompatActivity {
    private List<Item> itemList = new ArrayList<>();
    private RecyclerView mRecycler;
    private MyAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_another);

        mRecycler = (RecyclerView) findViewById(R.id.recycler_view);
        mRecycler.setHasFixedSize(true);
        setAnimationDecorator();
        mRecycler.requestLayout();
        //mRecycler.setItemAnimator(new DefaultItemAnimator());
        mRecycler.setDrawingCacheEnabled(true);
        mRecycler.setItemViewCacheSize(20);
        mRecycler.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);

        itemList = getDataList();
        adapter = new MyAdapter(this, itemList, mRecycler);
        mRecycler.setLayoutManager(new LinearLayoutManager(this));
        mRecycler.setAdapter(adapter);

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(callback);
        itemTouchHelper.attachToRecyclerView(mRecycler);
    }

    ItemTouchHelper.Callback callback = new ItemTouchHelper.Callback() {
        Drawable background;
        Drawable xMark;
        int xMarkMargin;
        boolean isInitiated = false;

        @RequiresApi(api = Build.VERSION_CODES.M)
        private void init() {
            background = new ColorDrawable(Color.RED);
            xMark = ContextCompat.getDrawable(getApplicationContext(), R.drawable.ic_delete);
            xMark.setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_ATOP);
            xMarkMargin = (int) getResources().getDimension(R.dimen.ic_clear_margin);
            isInitiated = true;
        }

        @Override
        public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
            //Toast.makeText(getApplicationContext(), "on move", Toast.LENGTH_SHORT).show();
            Collections.swap(itemList, viewHolder.getAdapterPosition(), target.getAdapterPosition());
            MyAdapter adapter = (MyAdapter) recyclerView.getAdapter();
            adapter.notifyItemMoved(viewHolder.getAdapterPosition(), target.getAdapterPosition());
            return true;
        }

        @Override
        public boolean isLongPressDragEnabled() {
            return true;
        }

        @Override
        public boolean isItemViewSwipeEnabled() {
            return true;
        }

        @Override
        public int getMovementFlags(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
            int dragFlags = ItemTouchHelper.UP | ItemTouchHelper.DOWN;
            int swipeFlags = ItemTouchHelper.START;

//            MyAdapter adapter = (MyAdapter) mRecycler.getAdapter();
//            int position = viewHolder.getAdapterPosition();
//
//            if (adapter.isUnDo() && adapter.isPendingRemoval(position)) {
//                return 0;
//            }

            return makeMovementFlags(dragFlags, swipeFlags);
        }

        @Override
        public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
            //Toast.makeText(getApplicationContext(), "on swiped", Toast.LENGTH_SHORT).show();
            int position = viewHolder.getAdapterPosition();
//            itemList.remove(position);
//            adapter.notifyDataSetChanged();

            MyAdapter adapter = (MyAdapter) mRecycler.getAdapter();
            boolean undo = adapter.isUnDo();

            if (!undo) {
                adapter.pendingRemoval(position);
            }
        }

        @RequiresApi(api = Build.VERSION_CODES.M)
        @Override
        public void onChildDraw(Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, float dX, float dY,
                                int actionState, boolean isCurrentlyActive) {
            View itemView = viewHolder.itemView;

            if (!isInitiated) {
                init();
            }

            background.setBounds((int) (itemView.getRight() + dX), itemView.getTop(), itemView.getRight(), itemView.getBottom());
            background.draw(c);

            int itemHeight = itemView.getBottom() - itemView.getTop();
            int intrinsicWidth = xMark.getIntrinsicWidth();
            int intrinsicHeight = xMark.getIntrinsicHeight();

            int xMarkLeft = itemView.getRight() - xMarkMargin - intrinsicWidth;
            int xMarkRight = itemView.getRight() - xMarkMargin;
            int xMarkTop = itemView.getTop() + ((itemHeight - intrinsicHeight) / 2);
            int xMarkBottom = xMarkTop + intrinsicHeight;
            xMark.setBounds(xMarkLeft, xMarkTop, xMarkRight, xMarkBottom);
            xMark.draw(c);

            super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
        }

        @Override
        public void clearView(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
            super.clearView(recyclerView, viewHolder);
        }
    };

    private void setAnimationDecorator() {
        mRecycler.addItemDecoration(new RecyclerView.ItemDecoration() {
            Drawable background;
            boolean isInitiated = false;

            private void init() {
                background = new ColorDrawable(Color.RED);
                isInitiated = true;
            }

            @Override
            public void onDraw(Canvas c, RecyclerView parent, RecyclerView.State state) {
                if (!isInitiated) {
                    init();
                }

                if (parent.getItemAnimator().isRunning()) {
                    View lastViewComingDown = null;
                    View firstViewComingUp = null;

                    int left = 0;
                    int right = parent.getRight();
                    int top = 0;
                    int bottom = 0;

                    int childCount = parent.getLayoutManager().getChildCount();
                    for (int i = 0; i < childCount; i++) {
                        View child = parent.getLayoutManager().getChildAt(i);

                        if (child.getTranslationY() < 0) {
                            lastViewComingDown = child;
                        } else if (child.getTranslationY() > 0) {
                            firstViewComingUp = child;
                        }
                    }

                    if (lastViewComingDown != null && firstViewComingUp != null) {
                        // views are coming down AND going up to fill the void
                        top = lastViewComingDown.getBottom() + (int) lastViewComingDown.getTranslationY();
                        bottom = firstViewComingUp.getTop() + (int) firstViewComingUp.getTranslationY();
                    } else if (lastViewComingDown != null) {
                        // views are going down to fill the void
                        top = lastViewComingDown.getBottom() + (int) lastViewComingDown.getTranslationY();
                        bottom = lastViewComingDown.getBottom();
                    } else if (firstViewComingUp != null) {
                        // views are coming up to fill the void
                        top = firstViewComingUp.getTop();
                        bottom = firstViewComingUp.getTop() + (int) firstViewComingUp.getTranslationY();
                    }

                    background.setBounds(left, top, right, bottom);
                    background.draw(c);
                }

                super.onDraw(c, parent, state);
            }
        });
    }

    private List<Item> getDataList() {
        Item item;

        for (int i = 0; i < 30; i++) {
            item = new Item("Item " + String.valueOf(i), "Item " + String.valueOf(i) + " content here");
            itemList.add(item);
        }

        return itemList;
    }
}
