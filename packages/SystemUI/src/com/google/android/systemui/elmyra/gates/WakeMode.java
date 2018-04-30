package com.google.android.systemui.elmyra.gates;

import android.net.Uri;
import java.util.function.Consumer;

import android.provider.Settings;
import android.provider.Settings.Secure;
import android.content.Context;
import com.google.android.systemui.elmyra.UserContentObserver;
import com.google.android.systemui.elmyra.actions.LambdaF;

public class WakeMode extends PowerState
{
    private UserContentObserver mSettingsObserver;
    private boolean mWakeSettingEnabled;
    private Context mContext;
    public WakeMode(final Context context) {
        super(context);
        this.mSettingsObserver = new UserContentObserver(this.getContext(), Settings.Secure.getUriFor("assist_gesture_wake_enabled"), new LambdaF((byte)1));
    }
    
    private boolean isWakeSettingEnabled() {
        boolean b = true;
        if (Settings.Secure.getInt(mContext.getContentResolver(), "assist_gesture_wake_enabled", 1) != 0) {
            b = false;
        }
        return b;
    }
    
    private void updateWakeSetting() {
        final boolean wakeSettingEnabled = this.isWakeSettingEnabled();
        if (wakeSettingEnabled != this.mWakeSettingEnabled) {
            this.mWakeSettingEnabled = wakeSettingEnabled;
            this.notifyListener();
        }
    }
    
    @Override
    protected boolean isBlocked() {
        return !this.mWakeSettingEnabled && super.isBlocked();
    }
    
    @Override
    protected void onActivate() {
        this.mWakeSettingEnabled = this.isWakeSettingEnabled();
        this.mSettingsObserver.activate();
    }
    
    @Override
    protected void onDeactivate() {
        this.mSettingsObserver.deactivate();
    }
    
    @Override
    public String toString() {
        return super.toString() + " [mWakeSettingEnabled -> " + this.mWakeSettingEnabled + "]";
    }
}
