package io.netty.util;

import io.netty.util.internal.ObjectUtil;
import io.netty.util.internal.SystemPropertyUtil;
import java.util.Locale;

public final class NettyRuntime {
   private static final NettyRuntime.AvailableProcessorsHolder holder = new NettyRuntime.AvailableProcessorsHolder();

   public static void setAvailableProcessors(int availableProcessors) {
      holder.setAvailableProcessors(availableProcessors);
   }

   public static int availableProcessors() {
      return holder.availableProcessors();
   }

   private NettyRuntime() {
   }

   static class AvailableProcessorsHolder {
      private int availableProcessors;

      synchronized void setAvailableProcessors(int availableProcessors) {
         ObjectUtil.checkPositive(availableProcessors, "availableProcessors");
         if (this.availableProcessors != 0) {
            String message = String.format(
               Locale.ROOT, "availableProcessors is already set to [%d], rejecting [%d]", this.availableProcessors, availableProcessors
            );
            throw new IllegalStateException(message);
         } else {
            this.availableProcessors = availableProcessors;
         }
      }

      @SuppressForbidden(reason = "to obtain default number of available processors")
      synchronized int availableProcessors() {
         if (this.availableProcessors == 0) {
            int availableProcessors = SystemPropertyUtil.getInt("io.netty.availableProcessors", Runtime.getRuntime().availableProcessors());
            this.setAvailableProcessors(availableProcessors);
         }

         return this.availableProcessors;
      }
   }
}
