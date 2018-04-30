package com.google.android.systemui.elmyra.actions;

        import java.util.Collection;
        import java.util.ArrayList;
        import android.support.annotation.Nullable;
        import android.os.Handler;
        import com.google.android.systemui.elmyra.feedback.FeedbackEffect;
        import java.util.List;
        import android.content.Context;

final class LambdaB implements Runnable
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
        }
    }
}
