package com.google.android.systemui.elmyra.feedback;

import java.util.Arrays;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Vibrator;
import android.os.VibrationEffect;

public class HapticClick implements FeedbackEffect
{
    private int mLastGestureStage;
    private final VibrationEffect mProgressVibrationEffect;
    private final VibrationEffect mResolveVibrationEffect;
    private final Vibrator mVibrator;
    
    public HapticClick(final Context context) {
        this.mVibrator = (Vibrator)context.getSystemService(context.VIBRATOR_SERVICE);
        this.mResolveVibrationEffect = VibrationEffect.createOneShot(500, VibrationEffect.DEFAULT_AMPLITUDE);
        @SuppressLint("ResourceType") final long[] array = Arrays.stream(context.getResources().getIntArray(17236010)).asLongStream().toArray();
        if (array.length == 1) {
            this.mProgressVibrationEffect = VibrationEffect.createOneShot(array[0], -1);
        }
        else {
            this.mProgressVibrationEffect = VibrationEffect.createWaveform(array, -1);
        }
    }
    
    @Override
    public void onProgress(final float n, final int mLastGestureStage) {
        if (this.mLastGestureStage != 2 && mLastGestureStage == 2 && this.mVibrator != null) {
            this.mVibrator.vibrate(this.mProgressVibrationEffect);
        }
        this.mLastGestureStage = mLastGestureStage;
    }
    
    @Override
    public void onRelease() {
    }
    
    @Override
    public void onResolve() {
        if (this.mVibrator != null) {
            this.mVibrator.vibrate(this.mResolveVibrationEffect);
        }
    }
}
