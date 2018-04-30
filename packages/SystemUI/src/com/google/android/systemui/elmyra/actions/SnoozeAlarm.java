package com.google.android.systemui.elmyra.actions;

import android.content.Intent;
import android.content.Context;

public class SnoozeAlarm extends DeskClockAction
{
    public SnoozeAlarm(final Context context) {
        super(context);
    }
    
    @Override
    protected Intent createDismissIntent() {
        return new Intent("android.intent.action.SNOOZE_ALARM");
    }
    
    @Override
    protected String getAlertAction() {
        return "com.google.android.deskclock.action.ALARM_ALERT";
    }
    
    @Override
    protected String getDoneAction() {
        return "com.google.android.deskclock.action.ALARM_DONE";
    }
}
