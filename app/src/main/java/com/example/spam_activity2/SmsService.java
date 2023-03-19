package com.example.spam_activity2;

import android.app.Service;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;

public class SmsService extends Service {
    private SmsReceiver smsReceiver;

    private MainActivity mainActivity;
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        IntentFilter filter = new IntentFilter("android.provider.Telephony.SMS_RECEIVED");
        smsReceiver = new SmsReceiver(mainActivity);
        registerReceiver(smsReceiver, filter);
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unregisterReceiver(smsReceiver);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}

