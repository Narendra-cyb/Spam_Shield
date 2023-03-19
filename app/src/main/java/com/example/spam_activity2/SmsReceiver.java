package com.example.spam_activity2;
import static android.app.Service.START_STICKY;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.util.Log;
import android.widget.Toast;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class SmsReceiver extends BroadcastReceiver {
    private static final String TAG = "SmsReceiver";

    private SpamDetector spamDetector;
    private MainActivity mainActivity;

    private SmsService smsService;
    private SmsReceiver smsReceiver;
    public SmsReceiver(MainActivity mainActivity) {
        this.mainActivity = mainActivity;
    }


    @Override
    public void onReceive(Context context, Intent intent) {
        // Initialize the spam detector
        spamDetector = new SpamDetector();

        try {
            // Train the spam detector with the training data
            spamDetector.train(context.getAssets().open("SMSSpamCollection.txt"), "spam");
            Log.d(TAG, "Spam detector trained successfully.");
        } catch (IOException e) {
            Log.e(TAG, "Error reading training data file", e);
            Toast.makeText(context, "Error reading training data file.", Toast.LENGTH_LONG).show();
            return;
        }

        // Check if the intent is an SMS received action
        if (intent.getAction().equals("android.provider.Telephony.SMS_RECEIVED")) {
            // Get the SMS messages from the intent
            Bundle bundle = intent.getExtras();
            if (bundle != null) {
                Object[] pdus = (Object[]) bundle.get("pdus");
                if (pdus != null) {
                    // Process each SMS message
                    for (Object pdu : pdus) {
                        SmsMessage smsMessage = SmsMessage.createFromPdu((byte[]) pdu);
                        String sender = smsMessage.getOriginatingAddress();
                        String message = smsMessage.getMessageBody();
                        long timestamp = smsMessage.getTimestampMillis();
                        Log.v("message body",message);

                        // Classify the SMS message using the spam detector
                        boolean isSpam = spamDetector.classify(smsMessage.getMessageBody(), 0.5);
                        // Display the classification result as a toast message
                        mainActivity.displayResult(message,sender,timestamp,isSpam);
                        Toast.makeText(context, isSpam ? "SPAM" : "NOT SPAM", Toast.LENGTH_LONG).show();
                    }
                }
            }
        }
    }
}

