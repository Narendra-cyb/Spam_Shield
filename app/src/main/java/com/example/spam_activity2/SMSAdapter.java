package com.example.spam_activity2;

import android.content.Intent;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.List;
public class SMSAdapter extends RecyclerView.Adapter<SMSAdapter.ViewHolder> {
    private OnItemClickListener listener;
    private List<SMSMessage> smsMessages;

    private int[] colors = new int[]{Color.parseColor("#E1F5FE"), Color.parseColor("#E0F7FA"), Color.parseColor("#E0F2F1"), Color.parseColor("#E8F5E9"), Color.parseColor("#F1F8E9"), Color.parseColor("#FFFDE7"), Color.parseColor("#FFF8E1"), Color.parseColor("#FFECB3"), Color.parseColor("#FFE0B2"), Color.parseColor("#FFCCBC")};


    public SMSAdapter(List<SMSMessage> smsMessages) {
        this.smsMessages = smsMessages;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_sms, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        SMSMessage smsMessage = smsMessages.get(position);
        holder.timestampTextView.setText(smsMessage.getTimestamp());
        holder.addressTextView.setText(smsMessage.getSender());
        String body = smsMessage.getBody();
        if (body.length() > 120) { // Set a maximum range of 50 characters
            body = body.substring(0, 70) + "...";
        }
        holder.bodyTextView.setText(body);
        holder.itemView.setBackgroundColor(colors[position % colors.length]);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) {
                    listener.onItemClick(smsMessage);
                }
            }
        });

        // Add a LongClickListener to enable the share option when a user holds down on a message
        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                // Get the message body and create an intent to share it
                String messageBody = smsMessage.getBody();
                Intent shareIntent = new Intent(Intent.ACTION_SEND);
                shareIntent.setType("text/plain");
                shareIntent.putExtra(Intent.EXTRA_TEXT, messageBody);

                // Start the share activity
                v.getContext().startActivity(Intent.createChooser(shareIntent, "Share message"));

                return true;
            }
        });
    }


    @Override
    public int getItemCount() {
        return smsMessages.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView addressTextView;
        public TextView bodyTextView;
        public TextView timestampTextView;
        SMSMessage smsMessage;

        public ViewHolder(View itemView) {
            super(itemView);
            addressTextView = itemView.findViewById(R.id.sms_address);
            bodyTextView = itemView.findViewById(R.id.sms_body);
            timestampTextView = itemView.findViewById(R.id.sms_time);
            smsMessage = new SMSMessage();
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener != null) {
                        Log.v("SMSadapter",smsMessage.getBody());
                        listener.onItemClick(smsMessage);
                    }
                }
            });
        }
    }

    //setter method for item click listener
    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

}

//interface ontimeclicklistener


