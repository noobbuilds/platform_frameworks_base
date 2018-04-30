package com.google.android.systemui;

import android.os.UserManager;
import java.util.ArrayList;
import android.view.View;
import com.android.systemui.SysUiServiceProvider;
import com.android.systemui.statusbar.phone.StatusBar;
import android.content.Context;

public class OpaEnabledDispatcher implements OpaEnabledListener
{
    private void dispatchUnchecked(final Context context, final boolean opaEnabled) {
        final StatusBar statusBar = SysUiServiceProvider.getComponent(context, StatusBar.class);
        if (statusBar != null && statusBar.getNavigationBarView() != null) {
            final ArrayList<View> views = statusBar.getNavigationBarView().getHomeButton().getViews();
            for (int i = 0; i < views.size(); ++i) {
                ((OpaLayout)views.get(i)).setOpaEnabled(opaEnabled);
            }
        }
    }
    
    @Override
    public void onOpaEnabledReceived(final Context context, final boolean b, final boolean b2, final boolean b3) {
        this.dispatchUnchecked(context, (b && b2));
    }
}
