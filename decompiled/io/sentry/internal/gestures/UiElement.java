package io.sentry.internal.gestures;

import io.sentry.util.Objects;
import java.lang.ref.WeakReference;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class UiElement {
   @NotNull
   final WeakReference<Object> viewRef;
   @Nullable
   final String className;
   @Nullable
   final String resourceName;
   @Nullable
   final String tag;
   @NotNull
   final String origin;

   public UiElement(@Nullable Object view, @Nullable String className, @Nullable String resourceName, @Nullable String tag, @NotNull String origin) {
      this.viewRef = new WeakReference<>(view);
      this.className = className;
      this.resourceName = resourceName;
      this.tag = tag;
      this.origin = origin;
   }

   @Nullable
   public String getClassName() {
      return this.className;
   }

   @Nullable
   public String getResourceName() {
      return this.resourceName;
   }

   @Nullable
   public String getTag() {
      return this.tag;
   }

   @NotNull
   public String getOrigin() {
      return this.origin;
   }

   @NotNull
   public String getIdentifier() {
      return this.resourceName != null ? this.resourceName : Objects.requireNonNull(this.tag, "UiElement.tag can't be null");
   }

   @Override
   public boolean equals(Object o) {
      if (this == o) {
         return true;
      } else if (o != null && this.getClass() == o.getClass()) {
         UiElement uiElement = (UiElement)o;
         return Objects.equals(this.className, uiElement.className)
            && Objects.equals(this.resourceName, uiElement.resourceName)
            && Objects.equals(this.tag, uiElement.tag);
      } else {
         return false;
      }
   }

   @Nullable
   public Object getView() {
      return this.viewRef.get();
   }

   @Override
   public int hashCode() {
      return Objects.hash(this.viewRef, this.resourceName, this.tag);
   }

   public static enum Type {
      CLICKABLE,
      SCROLLABLE;
   }
}
