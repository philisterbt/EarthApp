package com.example.earthapp.data.db.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.example.earthapp.data.db.entity.User;

import java.util.List;

@Dao
public interface UserDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertUser(User user);

    @Query("SELECT * FROM users WHERE userId = :userId")
    User getUser(String userId);

    @Query("SELECT * FROM users WHERE isSelf = 1 LIMIT 1")
    LiveData<User> getSelfUser();
    
    @Query("SELECT * FROM users WHERE isSelf = 1 LIMIT 1")
    User getSelfUserSync();

    @Query("SELECT * FROM users")
    LiveData<List<User>> getAllUsers();
}
