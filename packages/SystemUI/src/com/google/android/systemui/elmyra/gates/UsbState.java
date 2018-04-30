package com.google.android.systemui.elmyra.gates;

import android.content.IntentFilter;
import android.content.Intent;
import android.content.Context;
import android.content.BroadcastReceiver;

import com.android.systemui.R;

public class UsbState extends TransientGate
{
    private boolean mUsbConnected;
    private final BroadcastReceiver mUsbReceiver;
    
    public UsbState(final Context context) {
        super(context, context.getResources().getInteger(R.integer.elmyra_usb_gate_duration));
        this.mUsbReceiver = new BroadcastReceiver() {
            public void onReceive(final Context context, final Intent intent) {
                final boolean booleanExtra = intent.getBooleanExtra("connected", false);
                if (booleanExtra != UsbState.this.mUsbConnected) {
                    UsbState.this.mUsbConnected = booleanExtra;
                    UsbState.this.block();
                }
            }
        };
    }
    
    @Override
    protected void onActivate() {
        final IntentFilter intentFilter = new IntentFilter("android.hardware.usb.action.USB_STATE");
        final Intent registerReceiver = this.getContext().registerReceiver((BroadcastReceiver)null, intentFilter);
        if (registerReceiver != null) {
            this.mUsbConnected = registerReceiver.getBooleanExtra("connected", false);
        }
        this.getContext().registerReceiver(this.mUsbReceiver, intentFilter);
    }
    
    @Override
    protected void onDeactivate() {
        this.getContext().unregisterReceiver(this.mUsbReceiver);
    }
}
