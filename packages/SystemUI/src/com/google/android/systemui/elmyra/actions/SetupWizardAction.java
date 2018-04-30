package com.google.android.systemui.elmyra.actions;

import android.os.UserHandle;
import android.content.Intent;
import android.os.RemoteException;
import android.util.Log;
import java.util.Collections;
import com.android.keyguard.KeyguardUpdateMonitor;
import android.os.UserManager;
import android.content.Context;
import com.android.keyguard.KeyguardUpdateMonitorCallback;
import com.google.android.systemui.elmyra.gates.Gate;
import com.google.android.systemui.elmyra.gates.KeyguardDeferredSetup;
import com.google.android.systemui.elmyra.sensors.GestureSensor;

public class SetupWizardAction extends ServiceAction
{
    private boolean mDeviceInDemoMode;
    private final KeyguardDeferredSetup mKeyguardDeferredSetupGate;
    private final Gate.Listener mKeyguardDeferredSetupListener;
    private boolean mUserCompletedSuw;
    private final KeyguardUpdateMonitorCallback mUserSwitchCallback;
    
    public SetupWizardAction(final Context context, final LaunchOpa launchOpa) {
        super(context, launchOpa);
        this.mUserSwitchCallback = new KeyguardUpdateMonitorCallback() {
            @Override
            public void onUserSwitching(final int n) {
                SetupWizardAction.this.mDeviceInDemoMode = false;
            }
        };
        this.mKeyguardDeferredSetupListener = new Gate.Listener() {
            @Override
            public void onGestureDetected(GestureSensor gestureSensor) {

            }

            @Override
            public void onGestureProgress(GestureSensor gestureSensor, float n, int n2) {

            }

            @Override
            public void onGateChanged(final Gate gate) {
                SetupWizardAction.this.mUserCompletedSuw = ((KeyguardDeferredSetup)gate).isSuwComplete();
            }
        };
        KeyguardUpdateMonitor.getInstance(context).registerCallback(this.mUserSwitchCallback);
        (this.mKeyguardDeferredSetupGate = new KeyguardDeferredSetup(context, Collections.emptyList())).activate();
        this.mKeyguardDeferredSetupGate.setListener(this.mKeyguardDeferredSetupListener);
        this.mUserCompletedSuw = this.mKeyguardDeferredSetupGate.isSuwComplete();
    }
    
    @Override
    public boolean isAvailable() {
        final boolean b = false;
        if (this.mDeviceInDemoMode) {
            return false;
        }
        boolean b2 = b;
        if (this.mLaunchOpa.isAvailable()) {
            b2 = b;
            if (this.mElmyraServiceSettingsListener == null) {
                b2 = (this.mUserCompletedSuw ^ true);
            }
        }
        return b2;
    }
    
    @Override
    public void onProgress(final float n, final int n2) {
        this.updateFeedbackEffects(n, n2);
        if (this.mElmyraServiceSettingsListener == null) {
            return;
        }
        try {
            this.mElmyraServiceSettingsListener.onGestureProgress(n, n2);
        }
        catch (RemoteException ex) {
            Log.e("Elmyra/SetupWizardAction", "Unable to send progress", (Throwable)ex);
        }
    }
    
    @Override
    public void onTrigger() {
        this.triggerFeedbackEffects();
        if (!this.mUserCompletedSuw && this.mElmyraServiceSettingsListener == null) {
            final Intent intent = new Intent();
            intent.setAction("com.google.android.settings.ASSIST_GESTURE_TRAINING");
            intent.setPackage("com.android.settings");
     //       intent.setFlags(268468224);
            this.getContext().startActivity(intent);
        }
    }
}
