package ru.gb.lesson5.message;

public class Message {
    String from = "";
    String to = "";
    String message = "";
    boolean broadcast;
    boolean exit;

    public boolean isExit() {
        return exit;
    }

    public void setExit(boolean exit) {
        this.exit = exit;
    }

    public Message() {
    }

    public Message(String from, String to, String message, boolean broadcast) {
        this.from = from;
        this.to = to;
        this.message = message;
        this.broadcast = broadcast;
    }

    public Message(String from) {
        this.from = from;
    }

    public Message(String from, String message) {
        this.from = from;
        this.message = message;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public boolean isBroadcast() {
        return broadcast;
    }

    public void setBroadcast(boolean broadcast) {
        this.broadcast = broadcast;
    }
}
