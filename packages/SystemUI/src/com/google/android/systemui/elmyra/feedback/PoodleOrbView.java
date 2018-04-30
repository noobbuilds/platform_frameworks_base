package com.google.android.systemui.elmyra.feedback;

import java.util.Iterator;
import java.util.Collection;
import java.util.Arrays;
import android.animation.Animator;
import android.util.TypedValue;
import android.animation.PropertyValuesHolder;
import android.animation.TimeInterpolator;
import android.view.animation.OvershootInterpolator;
import android.animation.ObjectAnimator;
import android.animation.Keyframe;
import android.graphics.Path;
import android.util.AttributeSet;
import android.content.Context;
import android.view.View;
import android.animation.ValueAnimator;
import java.util.ArrayList;
import android.animation.Animator.AnimatorListener;
import android.widget.FrameLayout;

import com.android.systemui.R;

public class PoodleOrbView extends FrameLayout implements Animator.AnimatorListener, FeedbackEffect
{
    private ArrayList<ValueAnimator> mAnimations;
    private View mBackground;
    private View mBlue;
    private int mFeedbackHeight;
    private View mGreen;
    private View mRed;
    private int mState;
    private View mYellow;
    
    public PoodleOrbView(final Context context) {
        super(context);
        this.mState = 0;
        this.mAnimations = new ArrayList<ValueAnimator>();
    }
    
    public PoodleOrbView(final Context context, final AttributeSet set) {
        super(context, set);
        this.mState = 0;
        this.mAnimations = new ArrayList<ValueAnimator>();
    }
    
    public PoodleOrbView(final Context context, final AttributeSet set, final int n) {
        super(context, set, n);
        this.mState = 0;
        this.mAnimations = new ArrayList<ValueAnimator>();
    }
    
    public PoodleOrbView(final Context context, final AttributeSet set, final int n, final int n2) {
        super(context, set, n, n2);
        this.mState = 0;
        this.mAnimations = new ArrayList<ValueAnimator>();
    }
    
    private Keyframe[][] approximatePath(final Path path, final float n, final float n2) {
        final float[] approximate = path.approximate(0.5f);
        final Keyframe[] array = new Keyframe[approximate.length / 3];
        final Keyframe[] array2 = new Keyframe[approximate.length / 3];
        int n4;
        float n5;
        for (int i = 0, n3 = 0; i < approximate.length; i = n4 + 1, array[n3] = Keyframe.ofFloat(n5, approximate[n4]), array2[n3] = Keyframe.ofFloat(n5, approximate[i]), ++n3, ++i) {
            n4 = i + 1;
            n5 = n + (n2 - n) * approximate[i];
        }
        return new Keyframe[][] { array, array2 };
    }
    
    private ObjectAnimator[] createBackgroundAnimator(final View view) {
        int i = 0;
        final Keyframe[] array = { Keyframe.ofFloat(0.0f, 0.0f), Keyframe.ofFloat(0.375f, 1.2f), Keyframe.ofFloat(0.75f, 1.2f), Keyframe.ofFloat(0.95f, 0.2f), Keyframe.ofFloat(1.0f, 0.0f) };
        array[1].setInterpolator((TimeInterpolator)new OvershootInterpolator());
        ObjectAnimator[] array2;
        for (array2 = new ObjectAnimator[] { ObjectAnimator.ofPropertyValuesHolder((Object)view, new PropertyValuesHolder[] { PropertyValuesHolder.ofKeyframe(View.SCALE_X, array) }), ObjectAnimator.ofPropertyValuesHolder((Object)view, new PropertyValuesHolder[] { PropertyValuesHolder.ofKeyframe(View.SCALE_Y, array) }), ObjectAnimator.ofPropertyValuesHolder((Object)view, new PropertyValuesHolder[] { PropertyValuesHolder.ofKeyframe(View.TRANSLATION_Y, new Keyframe[] { Keyframe.ofFloat(0.0f, view.getTranslationY()), Keyframe.ofFloat(0.375f, this.px(27.5f)), Keyframe.ofFloat(0.75f, this.px(27.5f)), Keyframe.ofFloat(0.95f, this.px(21.75f)) }) }) }; i < array2.length; ++i) {
            array2[i].setDuration(1000L);
        }
        return array2;
    }
    
