package com.example.earthapp.data.repository;

import android.content.Context;
import androidx.lifecycle.LiveData;
import com.example.earthapp.data.db.AppDatabase;
import com.example.earthapp.data.db.entity.User;
import java.util.UUID;
import java.util.concurrent.ExecutorService;

public class UserManager {
    private final AppDatabase db;
    private final ExecutorService executor;
    private final Context context;

    public UserManager(Context context) {
        this.context = context.getApplicationContext();
        this.db = AppDatabase.getDatabase(context);
        this.executor = AppDatabase.databaseWriteExecutor;
    }

    public LiveData<User> getSelfUser() {
        return db.userDao().getSelfUser();
    }
    
    public User getSelfUserSync() {
        return db.userDao().getSelfUserSync();
    }

    public void registerUser(String username, String address) {
        executor.execute(() -> {
            String uuid = UUID.randomUUID().toString();
            User user = new User(uuid, username, address, true, System.currentTimeMillis());
            db.userDao().insertUser(user);
        });
    }

    public LiveData<java.util.List<com.example.earthapp.data.db.entity.Message>> getAllMessages() {
        return db.messageDao().getAllMessages();
    }

    public void broadcastMessage(String content, int type, double lat, double lon) {
        executor.execute(() -> {
            User self = getSelfUserSync();
            if (self == null) return;

            String msgId = UUID.randomUUID().toString();
            com.example.earthapp.data.db.entity.Message msg = new com.example.earthapp.data.db.entity.Message(
                    msgId, self.userId, self.username, self.address, content, System.currentTimeMillis(), type, lat, lon, 3
            );
            

            db.messageDao().insertMessage(msg);
            

            com.example.earthapp.service.MeshManager.getInstance(context).sendMessage(msg); 
        });
    }
}
