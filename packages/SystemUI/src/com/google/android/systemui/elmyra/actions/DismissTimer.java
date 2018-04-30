package com.google.android.systemui.elmyra.actions;

import android.content.Intent;
import android.content.Context;

public class DismissTimer extends DeskClockAction
{
    public DismissTimer(final Context context) {
        super(context);
    }
    
    @Override
    protected Intent createDismissIntent() {
        return new Intent("android.intent.action.DISMISS_TIMER");
    }
    
    @Override
    protected String getAlertAction() {
        return "com.google.android.deskclock.action.TIMER_ALERT";
    }
    
    @Override
    protected String getDoneAction() {
        return "com.google.android.deskclock.action.TIMER_DONE";
    }
}