    private ObjectAnimator[] createDotAnimator(final View view, final float n, final Path path) {
        final Keyframe[] array = { Keyframe.ofFloat(0.0f, view.getScaleX()), Keyframe.ofFloat(0.75f, view.getScaleX()), Keyframe.ofFloat(0.95f, 0.3f), Keyframe.ofFloat(1.0f, 0.0f) };
        final Keyframe ofFloat = Keyframe.ofFloat(0.0f, 1.0f);
        final Keyframe ofFloat2 = Keyframe.ofFloat(0.75f, 1.0f);
        final Keyframe ofFloat3 = Keyframe.ofFloat(0.95f, 0.25f);
        final Keyframe ofFloat4 = Keyframe.ofFloat(1.0f, 0.0f);
        final Keyframe[][] approximatePath = this.approximatePath(path, 0.75f, 1.0f);
        final Keyframe[] array2 = new Keyframe[approximatePath[0].length + 2];
        array2[0] = Keyframe.ofFloat(0.0f, view.getTranslationX());
        array2[1] = Keyframe.ofFloat(0.75f, view.getTranslationX());
        System.arraycopy(approximatePath[0], 0, array2, 2, approximatePath[0].length);
        final Keyframe[] array3 = new Keyframe[approximatePath[1].length + 3];
        array3[0] = Keyframe.ofFloat(0.0f, view.getTranslationY());
        array3[1] = Keyframe.ofFloat(n, view.getTranslationY());
        array3[2] = Keyframe.ofFloat(0.75f, view.getTranslationY() - this.mFeedbackHeight);
        System.arraycopy(approximatePath[1], 0, array3, 3, approximatePath[1].length);
        array3[2].setInterpolator((TimeInterpolator)new OvershootInterpolator());
        final ObjectAnimator[] array4 = { ObjectAnimator.ofPropertyValuesHolder((Object)view, new PropertyValuesHolder[] { PropertyValuesHolder.ofKeyframe(View.SCALE_X, array) }), ObjectAnimator.ofPropertyValuesHolder((Object)view, new PropertyValuesHolder[] { PropertyValuesHolder.ofKeyframe(View.SCALE_Y, array) }), ObjectAnimator.ofPropertyValuesHolder((Object)view, new PropertyValuesHolder[] { PropertyValuesHolder.ofKeyframe(View.TRANSLATION_X, array2) }), ObjectAnimator.ofPropertyValuesHolder((Object)view, new PropertyValuesHolder[] { PropertyValuesHolder.ofKeyframe(View.TRANSLATION_Y, array3) }), ObjectAnimator.ofPropertyValuesHolder((Object)view, new PropertyValuesHolder[] { PropertyValuesHolder.ofKeyframe(View.ALPHA, new Keyframe[] { ofFloat, ofFloat2, ofFloat3, ofFloat4 }) }) };
        for (int i = 0; i < array4.length; ++i) {
            array4[i].setDuration(1000L);
        }
        return array4;
    }
    
    private float px(final float n) {
        return TypedValue.applyDimension(1, n, this.getResources().getDisplayMetrics());
    }
    
    public void onAnimationCancel(final Animator animator) {
    }
    
    public void onAnimationEnd(final Animator animator) {
        this.onProgress(0.0f, this.mState = 0);
    }
    
    public void onAnimationRepeat(final Animator animator) {
    }
    
    public void onAnimationStart(final Animator animator) {
    }
    
