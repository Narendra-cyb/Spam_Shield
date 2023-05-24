package com.example.spam_activity2;

import java.util.Date;

public class SMSMessage {
    private String id;
    private String sender;
    private String body;
    private String timestamp;
    private boolean isExpanded;

    public SMSMessage() {
    }

    public SMSMessage(String id, String sender, String body, String timestamp) {
        this.id = id;
        this.sender = sender;
        this.body = body;
        this.timestamp = timestamp;
        this.isExpanded=false;
    }

    public String getId() {
        return id;
    }

    public String getSender() {
        return sender;
    }

    public String getBody() {
        return body;
    }

    public String getTimestamp() {
        return timestamp;
    }
    public boolean isExpanded() {
        return isExpanded;
    }

    public void setExpanded(boolean expanded) {
        isExpanded = expanded;
    }
}
