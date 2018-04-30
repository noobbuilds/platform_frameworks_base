package com.google.android.systemui.ambientmusic;

import android.os.UserHandle;
import android.content.IntentFilter;
import android.os.SystemClock;
import android.app.PendingIntent;
import com.android.keyguard.KeyguardUpdateMonitor;
import android.support.annotation.VisibleForTesting;
import com.android.systemui.util.wakelock.DelayedWakeLock;
import android.util.Log;
import android.content.Intent;
import android.os.Looper;
import com.android.systemui.util.wakelock.WakeLock;
import android.app.AlarmManager.OnAlarmListener;
import android.os.Handler;
import android.content.Context;
import com.android.keyguard.KeyguardUpdateMonitorCallback;
import android.app.AlarmManager;
import android.content.BroadcastReceiver;

public class AmbientIndicationService extends BroadcastReceiver
{
    private AlarmManager mAlarmManager;
    private AmbientIndicationContainer mAmbientIndicationContainer;
    private KeyguardUpdateMonitorCallback mCallback;
    private Context mContext;
    private Handler mHandler;
    private AlarmManager.OnAlarmListener mHideIndicationListener;
    private WakeLock mWakeLock;
    
    public AmbientIndicationService(final Context mContext, AmbientIndicationContainer mAmbientIndicationContainer) {
        this.mCallback = new KeyguardUpdateMonitorCallback() {
            @Override
            public void onUserSwitchComplete(final int n) {
                AmbientIndicationService.this.onUserSwitched();
            }
        };
        this.mContext = mContext;
        this.mAmbientIndicationContainer = mAmbientIndicationContainer;
        this.mHandler = new Handler(Looper.getMainLooper());
        this.mAlarmManager = (AlarmManager)mContext.getSystemService((Class)AlarmManager.class);
        this.mWakeLock = this.createWakeLock(this.mContext, this.mHandler);
        this.mHideIndicationListener = (AlarmManager.OnAlarmListener)new AmbientIndicationService(mContext, mAmbientIndicationContainer);
        this.start();
    }
    
    private boolean verifyAmbientApiVersion(final Intent intent) {
        int intExtra = intent.getIntExtra("com.google.android.ambientindication.extra.VERSION", 0);
        if (intExtra != 1) {
            Log.e("AmbientIndication", "AmbientIndicationApi.EXTRA_VERSION is " + 1 + ", but received an intent with version " + intExtra + ", dropping intent.");
            return false;
        }
        return true;
    }
    
    @VisibleForTesting
    WakeLock createWakeLock(final Context context, Handler handler) {
        return new DelayedWakeLock(handler, WakeLock.createPartial(context, "AmbientIndication"));
    }
    
    @VisibleForTesting
    int getCurrentUser() {
        return KeyguardUpdateMonitor.getCurrentUser();
    }
    
    @VisibleForTesting
    boolean isForCurrentUser() {
        boolean b = true;
        if (this.getCurrentUser() != this.getCurrentUser()) {
            b = (this.getCurrentUser() == -1 && b);
        }
        return b;
    }
    
    public void onReceive(final Context context, Intent intent) {
        //this.mHandler.post(this.mWakeLock.wrap(-$Lambda$W1wWGb46q6ZEHaa6C0H4h9Bj17w.$INST$1));
        if (!this.isForCurrentUser()) {
            return;
        }
        if (!this.verifyAmbientApiVersion(intent)) {
            return;
        }
        String action = intent.getAction();
        if (action.equals("com.google.android.ambientindication.action.AMBIENT_INDICATION_SHOW")) {
            CharSequence charSequenceExtra = intent.getCharSequenceExtra("com.google.android.ambientindication.extra.TEXT");
            PendingIntent pendingIntent = (PendingIntent)intent.getParcelableExtra("com.google.android.ambientindication.extra.OPEN_INTENT");
            long min = Math.min(Math.max(intent.getLongExtra("com.google.android.ambientindication.extra.TTL_MILLIS", 180000L), 0L), 180000L);
            this.mAmbientIndicationContainer.setIndication(charSequenceExtra, pendingIntent);
            this.mAlarmManager.setExact(AlarmManager.ELAPSED_REALTIME_WAKEUP, SystemClock.elapsedRealtime() + min, "AmbientIndication", this.mHideIndicationListener, (Handler)null);
        }
        else if (action.equals("com.google.android.ambientindication.action.AMBIENT_INDICATION_HIDE")) {
            this.mAlarmManager.cancel(this.mHideIndicationListener);
            this.mAmbientIndicationContainer.hideIndication();
        }
    }
    
    @VisibleForTesting
    void onUserSwitched() {
        this.mAmbientIndicationContainer.hideIndication();
    }
    
    @VisibleForTesting
    void start() {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("com.google.android.ambientindication.action.AMBIENT_INDICATION_SHOW");
        intentFilter.addAction("com.google.android.ambientindication.action.AMBIENT_INDICATION_HIDE");
    ///    mContext.registerReceiver((BroadcastReceiver)this, UserHandle.getUserHandleForUid(getCurrentUser()), intentFilter, "com.google.android.ambientindication.permission.AMBIENT_INDICATION", (Handler)null);
        KeyguardUpdateMonitor.getInstance(this.mContext).registerCallback(this.mCallback);
    }
}
