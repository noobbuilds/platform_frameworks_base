package com.google.android.systemui;

import com.android.keyguard.KeyguardUpdateMonitor;
import android.content.Context;
import com.android.systemui.statusbar.policy.DeviceProvisionedController;
import com.android.keyguard.KeyguardUpdateMonitorCallback;
import com.android.systemui.assist.AssistManager;

public class AssistManagerGoogle extends AssistManager
{
    private final OpaEnabledDispatcher mOpaEnabledDispatcher;
    private final OpaEnabledReceiver mOpaEnabledReceiver;
    private final KeyguardUpdateMonitorCallback mUserSwitchCallback;
    
    public AssistManagerGoogle(final DeviceProvisionedController deviceProvisionedController, final Context context) {
        super(deviceProvisionedController, context);
        this.mUserSwitchCallback = new KeyguardUpdateMonitorCallback() {
            @Override
            public void onUserSwitching(final int n) {
                AssistManagerGoogle.this.mOpaEnabledReceiver.onUserSwitching(n);
            }
        };
        this.mOpaEnabledReceiver = new OpaEnabledReceiver(this.mContext);
        this.addOpaEnabledListener(this.mOpaEnabledDispatcher = new OpaEnabledDispatcher());
        KeyguardUpdateMonitor.getInstance(this.mContext).registerCallback(this.mUserSwitchCallback);
    }
    
    public void addOpaEnabledListener(final OpaEnabledListener opaEnabledListener) {
        this.mOpaEnabledReceiver.addOpaEnabledListener(opaEnabledListener);
    }
    
    public void dispatchOpaEnabledState() {
        this.mOpaEnabledReceiver.dispatchOpaEnabledState();
    }
    
    public boolean shouldShowOrb() {
        return false;
    }
}
