package com.example.earthapp.ui.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.earthapp.R;
import com.example.earthapp.data.db.entity.Message;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.MessageViewHolder> {

    private List<Message> messages = new ArrayList<>();
    private final SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss", Locale.getDefault());
    private android.location.Location currentLocation;

    public void setMessages(List<Message> messages) {
        this.messages = messages;
        notifyDataSetChanged();
    }
    
    public void setCurrentLocation(android.location.Location location) {
        this.currentLocation = location;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public MessageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(android.R.layout.simple_list_item_2, parent, false);
        return new MessageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MessageViewHolder holder, int position) {
        Message msg = messages.get(position);
        String prefix = "";
        if (msg.type == 1) prefix = "[HELP] ";
        else if (msg.type == 0) prefix = "[SAFE] ";
        else prefix = "[MSG] ";
        
        String distStr = "";
        if (currentLocation != null && msg.latitude != 0.0 && msg.longitude != 0.0) {
            float[] results = new float[1];
            android.location.Location.distanceBetween(currentLocation.getLatitude(), currentLocation.getLongitude(), msg.latitude, msg.longitude, results);
            distStr = String.format(Locale.getDefault(), " (%.0fm)", results[0]);
        }
        
        holder.text1.setText(prefix + msg.senderName + distStr);
        holder.text2.setText(msg.content + " (" + sdf.format(new Date(msg.timestamp)) + ")");
        
        holder.text1.setOnClickListener(v -> {
            String address = msg.senderAddress;
            if (address != null && !address.isEmpty()) {
                new androidx.appcompat.app.AlertDialog.Builder(v.getContext())
                    .setTitle(msg.senderName)
                    .setMessage("Address: " + address)
                    .setPositiveButton("OK", null)
                    .show();
            } else {
                android.widget.Toast.makeText(v.getContext(), "Address not available for " + msg.senderName, android.widget.Toast.LENGTH_SHORT).show();
            }
        });

        if (msg.type == 1) { // HELP
            holder.text1.setTextColor(android.graphics.Color.RED);
        } else if (msg.type == 0) { // SAFE
            holder.text1.setTextColor(android.graphics.Color.GREEN);
        } else { // CUSTOM
             holder.text1.setTextColor(android.graphics.Color.BLUE);
        }
    }

    @Override
    public int getItemCount() {
        return messages.size();
    }

    static class MessageViewHolder extends RecyclerView.ViewHolder {
        TextView text1, text2;

        public MessageViewHolder(@NonNull View itemView) {
            super(itemView);
            text1 = itemView.findViewById(android.R.id.text1);
            text2 = itemView.findViewById(android.R.id.text2);
        }
    }
}
