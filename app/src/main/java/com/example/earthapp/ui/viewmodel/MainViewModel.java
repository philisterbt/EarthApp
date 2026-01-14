package com.example.earthapp.ui.viewmodel;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.earthapp.data.db.entity.User;
import com.example.earthapp.data.repository.UserManager;

public class MainViewModel extends AndroidViewModel {
    private final UserManager userManager;
    private final LiveData<User> selfUser;
    private final com.example.earthapp.util.LocationHelper locationHelper;

    public MainViewModel(@NonNull Application application) {
        super(application);
        userManager = new UserManager(application);
        selfUser = userManager.getSelfUser();
        locationHelper = new com.example.earthapp.util.LocationHelper(application);
    }
    
    @Override
    protected void onCleared() {
        super.onCleared();
    }

    public LiveData<User> getSelfUser() {
        return selfUser;
    }

    public void registerUser(String username, String address) {
        userManager.registerUser(username, address);
    }

    public void sendHelp() {
        User user = selfUser.getValue();
        if (user != null) {
            android.location.Location loc = locationHelper.getLastLocation();
            double lat = loc != null ? loc.getLatitude() : 0.0;
            double lon = loc != null ? loc.getLongitude() : 0.0;
            userManager.broadcastMessage("HELP PLEASE!", 1, lat, lon);
        }
    }

    public void sendSafe() {
        User user = selfUser.getValue();
        if (user != null) {
            android.location.Location loc = locationHelper.getLastLocation();
            double lat = loc != null ? loc.getLatitude() : 0.0;
            double lon = loc != null ? loc.getLongitude() : 0.0;
            userManager.broadcastMessage("I am Safe", 0, lat, lon);
        }
    }

    public void sendCustomMessage(String content) {
         User user = selfUser.getValue();
         if (user != null && content != null && !content.isEmpty()) {
            android.location.Location loc = locationHelper.getLastLocation();
            double lat = loc != null ? loc.getLatitude() : 0.0;
            double lon = loc != null ? loc.getLongitude() : 0.0;
             userManager.broadcastMessage(content, 2, lat, lon); 
         }
    }
    
    public com.example.earthapp.util.LocationHelper getLocationHelper() {
        return locationHelper;
    }

    public LiveData<java.util.List<com.example.earthapp.data.db.entity.Message>> getAllMessages() {
        return userManager.getAllMessages();
    }
}
