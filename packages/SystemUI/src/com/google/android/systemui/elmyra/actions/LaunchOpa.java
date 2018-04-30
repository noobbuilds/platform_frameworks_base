package com.google.android.systemui.elmyra.actions;

import android.os.Bundle;
import com.google.android.systemui.AssistManagerGoogle;
import android.net.Uri;
import java.util.function.Consumer;

import android.provider.Settings;
import android.provider.Settings.Secure;
import com.android.systemui.SysUiServiceProvider;
import com.android.systemui.Dependency;
import android.support.annotation.Nullable;
import com.google.android.systemui.elmyra.feedback.FeedbackEffect;
import java.util.List;
import android.content.Context;
import com.android.systemui.statusbar.phone.StatusBar;
import java.util.Random;
import com.google.android.systemui.elmyra.UserContentObserver;
import com.google.android.systemui.OpaEnabledListener;
import android.app.KeyguardManager;
import com.android.systemui.assist.AssistManager;

public class LaunchOpa extends Action
{
    private final AssistManager mAssistManager;
    private boolean mIsGestureEnabled;
    private boolean mIsOpaEnabled;
    private final KeyguardManager mKeyguardManager;
    private final OpaEnabledListener mOpaEnabledListener;
    private final UserContentObserver mSettingsObserver;
    private final Random mSqueezeIdGenerator;
    private StatusBar mStatusBar;
    
    public LaunchOpa(final Context context, @Nullable final List<FeedbackEffect> list) {
        super(context, list);
        this.mSqueezeIdGenerator = new Random();
        this.mOpaEnabledListener = new OpaEnabledListener() {
            @Override
            public void onOpaEnabledReceived(final Context context, final boolean b, final boolean b2, final boolean b3) {
                final boolean b4 = b && b2 && b3;
                if (LaunchOpa.this.mIsOpaEnabled != b4) {
                    LaunchOpa.this.mIsOpaEnabled = b4;
                    LaunchOpa.this.notifyListener();
                }
            }
        };
        this.mAssistManager = Dependency.get(AssistManager.class);
        this.mKeyguardManager = (KeyguardManager)this.getContext().getSystemService(context.KEYGUARD_SERVICE);
        this.mStatusBar = SysUiServiceProvider.getComponent(this.getContext(), StatusBar.class);
        this.mIsGestureEnabled = this.isGestureEnabled();
        this.mSettingsObserver = new UserContentObserver(this.getContext(), Settings.Secure.getUriFor("assist_gesture_enabled"),   new LambdaF((byte)2));
        ((AssistManagerGoogle)this.mAssistManager).addOpaEnabledListener(this.mOpaEnabledListener);
    }
    
    private boolean isGestureEnabled() {
        boolean b = true;
        if (Settings.Secure.getInt(this.getContext().getContentResolver(), "assist_gesture_enabled", 1) == 0) {
            b = false;
        }
        return b;
    }
    
    private void updateGestureEnabled() {
        final boolean gestureEnabled = this.isGestureEnabled();
        if (this.mIsGestureEnabled != gestureEnabled) {
            this.mIsGestureEnabled = gestureEnabled;
            this.notifyListener();
        }
    }
    
    @Override
    public boolean isAvailable() {
        return this.mIsGestureEnabled && this.mIsOpaEnabled;
    }
    
    public void launchOpa() {
        final long nextLong = this.mSqueezeIdGenerator.nextLong();
        final Bundle bundle = new Bundle();
        int n;
        if (this.mKeyguardManager.isKeyguardLocked()) {
            n = 14;
        }
        else {
            n = 13;
        }
        bundle.putInt("triggered_by", n);
        bundle.putLong("latency_id", nextLong);
        this.mAssistManager.startAssist(bundle);
    }
    
    @Override
    public void onProgress(final float n, final int n2) {
        this.updateFeedbackEffects(n, n2);
    }
    
    @Override
    public void onTrigger() {
        if (this.mStatusBar == null) {
            this.mStatusBar = SysUiServiceProvider.getComponent(this.getContext(), StatusBar.class);
        }
        if (this.mStatusBar != null) {
            this.mStatusBar.cancelCurrentTouch();
        }
        this.triggerFeedbackEffects();
        this.launchOpa();
    }
    
    @Override
    public String toString() {
        return super.toString() + " [mIsGestureEnabled -> " + this.mIsGestureEnabled + "; mIsOpaEnabled -> " + this.mIsOpaEnabled + "]";
    }
}
