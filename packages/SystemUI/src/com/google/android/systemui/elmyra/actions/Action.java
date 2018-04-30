package com.google.android.systemui.elmyra.actions;

import java.util.Collection;
import java.util.ArrayList;
import android.support.annotation.Nullable;
import android.os.Handler;
import com.google.android.systemui.elmyra.feedback.FeedbackEffect;
import java.util.List;
import android.content.Context;

public abstract class Action
{
    private final Context mContext;
    private final List<FeedbackEffect> mFeedbackEffects;
    private final Handler mHandler;
    @Nullable
    private Listener mListener;
    
    public Action(final Context mContext, @Nullable final List<FeedbackEffect> list) {
        this.mFeedbackEffects = new ArrayList<FeedbackEffect>();
        this.mContext = mContext;
        this.mHandler = new Handler(mContext.getMainLooper());
        if (list != null) {
            this.mFeedbackEffects.addAll(list);
        }
    }
    
    protected Context getContext() {
        return this.mContext;
    }
    
    public abstract boolean isAvailable();
    
    protected void notifyListener() {
        if (this.mListener != null) {
            this.mHandler.post((Runnable)new LambdaG((byte)1));
        }
        if (!this.isAvailable()) {
            this.mHandler.post((Runnable)new LambdaG((byte)0));
        }
    }
    
    public void onProgress(final float n, final int n2) {
    }
    
    public abstract void onTrigger();
    
    public void setListener(@Nullable final Listener mListener) {
        this.mListener = mListener;
    }
    
    @Override
    public String toString() {
        return this.getClass().getSimpleName();
    }
    
    protected void triggerFeedbackEffects() {
        if (!this.isAvailable()) {
            return;
        }
        for (int i = 0; i < this.mFeedbackEffects.size(); ++i) {
            this.mFeedbackEffects.get(i).onResolve();
        }
    }
    
    protected void updateFeedbackEffects(final float n, int i) {
        if (n == 0.0f || i == 0) {
            for (i = 0; i < this.mFeedbackEffects.size(); ++i) {
                this.mFeedbackEffects.get(i).onRelease();
            }
        }
        else if (this.isAvailable()) {
            for (int j = 0; j < this.mFeedbackEffects.size(); ++j) {
                this.mFeedbackEffects.get(j).onProgress(n, i);
            }
        }
    }
    
    public interface Listener
    {
        void onActionAvailabilityChanged(final Action p0);
    }
}
