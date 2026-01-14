package com.example.earthapp.data.db.entity;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "messages")
public class Message {
    @PrimaryKey
    @NonNull
    public String messageId;

    public String senderId;
    public String senderName;
    public String senderAddress;
    public String content;
    public long timestamp;
    public int type;
    public double latitude;
    public double longitude;
    public int ttl;

    public Message(@NonNull String messageId, String senderId, String senderName, String senderAddress, String content, long timestamp, int type, double latitude, double longitude, int ttl) {
        this.messageId = messageId;
        this.senderId = senderId;
        this.senderName = senderName;
        this.senderAddress = senderAddress;
        this.content = content;
        this.timestamp = timestamp;
        this.type = type;
        this.latitude = latitude;
        this.longitude = longitude;
        this.ttl = ttl;
    }
}
