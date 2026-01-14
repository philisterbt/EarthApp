package com.example.earthapp.service;

import android.content.Context;
import android.util.Log;

import com.example.earthapp.data.db.AppDatabase;
import com.example.earthapp.data.db.entity.Message;
import com.google.gson.Gson;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MeshClient {
    private static final String TAG = "MeshClient";
    private static final int PORT = 8888;
    private final ExecutorService executor = Executors.newSingleThreadExecutor();
    private final AppDatabase db;
    private final Gson gson = new Gson();
    private Socket socket;
    private boolean isRunning = false;

    public MeshClient(Context context) {
        this.db = AppDatabase.getDatabase(context);
    }

    public void connect(InetAddress hostAddress) {
        executor.execute(() -> {
            try {
                socket = new Socket();
                socket.bind(null);
                socket.connect(new InetSocketAddress(hostAddress, PORT), 5000);
                Log.d(TAG, "Connected to Host: " + hostAddress);
                isRunning = true;
                
                readLoop();

            } catch (IOException e) {
                Log.e(TAG, "Connection Failed", e);
            }
        });
    }
    
    public void sendMessage(Message message) {
        if (socket == null || !socket.isConnected()) return;
        executor.execute(() -> {
            try {
                String json = gson.toJson(message);
                DataOutputStream dos = new DataOutputStream(socket.getOutputStream());
                dos.writeUTF(json);
                dos.flush();
            } catch (IOException e) {
                Log.e(TAG, "Send Failed", e);
            }
        });
    }

    private void readLoop() {
        try {
            DataInputStream dis = new DataInputStream(socket.getInputStream());
            while (isRunning && socket.isConnected()) {
                String json = dis.readUTF();
                Log.d(TAG, "Client Received: " + json);
                Message msg = gson.fromJson(json, Message.class);
                
                // Save to DB
                AppDatabase.databaseWriteExecutor.execute(() -> {
                    db.messageDao().insertMessage(msg);
                });
            }
        } catch (IOException e) {
            Log.e(TAG, "Read Loop Error", e);
        }
    }

    public void stop() {
        isRunning = false;
        try {
            if (socket != null) socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
