package com.example.earthapp;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;

import com.example.earthapp.ui.viewmodel.MainViewModel;
import com.google.android.material.textfield.TextInputEditText;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private MainViewModel viewModel;
    private View layoutOnboarding;
    private View layoutDashboard;
    private TextInputEditText etUsername;
    private TextInputEditText etAddress;

    private static final String[] REQUIRED_PERMISSIONS;

    static {
        List<String> perms = new ArrayList<>();
        perms.add(Manifest.permission.ACCESS_FINE_LOCATION);
        perms.add(Manifest.permission.ACCESS_COARSE_LOCATION);
        perms.add(Manifest.permission.NEARBY_WIFI_DEVICES);
        perms.add(Manifest.permission.BLUETOOTH_SCAN);
        perms.add(Manifest.permission.BLUETOOTH_ADVERTISE);
        perms.add(Manifest.permission.BLUETOOTH_CONNECT);
        if (Build.VERSION.SDK_INT >= 33) { // Android 13+
             perms.add(Manifest.permission.POST_NOTIFICATIONS);
        }
        REQUIRED_PERMISSIONS = perms.toArray(new String[0]);
    }
    private static final int PERMISSION_REQUEST_CODE = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        viewModel = new ViewModelProvider(this).get(MainViewModel.class);

        layoutOnboarding = findViewById(R.id.layout_onboarding);
        layoutDashboard = findViewById(R.id.layout_dashboard);
        etUsername = findViewById(R.id.et_username);
        etAddress = findViewById(R.id.et_address);
        Button btnJoin = findViewById(R.id.btn_join);

        // Setup RecyclerView
        // Setup RecyclerViews
        androidx.recyclerview.widget.RecyclerView recyclerHelp = findViewById(R.id.recycler_help);
        androidx.recyclerview.widget.RecyclerView recyclerSafe = findViewById(R.id.recycler_safe);
        
        recyclerHelp.setLayoutManager(new androidx.recyclerview.widget.LinearLayoutManager(this));
        recyclerSafe.setLayoutManager(new androidx.recyclerview.widget.LinearLayoutManager(this));

        com.example.earthapp.ui.adapter.MessageAdapter adapterHelp = new com.example.earthapp.ui.adapter.MessageAdapter();
        com.example.earthapp.ui.adapter.MessageAdapter adapterSafe = new com.example.earthapp.ui.adapter.MessageAdapter();
        
        recyclerHelp.setAdapter(adapterHelp);
        recyclerSafe.setAdapter(adapterSafe);

        // Buttons
        Button btnHelp = findViewById(R.id.btn_help);
        Button btnSafe = findViewById(R.id.btn_safe);
        
        // Chat UI
        EditText etMessage = findViewById(R.id.et_message);
        Button btnSend = findViewById(R.id.btn_send);

        btnHelp.setOnClickListener(v -> viewModel.sendHelp());
        btnSafe.setOnClickListener(v -> viewModel.sendSafe());
        
        btnSend.setOnClickListener(v -> {
            String msg = etMessage.getText().toString().trim();
            if (!msg.isEmpty()) {
                viewModel.sendCustomMessage(msg);
                etMessage.setText(""); // Clear input

            }
        });

        btnJoin.setOnClickListener(v -> {
            String name = etUsername.getText().toString().trim();
            String address = etAddress.getText().toString().trim();
            if (!name.isEmpty()) {
                viewModel.registerUser(name, address);
            } else {
                Toast.makeText(this, "Please enter your name", Toast.LENGTH_SHORT).show();
            }
        });

        // Observer self user to switch screens
        viewModel.getSelfUser().observe(this, user -> {
            if (user != null) {
                // User is registered
                layoutOnboarding.setVisibility(View.GONE);
                layoutDashboard.setVisibility(View.VISIBLE);
                checkPermissions();
            } else {
                // User not registered
                layoutOnboarding.setVisibility(View.VISIBLE);
                layoutDashboard.setVisibility(View.GONE);
            }
        });
        
        // Observe messages
        // Observe messages
        viewModel.getAllMessages().observe(this, messages -> {
            List<com.example.earthapp.data.db.entity.Message> helpList = new ArrayList<>();
            List<com.example.earthapp.data.db.entity.Message> safeList = new ArrayList<>();
            
            for (com.example.earthapp.data.db.entity.Message m : messages) {
                if (m.type == 1) { // HELP
                    helpList.add(m);
                } else {
                    safeList.add(m);
                }
            }
            
            adapterHelp.setMessages(helpList);
            adapterSafe.setMessages(safeList);
            
            if (helpList.size() > 0) recyclerHelp.smoothScrollToPosition(helpList.size() - 1);
            if (safeList.size() > 0) recyclerSafe.smoothScrollToPosition(safeList.size() - 1);
        });
        
        // Observe Location for Distance Calculation
        viewModel.getLocationHelper().observe(this, location -> {
            if (location != null) {
                adapterHelp.setCurrentLocation(location);
                adapterSafe.setCurrentLocation(location);
            }
        });
    }

    private void checkPermissions() {
        List<String> missingPermissions = new ArrayList<>();
        for (String perm : REQUIRED_PERMISSIONS) {
            if (ContextCompat.checkSelfPermission(this, perm) != PackageManager.PERMISSION_GRANTED) {
                missingPermissions.add(perm);
            }
        }

        if (!missingPermissions.isEmpty()) {
            ActivityCompat.requestPermissions(this, missingPermissions.toArray(new String[0]), PERMISSION_REQUEST_CODE);
        } else {
            startMeshService();
        }
    }
    
    private void startMeshService() {
        android.content.Intent serviceIntent = new android.content.Intent(this, com.example.earthapp.service.MeshService.class);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(serviceIntent);
        } else {
            startService(serviceIntent);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUEST_CODE) {
           // Handle rejection gracefully in real app
        }
    }
}