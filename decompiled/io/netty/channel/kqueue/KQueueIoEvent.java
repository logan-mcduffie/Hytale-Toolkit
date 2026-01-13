package io.netty.channel.kqueue;

import io.netty.channel.IoEvent;

public final class KQueueIoEvent implements IoEvent {
   private int ident;
   private short filter;
   private short flags;
   private int fflags;
   private long data;
   private long udata;

   @Deprecated
   public static KQueueIoEvent newEvent(int ident, short filter, short flags, int fflags) {
      return new KQueueIoEvent(ident, filter, flags, fflags, 0L, 0L);
   }

   public static KQueueIoEvent newEvent(int ident, short filter, short flags, int fflags, long data, long udata) {
      return new KQueueIoEvent(ident, filter, flags, fflags, data, udata);
   }

   private KQueueIoEvent(int ident, short filter, short flags, int fflags, long data, long udata) {
      this.ident = ident;
      this.filter = filter;
      this.flags = flags;
      this.fflags = fflags;
      this.data = data;
      this.udata = udata;
   }

   KQueueIoEvent() {
      this(0, (short)0, (short)0, 0, 0L, 0L);
   }

   void update(int ident, short filter, short flags, int fflags, long data, long udata) {
      this.ident = ident;
      this.filter = filter;
      this.flags = flags;
      this.fflags = fflags;
      this.data = data;
      this.udata = udata;
   }

   public int ident() {
      return this.ident;
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

   public long udata() {
      return this.udata;
   }

   @Override
   public String toString() {
      return "KQueueIoEvent{ident="
         + this.ident
         + ", filter="
         + this.filter
         + ", flags="
         + this.flags
         + ", fflags="
         + this.fflags
         + ", data="
         + this.data
         + ", udata="
         + this.udata
         + '}';
   }
}
