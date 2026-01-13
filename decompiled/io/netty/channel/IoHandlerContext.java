package io.netty.channel;

public interface IoHandlerContext {
   boolean canBlock();

   long delayNanos(long var1);

   long deadlineNanos();

   default void reportActiveIoTime(long activeNanos) {
   }

   default boolean shouldReportActiveIoTime() {
      return false;
   }
}
