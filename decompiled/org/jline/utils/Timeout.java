package org.jline.utils;

public class Timeout {
   private final long timeout;
   private long cur = 0L;
   private long end = Long.MAX_VALUE;

   public Timeout(long timeout) {
      this.timeout = timeout;
   }

   public boolean isInfinite() {
      return this.timeout <= 0L;
   }

   public boolean isFinite() {
      return this.timeout > 0L;
   }

   public boolean elapsed() {
      if (this.timeout > 0L) {
         this.cur = System.currentTimeMillis();
         if (this.end == Long.MAX_VALUE) {
            this.end = this.cur + this.timeout;
         }

         return this.cur >= this.end;
      } else {
         return false;
      }
   }

   public long timeout() {
      return this.timeout > 0L ? Math.max(1L, this.end - this.cur) : this.timeout;
   }
}
