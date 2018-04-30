package com.google.android.systemui.ambientmusic;

import android.annotation.SuppressLint;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View.OnTouchListener;
import android.view.View.OnLayoutChangeListener;
import android.animation.Animator.AnimatorListener;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator.AnimatorUpdateListener;
import android.animation.TimeInterpolator;
import com.android.systemui.Interpolators;
import com.android.systemui.statusbar.phone.NotificationPanelView;
import android.os.SystemClock;
import android.util.AttributeSet;
import android.content.Context;
import android.animation.ValueAnimator;
import android.widget.TextView;
import com.android.systemui.statusbar.phone.StatusBar;
import android.app.PendingIntent;
import android.widget.ImageView;
import com.android.systemui.statusbar.phone.DoubleTapHelper;
import android.view.View;
import com.android.systemui.doze.DozeReceiver;
import com.android.systemui.AutoReinflateContainer;

public class AmbientIndicationContainer extends AutoReinflateContainer implements DozeReceiver
{
    private View mAmbientIndication;
    private DoubleTapHelper mDoubleTapHelper;
    private boolean mDozing;
    private ImageView mIcon;
    private CharSequence mIndication;
    private PendingIntent mIntent;
    private StatusBar mStatusBar;
    private TextView mText;
    private int mTextColor;
    private ValueAnimator mTextColorAnimator;
    
    public AmbientIndicationContainer(final Context context, final AttributeSet set) {
        super(context, set);
    }
    
    private boolean onDoubleTap() {
        if (this.mIntent != null) {
            this.mStatusBar.wakeUpIfDozing(SystemClock.uptimeMillis(), this.mAmbientIndication);
            this.mStatusBar.startPendingIntentDismissingKeyguard(this.mIntent);
            return true;
        }
        return false;
    }
    
    @SuppressLint("WrongConstant")
    private void updateBottomPadding() {
        final NotificationPanelView panel = this.mStatusBar.getPanel();
        int ambientIndicationBottomPadding = 0;
        if (this.mAmbientIndication.getVisibility() == 0) {
            ambientIndicationBottomPadding = this.mStatusBar.getNotificationScrollLayout().getBottom() - this.getTop();
        }
        panel.setAmbientIndicationBottomPadding(ambientIndicationBottomPadding);
    }
    
    private void updateColors() {
        if (this.mTextColorAnimator != null && this.mTextColorAnimator.isRunning()) {
            this.mTextColorAnimator.cancel();
        }
        final int defaultColor = this.mText.getTextColors().getDefaultColor();
        int mTextColor;
        if (this.mDozing) {
            mTextColor = -1;
        }
        else {
            mTextColor = this.mTextColor;
        }
        if (defaultColor == mTextColor) {
            return;
        }
        (this.mTextColorAnimator = ValueAnimator.ofArgb(new int[] { defaultColor, mTextColor })).setInterpolator((TimeInterpolator)Interpolators.LINEAR_OUT_SLOW_IN);
        this.mTextColorAnimator.setDuration(200L);
    //    this.mTextColorAnimator.addUpdateListener((ValueAnimator$AnimatorUpdateListener)new -$Lambda$5JKPrIDcanF1JcTf-j26JEesQKM(this));
        this.mTextColorAnimator.addListener((Animator.AnimatorListener)new AnimatorListenerAdapter() {
            public void onAnimationEnd(final Animator animator) {
                AmbientIndicationContainer.this.mTextColorAnimator = null;
            }
        });
        this.mTextColorAnimator.start();
    }
    
    public void hideIndication() {
        this.setIndication(null, null);
    }
    
    public void initializeView(final StatusBar mStatusBar) {
        this.mStatusBar = mStatusBar;
   //     this.addInflateListener((InflateListener)new -$Lambda$5JKPrIDcanF1JcTf-j26JEesQKM$3(this));
    //    this.addOnLayoutChangeListener((View$OnLayoutChangeListener)new -$Lambda$5JKPrIDcanF1JcTf-j26JEesQKM$1(this));
    }
    
    @Override
    public void setDozing(final boolean mDozing) {
        this.mDozing = mDozing;
        this.updateColors();
    }
    
    public void setIndication(final CharSequence charSequence, final PendingIntent mIntent) {
        int visibility = 0;
        this.mText.setText(charSequence);
        this.mIndication = charSequence;
        this.mIntent = mIntent;
        this.mAmbientIndication.setClickable(mIntent != null);
        final boolean empty = TextUtils.isEmpty(charSequence);
        final View mAmbientIndication = this.mAmbientIndication;
        if (!(empty ^ true)) {
            visibility = 4;
        }
        mAmbientIndication.setVisibility(visibility);
        this.updateBottomPadding();
    }
}
