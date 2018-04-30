package com.google.android.systemui.elmyra.feedback;

public interface FeedbackEffect
{
    void onProgress(final float p0, final int p1);
    
    void onRelease();
    
    void onResolve();
}
