package io.sentry.internal.gestures;

import org.jetbrains.annotations.Nullable;

public interface GestureTargetLocator {
   @Nullable
   UiElement locate(@Nullable Object var1, float var2, float var3, UiElement.Type var4);
}
