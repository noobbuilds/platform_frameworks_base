package com.google.android.systemui.elmyra.feedback;

import com.android.systemui.statusbar.phone.NavigationBarView;
import java.util.Collection;
import com.android.systemui.SysUiServiceProvider;
import com.android.systemui.statusbar.phone.StatusBar;
import java.util.ArrayList;
import java.util.List;
import android.content.Context;

public abstract class NavigationBarEffect implements FeedbackEffect
{
    private final Context mContext;
    private final List<FeedbackEffect> mFeedbackEffects;
    
    public NavigationBarEffect(final Context mContext) {
        this.mFeedbackEffects = new ArrayList<FeedbackEffect>();
        this.mContext = mContext;
    }
    
    private void refreshFeedbackEffects() {
        final StatusBar statusBar = SysUiServiceProvider.getComponent(this.mContext, StatusBar.class);
        if (statusBar == null || statusBar.getNavigationBarView() == null) {
            this.mFeedbackEffects.clear();
            return;
        }
        if (!this.validateFeedbackEffects(this.mFeedbackEffects)) {
            this.mFeedbackEffects.clear();
        }
        final NavigationBarView navigationBarView = statusBar.getNavigationBarView();
        if (navigationBarView == null) {
            this.mFeedbackEffects.clear();
        }
        if (this.mFeedbackEffects.isEmpty() && navigationBarView != null) {
            this.mFeedbackEffects.addAll(this.findFeedbackEffects(navigationBarView));
        }
    }
    
    protected abstract List<FeedbackEffect> findFeedbackEffects(final NavigationBarView p0);
    
    protected boolean isActiveFeedbackEffect(final FeedbackEffect feedbackEffect) {
        return true;
    }
    
    @Override
    public void onProgress(final float n, final int n2) {
        this.refreshFeedbackEffects();
        for (int i = 0; i < this.mFeedbackEffects.size(); ++i) {
            final FeedbackEffect feedbackEffect = this.mFeedbackEffects.get(i);
            if (this.isActiveFeedbackEffect(feedbackEffect)) {
                feedbackEffect.onProgress(n, n2);
            }
        }
    }
    
    @Override
    public void onRelease() {
        this.refreshFeedbackEffects();
        for (int i = 0; i < this.mFeedbackEffects.size(); ++i) {
            this.mFeedbackEffects.get(i).onRelease();
        }
    }
    
    @Override
    public void onResolve() {
        this.refreshFeedbackEffects();
        for (int i = 0; i < this.mFeedbackEffects.size(); ++i) {
            this.mFeedbackEffects.get(i).onResolve();
        }
    }
    
    protected abstract boolean validateFeedbackEffects(final List<FeedbackEffect> p0);
}
