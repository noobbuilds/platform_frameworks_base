package com.google.android.systemui.elmyra.gates;

import android.content.IntentFilter;
import android.content.Intent;
import android.content.Context;
import android.content.BroadcastReceiver;

import com.android.systemui.R;

public class ChargingState extends TransientGate
{
    private final BroadcastReceiver mPowerReceiver;
    
    public ChargingState(final Context context) {
        super(context, context.getResources().getInteger(R.integer.elmyra_charging_gate_duration));
        this.mPowerReceiver = new BroadcastReceiver() {
            public void onReceive(final Context context, final Intent intent) {
                ChargingState.this.block();
            }
        };
    }
    
    @Override
    protected void onActivate() {
        final IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("android.intent.action.ACTION_POWER_CONNECTED");
        intentFilter.addAction("android.intent.action.ACTION_POWER_DISCONNECTED");
        this.getContext().registerReceiver(this.mPowerReceiver, intentFilter);
    }
    
    @Override
    protected void onDeactivate() {
        this.getContext().unregisterReceiver(this.mPowerReceiver);
    }
}
