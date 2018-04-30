package com.google.android.systemui.elmyra.gates;

import com.android.systemui.Dependency;
import android.content.Context;
import com.android.systemui.statusbar.policy.DeviceProvisionedController;

public class SetupWizard extends Gate
{
    private final DeviceProvisionedController mProvisionedController;
    private final DeviceProvisionedController.DeviceProvisionedListener mProvisionedListener;
    private boolean mSetupComplete;
    
    public SetupWizard(final Context context) {
        super(context);
        this.mProvisionedListener = new DeviceProvisionedController.DeviceProvisionedListener() {
            private void updateSetupComplete() {
                final boolean setupComplete = SetupWizard.this.isSetupComplete();
                if (setupComplete != SetupWizard.this.mSetupComplete) {
                    SetupWizard.this.mSetupComplete = setupComplete;
                    SetupWizard.this.notifyListener();
                }
            }
            
            @Override
            public void onDeviceProvisionedChanged() {
                this.updateSetupComplete();
            }
            
            @Override
            public void onUserSetupChanged() {
                this.updateSetupComplete();
            }
        };
        this.mProvisionedController = Dependency.get(DeviceProvisionedController.class);
    }
    
    private boolean isSetupComplete() {
        return this.mProvisionedController.isDeviceProvisioned() && this.mProvisionedController.isCurrentUserSetup();
    }
    
    @Override
    protected boolean isBlocked() {
        return this.mSetupComplete ^ true;
    }
    
    @Override
    protected void onActivate() {
        this.mSetupComplete = this.isSetupComplete();
        this.mProvisionedController.addCallback(this.mProvisionedListener);
    }
    
    @Override
    protected void onDeactivate() {
        this.mProvisionedController.removeCallback(this.mProvisionedListener);
    }
    
    @Override
    public String toString() {
        return super.toString() + " [isDeviceProvisioned -> " + this.mProvisionedController.isDeviceProvisioned() + "; isCurrentUserSetup -> " + this.mProvisionedController.isCurrentUserSetup() + "]";
    }
}
