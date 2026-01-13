package io.sentry;

import java.util.Iterator;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.ApiStatus.Internal;

@Internal
public final class FullyDisplayedReporter {
   @NotNull
   private static final FullyDisplayedReporter instance = new FullyDisplayedReporter();
   @NotNull
   private final List<FullyDisplayedReporter.FullyDisplayedReporterListener> listeners = new CopyOnWriteArrayList<>();

   private FullyDisplayedReporter() {
   }

   @NotNull
   public static FullyDisplayedReporter getInstance() {
      return instance;
   }

   public void registerFullyDrawnListener(@NotNull FullyDisplayedReporter.FullyDisplayedReporterListener listener) {
      this.listeners.add(listener);
   }

   public void reportFullyDrawn() {
      Iterator<FullyDisplayedReporter.FullyDisplayedReporterListener> listenerIterator = this.listeners.iterator();
      this.listeners.clear();

      while (listenerIterator.hasNext()) {
         listenerIterator.next().onFullyDrawn();
      }
   }

   @Internal
   public interface FullyDisplayedReporterListener {
      void onFullyDrawn();
   }
}
