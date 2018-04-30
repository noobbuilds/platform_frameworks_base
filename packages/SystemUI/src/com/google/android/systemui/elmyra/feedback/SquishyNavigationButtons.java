package com.google.android.systemui.elmyra.feedback;

import java.util.ArrayList;
import java.util.Arrays;
import android.view.View;
import java.util.List;
import com.android.systemui.statusbar.phone.NavigationBarView;
import com.android.systemui.SysUiServiceProvider;
import android.content.Context;
import com.android.systemui.keyguard.KeyguardViewMediator;

public class SquishyNavigationButtons extends NavigationBarEffect
{
    private final KeyguardViewMediator mKeyguardViewMediator;
    private final SquishyViewController mViewController;
    
    public SquishyNavigationButtons(final Context context) {
        super(context);
        this.mViewController = new SquishyViewController(context);
        this.mKeyguardViewMediator = SysUiServiceProvider.getComponent(context, KeyguardViewMediator.class);
    }
    
    @Override
    protected List<FeedbackEffect> findFeedbackEffects(final NavigationBarView navigationBarView) {
        this.mViewController.clearViews();
        final ArrayList<View> views = navigationBarView.getBackButton().getViews();
        for (int i = 0; i < views.size(); ++i) {
            this.mViewController.addLeftView((View)views.get(i));
        }
        final ArrayList<View> views2 = navigationBarView.getRecentsButton().getViews();
        for (int j = 0; j < views2.size(); ++j) {
            this.mViewController.addRightView((View)views2.get(j));
        }
        return Arrays.asList(this.mViewController);
    }
    
    @Override
    protected boolean isActiveFeedbackEffect(final FeedbackEffect feedbackEffect) {
        return this.mKeyguardViewMediator.isShowingAndNotOccluded() ^ true;
    }
    
    @Override
    protected boolean validateFeedbackEffects(final List<FeedbackEffect> list) {
        return this.mViewController.isAttachedToWindow();
    }
}
