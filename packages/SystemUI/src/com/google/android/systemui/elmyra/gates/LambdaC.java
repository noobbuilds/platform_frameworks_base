package com.google.android.systemui.elmyra.gates;

import android.os.Handler;
import android.support.annotation.Nullable;
import android.content.Context;
import android.app.TaskStackListener;

final class LambdaC implements Runnable
{
    private byte id;

    @Override
    public final void run() {
        switch (this.id) {
            default: {
                throw new AssertionError();
            }
            case 0: {
                this.run();
            }
            case 1: {
                this.run();
            }
            case 2: {
                this.run();
            }
        }
    }
}
