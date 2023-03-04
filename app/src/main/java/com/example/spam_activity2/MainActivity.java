package com.example.spam_activity2;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.Manifest;

public class MainActivity extends AppCompatActivity {
    private TextView resultTextView, sms_textview;
    private Button res;
    private static final String TAG = "MainActivity";
    private static final int MY_PERMISSIONS_REQUEST_READ_SMS = 1;


    private SmsReceiver smsReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        sms_textview = findViewById(R.id.SmsTextView);
        resultTextView = findViewById(R.id.resultTextView);
        // Register the SMS receiver
        IntentFilter filter = new IntentFilter("android.provider.Telephony.SMS_RECEIVED");
        smsReceiver = new SmsReceiver(this);
        registerReceiver(smsReceiver, filter);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        // Unregister the SMS receiver
        unregisterReceiver(smsReceiver);
    }

    public void displayResult(String msg, boolean isSpam) {
        runOnUiThread(() -> {
            if (isSpam) {
                sms_textview.setText(msg);
                resultTextView.setTextColor(Color.RED);
                resultTextView.setText("SPAM");
            } else {
                sms_textview.setText(msg);
                resultTextView.setTextColor(Color.GREEN);
                resultTextView.setText("HAM");
            }
        });
    }
}
