package com.app_mo.servertest;

import android.widget.AbsListView;

public class EndlessScroll implements AbsListView.OnScrollListener {
    private static final int THRESHOLD = 4;
    private int previousTotalCount = 0;
    private int currentPage = 0;
    private boolean isLoading = true;

    @Override
    public void onScrollStateChanged(AbsListView absListView, int i) {
        // No implementation
    }

    @Override
    public void onScroll(AbsListView absListView, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
        if (isLoading) {
            if (totalItemCount > previousTotalCount) {
                isLoading = false;
                previousTotalCount = totalItemCount;
                currentPage++;
            }
        }

        if (!isLoading && (totalItemCount - visibleItemCount) <= firstVisibleItem + THRESHOLD) {
            isLoading = true;
        }
    }
}
