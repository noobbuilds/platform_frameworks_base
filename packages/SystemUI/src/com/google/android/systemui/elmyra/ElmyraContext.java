package com.google.android.systemui.elmyra;

import java.util.Iterator;
import android.hardware.Sensor;
import com.android.systemui.Dependency;
import com.android.systemui.R;
import com.android.systemui.util.AsyncSensorManager;
import android.content.Context;

public final class ElmyraContext
{
    private Context mContext;
    public ElmyraContext(final Context mContext) {
        this.mContext = mContext;
    }

    public boolean isAvailable() {
        final String string = this.mContext.getResources().getString(R.string.elmyra_sensor_string_type);
        final Iterator iterator = Dependency.get(AsyncSensorManager.class).getSensorList(-1).iterator();
        while (iterator.hasNext()) {
            if (iterator.next().toString().equals(string)) {
                return true;
            }
        }
        return false;
    }
}

