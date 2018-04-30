package com.google.android.systemui.elmyra.gates;

import com.android.systemui.SysUiServiceProvider;
import java.util.Collection;
import java.util.ArrayList;
import android.content.Context;
import com.google.android.systemui.elmyra.actions.Action;
import java.util.List;
import com.android.systemui.statusbar.CommandQueue;

public class NavigationBarVisibility extends Gate
{
    private final CommandQueue mCommandQueue;
    private final CommandQueue.Callbacks mCommandQueueCallbacks;
    private final List<Action> mExceptions;
    private boolean mIsNavigationHidden;
    
    public NavigationBarVisibility(final Context context, final List<Action> list) {
        super(context);
        this.mCommandQueueCallbacks = new CommandQueue.Callbacks() {
            @Override
            public void setWindowState(final int n, final int n2) {
                if (n == 2) {
                    final boolean b = n2 != 0;
                    if (b != NavigationBarVisibility.this.mIsNavigationHidden) {
                        NavigationBarVisibility.this.mIsNavigationHidden = b;
                        NavigationBarVisibility.this.notifyListener();
                    }
                }
            }
        };
        this.mExceptions = new ArrayList<Action>(list);
        this.mIsNavigationHidden = false;
        (this.mCommandQueue = SysUiServiceProvider.getComponent(context, CommandQueue.class)).addCallbacks(this.mCommandQueueCallbacks);
    }
    
    @Override
    protected boolean isBlocked() {
        for (int i = 0; i < this.mExceptions.size(); ++i) {
            if (this.mExceptions.get(i).isAvailable()) {
                return false;
            }
        }
        return this.mIsNavigationHidden;
    }
    
    @Override
    protected void onActivate() {
    }
    
    @Override
    protected void onDeactivate() {
    }
    
    @Override
    public String toString() {
        return super.toString() + " [mIsNavigationHidden -> " + this.mIsNavigationHidden + "; mExceptions -> " + this.mExceptions + "]";
    }
}
