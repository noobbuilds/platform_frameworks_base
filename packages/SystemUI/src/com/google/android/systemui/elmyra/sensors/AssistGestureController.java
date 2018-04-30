package com.google.android.systemui.elmyra.sensors;

import android.support.annotation.Nullable;
import android.os.SystemClock;
import android.content.res.Resources;
import android.util.TypedValue;
import android.content.Context;

import com.android.systemui.R;

class AssistGestureController
{
    private final long mFalsePrimeWindow;
    private final long mGestureCooldownTime;
    private GestureSensor.Listener mGestureListener;
    private float mGestureProgress;
    private final GestureSensor mGestureSensor;
    private boolean mIsFalsePrimed;
    private long mLastDetectionTime;
    private final float mProgressAlpha;
    private final float mProgressReportThreshold;
    
    AssistGestureController(final Context context, final GestureSensor mGestureSensor) {
        this.mGestureSensor = mGestureSensor;
        final Resources resources = context.getResources();
        final TypedValue typedValue = new TypedValue();
        resources.getValue(R.dimen.elmyra_progress_alpha, typedValue, true);
        this.mProgressAlpha = typedValue.getFloat();
        resources.getValue(R.dimen.elmyra_progress_report_threshold, typedValue, true);
        this.mProgressReportThreshold = typedValue.getFloat();
        this.mGestureCooldownTime = resources.getInteger(R.integer.elmyra_gesture_cooldown_time);
        this.mFalsePrimeWindow = this.mGestureCooldownTime + resources.getInteger(R.integer.elmyra_false_prime_window);
    }
    
    public void onGestureDetected() {
        final long uptimeMillis = SystemClock.uptimeMillis();
        if (uptimeMillis - this.mLastDetectionTime < this.mGestureCooldownTime || this.mIsFalsePrimed) {
            return;
        }
        if (this.mGestureListener != null) {
            this.mGestureListener.onGestureDetected(this.mGestureSensor);
        }
        this.mLastDetectionTime = uptimeMillis;
    }
    
    public void onGestureProgress(final float n) {
        if (n == 0.0f) {
            this.mGestureProgress = 0.0f;
            this.mIsFalsePrimed = false;
        }
        else {
            this.mGestureProgress = this.mProgressAlpha * n + (1.0f - this.mProgressAlpha) * this.mGestureProgress;
        }
        final long uptimeMillis = SystemClock.uptimeMillis();
        if (uptimeMillis - this.mLastDetectionTime < this.mGestureCooldownTime || this.mIsFalsePrimed) {
            return;
        }
        if (uptimeMillis - this.mLastDetectionTime < this.mFalsePrimeWindow && n == 1.0f) {
            this.mIsFalsePrimed = true;
            return;
        }
        if (this.mGestureListener != null) {
            if (this.mGestureProgress < this.mProgressReportThreshold) {
                this.mGestureListener.onGestureProgress(this.mGestureSensor, 0.0f, 0);
            }
            else {
                final float n2 = (this.mGestureProgress - this.mProgressReportThreshold) / (1.0f - this.mProgressReportThreshold);
                int n3;
                if (n == 1.0f) {
                    n3 = 2;
                }
                else {
                    n3 = 1;
                }
                this.mGestureListener.onGestureProgress(this.mGestureSensor, n2, n3);
            }
        }
    }
    
    public AssistGestureController setGestureListener(@Nullable final GestureSensor.Listener mGestureListener) {
        this.mGestureListener = mGestureListener;
        return null;
    }
}
