package com.example.earthapp.data.db.entity;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "users")
public class User {
    @PrimaryKey
    @NonNull
    public String userId;

    public String username;
    public boolean isSelf;
    public long lastSeen;

    public String address;

    public User(@NonNull String userId, String username, String address, boolean isSelf, long lastSeen) {
        this.userId = userId;
        this.username = username;
        this.address = address;
        this.isSelf = isSelf;
        this.lastSeen = lastSeen;
    }
}
