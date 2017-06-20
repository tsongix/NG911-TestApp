package com.android.albert.ng911bluetoothtester;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

/**
 * Created by Albert on 2/1/2016.TBC
 * Check bluetooth status and turn it on if it is turned off.
 */
public class BluetoothChecker {

    private Context context;    //application context
    private BluetoothManager bluemgr;
    private BluetoothDevice dev;
    private BluetoothAdapter badapt;

    public BluetoothChecker(Context c) {
        bluemgr = (BluetoothManager) c.getSystemService(Context.BLUETOOTH_SERVICE);
        context = c;
        badapt = BluetoothAdapter.getDefaultAdapter();
    }

    /*
     * Checks if Bluetooth is enabled
     * If it's not, enables Bluetooth
     * Returns a message showing the results
     */
    public boolean enableBluetooth() {
        if (badapt == null) {
            // Device does not support Bluetooth
            Toast.makeText(context, "Device do not have Bluetooth", Toast.LENGTH_SHORT).show();
            Log.i("[enableBluetooth()]", "Device do not have Bluetooth functionality.");
            return false;
        }
        if (!badapt.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            enableBtIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            Log.i("[enableBluetooth()]", "Bluetooth is NOT enabled, enabling Bluetooth...");
            context.startActivity(enableBtIntent);
        }
        if (badapt.isEnabled()) {
            Log.i("[enableBluetooth()]", "Bluetooth is already enabled.");
        }
        return true;
    }
}



