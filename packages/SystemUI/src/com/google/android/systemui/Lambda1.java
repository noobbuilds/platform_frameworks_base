package com.google.android.systemui;

import android.view.View.OnTouchListener;
import android.view.ViewConfiguration;
import android.os.SystemClock;
import android.animation.ValueAnimator;
import android.view.MotionEvent;
import android.graphics.Xfermode;
import android.graphics.PorterDuffXfermode;
import android.graphics.PorterDuff.Mode;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import com.android.systemui.statusbar.policy.KeyButtonDrawable;
import android.view.ContextThemeWrapper;
import android.util.Log;
import com.android.systemui.Dependency;
import com.android.systemui.assist.AssistManager;
import android.animation.ObjectAnimator;
import android.animation.Animator.AnimatorListener;
import android.animation.AnimatorListenerAdapter;
import com.android.systemui.Interpolators;
import android.util.AttributeSet;
import android.view.animation.PathInterpolator;
import android.content.Context;
import android.content.res.Resources;
import com.android.systemui.statusbar.policy.KeyButtonView;
import android.widget.ImageView;
import android.animation.AnimatorSet;
import android.animation.Animator;
import android.util.ArraySet;
import java.util.ArrayList;
import android.view.animation.Interpolator;
import com.google.android.systemui.elmyra.feedback.FeedbackEffect;
import com.android.systemui.plugins.statusbar.phone.NavBarButtonProvider;
import android.widget.FrameLayout;
import android.view.View;
import android.view.View.OnLongClickListener;

final class Lambda1 implements View.OnLongClickListener
{
    public final boolean onLongClick(final View view) {
        return this.onLongClick(view);
    }
}
