package com.google.android.systemui;

import android.animation.Animator.AnimatorListener;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import com.android.systemui.Interpolators;
import android.util.AttributeSet;
import android.view.animation.PathInterpolator;
import android.content.Context;
import android.content.res.Resources;
import android.animation.Animator;
import android.util.ArraySet;
import android.animation.AnimatorSet;
import android.view.View;
import java.util.ArrayList;
import android.view.animation.Interpolator;

import com.android.systemui.R;
import com.google.android.systemui.elmyra.feedback.FeedbackEffect;
import android.widget.FrameLayout;

public class LockscreenOpaLayout extends FrameLayout implements FeedbackEffect
{
    private final Interpolator INTERPOLATOR_5_100;
    private final int RED_YELLOW_START_DELAY;
    private final ArrayList<View> mAnimatedViews;
    private View mBlue;
    private AnimatorSet mCannedAnimatorSet;
    private final ArraySet<Animator> mCurrentAnimators;
    private AnimatorSet mGestureAnimatorSet;
    private int mGestureState;
    private View mGreen;
    private AnimatorSet mLineAnimatorSet;
    private View mRed;
    private Resources mResources;
    private View mYellow;

    public LockscreenOpaLayout(final Context context) {
        super(context);
        this.RED_YELLOW_START_DELAY = 17;
        this.INTERPOLATOR_5_100 = (Interpolator)new PathInterpolator(1.0f, 0.0f, 0.95f, 1.0f);
        this.mGestureState = 0;
        this.mCurrentAnimators = (ArraySet<Animator>)new ArraySet();
        this.mAnimatedViews = new ArrayList<View>();
    }

    public LockscreenOpaLayout(final Context context, final AttributeSet set) {
        super(context, set);
        this.RED_YELLOW_START_DELAY = 17;
        this.INTERPOLATOR_5_100 = (Interpolator)new PathInterpolator(1.0f, 0.0f, 0.95f, 1.0f);
        this.mGestureState = 0;
        this.mCurrentAnimators = (ArraySet<Animator>)new ArraySet();
        this.mAnimatedViews = new ArrayList<View>();
    }

    public LockscreenOpaLayout(final Context context, final AttributeSet set, final int n) {
        super(context, set, n);
        this.RED_YELLOW_START_DELAY = 17;
        this.INTERPOLATOR_5_100 = (Interpolator)new PathInterpolator(1.0f, 0.0f, 0.95f, 1.0f);
        this.mGestureState = 0;
        this.mCurrentAnimators = (ArraySet<Animator>)new ArraySet();
        this.mAnimatedViews = new ArrayList<View>();
    }

    public LockscreenOpaLayout(final Context context, final AttributeSet set, final int n, final int n2) {
        super(context, set, n, n2);
        this.RED_YELLOW_START_DELAY = 17;
        this.INTERPOLATOR_5_100 = (Interpolator)new PathInterpolator(1.0f, 0.0f, 0.95f, 1.0f);
        this.mGestureState = 0;
        this.mCurrentAnimators = (ArraySet<Animator>)new ArraySet();
        this.mAnimatedViews = new ArrayList<View>();
    }

    private void endCurrentAnimation() {
        if (!this.mCurrentAnimators.isEmpty()) {
            for (int i = this.mCurrentAnimators.size() - 1; i >= 0; --i) {
                final Animator animator = (Animator)this.mCurrentAnimators.valueAt(i);
                animator.removeAllListeners();
                animator.end();
            }
            this.mCurrentAnimators.clear();
        }
        this.mGestureState = 0;
    }

