package com.google.android.systemui.elmyra.gates;

import android.net.Uri;
import java.util.function.Consumer;

import android.provider.Settings;
import android.provider.Settings.Secure;
import java.util.Collection;
import java.util.ArrayList;
import android.content.Context;
import com.google.android.systemui.elmyra.UserContentObserver;
import com.google.android.systemui.elmyra.actions.Action;
import com.google.android.systemui.elmyra.actions.LambdaF;
import com.google.android.systemui.elmyra.sensors.GestureSensor;

import java.util.List;

public class KeyguardDeferredSetup extends Gate
{
    private boolean mDeferredSetupComplete;
    private Context mContext;
    private List<Action> mExceptions;
    private KeyguardVisibility mKeyguardGate;
    private Listener mKeyguardGateListener = new Listener() {
        @Override
        public void onGestureDetected(GestureSensor gestureSensor) {

        }

        @Override
        public void onGestureProgress(GestureSensor gestureSensor, float n, int n2) {

        }

        @Override
        public void onGateChanged(final Gate gate) {
            KeyguardDeferredSetup.this.notifyListener();
        }
    };
    private UserContentObserver mSettingsObserver;
    
    public KeyguardDeferredSetup(final Context context, final List<Action> list) {
        super(context);
        this.mExceptions = new ArrayList<Action>(list);
        (this.mKeyguardGate = new KeyguardVisibility(context)).setListener(this.mKeyguardGateListener);
        this.mSettingsObserver = new UserContentObserver(context, Settings.Secure.getUriFor("assist_gesture_setup_complete"), new LambdaF((byte)0));
    }
    
    private boolean isDeferredSetupComplete() {
        boolean b = false;
        final Uri assist_gesture_setup_complete = Secure.getUriFor(Uri.parse("assist_gesture_setup_complete"), "");
        if (assist_gesture_setup_complete != null) {
            b = true;
        }
        return b;
    }
    
    private void updateSetupComplete() {
        final boolean deferredSetupComplete = this.isDeferredSetupComplete();
        if (this.mDeferredSetupComplete != deferredSetupComplete) {
            this.mDeferredSetupComplete = deferredSetupComplete;
            this.notifyListener();
        }
    }
    
    @Override
    protected boolean isBlocked() {
        for (int i = 0; i < this.mExceptions.size(); ++i) {
            if (this.mExceptions.get(i).isAvailable()) {
                return false;
            }
        }
        return !this.mDeferredSetupComplete && this.mKeyguardGate.isBlocking();
    }
    
    public boolean isSuwComplete() {
        return this.mDeferredSetupComplete;
    }
    
    @Override
    protected void onActivate() {
        this.mKeyguardGate.activate();
        this.mDeferredSetupComplete = this.isDeferredSetupComplete();
        this.mSettingsObserver.activate();
    }
    
    @Override
    protected void onDeactivate() {
        this.mKeyguardGate.deactivate();
        this.mSettingsObserver.deactivate();
    }
    
    @Override
    public String toString() {
        return super.toString() + " [mDeferredSetupComplete -> " + this.mDeferredSetupComplete + "]";
    }
}
