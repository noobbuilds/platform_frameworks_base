package com.google.android.systemui.elmyra.feedback;

import com.android.systemui.R;
import com.android.systemui.SysUiServiceProvider;
import com.android.systemui.statusbar.phone.StatusBar;
import android.animation.PropertyValuesHolder;
import android.animation.AnimatorSet;
import android.animation.TimeInterpolator;
import android.animation.ObjectAnimator;
import android.view.View;
import com.android.systemui.Dependency;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import com.android.systemui.statusbar.phone.LockIcon;
import com.android.systemui.statusbar.policy.KeyguardMonitor;
import com.android.systemui.statusbar.phone.KeyguardBottomAreaView;
import android.animation.Animator;
import android.content.Context;
import android.view.animation.Interpolator;

public class OpaLockscreen implements FeedbackEffect
{
    private static final Interpolator LOCK_ICON_HIDE_INTERPOLATOR;
    private static final Interpolator LOCK_ICON_SHOW_INTERPOLATOR;

    static {
        LOCK_ICON_HIDE_INTERPOLATOR = (Interpolator)new DecelerateInterpolator();
        LOCK_ICON_SHOW_INTERPOLATOR = (Interpolator)new AccelerateInterpolator();
    }

    private final Context mContext;
    private final KeyguardMonitor mKeyguardMonitor;
    private Animator mHideLockIconAnimator;
    private KeyguardBottomAreaView mKeyguardBottomAreaView;
    private LockIcon mLockIcon;
    private FeedbackEffect mLockscreenOpaLayout;
    private Animator mShowLockIconAnimator;

    public OpaLockscreen(final Context mContext) {
        this.mContext = mContext;
        this.mKeyguardMonitor = Dependency.get(KeyguardMonitor.class);
        this.refreshLockscreenOpaLayout();
    }

    private ObjectAnimator createAlphaObjectAnimator(final View view, final float n, final int n2, final int n3, final Interpolator interpolator) {
        final ObjectAnimator ofFloat = ObjectAnimator.ofFloat(view, View.ALPHA, n);
        ofFloat.setDuration((long)n2);
        ofFloat.setStartDelay((long)n3);
        ofFloat.setInterpolator((TimeInterpolator)interpolator);
        return ofFloat;
    }

    private Animator createHideAnimator(final View view) {
        final ObjectAnimator scaleObjectAnimator = this.createScaleObjectAnimator(view, 0.0f, 200, 0, OpaLockscreen.LOCK_ICON_HIDE_INTERPOLATOR);
        final ObjectAnimator alphaObjectAnimator = this.createAlphaObjectAnimator(view, 0.0f, 200, 0, OpaLockscreen.LOCK_ICON_HIDE_INTERPOLATOR);
        final AnimatorSet set = new AnimatorSet();
        set.play((Animator)scaleObjectAnimator).with((Animator)alphaObjectAnimator);
        return (Animator)set;
    }

    private ObjectAnimator createScaleObjectAnimator(final View view, final float n, final int n2, final int n3, final Interpolator interpolator) {
        final ObjectAnimator ofPropertyValuesHolder = ObjectAnimator.ofPropertyValuesHolder((Object)view, new PropertyValuesHolder[] { PropertyValuesHolder.ofFloat(View.SCALE_X, new float[] { n }), PropertyValuesHolder.ofFloat(View.SCALE_Y, new float[] { n }) });
        ofPropertyValuesHolder.setDuration((long)n2);
        ofPropertyValuesHolder.setStartDelay((long)n3);
        ofPropertyValuesHolder.setInterpolator((TimeInterpolator)interpolator);
        return ofPropertyValuesHolder;
    }

    private Animator createShowAnimator(final View view) {
        final ObjectAnimator scaleObjectAnimator = this.createScaleObjectAnimator(view, 1.0f, 200, 175, OpaLockscreen.LOCK_ICON_SHOW_INTERPOLATOR);
        final ObjectAnimator alphaObjectAnimator = this.createAlphaObjectAnimator(view, 1.0f, 200, 175, OpaLockscreen.LOCK_ICON_SHOW_INTERPOLATOR);
        final AnimatorSet set = new AnimatorSet();
        set.play((Animator)scaleObjectAnimator).with((Animator)alphaObjectAnimator);
        return (Animator)set;
    }

    private void hideLockIcon() {
        this.mShowLockIconAnimator.cancel();
        if (!this.isLockIconHidden() && this.mLockIcon.isAttachedToWindow()) {
            this.mHideLockIconAnimator.start();
        }
    }

    private boolean isLockIconHidden() {
        boolean b = true;
        if (!this.mHideLockIconAnimator.isRunning()) {
            b = (this.mLockIcon.getAlpha() == 0.0f && b);
        }
        return b;
    }

    private boolean isLockIconShown() {
        boolean b = true;
        if (!this.mShowLockIconAnimator.isRunning()) {
            b = (this.mLockIcon.getAlpha() == 1.0f && b);
        }
        return b;
    }

    private void refreshLockscreenOpaLayout() {
        final StatusBar statusBar = SysUiServiceProvider.getComponent(this.mContext, StatusBar.class);
        if (statusBar != null && statusBar.getKeyguardBottomAreaView() != null && !(this.mKeyguardMonitor.isShowing() ^ true)) {
            final KeyguardBottomAreaView keyguardBottomAreaView = statusBar.getKeyguardBottomAreaView();
            if (this.mLockscreenOpaLayout == null || (keyguardBottomAreaView.equals(this.mKeyguardBottomAreaView) ^ true)) {
                this.mKeyguardBottomAreaView = keyguardBottomAreaView;
                if (this.mLockIcon != null) {
                    this.showLockIcon();
                }
                this.mLockIcon = keyguardBottomAreaView.getLockIcon();
                this.mHideLockIconAnimator = this.createHideAnimator((View)this.mLockIcon);
                this.mShowLockIconAnimator = this.createShowAnimator((View)this.mLockIcon);
                if (this.mLockscreenOpaLayout != null) {
                    this.mLockscreenOpaLayout.onRelease();
                }
                this.mLockscreenOpaLayout = (FeedbackEffect)keyguardBottomAreaView.findViewById(R.id.lockscreen_opa);
            }
            return;
        }
        this.mKeyguardBottomAreaView = null;
        this.mLockIcon = null;
        this.mLockscreenOpaLayout = null;
    }

    private void showLockIcon() {
        this.mHideLockIconAnimator.cancel();
        if (!this.isLockIconShown() && this.mLockIcon.isAttachedToWindow()) {
            this.mShowLockIconAnimator.start();
        }
    }

    @Override
    public void onProgress(final float n, final int n2) {
        this.refreshLockscreenOpaLayout();
        if (this.mLockscreenOpaLayout != null) {
            this.hideLockIcon();
            this.mLockscreenOpaLayout.onProgress(n, n2);
        }
    }

    @Override
    public void onRelease() {
        this.refreshLockscreenOpaLayout();
        if (this.mLockscreenOpaLayout != null) {
            this.showLockIcon();
            this.mLockscreenOpaLayout.onRelease();
        }
    }

    @Override
    public void onResolve() {
        this.refreshLockscreenOpaLayout();
        if (this.mLockscreenOpaLayout != null) {
            this.showLockIcon();
            this.mLockscreenOpaLayout.onResolve();
        }
    }
}

