package com.example.spam_activity2;

import android.annotation.SuppressLint;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class SMSFragment extends Fragment {

    private RecyclerView recyclerView;
    private SMSAdapter adapter;
    private List<SMSMessage> smsMessages;
    Context context;
    public SMSFragment(Context context) {
        this.context = context;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_sms, container, false);

        recyclerView = view.findViewById(R.id.sms_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        smsMessages = new ArrayList<>();
        adapter = new SMSAdapter(smsMessages);

        //when click on item
        adapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(SMSMessage message) {
                // Show a dialog and check if the message is spam
                SpamDetector spamDetector = new SpamDetector();
                try {
                    // Train the spam detector with the training data
                    Log.d("SMSFRAgment","Success ");
                    spamDetector.train(context.getAssets().open("SMSSpamCollection.txt"), "spam");

                } catch (IOException e) {
                    Log.d("SMSFragment", "Error reading training data file", e);

                    return;
                }
                Log.v("SMSFragment",message.getBody());
                boolean isSpam = spamDetector.classify(message.getBody(),0.5);


                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setTitle("Spam Check Result");

                builder.setMessage("The message is " +" "+(isSpam ? "spam" : "not spam"));
                builder.setPositiveButton("OK", null);
                builder.show();
            }
        });



        recyclerView.setAdapter(adapter);

        // Query the SMS inbox and retrieve the SMS messages
        Uri inboxUri = Uri.parse("content://sms");
        String[] projection = new String[]{"_id", "address", "body","date"};
        Cursor cursor = getActivity().getContentResolver().query(inboxUri, projection, null, null, null);

        if (cursor != null && cursor.moveToFirst()) {
            do {
                // Retrieve the SMS message data
                @SuppressLint("Range") int id = cursor.getInt(cursor.getColumnIndex("_id"));
                @SuppressLint("Range") String address = cursor.getString(cursor.getColumnIndex("address"));
                @SuppressLint("Range") String body = cursor.getString(cursor.getColumnIndex("body"));
                Long time = cursor.getLong(cursor.getColumnIndexOrThrow("date"));
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                String formattedDate = sdf.format(new Date(time));
                // Create a new SMSMessage object and add it to the list
                SMSMessage smsMessage = new SMSMessage(String.valueOf(id), address, body,formattedDate);
                smsMessages.add(smsMessage);

            } while (cursor.moveToNext());
            cursor.close();
        }

        // Notify the adapter that the data has changed
        adapter.notifyDataSetChanged();

        return view;
    }
}
