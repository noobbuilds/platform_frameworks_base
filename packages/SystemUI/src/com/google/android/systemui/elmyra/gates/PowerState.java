package com.google.android.systemui.elmyra.gates;

import com.android.keyguard.KeyguardUpdateMonitor;
import android.content.Context;
import android.os.PowerManager;
import com.android.keyguard.KeyguardUpdateMonitorCallback;

public class PowerState extends Gate
{
    private final KeyguardUpdateMonitorCallback mKeyguardUpdateMonitorCallback;
    private final PowerManager mPowerManager;
    
    public PowerState(final Context context) {
        super(context);
        this.mKeyguardUpdateMonitorCallback = new KeyguardUpdateMonitorCallback() {
            @Override
            public void onFinishedGoingToSleep(final int n) {
                PowerState.this.notifyListener();
            }
            
            @Override
            public void onStartedWakingUp() {
                PowerState.this.notifyListener();
            }
        };
        this.mPowerManager = (PowerManager)context.getSystemService(context.POWER_SERVICE);
    }
    
    @Override
    protected boolean isBlocked() {
        return this.mPowerManager.isInteractive() ^ true;
    }
    
    @Override
    protected void onActivate() {
        KeyguardUpdateMonitor.getInstance(this.getContext()).registerCallback(this.mKeyguardUpdateMonitorCallback);
    }
    
    @Override
    protected void onDeactivate() {
        KeyguardUpdateMonitor.getInstance(this.getContext()).removeCallback(this.mKeyguardUpdateMonitorCallback);
    }
}