    private AnimatorSet getCannedAnimatorSet() {
        if (this.mCannedAnimatorSet != null) {
            this.mCannedAnimatorSet.removeAllListeners();
            this.mCannedAnimatorSet.cancel();
            return this.mCannedAnimatorSet;
        }
        this.mCannedAnimatorSet = new AnimatorSet();
        final ObjectAnimator translationObjectAnimatorX = OpaUtils.getTranslationObjectAnimatorX(this.mRed, OpaUtils.INTERPOLATOR_40_40, -OpaUtils.getPxVal(this.mResources, R.dimen.opa_lockscreen_canned_ry), this.mRed.getX(), 83);
        ((Animator)translationObjectAnimatorX).setStartDelay(17L);
        final ObjectAnimator translationObjectAnimatorX2 = OpaUtils.getTranslationObjectAnimatorX(this.mYellow, OpaUtils.INTERPOLATOR_40_40, OpaUtils.getPxVal(this.mResources, R.dimen.opa_lockscreen_canned_ry), this.mYellow.getX(), 83);
        ((Animator)translationObjectAnimatorX2).setStartDelay(17L);
        this.mCannedAnimatorSet.play((Animator)translationObjectAnimatorX).with((Animator)translationObjectAnimatorX2).with((Animator)OpaUtils.getTranslationObjectAnimatorX(this.mBlue, OpaUtils.INTERPOLATOR_40_40, -OpaUtils.getPxVal(this.mResources, R.dimen.opa_lockscreen_canned_bg), this.mBlue.getX(), 167)).with((Animator)OpaUtils.getTranslationObjectAnimatorX(this.mGreen, OpaUtils.INTERPOLATOR_40_40, OpaUtils.getPxVal(this.mResources, R.dimen.opa_lockscreen_canned_bg), this.mGreen.getX(), 167)).with((Animator)OpaUtils.getAlphaObjectAnimator(this.mRed, 1.0f, 50, 130, Interpolators.LINEAR)).with((Animator)OpaUtils.getAlphaObjectAnimator(this.mYellow, 1.0f, 50, 130, Interpolators.LINEAR)).with((Animator)OpaUtils.getAlphaObjectAnimator(this.mBlue, 1.0f, 50, 113, Interpolators.LINEAR)).with((Animator)OpaUtils.getAlphaObjectAnimator(this.mGreen, 1.0f, 50, 113, Interpolators.LINEAR));
        return this.mCannedAnimatorSet;
    }

    private ArraySet<Animator> getCollapseAnimatorSet() {
        final ArraySet set = new ArraySet();
        set.add((Object)OpaUtils.getTranslationAnimatorX(this.mRed, OpaUtils.INTERPOLATOR_40_OUT, 133));
        set.add((Object)OpaUtils.getTranslationAnimatorX(this.mBlue, OpaUtils.INTERPOLATOR_40_OUT, 150));
        set.add((Object)OpaUtils.getTranslationAnimatorX(this.mYellow, OpaUtils.INTERPOLATOR_40_OUT, 133));
        set.add((Object)OpaUtils.getTranslationAnimatorX(this.mGreen, OpaUtils.INTERPOLATOR_40_OUT, 150));
        OpaUtils.getLongestAnim((ArraySet<Animator>)set).addListener((Animator.AnimatorListener)new AnimatorListenerAdapter() {
            public void onAnimationEnd(final Animator animator) {
                LockscreenOpaLayout.this.mCurrentAnimators.clear();
                LockscreenOpaLayout.this.mGestureAnimatorSet = null;
                LockscreenOpaLayout.this.mGestureState = 0;
                LockscreenOpaLayout.this.skipToStartingValue();
            }
        });
        return (ArraySet<Animator>)set;
    }

