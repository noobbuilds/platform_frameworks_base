package com.google.android.systemui;

import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.content.IntentFilter;
import android.os.UserHandle;
import android.os.RemoteException;
import android.provider.Settings;
import android.provider.Settings.Secure;
import android.content.ComponentName;
import com.android.internal.app.AssistUtils;
import android.util.Log;
import com.android.internal.widget.ILockSettings.Stub;
import android.os.ServiceManager;
import java.util.ArrayList;
import com.android.internal.widget.ILockSettings;
import java.util.List;
import android.content.Context;
import android.content.ContentResolver;
import android.database.ContentObserver;
import android.content.BroadcastReceiver;

public class OpaEnabledReceiver
{
    private final BroadcastReceiver mBroadcastReceiver;
    private final ContentObserver mContentObserver;
    private final ContentResolver mContentResolver;
    private final Context mContext;
    private boolean mIsAGSAAssistant;
    private boolean mIsOpaEligible;
    private boolean mIsOpaEnabled;
    private final List<OpaEnabledListener> mListeners;
    private final ILockSettings mLockSettings;
    
    public OpaEnabledReceiver(final Context mContext) {
        this.mBroadcastReceiver = new OpaEnabledBroadcastReceiver((OpaEnabledBroadcastReceiver)null);
        this.mListeners = new ArrayList<OpaEnabledListener>();
        this.mContext = mContext;
        this.mContentResolver = this.mContext.getContentResolver();
        this.mContentObserver = new AssistantContentObserver(this.mContext);
        this.mLockSettings = ILockSettings.Stub.asInterface(ServiceManager.getService("lock_settings"));
        this.updateOpaEnabledState(this.mContext);
        this.registerContentObserver();
        this.registerEnabledReceiver(-2);
    }
    
    private void dispatchOpaEnabledState(final Context context) {
        Log.i("OpaEnabledReceiver", "Dispatching OPA eligble = " + this.mIsOpaEligible + "; AGSA = " + this.mIsAGSAAssistant + "; OPA enabled = " + this.mIsOpaEnabled);
        for (int i = 0; i < this.mListeners.size(); ++i) {
            this.mListeners.get(i).onOpaEnabledReceived(context, this.mIsOpaEligible, this.mIsAGSAAssistant, this.mIsOpaEnabled);
        }
    }
    
    private boolean isAGSACurrentAssistant(final Context context) {
        final ComponentName assistComponentForUser = new AssistUtils(context).getAssistComponentForUser(-2);
        return assistComponentForUser != null && "com.google.android.googlequicksearchbox/com.google.android.voiceinteraction.GsaVoiceInteractionService".equals(assistComponentForUser.flattenToString());
    }
    
    private boolean isOpaEligible(final Context context) {
        boolean b = false;
        if (Settings.Secure.getInt(context.getContentResolver(), "systemui.google.opa_enabled", 0) != 0) {
            b = true;
        }
        return b;
    }
    
    private boolean isOpaEnabled(final Context context) {
        try {
            return this.mLockSettings.getBoolean("systemui.google.opa_user_enabled", false, -2);
        }
        catch (RemoteException ex) {
            Log.e("OpaEnabledReceiver", "isOpaEnabled RemoteException", (Throwable)ex);
            return false;
        }
    }
    
    private void registerContentObserver() {
        this.mContentResolver.registerContentObserver(Settings.Secure.getUriFor("assistant"), false, this.mContentObserver);
    }
    
    private void registerEnabledReceiver(final int n) {
     //   this.mContext.registerReceiverAsUser(this.mBroadcastReceiver, new UserHandle(n), new IntentFilter("com.google.android.systemui.OPA_ENABLED"), (String)null, (Handler)null);
     //   this.mContext.registerReceiverAsUser(this.mBroadcastReceiver, new UserHandle(n), new IntentFilter("com.google.android.systemui.OPA_USER_ENABLED"), (String)null, (Handler)null);
    }
    
    private void updateOpaEnabledState(final Context context) {
        this.mIsOpaEligible = this.isOpaEligible(context);
        this.mIsAGSAAssistant = this.isAGSACurrentAssistant(context);
        this.mIsOpaEnabled = this.isOpaEnabled(context);
    }
    
    public void addOpaEnabledListener(final OpaEnabledListener opaEnabledListener) {
        this.mListeners.add(opaEnabledListener);
        opaEnabledListener.onOpaEnabledReceived(this.mContext, this.mIsOpaEligible, this.mIsAGSAAssistant, this.mIsOpaEnabled);
    }
    
    public void dispatchOpaEnabledState() {
        this.dispatchOpaEnabledState(this.mContext);
    }
    
    public void onUserSwitching(final int n) {
        this.updateOpaEnabledState(this.mContext);
        this.dispatchOpaEnabledState(this.mContext);
        this.mContentResolver.unregisterContentObserver(this.mContentObserver);
        this.registerContentObserver();
        this.mContext.unregisterReceiver(this.mBroadcastReceiver);
        this.registerEnabledReceiver(n);
    }
    
    private class AssistantContentObserver extends ContentObserver
    {
        public AssistantContentObserver(final Context context) {
            super(new Handler(context.getMainLooper()));
        }
        
        public void onChange(final boolean b, final Uri uri) {
            OpaEnabledReceiver.this.updateOpaEnabledState(OpaEnabledReceiver.this.mContext);
            OpaEnabledReceiver.this.dispatchOpaEnabledState(OpaEnabledReceiver.this.mContext);
        }
    }
    
    private class OpaEnabledBroadcastReceiver extends BroadcastReceiver
    {
        public OpaEnabledBroadcastReceiver(OpaEnabledBroadcastReceiver opaEnabledBroadcastReceiver) {

        }

        public void onReceive(final Context context, final Intent intent) {
            if (intent.getAction().equals("com.google.android.systemui.OPA_ENABLED")) {
                int n;
                if (intent.getBooleanExtra("OPA_ENABLED", false)) {
                    n = 1;
                }
                else {
                    // set it to 1 by default because fuck google
                    //  n = 0;
                    n = 1;
                }
                Settings.Secure.getInt(context.getContentResolver(), "systemui.google.opa_enabled", n);
            }
            else if (intent.getAction().equals("com.google.android.systemui.OPA_USER_ENABLED")) {
                final boolean booleanExtra = intent.getBooleanExtra("OPA_USER_ENABLED", false);
                try {
                    OpaEnabledReceiver.this.mLockSettings.setBoolean("systemui.google.opa_user_enabled", booleanExtra, -2);
                }
                catch (RemoteException ex) {
                    Log.e("OpaEnabledReceiver", "RemoteException on OPA_USER_ENABLED", (Throwable)ex);
                }
            }
            OpaEnabledReceiver.this.updateOpaEnabledState(context);
            OpaEnabledReceiver.this.dispatchOpaEnabledState(context);
        }
    }
}
