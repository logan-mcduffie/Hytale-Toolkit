package org.bouncycastle.jcajce.provider.drbg;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.bouncycastle.util.Properties;

class EntropyGatherer implements Runnable {
   private static final Logger LOG = Logger.getLogger(EntropyGatherer.class.getName());
   private final long pause;
   private final AtomicBoolean seedAvailable;
   private final AtomicReference<byte[]> entropy;
   private final IncrementalEntropySource baseRandom;

   EntropyGatherer(IncrementalEntropySource var1, AtomicBoolean var2, AtomicReference<byte[]> var3) {
      this.baseRandom = var1;
      this.seedAvailable = var2;
      this.entropy = var3;
      this.pause = getPause();
   }

   @Override
   public void run() {
      try {
         this.entropy.set(this.baseRandom.getEntropy(this.pause));
         this.seedAvailable.set(true);
      } catch (InterruptedException var2) {
         if (LOG.isLoggable(Level.FINE)) {
            LOG.fine("entropy request interrupted - exiting");
         }

         Thread.currentThread().interrupt();
      }
   }

   private static long getPause() {
      String var0 = Properties.getPropertyValue("org.bouncycastle.drbg.gather_pause_secs");
      if (var0 != null) {
         try {
            return Long.parseLong(var0) * 1000L;
         } catch (Exception var2) {
            return 5000L;
         }
      } else {
         return 5000L;
      }
   }
}