    private AnimatorSet getLineAnimatorSet() {
        if (this.mLineAnimatorSet != null) {
            this.mLineAnimatorSet.removeAllListeners();
            this.mLineAnimatorSet.cancel();
            return this.mLineAnimatorSet;
        }
        this.mLineAnimatorSet = new AnimatorSet();
        this.mLineAnimatorSet.play((Animator)OpaUtils.getTranslationObjectAnimatorX(this.mRed, this.INTERPOLATOR_5_100, -OpaUtils.getPxVal(this.mResources, R.dimen.opa_lockscreen_translation_ry), this.mRed.getX(), 366)).with((Animator)OpaUtils.getTranslationObjectAnimatorX(this.mYellow, this.INTERPOLATOR_5_100, OpaUtils.getPxVal(this.mResources, R.dimen.opa_lockscreen_translation_ry), this.mYellow.getX(), 366)).with((Animator)OpaUtils.getTranslationObjectAnimatorX(this.mGreen, this.INTERPOLATOR_5_100, OpaUtils.getPxVal(this.mResources, R.dimen.opa_lockscreen_translation_bg), this.mGreen.getX(), 366)).with((Animator)OpaUtils.getTranslationObjectAnimatorX(this.mBlue, this.INTERPOLATOR_5_100, -OpaUtils.getPxVal(this.mResources, R.dimen.opa_lockscreen_translation_bg), this.mBlue.getX(), 366));
        return this.mLineAnimatorSet;
    }

    private ArraySet<Animator> getRetractAnimatorSet() {
        final ArraySet set = new ArraySet();
        set.add((Object)OpaUtils.getTranslationAnimatorX(this.mRed, OpaUtils.INTERPOLATOR_40_OUT, 190));
        set.add((Object)OpaUtils.getTranslationAnimatorX(this.mBlue, OpaUtils.INTERPOLATOR_40_OUT, 190));
        set.add((Object)OpaUtils.getTranslationAnimatorX(this.mGreen, OpaUtils.INTERPOLATOR_40_OUT, 190));
        set.add((Object)OpaUtils.getTranslationAnimatorX(this.mYellow, OpaUtils.INTERPOLATOR_40_OUT, 190));
        OpaUtils.getLongestAnim((ArraySet<Animator>)set).addListener((Animator.AnimatorListener)new AnimatorListenerAdapter() {
            public void onAnimationEnd(final Animator animator) {
                LockscreenOpaLayout.this.mCurrentAnimators.clear();
                LockscreenOpaLayout.this.skipToStartingValue();
                LockscreenOpaLayout.this.mGestureState = 0;
                LockscreenOpaLayout.this.mGestureAnimatorSet = null;
            }
        });
        return (ArraySet<Animator>)set;
    }

    private void skipToStartingValue() {
        for (int size = this.mAnimatedViews.size(), i = 0; i < size; ++i) {
            final View view = this.mAnimatedViews.get(i);
            view.setAlpha(0.0f);
            view.setTranslationX(0.0f);
        }
    }

    private void startAll(final ArraySet<Animator> set) {
        for (int i = set.size() - 1; i >= 0; --i) {
            ((Animator)set.valueAt(i)).start();
        }
    }

    private void startCannedAnimation() {
        if (this.isAttachedToWindow()) {
            this.skipToStartingValue();
            this.mGestureState = 3;
            (this.mGestureAnimatorSet = this.getCannedAnimatorSet()).addListener((Animator.AnimatorListener)new AnimatorListenerAdapter() {
                public void onAnimationEnd(final Animator animator) {
                    LockscreenOpaLayout.this.mGestureState = 1;
                    LockscreenOpaLayout.this.mGestureAnimatorSet = LockscreenOpaLayout.this.getLineAnimatorSet();
                    LockscreenOpaLayout.this.mGestureAnimatorSet.setCurrentPlayTime(0L);
                }
            });
            this.mGestureAnimatorSet.start();
        }
        else {
            this.skipToStartingValue();
        }
    }

    private void startCollapseAnimation() {
        if (this.isAttachedToWindow()) {
            this.mCurrentAnimators.clear();
            this.mCurrentAnimators.addAll((ArraySet)this.getCollapseAnimatorSet());
            this.startAll(this.mCurrentAnimators);
            this.mGestureState = 2;
        }
        else {
            this.skipToStartingValue();
        }
    }

