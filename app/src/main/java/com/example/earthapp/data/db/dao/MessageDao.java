package com.example.earthapp.data.db.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.example.earthapp.data.db.entity.Message;

import java.util.List;

@Dao
public interface MessageDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insertMessage(Message message);

    @Query("SELECT * FROM messages ORDER BY timestamp DESC")
    LiveData<List<Message>> getAllMessages();

    @Query("SELECT * FROM messages WHERE messageId = :id")
    Message getMessage(String id);
}
