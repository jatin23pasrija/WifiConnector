package com.thedevians.wificonnector;

import android.content.Context;
import android.graphics.Typeface;
import android.net.wifi.ScanResult;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class WifiListAdapter extends RecyclerView.Adapter<WifiListAdapter.ViewHolder> {
    private Context context;
    private List<ScanResult> scanResultList;
    private String connectedSSID;

    public WifiListAdapter(Context context, List<ScanResult> scanResultList, String connectedSSID) {
        this.context = context;
        this.scanResultList = scanResultList;
        this.connectedSSID = connectedSSID;
    }

    public WifiListAdapter(Context context, List<ScanResult> scanResultList) {
        this.context = context;
        this.scanResultList = scanResultList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(context).inflate(R.layout.item_wifi, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ScanResult scanResult = scanResultList.get(position);
        holder.txtName.setText(scanResult.SSID);
        holder.txtPassword.setText(scanResult.capabilities);
        if (connectedSSID != null) {
            Log.d("appLogger", connectedSSID);
            Log.d("appLogger1", scanResult.SSID);
            if (scanResult.SSID.equals(connectedSSID)) {
                holder.txtName.setTypeface(holder.txtName.getTypeface(), Typeface.BOLD);
                holder.txtName.setAllCaps(true);
            }
        }
    }

    @Override
    public int getItemCount() {
        return scanResultList.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        private TextView txtName;
        private TextView txtPassword;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            txtName = itemView.findViewById(R.id.txtName);
            txtPassword = itemView.findViewById(R.id.txtHasWifiPassword);
        }
    }
}
