package com.example.earthapp.service;

import android.content.Context;
import android.content.IntentFilter;
import android.net.wifi.p2p.WifiP2pConfig;
import android.net.wifi.p2p.WifiP2pInfo;
import android.net.wifi.p2p.WifiP2pManager;
import android.os.Looper;
import android.util.Log;

public class MeshManager implements WifiP2pManager.ConnectionInfoListener {

    private static final String TAG = "MeshManager";
    private final Context context;
    private final WifiP2pManager manager;
    private final WifiP2pManager.Channel channel;
    private final MeshBroadcastReceiver receiver;
    private final IntentFilter intentFilter;
    
    private final MeshServer meshServer;
    private final MeshClient meshClient;
    
    private static MeshManager instance;

    public static MeshManager getInstance(Context context) {
        if (instance == null) instance = new MeshManager(context.getApplicationContext());
        return instance;
    }

    private MeshManager(Context context) {
        this.context = context;
        this.manager = (WifiP2pManager) context.getSystemService(Context.WIFI_P2P_SERVICE);
        this.channel = manager.initialize(context, Looper.getMainLooper(), null);
        this.receiver = new MeshBroadcastReceiver(manager, channel, this);
        
        this.meshServer = new MeshServer(context);
        this.meshClient = new MeshClient(context);

        intentFilter = new IntentFilter();
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION);
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION);
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION);
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION);
    }

    public void start() {
        context.registerReceiver(receiver, intentFilter);
        discoverPeers();
    }

    public void stop() {
        try {
            context.unregisterReceiver(receiver);
        } catch (IllegalArgumentException e) {
            // Already unregistered
        }
    }

    public void discoverPeers() {
        manager.discoverPeers(channel, new WifiP2pManager.ActionListener() {
            @Override
            public void onSuccess() {
                Log.d(TAG, "Discovery Initiated");
            }

            @Override
            public void onFailure(int reasonCode) {
                Log.d(TAG, "Discovery Failed : " + reasonCode);
            }
        });
    }

    public void connect(WifiP2pConfig config) {
        manager.connect(channel, config, new WifiP2pManager.ActionListener() {
            @Override
            public void onSuccess() {

            }

            @Override
            public void onFailure(int reason) {
                Log.d(TAG, "Connect failed. Retry.");
            }
        });
    }

    @Override
    public void onConnectionInfoAvailable(WifiP2pInfo info) {
        Log.d(TAG, "Connection Info Available: " + info.toString());

        if (info.groupFormed && info.isGroupOwner) {
            Log.d(TAG, "Host connected - Start Server");
            meshServer.start();
        } else if (info.groupFormed) {
             Log.d(TAG, "Client connected - Start Client");
             meshClient.connect(info.groupOwnerAddress);
        }
    }
    
    public void sendMessage(com.example.earthapp.data.db.entity.Message message) {
        // Build logic to send via Server (multicast) or Client (unicast to GO)
        // Ideally we check if we are server or client
        // For now, simplify: try both or check state
        meshServer.broadcastMessage(message); 
        meshClient.sendMessage(message);
    }
}

