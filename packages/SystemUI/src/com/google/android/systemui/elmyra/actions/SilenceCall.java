package com.google.android.systemui.elmyra.actions;

import android.net.Uri;
import java.util.function.Consumer;

import android.provider.Settings;
import android.provider.Settings.Secure;
import com.google.android.systemui.elmyra.feedback.FeedbackEffect;
import java.util.List;
import android.content.Context;
import android.telephony.TelephonyManager;
import com.google.android.systemui.elmyra.UserContentObserver;
import android.telephony.PhoneStateListener;

public class SilenceCall extends Action
{
    private boolean mIsPhoneRinging;
    private PhoneStateListener mPhoneStateListener;
    private UserContentObserver mSettingsObserver;
    private boolean mSilenceSettingEnabled;
    private TelephonyManager mTelephonyManager;

    public SilenceCall(final Context context) {
        super(context, null);
        this.mPhoneStateListener = new PhoneStateListener() {
            public void onCallStateChanged(final int n, String s) {
                boolean silencePhone = SilenceCall.this.isPhoneRinging(n);
                if (SilenceCall.this.mIsPhoneRinging != silencePhone) {
                    SilenceCall.this.mIsPhoneRinging = silencePhone;
                    SilenceCall.this.notifyListener();
                }
            }
        };
        this.mTelephonyManager = (TelephonyManager)this.getContext().getSystemService(context.TELEPHONY_SERVICE);
        this.updatePhoneStateListener();
        this.mSettingsObserver = new UserContentObserver(this.getContext(), Settings.Secure.getUriFor("assist_gesture_silence_alerts_enabled"), new LambdaF((byte)3));
    }

    private boolean isPhoneRinging(final int n) {
        boolean b = true;
        if (n != 1) {
            b = false;
        }
        return b;
    }

    private void updatePhoneStateListener() {
        boolean mSilenceSettingEnabled = Settings.Secure.getInt(this.getContext().getContentResolver(), "assist_gesture_silence_alerts_enabled", 1) != 0;
        if (mSilenceSettingEnabled != this.mSilenceSettingEnabled) {
            this.mSilenceSettingEnabled = mSilenceSettingEnabled;
            int n;
            if (this.mSilenceSettingEnabled) {
                n = 32;
            }
            else {
                n = 0;
            }
            this.mTelephonyManager.listen(this.mPhoneStateListener, n);
            this.mIsPhoneRinging = this.isPhoneRinging(this.mTelephonyManager.getCallState());
            this.notifyListener();
        }
    }

    @Override
    public boolean isAvailable() {
        return this.mSilenceSettingEnabled && this.mIsPhoneRinging;
    }

    @Override
    public void onTrigger() {
        //    this.mTelephonyManager.silenceRinger();
    }

    @Override
    public String toString() {
        return super.toString() + " [mSilenceSettingEnabled -> " + this.mSilenceSettingEnabled + "]";
    }
}

