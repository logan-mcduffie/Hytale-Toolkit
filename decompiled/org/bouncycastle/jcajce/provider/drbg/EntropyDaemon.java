package org.bouncycastle.jcajce.provider.drbg;

import java.util.LinkedList;
import java.util.logging.Level;
import java.util.logging.Logger;

class EntropyDaemon implements Runnable {
   private static final Logger LOG = Logger.getLogger(EntropyDaemon.class.getName());
   private final LinkedList<Runnable> tasks = new LinkedList<>();

   public EntropyDaemon() {
   }

   void addTask(Runnable var1) {
      synchronized (this.tasks) {
         this.tasks.add(var1);
      }
   }

   @Override
   public void run() {
      while (!Thread.currentThread().isInterrupted()) {
         Runnable var1;
         synchronized (this.tasks) {
            var1 = this.tasks.poll();
         }

         if (var1 != null) {
            try {
               var1.run();
            } catch (Throwable var4) {
            }
         } else {
            try {
               Thread.sleep(5000L);
            } catch (InterruptedException var5) {
               Thread.currentThread().interrupt();
            }
         }
      }

      if (LOG.isLoggable(Level.FINE)) {
         LOG.fine("entropy thread interrupted - exiting");
      }
   }
}
