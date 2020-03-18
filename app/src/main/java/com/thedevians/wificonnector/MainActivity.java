package com.thedevians.wificonnector;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.content.Context;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.nabinbhandari.android.permissions.PermissionHandler;
import com.nabinbhandari.android.permissions.Permissions;
import com.thanosfisherman.wifiutils.WifiUtils;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private Button btnSearch;
    private WifiListAdapter wifiListAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        recyclerView = findViewById(R.id.recyclerWifi);
        btnSearch = findViewById(R.id.btnSearch);
        btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String[] permissions = {Manifest.permission.ACCESS_COARSE_LOCATION
                        , Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_WIFI_STATE
                        , Manifest.permission.CHANGE_WIFI_STATE};
                Permissions.check(MainActivity.this/*context*/, permissions, null/*rationale*/, null/*options*/, new PermissionHandler() {
                    @Override
                    public void onGranted() {
                        WifiUtils.withContext(getApplicationContext()).enableWifi(isSuccess -> {
                            Toast.makeText(MainActivity.this, "Wifi enabled, Scanning", Toast.LENGTH_SHORT).show();
                        });
                        WifiManager wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);

                        onScan(wifiManager);
                    }

                    @Override
                    public void onDenied(Context context, ArrayList<String> deniedPermissions) {
                        super.onDenied(context, deniedPermissions);
                        Toast.makeText(context, "Provide permissions to continue", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }

    private void onScan(WifiManager wifiManager) {
        recyclerView.setAdapter(null);

        if (wifiManager != null && wifiManager.isWifiEnabled()) {
            WifiUtils.withContext(getApplicationContext()).scanWifi(scanResults -> {
                if (scanResults.isEmpty()) {
                    WifiUtils.withContext(getApplicationContext()).disableWifi();
                    Toast.makeText(MainActivity.this, "No Found", Toast.LENGTH_SHORT).show();
                    return;
                }
                for (int i = 0; i < scanResults.size(); i++) {
                    Log.d("appLogger", scanResults.get(i).capabilities);
                    String ssid = scanResults.get(i).SSID;
                    if (scanResults.get(i).capabilities.equals("[ESS]")) {
                        WifiUtils.withContext(getApplicationContext())
                                .connectWith(scanResults.get(i).SSID, "")
                                .setTimeout(15000)
                                .onConnectionResult(isSuccess -> {
                                    Toast.makeText(MainActivity.this, "Connected to Open Network " +
                                            ssid, Toast.LENGTH_SHORT).show();
                                    WifiInfo info = wifiManager.getConnectionInfo();
                                    String connectedSSID = info.getSSID();
                                    String nSID = connectedSSID.replace("\"", "");
                                    wifiListAdapter = new WifiListAdapter(getApplicationContext(), scanResults, nSID);
                                    recyclerView.setAdapter(wifiListAdapter);
                                })
                                .start();
                        break;
                    }
                }
                recyclerView.setLayoutManager(new LinearLayoutManager(MainActivity.this));
                wifiListAdapter = new WifiListAdapter(getApplicationContext(), scanResults);
                recyclerView.setAdapter(wifiListAdapter);
            }).start();

        }

    }


}
