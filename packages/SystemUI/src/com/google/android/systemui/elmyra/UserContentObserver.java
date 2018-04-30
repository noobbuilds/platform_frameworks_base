package com.google.android.systemui.elmyra;

import com.android.keyguard.KeyguardUpdateMonitor;
import android.os.Handler;
import com.android.keyguard.KeyguardUpdateMonitorCallback;
import android.content.Context;
import android.net.Uri;
import java.util.function.Consumer;
import android.database.ContentObserver;

public class UserContentObserver extends ContentObserver
{
    private final Consumer<Uri> mCallback;
    private final Context mContext;
    private final Uri mSettingsUri;
    private final KeyguardUpdateMonitorCallback mUserSwitchCallback;
    
    public UserContentObserver(final Context context, final Uri uri, final Consumer<Uri> consumer) {
        this(context, uri, consumer, true);
    }
    
    public UserContentObserver(final Context mContext, final Uri mSettingsUri, final Consumer<Uri> mCallback, final boolean b) {
        super(new Handler(mContext.getMainLooper()));
        this.mUserSwitchCallback = new KeyguardUpdateMonitorCallback() {
            @Override
            public void onUserSwitching(final int n) {
                UserContentObserver.this.updateContentObserver();
                UserContentObserver.this.mCallback.accept(UserContentObserver.this.mSettingsUri);
            }
        };
        this.mContext = mContext;
        this.mSettingsUri = mSettingsUri;
        this.mCallback = mCallback;
        if (b) {
            this.activate();
        }
    }
    
    private void updateContentObserver() {
        this.mContext.getContentResolver().unregisterContentObserver((ContentObserver)this);
        this.mContext.getContentResolver().registerContentObserver(this.mSettingsUri, false, (ContentObserver)this);
    }
    
    public void activate() {
        this.updateContentObserver();
        KeyguardUpdateMonitor.getInstance(this.mContext).registerCallback(this.mUserSwitchCallback);
    }
    
    public void deactivate() {
        this.mContext.getContentResolver().unregisterContentObserver((ContentObserver)this);
        KeyguardUpdateMonitor.getInstance(this.mContext).removeCallback(this.mUserSwitchCallback);
    }
    
    public void onChange(final boolean b, final Uri uri) {
        this.mCallback.accept(uri);
    }
}
