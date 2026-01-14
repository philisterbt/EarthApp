package com.example.earthapp.service;

import android.content.Context;
import android.util.Log;

import com.example.earthapp.data.db.AppDatabase;
import com.example.earthapp.data.db.entity.Message;
import com.google.gson.Gson;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MeshServer {
    private static final String TAG = "MeshServer";
    private static final int PORT = 8888;
    private final ExecutorService executor = Executors.newCachedThreadPool();
    private final List<Socket> clientSockets = new ArrayList<>();
    private final AppDatabase db;
    private final Gson gson = new Gson();
    private boolean isRunning = false;
    private ServerSocket serverSocket;

    public MeshServer(Context context) {
        this.db = AppDatabase.getDatabase(context);
    }

    public void start() {
        if (isRunning) return;
        isRunning = true;
        executor.execute(() -> {
            try {
                serverSocket = new ServerSocket(PORT);
                Log.d(TAG, "Server started on port " + PORT);
                while (isRunning) {
                    Socket client = serverSocket.accept();
                    Log.d(TAG, "Client connected: " + client.getInetAddress());
                    synchronized (clientSockets) {
                        clientSockets.add(client);
                    }
                    executor.execute(new ClientHandler(client));
                }
            } catch (IOException e) {
                Log.e(TAG, "Server Error", e);
            }
        });
    }

    public void broadcastMessage(Message message) {
        String json = gson.toJson(message);
        synchronized (clientSockets) {
            for (Socket socket : clientSockets) {
                sendToSocket(socket, json);
            }
        }
    }

    private void sendToSocket(Socket socket, String json) {
        executor.execute(() -> {
            try {
                DataOutputStream dos = new DataOutputStream(socket.getOutputStream());
                dos.writeUTF(json);
                dos.flush();
            } catch (IOException e) {
                Log.e(TAG, "Send Error", e);
            }
        });
    }

    public void stop() {
        isRunning = false;
        try {
            if (serverSocket != null) serverSocket.close();
            synchronized (clientSockets) {
                for (Socket s : clientSockets) s.close();
                clientSockets.clear();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private class ClientHandler implements Runnable {
        private final Socket socket;

        public ClientHandler(Socket socket) {
            this.socket = socket;
        }

        @Override
        public void run() {
            try {
                DataInputStream dis = new DataInputStream(socket.getInputStream());
                while (isRunning && !socket.isClosed()) {
                    String json = dis.readUTF();
                    Log.d(TAG, "Received msg: " + json);
                    Message msg = gson.fromJson(json, Message.class);
                    
                    AppDatabase.databaseWriteExecutor.execute(() -> {
                        db.messageDao().insertMessage(msg);
                    });

                    if (msg.ttl > 0) {
                        msg.ttl--;
                        broadcastMessage(msg);
                    }
                }
            } catch (IOException e) {
                Log.e(TAG, "Client Handler Error", e);
            } finally {
                synchronized (clientSockets) {
                    clientSockets.remove(socket);
                }
            }
        }
    }
}
