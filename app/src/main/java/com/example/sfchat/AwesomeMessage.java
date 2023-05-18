package com.example.sfchat;

public class AwesomeMessage {

    private String text;
    private String name;
    private String imageUrl;
    private String sender;
    private String getter;
    private boolean isMine;


    public AwesomeMessage() {
    }

    public AwesomeMessage(String text, String name, String imageUrl, String sender, String getter, boolean isMine) {
        this.text = text;
        this.name = name;
        this.imageUrl = imageUrl;
        this.sender = sender;
        this.getter = getter;
        this.isMine = isMine;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getGetter() {
        return getter;
    }

    public void setGetter(String getter) {
        this.getter = getter;
    }

    public boolean isMine() {
        return isMine;
    }

    public void setMine(boolean mine) {
        isMine = mine;
    }
}
