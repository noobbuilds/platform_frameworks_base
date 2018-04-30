package com.google.android.systemui.elmyra.actions;

import java.util.function.Consumer;

public final class LambdaF implements Consumer
{
    private byte id;
    public LambdaF(byte id) {
        this.id = id;
    }

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
            case 2: {
                this.accept(o);
            }
            case 3: {
                this.accept(o);
            }
        }
    }

    @Override
    public Consumer andThen(Consumer after) {
        return null;
    }
}
