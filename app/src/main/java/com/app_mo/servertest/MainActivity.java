package com.app_mo.servertest;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.app_mo.servertest.networking.ApiClient;
import com.app_mo.servertest.networking.ApiInterface;
import com.github.pwittchen.infinitescroll.library.InfiniteScrollListener;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    private RecyclerView.Adapter adapter;
    private GridLayoutManager gridLayoutManager;
    private int page = 0;
    private static final int THRESHOLD = 4;
    private boolean isLoading = true;
    private List<Model> list = new ArrayList<>();

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    //mTextMessage.setText(R.string.title_home);
                    return true;
                case R.id.navigation_dashboard:
                    //mTextMessage.setText(R.string.title_dashboard);
                    startActivity(new Intent(getApplicationContext(), Login.class));
                    return true;
                case R.id.navigation_notifications:
                    //mTextMessage.setText(R.string.title_notifications);
                    startActivity(new Intent(getApplicationContext(), Timer.class));
                    return true;
            }
            return false;
        }

    };

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        generateKeyHash();

        RecyclerView mRecyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        mRecyclerView.invalidateItemDecorations();
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.requestLayout();
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        gridLayoutManager = new GridLayoutManager(this, 2);
        mRecyclerView.setLayoutManager(gridLayoutManager);
        mRecyclerView.setOnScrollListener(getInfiniteScroll());
        mRecyclerView.setDrawingCacheEnabled(true);
        mRecyclerView.setItemViewCacheSize(10);
        mRecyclerView.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);
        adapter = new Adapter(getApplicationContext(), list);
        mRecyclerView.setAdapter(adapter);

        loadMore();

        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_go) {
            startActivity(new Intent(this, Another.class));
        }
        return true;
    }

    private void generateKeyHash() {
        try {
            @SuppressLint("PackageManagerGetSignatures")
            PackageInfo info = getPackageManager().getPackageInfo(
                    "com.app_mo.servertest", PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                Log.d("KeyHash", Base64.encodeToString(md.digest(), Base64.DEFAULT));
            }
        } catch (PackageManager.NameNotFoundException | NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
    }

    private InfiniteScrollListener getInfiniteScroll() {
        int maxPages = 4;
        return new InfiniteScrollListener(maxPages, gridLayoutManager) {
            @Override
            public void onScrolledToEnd(int firstVisibleItemPosition) {
                int visibleItemCount = gridLayoutManager.getChildCount();
                int totalItemCount = gridLayoutManager.getItemCount();
                int previousItemCount = gridLayoutManager.findFirstVisibleItemPosition();

                if (isLoading) {
                    if (totalItemCount > previousItemCount) {
                        isLoading = false;
                        previousItemCount = totalItemCount;
                        page += 4;
                    }
                }

                if (!isLoading && (totalItemCount - visibleItemCount) <= firstVisibleItemPosition + THRESHOLD) {
                    loadMore();
                    isLoading = true;
                }
            }
        };
    }

    private void loadMore() {
        ApiInterface api = ApiClient.getClient().create(ApiInterface.class);
        Call<List<Model>> call = api.getItems(String.valueOf(page));

        call.enqueue(new Callback<List<Model>>() {
            @Override
            public void onResponse(@NonNull Call<List<Model>> call, @NonNull Response<List<Model>> response) {
                list.addAll(response.body());
                refresh();
            }

            @Override
            public void onFailure(@NonNull Call<List<Model>> call, @NonNull Throwable t) {
                loadMore();
                Toast.makeText(getApplicationContext(), t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void refresh() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                adapter.notifyDataSetChanged();
            }
        });
    }
}