    protected void onFinishInflate() {
        super.onFinishInflate();
        this.mBackground = this.findViewById(R.id.elmyra_feedback_background);
        this.mBlue = this.findViewById(R.id.elmyra_feedback_blue);
        this.mGreen = this.findViewById(R.id.elmyra_feedback_green);
        this.mRed = this.findViewById(R.id.elmyra_feedback_red);
        this.mYellow = this.findViewById(R.id.elmyra_feedback_yellow);
        this.mFeedbackHeight = this.getResources().getDimensionPixelSize(R.dimen.opa_elmyra_orb_height);
        this.mBackground.setScaleX(0.0f);
        this.mBackground.setScaleY(0.0f);
        this.mBlue.setTranslationY(this.mBlue.getTranslationY() + this.mFeedbackHeight);
        this.mGreen.setTranslationY(this.mGreen.getTranslationY() + this.mFeedbackHeight);
        this.mRed.setTranslationY(this.mRed.getTranslationY() + this.mFeedbackHeight);
        this.mYellow.setTranslationY(this.mYellow.getTranslationY() + this.mFeedbackHeight);
        this.mAnimations.addAll((Collection<? extends ValueAnimator>)Arrays.asList(this.createBackgroundAnimator(this.mBackground)));
        this.mAnimations.get(0).addListener((Animator.AnimatorListener)this);
        final Path path = new Path();
        path.moveTo(this.mBlue.getTranslationX(), this.mBlue.getTranslationY() - this.mFeedbackHeight);
        path.cubicTo(this.px(-32.5f), this.px(-27.5f), this.px(15.0f), this.px(-33.75f), this.px(-2.5f), this.px(-20.0f));
        this.mAnimations.addAll((Collection<? extends ValueAnimator>)Arrays.asList(this.createDotAnimator(this.mBlue, 0.0f, path)));
        final Path path2 = new Path();
        path2.moveTo(this.mRed.getTranslationX(), this.mRed.getTranslationY() - this.mFeedbackHeight);
        path2.cubicTo(this.px(-25.0f), this.px(-17.5f), this.px(-20.0f), this.px(-27.5f), this.px(2.5f), this.px(-20.0f));
        this.mAnimations.addAll((Collection<? extends ValueAnimator>)Arrays.asList(this.createDotAnimator(this.mRed, 0.05f, path2)));
        final Path path3 = new Path();
        path3.moveTo(this.mYellow.getTranslationX(), this.mYellow.getTranslationY() - this.mFeedbackHeight);
        path3.cubicTo(this.px(21.25f), this.px(-33.75f), this.px(15.0f), this.px(-27.5f), this.px(0.0f), this.px(-20.0f));
        this.mAnimations.addAll((Collection<? extends ValueAnimator>)Arrays.asList(this.createDotAnimator(this.mYellow, 0.1f, path3)));
        final Path path4 = new Path();
        path4.moveTo(this.mGreen.getTranslationX(), this.mGreen.getTranslationY() - this.mFeedbackHeight);
        path4.cubicTo(this.px(-27.5f), this.px(-20.0f), this.px(35.0f), this.px(-30.0f), this.px(0.0f), this.px(-20.0f));
        this.mAnimations.addAll((Collection<? extends ValueAnimator>)Arrays.asList(this.createDotAnimator(this.mGreen, 0.2f, path4)));
    }
    
    public void onProgress(final float n, final int n2) {
        if (this.mState == 3) {
            return;
        }
        for (final ValueAnimator valueAnimator : this.mAnimations) {
            valueAnimator.cancel();
            valueAnimator.setCurrentFraction(0.0f + 0.75f * n);
        }
        if (n == 0.0f) {
            this.mState = 0;
        }
        else if (n == 1.0f) {
            this.mState = 2;
        }
        else {
            this.mState = 1;
        }
    }
    
    public void onRelease() {
        if (this.mState == 2 || this.mState == 1) {
            final Iterator<ValueAnimator> iterator = this.mAnimations.iterator();
            while (iterator.hasNext()) {
                iterator.next().reverse();
            }
            this.mState = 0;
        }
    }
    
    public void onResolve() {
        final Iterator<ValueAnimator> iterator = this.mAnimations.iterator();
        while (iterator.hasNext()) {
            iterator.next().start();
        }
        this.mState = 3;
    }
}
