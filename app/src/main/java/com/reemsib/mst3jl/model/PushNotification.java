package com.reemsib.mst3jl.model;

public class PushNotification {

    private String body;
    private String type;
    private String title;
    private String target_id;
    private String msgType;

    public PushNotification() {
    }



    public PushNotification(String body, String type, String title, String target_id, String msgType) {
        this.body = body;
        this.type = type;
        this.title = title;
        this.target_id = target_id;
        this.msgType = msgType;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getTarget_id() {
        return target_id;
    }

    public void setTarget_id(String target_id) {
        this.target_id = target_id;
    }

    public String getMsgType() {
        return msgType;
    }

    public void setMsgType(String msgType) {
        this.msgType = msgType;
    }
}