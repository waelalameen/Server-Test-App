package com.app_mo.servertest;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;

public class Timer extends AppCompatActivity {
    private TextView tvCount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timer);

        tvCount = (TextView) findViewById(R.id.count);

        startService(new Intent(this, BroadcastService.class));
    }

    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getExtras() != null) {
                int counter = intent.getExtras().getInt("counter", 0);
                tvCount.setText(String.valueOf(counter));
            }
        }
    };

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(broadcastReceiver);
        Log.d("status", "Unregistered broadcast receiver");
    }

    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(broadcastReceiver, new IntentFilter(BroadcastService.COUNTDOWN_BR));
        Log.d("status", "Registered broadcast receiver");
    }

//    @Override
//    protected void onDestroy() {
//        stopService(new Intent(this, BroadcastService.class));
//        super.onDestroy();
//    }
}
