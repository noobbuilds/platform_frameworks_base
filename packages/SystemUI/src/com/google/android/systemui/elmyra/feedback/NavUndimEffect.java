package com.google.android.systemui.elmyra.feedback;

import com.android.systemui.SysUiServiceProvider;
import android.content.Context;
import com.android.systemui.statusbar.phone.StatusBar;

public class NavUndimEffect implements FeedbackEffect
{
    private final StatusBar mStatusBar;
    
    public NavUndimEffect(final Context context) {
        this.mStatusBar = SysUiServiceProvider.getComponent(context, StatusBar.class);
    }
    
    @Override
    public void onProgress(final float n, final int n2) {
        if (this.mStatusBar != null) {
            this.mStatusBar.touchAutoDim();
        }
    }
    
    @Override
    public void onRelease() {
        if (this.mStatusBar != null) {
            this.mStatusBar.touchAutoDim();
        }
    }
    
    @Override
    public void onResolve() {
        if (this.mStatusBar != null) {
            this.mStatusBar.touchAutoDim();
        }
    }
}
