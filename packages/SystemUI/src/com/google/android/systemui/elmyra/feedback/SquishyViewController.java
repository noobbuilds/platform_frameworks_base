package com.google.android.systemui.elmyra.feedback;

import android.animation.PropertyValuesHolder;
import android.animation.TypeEvaluator;
import android.animation.PropertyValuesHolder;
import android.hardware.display.IDisplayManager;
import android.util.Property;
import android.util.TypedValue;
import android.animation.Animator;
import android.animation.TimeInterpolator;
import android.animation.ObjectAnimator;
import android.os.RemoteException;
import android.util.Log;
import android.view.Display;
import android.view.IRotationWatcher;
import android.os.ServiceManager;
import java.util.ArrayList;
import android.view.animation.PathInterpolator;
import android.view.IWindowManager;
import android.view.IRotationWatcher.Stub;
import android.view.View;
import java.util.List;
import android.content.Context;
import android.animation.AnimatorSet;
import android.view.animation.Interpolator;

import static android.animation.ObjectAnimator.ofFloat;

class SquishyViewController implements FeedbackEffect
{
    private static Interpolator SQUISH_TRANSLATION_MAP;
    private AnimatorSet mAnimatorSet;
    private Context mContext;
    private float mLastPressure;
    private List<View> mLeftViews;
    private float mPressure;
    private List<View> mRightViews;
    private IRotationWatcher.Stub mRotationWatcher;
    private int mScreenRotation;
    private float mSquishTranslationMax;
    private IWindowManager mWindowManager;
    private IDisplayManager mDisplayManager;
    
    static {
        SQUISH_TRANSLATION_MAP = (Interpolator)new PathInterpolator(0.4f, 0.0f, 0.6f, 1.0f);
    }
    
    public SquishyViewController(final Context mContext) {
        this.mLeftViews = new ArrayList<View>();
        this.mRightViews = new ArrayList<View>();
        this.mRotationWatcher = new IRotationWatcher.Stub() {
            public void onRotationChanged(final int n) {
                SquishyViewController.this.mScreenRotation = n;
            }
        };

        this.mContext = mContext;
        this.mSquishTranslationMax = this.px(8.0f);
        this.mWindowManager = IWindowManager.Stub.asInterface(ServiceManager.getService(Context.WINDOW_SERVICE));
        this.mDisplayManager = IDisplayManager.Stub.asInterface(ServiceManager.getService(Context.DISPLAY_SERVICE));
        try {
            this.mScreenRotation = this.mWindowManager.watchRotation((IRotationWatcher)this.mRotationWatcher, Display.DEFAULT_DISPLAY);
        }
        catch (RemoteException ex) {
            Log.e("SquishyViewController", "Couldn't get screen rotation or set watcher", (Throwable)ex);
            this.mScreenRotation = 0;
        }
    }
    
    private AnimatorSet createSpringbackAnimatorSet(final View view) {
        ObjectAnimator ofFloat = ofFloat((Object)view, View.TRANSLATION_X, new float[] { view.getTranslationX(), 0.0f });
        ObjectAnimator ofFloat2 = ofFloat((Object)view, View.TRANSLATION_Y, new float[] { view.getTranslationY(), 0.0f });
        ofFloat.setDuration(250L);
        ofFloat2.setDuration(250L);
        float n = Math.max(Math.abs(view.getTranslationX()) / 8.0f, Math.abs(view.getTranslationY()) / 8.0f) * 3.1f;
        ofFloat.setInterpolator((TimeInterpolator)new SpringInterpolator(0.31f, n));
        ofFloat2.setInterpolator((TimeInterpolator)new SpringInterpolator(0.31f, n));
        AnimatorSet set = new AnimatorSet();
        set.playTogether(new Animator[] { ofFloat, ofFloat2 });
        set.setStartDelay(50L);
        return set;
    }

