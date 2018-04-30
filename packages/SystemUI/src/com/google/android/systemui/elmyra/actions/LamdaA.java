package com.google.android.systemui.elmyra.actions;

import android.telephony.TelephonyManager;
import android.telephony.PhoneStateListener;
import android.os.Bundle;
import com.google.android.systemui.AssistManagerGoogle;
import com.android.systemui.SysUiServiceProvider;
import com.android.systemui.Dependency;
import android.support.annotation.Nullable;
import com.android.systemui.statusbar.phone.StatusBar;
import java.util.Random;
import com.google.android.systemui.OpaEnabledListener;
import android.app.KeyguardManager;
import com.android.systemui.assist.AssistManager;
import android.content.ActivityNotFoundException;
import android.util.Log;
import android.os.Parcelable;
import android.os.Handler;
import android.os.UserHandle;
import android.content.IntentFilter;
import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.res.Resources.NotFoundException;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.Resources;
import android.provider.Settings.Secure;
import com.google.android.systemui.elmyra.feedback.FeedbackEffect;
import java.util.List;
import android.content.Context;
import com.google.android.systemui.elmyra.UserContentObserver;
import com.google.android.systemui.elmyra.gates.Gate;
import com.google.android.systemui.elmyra.gates.CameraVisibility;
import android.net.Uri;
import java.util.function.Consumer;

final class LambdaA implements Consumer
{
    private byte id;
    @Override
    public  void accept(final Object o) {
        switch (this.id) {
            default: {
                throw new AssertionError();
            }
            case 0: {
                this.accept(o);
            }
            case 1: {
                this.accept(o);
            }
            case 2: {
                this.accept(o);
            }
            case 3: {
                this.accept(o);
            }
        }
    }
}
