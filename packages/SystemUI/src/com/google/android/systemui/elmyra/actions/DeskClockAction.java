package com.google.android.systemui.elmyra.actions;

import android.content.ActivityNotFoundException;
import android.provider.Settings;
import android.util.Log;
import android.os.Parcelable;
import android.os.Handler;
import android.os.UserHandle;
import android.content.IntentFilter;
import android.net.Uri;
import java.util.function.Consumer;
import android.provider.Settings.Secure;
import android.content.Intent;
import com.google.android.systemui.elmyra.feedback.FeedbackEffect;
import java.util.List;
import android.content.Context;
import com.google.android.systemui.elmyra.UserContentObserver;
import android.content.BroadcastReceiver;

abstract class DeskClockAction extends Action
{
    private boolean mAlertFiring;
    private BroadcastReceiver mAlertReceiver;
    private boolean mReceiverRegistered;
    private UserContentObserver mSettingsObserver;
    
    DeskClockAction(final Context context) {
        super(context, null);
        this.mAlertReceiver = new BroadcastReceiver() {
            public void onReceive(final Context context, Intent intent) {
                if (intent.getAction().equals(DeskClockAction.this.getAlertAction())) {
                    DeskClockAction.this.mAlertFiring = true;
                }
                else if (intent.getAction().equals(DeskClockAction.this.getDoneAction())) {
                    DeskClockAction.this.mAlertFiring = false;
                }
                DeskClockAction.this.notifyListener();
            }
        };
        this.updateBroadcastReceiver();
        this.mSettingsObserver = new UserContentObserver(this.getContext(), Settings.Secure.getUriFor("assist_gesture_silence_alerts_enabled"), new LambdaF((byte)1));
    }
    
    private void updateBroadcastReceiver() {
        this.mAlertFiring = false;
        if (this.mReceiverRegistered) {
            this.getContext().unregisterReceiver(this.mAlertReceiver);
            this.mReceiverRegistered = false;
        }
        int n;
        if (Secure.getInt(this.getContext().getContentResolver(), "assist_gesture_silence_alerts_enabled", 1) != 0) {
            n = 1;
        }
        else {
            n = 0;
        }
        if (n != 0) {
            IntentFilter intentFilter = new IntentFilter();
            intentFilter.addAction(this.getAlertAction());
            intentFilter.addAction(this.getDoneAction());
        //    this.getContext().registerReceiver(this.mAlertReceiver, UserHandle.getUserHandleForUid(1000), intentFilter, "com.android.systemui.permission.SEND_ALERT_BROADCASTS", (Handler)null);
            this.mReceiverRegistered = true;
        }
        this.notifyListener();
    }
    
    protected abstract Intent createDismissIntent();
    
    protected abstract String getAlertAction();
    
    protected abstract String getDoneAction();
    
    @Override
    public boolean isAvailable() {
        return this.mAlertFiring;
    }
    
    @Override
    public void onTrigger() {
        while (true) {
            try {
                Intent dismissIntent = this.createDismissIntent();
                dismissIntent.putExtra("android.intent.extra.REFERRER", (Parcelable)Uri.parse("android-app://" + this.getContext().getPackageName()));
                this.getContext().startActivity(dismissIntent);
                this.mAlertFiring = false;
                this.notifyListener();
            }
            catch (ActivityNotFoundException ex) {
                Log.e("Elmyra/DeskClockAction", "Failed to dismiss alert", (Throwable)ex);
                continue;
            }
            break;
        }
    }
    
    @Override
    public String toString() {
        return super.toString() + " [mReceiverRegistered -> " + this.mReceiverRegistered + "]";
    }
}
