package com.google.android.systemui.elmyra.feedback;

import android.view.ViewParent;
import android.view.View;
import java.util.ArrayList;
import java.util.List;
import com.android.systemui.SysUiServiceProvider;
import android.content.Context;
import com.android.systemui.statusbar.phone.NavigationBarView;
import com.android.systemui.keyguard.KeyguardViewMediator;

public class OpaHomeButton extends NavigationBarEffect
{
    private final KeyguardViewMediator mKeyguardViewMediator;
    private NavigationBarView mNavigationBar;
    
    public OpaHomeButton(final Context context) {
        super(context);
        this.mKeyguardViewMediator = SysUiServiceProvider.getComponent(context, KeyguardViewMediator.class);
    }
    
    @Override
    protected List<FeedbackEffect> findFeedbackEffects(final NavigationBarView mNavigationBar) {
        final ArrayList<FeedbackEffect> list = new ArrayList<FeedbackEffect>();
        final ArrayList<View> views = mNavigationBar.getHomeButton().getViews();
        for (int i = 0; i < views.size(); ++i) {
            final View view = views.get(i);
            if (view instanceof FeedbackEffect) {
                list.add((FeedbackEffect)view);
            }
        }
        this.mNavigationBar = mNavigationBar;
        return list;
    }
    
    @Override
    protected boolean isActiveFeedbackEffect(final FeedbackEffect feedbackEffect) {
        if (this.mKeyguardViewMediator.isShowingAndNotOccluded()) {
            return false;
        }
        final View currentView = this.mNavigationBar.getCurrentView();
        for (ViewParent viewParent = ((View)feedbackEffect).getParent(); viewParent != null; viewParent = viewParent.getParent()) {
            if (viewParent.equals(currentView)) {
                return true;
            }
        }
        return false;
    }
    
    @Override
    protected boolean validateFeedbackEffects(final List<FeedbackEffect> list) {
        for (int i = 0; i < list.size(); ++i) {
            if (!((View)list.get(i)).isAttachedToWindow()) {
                return false;
            }
        }
        return true;
    }
}
