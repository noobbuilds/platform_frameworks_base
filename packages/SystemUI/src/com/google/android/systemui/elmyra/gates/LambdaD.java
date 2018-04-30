package com.google.android.systemui.elmyra.gates;

import android.provider.Settings.Secure;
import java.util.Collection;
import java.util.ArrayList;
import android.content.Context;
import com.google.android.systemui.elmyra.UserContentObserver;
import com.google.android.systemui.elmyra.actions.Action;
import java.util.List;
import android.net.Uri;
import java.util.function.Consumer;

final class LambdaD implements Consumer
{
    private byte id;

    @Override
    public final void accept(final Object o) {
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
        }
    }
}
