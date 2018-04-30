package com.google.android.systemui.elmyra.gates;

import com.android.systemui.Dependency;
import android.content.Context;
import com.android.systemui.statusbar.policy.KeyguardMonitor;

public class KeyguardVisibility extends Gate
{
    private final KeyguardMonitor mKeyguardMonitor;
    private final KeyguardMonitor.Callback mKeyguardMonitorCallback;
    
    public KeyguardVisibility(final Context context) {
        super(context);
        this.mKeyguardMonitorCallback = new KeyguardMonitor.Callback() {
            @Override
            public void onKeyguardShowingChanged() {
                KeyguardVisibility.this.notifyListener();
            }
        };
        this.mKeyguardMonitor = Dependency.get(KeyguardMonitor.class);
    }
    
    @Override
    protected boolean isBlocked() {
        return this.isKeyguardShowing();
    }
    
    public boolean isKeyguardOccluded() {
        return this.mKeyguardMonitor.isOccluded();
    }
    
    public boolean isKeyguardShowing() {
        return this.mKeyguardMonitor.isShowing();
    }
    
    @Override
    protected void onActivate() {
        this.mKeyguardMonitor.addCallback(this.mKeyguardMonitorCallback);
    }
    
    @Override
    protected void onDeactivate() {
        this.mKeyguardMonitor.removeCallback(this.mKeyguardMonitorCallback);
    }
}