    private void startRetractAnimation() {
        if (this.isAttachedToWindow()) {
            if (this.mGestureAnimatorSet != null) {
                this.mGestureAnimatorSet.removeAllListeners();
                this.mGestureAnimatorSet.cancel();
            }
            this.mCurrentAnimators.clear();
            this.mCurrentAnimators.addAll((ArraySet)this.getRetractAnimatorSet());
            this.startAll(this.mCurrentAnimators);
            this.mGestureState = 4;
        }
        else {
            this.skipToStartingValue();
        }
    }

    protected void onFinishInflate() {
        super.onFinishInflate();
        this.mResources = this.getResources();
        this.mBlue = this.findViewById(R.id.blue);
        this.mRed = this.findViewById(R.id.red);
        this.mYellow = this.findViewById(R.id.yellow);
        this.mGreen = this.findViewById(R.id.green);
        this.mAnimatedViews.add(this.mBlue);
        this.mAnimatedViews.add(this.mRed);
        this.mAnimatedViews.add(this.mYellow);
        this.mAnimatedViews.add(this.mGreen);
    }

    public void onProgress(final float n, final int n2) {
        if (this.mGestureState == 2) {
            return;
        }
        if (this.mGestureState == 4) {
            this.endCurrentAnimation();
        }
        if (n == 0.0f) {
            this.mGestureState = 0;
            return;
        }
        final long max = Math.max(0L, (long)(533.0f * n) - 167L);
        switch (this.mGestureState) {
            default: {}
            case 0: {
                this.startCannedAnimation();
            }
            case 3: {
                if (max < 167L) {
                    return;
                }
                this.mGestureAnimatorSet.end();
                if (this.mGestureState == 1) {
                    this.mGestureAnimatorSet.setCurrentPlayTime(max);
                }
            }
            case 1: {
                this.mGestureAnimatorSet.setCurrentPlayTime(max);
            }
        }
    }

    public void onRelease() {
        if (this.mGestureState == 2 || this.mGestureState == 4) {
            return;
        }
        if (this.mGestureState == 3) {
            if (this.mGestureAnimatorSet.isRunning()) {
                this.mGestureAnimatorSet.removeAllListeners();
                this.mGestureAnimatorSet.addListener((Animator.AnimatorListener)new AnimatorListenerAdapter() {
                    public void onAnimationEnd(final Animator animator) {
                        LockscreenOpaLayout.this.startRetractAnimation();
                    }
                });
            }
            else {
                this.mGestureState = 4;
                this.startRetractAnimation();
            }
            return;
        }
        if (this.mGestureState == 1) {
            this.startRetractAnimation();
        }
    }

    public void onResolve() {
        if (this.mGestureState == 4 || this.mGestureState == 2) {
            return;
        }
        if (this.mGestureState == 3) {
            this.mGestureState = 2;
            this.mGestureAnimatorSet.removeAllListeners();
            this.mGestureAnimatorSet.addListener((Animator.AnimatorListener)new AnimatorListenerAdapter() {
                public void onAnimationEnd(final Animator animator) {
                    LockscreenOpaLayout.this.mGestureAnimatorSet = LockscreenOpaLayout.this.getLineAnimatorSet();
                    LockscreenOpaLayout.this.mGestureAnimatorSet.removeAllListeners();
                    LockscreenOpaLayout.this.mGestureAnimatorSet.addListener((Animator.AnimatorListener)new AnimatorListenerAdapter() {
                        public void onAnimationEnd(final Animator animator) {
                            LockscreenOpaLayout.this.startCollapseAnimation();
                        }
                    });
                    LockscreenOpaLayout.this.mGestureAnimatorSet.end();
                }
            });
            return;
        }
        if (this.mGestureAnimatorSet != null) {
            this.mGestureState = 2;
            this.mGestureAnimatorSet.removeAllListeners();
            this.mGestureAnimatorSet.addListener((Animator.AnimatorListener)new AnimatorListenerAdapter() {
                public void onAnimationEnd(final Animator animator) {
                    LockscreenOpaLayout.this.startCollapseAnimation();
                }
            });
            if (!this.mGestureAnimatorSet.isStarted()) {
                this.mGestureAnimatorSet.start();
            }
        }
    }
}

