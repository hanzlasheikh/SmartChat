package com.example.dell.smartchat;

public class Messages {


    public Messages(String from) {
        this.from = from;
    }

    private String from;

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    private String message,type;
    private long time;
    private boolean seen;

    public Messages (){}

    public Messages(String message, String type, long time, boolean seen) {
        this.message = message;
        this.type = type;
        this.time = time;
        this.seen = seen;
    }



    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public boolean isSeen() {
        return seen;
    }

    public void setSeen(boolean seen) {
        this.seen = seen;
    }


}
