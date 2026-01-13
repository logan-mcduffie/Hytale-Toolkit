package io.sentry;

import io.sentry.util.AutoClosableReentrantLock;
import java.io.File;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.TestOnly;
import org.jetbrains.annotations.ApiStatus.Internal;

@Internal
public final class SentryCrashLastRunState {
   private static final SentryCrashLastRunState INSTANCE = new SentryCrashLastRunState();
   private boolean readCrashedLastRun;
   @Nullable
   private Boolean crashedLastRun;
   @NotNull
   private final AutoClosableReentrantLock crashedLastRunLock = new AutoClosableReentrantLock();

   private SentryCrashLastRunState() {
   }

   public static SentryCrashLastRunState getInstance() {
      return INSTANCE;
   }

   @Nullable
   public Boolean isCrashedLastRun(@Nullable String cacheDirPath, boolean deleteFile) {
      ISentryLifecycleToken ignored = this.crashedLastRunLock.acquire();

      Boolean var12;
      label69: {
         label70: {
            try {
               if (this.readCrashedLastRun) {
                  var12 = this.crashedLastRun;
                  break label69;
               }

               if (cacheDirPath == null) {
                  var12 = null;
                  break label70;
               }

               this.readCrashedLastRun = true;
               File javaMarker = new File(cacheDirPath, "last_crash");
               File nativeMarker = new File(cacheDirPath, ".sentry-native/last_crash");
               boolean exists = false;

               try {
                  if (javaMarker.exists()) {
                     exists = true;
                     javaMarker.delete();
                  } else if (nativeMarker.exists()) {
                     exists = true;
                     if (deleteFile) {
                        nativeMarker.delete();
                     }
                  }
               } catch (Throwable var9) {
               }

               this.crashedLastRun = exists;
            } catch (Throwable var10) {
               if (ignored != null) {
                  try {
                     ignored.close();
                  } catch (Throwable var8) {
                     var10.addSuppressed(var8);
                  }
               }

               throw var10;
            }

            if (ignored != null) {
               ignored.close();
            }

            return this.crashedLastRun;
         }

         if (ignored != null) {
            ignored.close();
         }

         return var12;
      }

      if (ignored != null) {
         ignored.close();
      }

      return var12;
   }

   public void setCrashedLastRun(boolean crashedLastRun) {
      ISentryLifecycleToken ignored = this.crashedLastRunLock.acquire();

      try {
         if (!this.readCrashedLastRun) {
            this.crashedLastRun = crashedLastRun;
            this.readCrashedLastRun = true;
         }
      } catch (Throwable var6) {
         if (ignored != null) {
            try {
               ignored.close();
            } catch (Throwable var5) {
               var6.addSuppressed(var5);
            }
         }

         throw var6;
      }

      if (ignored != null) {
         ignored.close();
      }
   }

   @TestOnly
   public void reset() {
      ISentryLifecycleToken ignored = this.crashedLastRunLock.acquire();

      try {
         this.readCrashedLastRun = false;
         this.crashedLastRun = null;
      } catch (Throwable var5) {
         if (ignored != null) {
            try {
               ignored.close();
            } catch (Throwable var4) {
               var5.addSuppressed(var4);
            }
         }

         throw var5;
      }

      if (ignored != null) {
         ignored.close();
      }
   }
}
