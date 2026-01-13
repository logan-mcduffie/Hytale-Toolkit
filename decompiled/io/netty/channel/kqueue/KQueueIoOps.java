package io.netty.channel.kqueue;

import io.netty.channel.IoOps;

public final class KQueueIoOps implements IoOps {
   private final short filter;
   private final short flags;
   private final int fflags;
   private final long data;

   public static KQueueIoOps newOps(short filter, short flags, int fflags) {
      return new KQueueIoOps(filter, flags, fflags, 0L);
   }

   private KQueueIoOps(short filter, short flags, int fflags, long data) {
      this.filter = filter;
      this.flags = flags;
      this.fflags = fflags;
      this.data = data;
   }

   public short filter() {
      return this.filter;
   }

   public short flags() {
      return this.flags;
   }

   public int fflags() {
      return this.fflags;
   }

   public long data() {
      return this.data;
   }

   @Override
   public String toString() {
      return "KQueueIoOps{filter=" + this.filter + ", flags=" + this.flags + ", fflags=" + this.fflags + ", data=" + this.data + '}';
   }
}
