package com.google.android.systemui;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.ContentObserver;
import android.net.Uri;
import android.os.Handler;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.provider.Settings;
import android.util.Log;

import com.android.internal.app.AssistUtils;
import com.android.internal.widget.ILockSettings;

import java.util.ArrayList;
import java.util.List;

public class OpaEnabledReceiver {
    private BroadcastReceiver mBroadcastReceiver;
    private ContentObserver mContentObserver;
    private ContentResolver mContentResolver;
    private Context mContext;
    private boolean mIsAGSAAssistant;
    private boolean mIsOpaEligible;
    private boolean mIsOpaEnabled;
    private List<OpaEnabledListener> mListeners;
    private ILockSettings mLockSettings;

    public OpaEnabledReceiver(Context mContext) {
        this.mBroadcastReceiver = new OpaEnabledBroadcastReceiver((OpaEnabledBroadcastReceiver) null);
        this.mListeners = new ArrayList<OpaEnabledListener>();
        this.mContext = mContext;
        this.mContentResolver = this.mContext.getContentResolver();
        this.mContentObserver = new AssistantContentObserver(this.mContext);
        this.mLockSettings = ILockSettings.Stub.asInterface(ServiceManager.getService("lock_settings"));
        this.updateOpaEnabledState(this.mContext);
        this.registerContentObserver();
        this.registerEnabledReceiver(-2);
    }

    private void dispatchOpaEnabledState(Context context) {
        Log.i("OpaEnabledReceiver", "Dispatching OPA eligble = " + this.mIsOpaEligible + "; AGSA = " + this.mIsAGSAAssistant + "; OPA enabled = " + this.mIsOpaEnabled);
        for (int i = 0; i < this.mListeners.size(); ++i) {
            this.mListeners.get(i).onOpaEnabledReceived(context, this.mIsOpaEligible, this.mIsAGSAAssistant, this.mIsOpaEnabled);
        }
    }

    private boolean isAGSACurrentAssistant(Context context) {
        boolean enabled;
        ComponentName assistComponentForUser = new AssistUtils(context).getAssistComponentForUser(-2);
        enabled = assistComponentForUser != null && "com.google.android.googlequicksearchbox/com.google.android.voiceinteraction.GsaVoiceInteractionService".equals(assistComponentForUser.flattenToString());
        if (!enabled) {
            Log.e("OPACHECK: ", "false but forcing true");
            return true;
        } else {
            return true;
        }
    }

    private boolean isOpaEligible(Context context) {
        boolean b = true;
        if (Settings.Secure.getInt(context.getContentResolver(), "systemui.google.opa_enabled", 0) != 0) {
            b = true;
        }
        return b;
    }

    private boolean isOpaEnabled(Context context) {
        boolean enabled;
        try {
            enabled = this.mLockSettings.getBoolean("systemui.google.opa_user_enabled", true, -2);
            if (!enabled) {
                Log.e("OPACHECK: ", "false but forcing true");
                return true;
            } else {
                return true;
            }
        } catch (RemoteException ex) {
            Log.e("OpaEnabledReceiver", "isOpaEnabled RemoteException", (Throwable) ex);
            return true;
        }
    }

    private void registerContentObserver() {
        this.mContentResolver.registerContentObserver(Settings.Secure.getUriFor("assistant"), false, this.mContentObserver);
    }

    private void registerEnabledReceiver(int n) {
        this.mContext.registerReceiver(this.mBroadcastReceiver, new IntentFilter("com.google.android.systemui.OPA_ENABLED"), null, null);
        this.mContext.registerReceiver(this.mBroadcastReceiver, new IntentFilter("com.google.android.systemui.OPA_USER_ENABLED"), null, null);
    }

    private void updateOpaEnabledState(Context context) {
        this.mIsOpaEligible = this.isOpaEligible(context);
        this.mIsAGSAAssistant = this.isAGSACurrentAssistant(context);
        this.mIsOpaEnabled = this.isOpaEnabled(context);
    }

    public void addOpaEnabledListener(OpaEnabledListener opaEnabledListener) {
        this.mListeners.add(opaEnabledListener);
        opaEnabledListener.onOpaEnabledReceived(this.mContext, this.mIsOpaEligible, this.mIsAGSAAssistant, this.mIsOpaEnabled);
    }

    public void dispatchOpaEnabledState() {
        this.dispatchOpaEnabledState(this.mContext);
    }

    public void onUserSwitching(int n) {
        this.updateOpaEnabledState(this.mContext);
        this.dispatchOpaEnabledState(this.mContext);
        this.mContentResolver.unregisterContentObserver(this.mContentObserver);
        this.registerContentObserver();
        this.mContext.unregisterReceiver(this.mBroadcastReceiver);
        this.registerEnabledReceiver(n);
    }

    private class AssistantContentObserver extends ContentObserver {
        public AssistantContentObserver(Context context) {
            super(new Handler(context.getMainLooper()));
        }

        public void onChange(boolean b, Uri uri) {
            OpaEnabledReceiver.this.updateOpaEnabledState(OpaEnabledReceiver.this.mContext);
            OpaEnabledReceiver.this.dispatchOpaEnabledState(OpaEnabledReceiver.this.mContext);
        }
    }

    private class OpaEnabledBroadcastReceiver extends BroadcastReceiver {
        public OpaEnabledBroadcastReceiver(OpaEnabledBroadcastReceiver opaEnabledBroadcastReceiver) {

        }

        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals("com.google.android.systemui.OPA_ENABLED")) {
                int n;
                if (intent.getBooleanExtra("OPA_ENABLED", false)) {
                    n = 1;
                } else {
                    // set it to 1 by default because fuck google
                    //  n = 0;
                    n = 1;
                }
                Settings.Secure.getInt(context.getContentResolver(), "systemui.google.opa_enabled", n);
            } else if (intent.getAction().equals("com.google.android.systemui.OPA_USER_ENABLED")) {
                boolean booleanExtra = intent.getBooleanExtra("OPA_USER_ENABLED", false);
                boolean enabled;
                try {
                    enabled = OpaEnabledReceiver.this.mLockSettings.getBoolean("systemui.google.opa_user_enabled", true, -2);
                    if (!enabled) {
                        Log.e("OPACHECK: ", "false but forcing true");
                        OpaEnabledReceiver.this.mLockSettings.setBoolean("systemui.google.opa_user_enabled", true, 1);
                    } else {
                        Log.e("OPACHECK: ", "false but forcing true part 2");

                    }
                } catch (RemoteException ex) {
                    Log.e("OpaEnabledReceiver", "isOpaEnabled RemoteException", (Throwable) ex);
                }
            }
            OpaEnabledReceiver.this.updateOpaEnabledState(context);
            OpaEnabledReceiver.this.dispatchOpaEnabledState(context);
        }
    }
}

