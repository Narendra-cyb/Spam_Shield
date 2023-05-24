package com.example.spam_activity2;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import androidx.core.app.NotificationCompat;
import androidx.core.content.res.ResourcesCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;

import android.graphics.Bitmap;
import android.graphics.Color;

import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;

import android.util.Log;
import android.view.MenuItem;

import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.navigation.NavigationView;

import java.text.SimpleDateFormat;
import java.util.Date;

public class MainActivity extends AppCompatActivity {

    DrawerLayout drawerLayout;
    Toolbar toolbar;
    NavigationView navigationView;

    private TextView resultTextView, sms_textview, senderTextView, timestampTV;
    private Button res;
    private static final String TAG = "MainActivity";
    private static final int MY_PERMISSIONS_REQUEST_READ_SMS = 1;
    private SmsReceiver smsReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //notification drawer
        drawerLayout = findViewById(R.id.drawerLayout);
        toolbar = findViewById(R.id.toolbar);
        navigationView = findViewById(R.id.navigationView);
        navigationView.bringToFront();


        //navigationView work

        //step1 : setup toolbar

        setSupportActionBar(toolbar);

        //step2 : Drawer Toggle (open/close)

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawerLayout, toolbar, R.string.opendrawer, R.string.closedrawer);

        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
        //step 3 : add click

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id = item.getItemId();
                if (id == R.id.optSMS) {
                    loadfragment(new SMSFragment(getApplicationContext()));
                } else if (id == R.id.optHome) {
                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                } else if (id == R.id.optHelp) {
                    loadfragment(new HelpFragment());

                } else {
                    loadfragment(new aboutFragment());
                }

                //closedrawer
                drawerLayout.closeDrawer(GravityCompat.START);
                //when click backpress


                return true;
            }

        });


        //sms work
        sms_textview = findViewById(R.id.SmsTextView);
        resultTextView = findViewById(R.id.resultTextView);
        senderTextView = findViewById(R.id.SenderTextView);
        timestampTV = findViewById(R.id.TimestampTextView);

        // Register the SMS receiver
        IntentFilter filter = new IntentFilter("android.provider.Telephony.SMS_RECEIVED");
        smsReceiver = new SmsReceiver(this);
        registerReceiver(smsReceiver, filter);
        startService(new Intent(this, SmsService.class));

        //sharedprefernces get
        SharedPreferences prefs = getSharedPreferences("myPrefs", MODE_PRIVATE);
        String lastMessage = prefs.getString("lastMessage", "");
        String lastSender = prefs.getString("lastsender", "");
        long lastTime = prefs.getLong("lasttime", 0);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String formattedDate = sdf.format(new Date(lastTime));

        boolean isSpam = prefs.getBoolean("isSpam", false);
        if (!lastMessage.isEmpty()) {
            sms_textview.setText(lastMessage);
            senderTextView.setText(lastSender);
            timestampTV.setText(formattedDate);
            if (isSpam) {
                resultTextView.setTextColor(Color.RED);
                resultTextView.setText("SPAM");
            } else {
                resultTextView.setTextColor(Color.GREEN);
                resultTextView.setText("NOT SPAM");
            }
        }
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
//            super.onBackPressed();
//            moveTaskToBack(true);
            AlertDialog.Builder exitdlg = new AlertDialog.Builder(this);
            exitdlg.setTitle("Exit?");
            exitdlg.setMessage("Are you sure want to exit?");
            exitdlg.setIcon(R.drawable.baseline_exit_to_app_24);

            exitdlg.setPositiveButton("No", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    Toast.makeText(MainActivity.this, "Welcome Back", Toast.LENGTH_SHORT).show();
                }
            });
            exitdlg.setNegativeButton("Yes", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    MainActivity.super.onBackPressed();
                }
            });
            exitdlg.show();

        }
    }

    private void loadfragment(Fragment fragment) {
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        ft.add(R.id.framecontainer, fragment);
        // Replace the existing fragment with the new fragment
        // Commit the transaction
        ft.commit();
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();

        // Unregister the SMS receiver
        unregisterReceiver(smsReceiver);
    }


    public void displayResult(String msg, String sender, long timestamp, boolean isSpam) {
        runOnUiThread(() -> {

            Drawable drawable = ResourcesCompat.getDrawable(getResources(),R.drawable.alert,null);
            BitmapDrawable bitmapDrawable = (BitmapDrawable) drawable;
            Bitmap Imageicon = bitmapDrawable.getBitmap();
            //notification manager implements
            Notification notification;
            NotificationManager nm = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

            Notification.BigTextStyle bigTextStyle = new Notification.BigTextStyle()
                    .bigText(msg);

            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                        notification = new Notification.Builder(this)
                        .setLargeIcon(Imageicon)
                        .setSmallIcon(R.drawable.email_svg)
                                .setContentTitle(sender)
                        .setContentText(msg)
                        .setStyle(bigTextStyle)
                        .setSubText("New message")
                        .setChannelId("spammessage")
                        .build();
                        nm.createNotificationChannel(new NotificationChannel("spammessage","main",NotificationManager.IMPORTANCE_HIGH));
            }
            else{
                notification = new Notification.Builder(this)
                        .setLargeIcon(Imageicon)
                        .setSmallIcon(R.drawable.email_svg)
                        .setContentText("New Message")
                        .setStyle(bigTextStyle)
                        .setSubText(String.valueOf(isSpam))
                        .build();
            }
            nm.notify(100,notification);


            sms_textview.setText(msg);

            senderTextView.setText(sender);
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String formattedDate = sdf.format(new Date(timestamp));
            timestampTV.setText(formattedDate);

            if (isSpam) {
                resultTextView.setTextColor(Color.RED);
                resultTextView.setText("SPAM");
            } else {
                resultTextView.setTextColor(Color.GREEN);
                resultTextView.setText("NOT SPAM");
            }

            //save last message
            saveLastMessage(msg, sender, timestamp, isSpam);

            // Update the SmsFragment.java UI with the new SMS message data
        });
    }


    private void saveLastMessage(String message, String sender, long timestamp, boolean isSpam) { //save last message method
        SharedPreferences prefs = getSharedPreferences("myPrefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("lastMessage", message);
        editor.putString("lastsender", sender);
        editor.putLong("lasttime", timestamp);
        editor.putBoolean("isSpam", isSpam);
        editor.apply();
    }

}
