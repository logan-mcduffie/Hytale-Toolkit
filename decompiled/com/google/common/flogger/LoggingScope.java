package com.google.common.flogger;

import com.google.common.flogger.util.Checks;
import java.lang.ref.ReferenceQueue;
import java.lang.ref.WeakReference;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

public abstract class LoggingScope {
   private final String label;

   public static LoggingScope create(String label) {
      return new LoggingScope.WeakScope(Checks.checkNotNull(label, "label"));
   }

   protected LoggingScope(String label) {
      this.label = label;
   }

   protected abstract LogSiteKey specialize(LogSiteKey var1);

   protected abstract void onClose(Runnable var1);

   @Override
   public final String toString() {
      return this.label;
   }

   static final class WeakScope extends LoggingScope {
      private final LoggingScope.WeakScope.KeyPart keyPart = new LoggingScope.WeakScope.KeyPart(this);

      public WeakScope(String label) {
         super(label);
      }

      @Override
      protected LogSiteKey specialize(LogSiteKey key) {
         return SpecializedLogSiteKey.of(key, this.keyPart);
      }

      @Override
      protected void onClose(Runnable remove) {
         LoggingScope.WeakScope.KeyPart.removeUnusedKeys();
         this.keyPart.onCloseHooks.offer(remove);
      }

      void closeForTesting() {
         this.keyPart.close();
      }

      private static class KeyPart extends WeakReference<LoggingScope> {
         private static final ReferenceQueue<LoggingScope> queue = new ReferenceQueue<>();
         private final Queue<Runnable> onCloseHooks = new ConcurrentLinkedQueue<>();

         KeyPart(LoggingScope scope) {
            super(scope, queue);
         }

         static void removeUnusedKeys() {
            for (LoggingScope.WeakScope.KeyPart p = (LoggingScope.WeakScope.KeyPart)queue.poll(); p != null; p = (LoggingScope.WeakScope.KeyPart)queue.poll()) {
               p.close();
            }
         }

         private void close() {
            for (Runnable r = this.onCloseHooks.poll(); r != null; r = this.onCloseHooks.poll()) {
               r.run();
            }
         }
      }
   }
}
