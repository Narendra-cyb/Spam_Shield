package com.example.spam_activity2;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.util.Log;
import android.widget.Toast;

import java.io.IOException;

public class SmsReceiver extends BroadcastReceiver {
    private static final String TAG = "SmsReceiver";

    public SmsReceiver(MainActivity mainActivity) {
        this.mainActivity = mainActivity;
    }

    private SpamDetector spamDetector;
    private MainActivity mainActivity;

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
                        Log.v("message id",smsMessage.getMessageBody());
                        // Classify the SMS message using the spam detector
                        boolean isSpam = spamDetector.classify(smsMessage.getMessageBody(), 0.5);
                        // Display the classification result as a toast message
                        mainActivity.displayResult(smsMessage.getMessageBody(),isSpam);
                        Toast.makeText(context, isSpam ? "SPAM" : "HAM", Toast.LENGTH_LONG).show();
                    }
                }
            }
        }
    }
}