    private ObjectAnimator ofFloat(Object view, Property<View,Float> translationX, float[] floats) {
        return null;
    }

    private AnimatorSet createSpringbackAnimatorSets() {
        AnimatorSet set = new AnimatorSet();
        for (int i = 0; i < this.mLeftViews.size(); ++i) {
            set.play((Animator)this.createSpringbackAnimatorSet(this.mLeftViews.get(i)));
        }
        for (int j = 0; j < this.mRightViews.size(); ++j) {
            set.play((Animator)this.createSpringbackAnimatorSet(this.mRightViews.get(j)));
        }
        return set;
    }
    
    private float px(final float n) {
        return TypedValue.applyDimension(1, n, this.mContext.getResources().getDisplayMetrics());
    }
    
    private void setViewTranslation(final View view, float n) {
        if (!view.isAttachedToWindow()) {
            return;
        }
        float n2 = n;
        if (view.getLayoutDirection() == View.LAYOUT_DIRECTION_RTL) {
            n2 = n * -1.0f;
        }
        switch (this.mScreenRotation) {
            case 0:
            case 2: {
                view.setTranslationX(n2);
                view.setTranslationY(0.0f);
                break;
            }
            case 1: {
                view.setTranslationX(0.0f);
                view.setTranslationY(-n2);
                break;
            }
            case 3: {
                view.setTranslationX(0.0f);
                view.setTranslationY(n2);
                break;
            }
        }
    }
    
    private void translateViews(final float n) {
        for (int i = 0; i < this.mLeftViews.size(); ++i) {
            this.setViewTranslation(this.mLeftViews.get(i), n);
        }
        for (int j = 0; j < this.mRightViews.size(); ++j) {
            this.setViewTranslation(this.mRightViews.get(j), -n);
        }
    }
    
    public void addLeftView(final View view) {
        this.mLeftViews.add(view);
    }
    
    public void addRightView(final View view) {
        this.mRightViews.add(view);
    }
    
    public void clearViews() {
        this.translateViews(0.0f);
        this.mLeftViews.clear();
        this.mRightViews.clear();
    }
    
    public boolean isAttachedToWindow() {
        for (int i = 0; i < this.mLeftViews.size(); ++i) {
            if (!this.mLeftViews.get(i).isAttachedToWindow()) {
                return false;
            }
        }
        for (int j = 0; j < this.mRightViews.size(); ++j) {
            if (!this.mRightViews.get(j).isAttachedToWindow()) {
                return false;
            }
        }
        return true;
    }
    
    @Override
    public void onProgress(float mPressure, int n) {
        mPressure = Math.min(mPressure, 1.0f) / 1.0f;
        if (mPressure != 0.0f) {
            this.mPressure = 1.0f * mPressure + this.mLastPressure * 0.0f;
        }
        else {
            this.mPressure = mPressure;
        }
        if (this.mAnimatorSet == null || (this.mAnimatorSet.isRunning() ^ true)) {
            if (mPressure - this.mLastPressure < -0.1f) {
                (this.mAnimatorSet = this.createSpringbackAnimatorSets()).start();
            }
            else {
                this.translateViews(this.mSquishTranslationMax * SquishyViewController.SQUISH_TRANSLATION_MAP.getInterpolation(this.mPressure));
            }
        }
        this.mLastPressure = this.mPressure;
    }
    
    @Override
    public void onRelease() {
        this.onProgress(0.0f, 0);
    }
    
    @Override
    public void onResolve() {
        this.onProgress(0.0f, 0);
    }
    
    private class SpringInterpolator implements Interpolator
    {
        private float mBounce;
        private float mMass;
        
        SpringInterpolator(final float mMass, float mBounce) {
            this.mMass = mMass;
            this.mBounce = mBounce;
        }
        
        public float getInterpolation(final float n) {
            return (float)(-(Math.exp(-(n / this.mMass)) * Math.cos(this.mBounce * n)) + 1.0);
        }
    }
}
