package sample;

import javafx.beans.property.SimpleStringProperty;

public class Touple {
    private String name;
    private int messages = 0;
    private int sent = 0;
    private int media = 0;
    private int stickers = 0;
    private int calls = 0;
    private int callTime = 0;
    private long characters = 0;

//    public Touple(String Name, int Messages, int Sent, int Media, int Stickers, int Calls, int CallTime) {
//        this.name = Name;
//        this.messages = new SimpleStringProperty(String.valueOf(Messages));
//        this.sent = new SimpleStringProperty(String.valueOf(Sent));
//        this.recieved = new SimpleStringProperty(String.valueOf(Messages - Sent));
//        this.media = new SimpleStringProperty(String.valueOf(Media));
//        this.stickers = new SimpleStringProperty(String.valueOf(Stickers));
//        this.calls = new SimpleStringProperty(String.valueOf(Calls));
//        this.callTime = new SimpleStringProperty(String.valueOf(CallTime / 60 + "h " + CallTime % 60 + "m"));
//    }

    public Touple(String name, int messages, int sent, int media, int stickers, int calls, int callTime) {
        this.name = name;
        this.messages = messages;
        this.sent = sent;
        this.media = media;
        this.stickers = stickers;
        this.calls = calls;
        this.callTime = callTime;
    }

    public Touple(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public int getMessages() {
        return messages;
    }

    public int getSent() {
        return sent;
    }

    public int getRecieved() {
        return messages - sent;
    }

    public int getMedia() {
        return media;
    }

    public int getStickers() {
        return stickers;
    }

    public int getCalls() {
        return calls;
    }

    public String getCallTime() {
//        return String.valueOf(callTime / 60 + "h " + callTime % 60 + "m");
        return String.valueOf(callTime / 3600  + "h "+(callTime%3600)/60   + "m " + (callTime%3600%60)  + "s");

    }

    public int getCallTimeSeconds() {
        return callTime;
    }

    public void incrementMessages() {
        messages++;
    }

    public void incrementSent() {
        sent++;
    }

    public void incrementMedia() {
        media++;
    }

    public void incrementStickers() {
        stickers++;
    }

    public void incrementCalls() {
        calls++;
    }

    public void incrementCallTime(int seconds) {
        callTime += seconds;
    }

    public void incrementCharacters(int amount) {
        characters += amount;
    }

    public Long getCharacters() {
        return characters;
    }

}
