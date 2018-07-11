package com.google.android.systemui.elmyra;

import android.content.Context;

import com.android.systemui.Dependency;
import com.android.systemui.R;
import com.android.systemui.util.AsyncSensorManager;

public final class ElmyraContext
{
    private Context mContext;

    public ElmyraContext(final Context mContext) {
        this.mContext = mContext;
    }

    public boolean isAvailable() {
        final String string = this.mContext.getResources().getString(R.string.elmyra_sensor_string_type);
        for (Object o : Dependency.get(AsyncSensorManager.class).getSensorList(-1)) {
            if (o.toString().equals(string)) {
                return true;
            }
        }
        return false;
    }
}

