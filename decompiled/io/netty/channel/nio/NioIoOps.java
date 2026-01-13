package io.netty.channel.nio;

import io.netty.channel.IoOps;

public final class NioIoOps implements IoOps {
   public static final NioIoOps NONE = new NioIoOps(0);
   public static final NioIoOps ACCEPT = new NioIoOps(16);
   public static final NioIoOps CONNECT = new NioIoOps(8);
   public static final NioIoOps WRITE = new NioIoOps(4);
   public static final NioIoOps READ = new NioIoOps(1);
   public static final NioIoOps READ_AND_ACCEPT = new NioIoOps(17);
   public static final NioIoOps READ_AND_WRITE = new NioIoOps(5);
   private static final NioIoEvent[] EVENTS;
   final int value;

   private static void addToArray(NioIoEvent[] array, NioIoOps opt) {
      array[opt.value] = new NioIoOps.DefaultNioIoEvent(opt);
   }

   private NioIoOps(int value) {
      this.value = value;
   }

   public boolean contains(NioIoOps ops) {
      return this.isIncludedIn(ops.value);
   }

   public NioIoOps with(NioIoOps ops) {
      return this.contains(ops) ? this : valueOf(this.value | ops.value());
   }

   public NioIoOps without(NioIoOps ops) {
      return !this.contains(ops) ? this : valueOf(this.value & ~ops.value());
   }

   public int value() {
      return this.value;
   }

   @Override
   public boolean equals(Object o) {
      if (this == o) {
         return true;
      } else if (o != null && this.getClass() == o.getClass()) {
         NioIoOps nioOps = (NioIoOps)o;
         return this.value == nioOps.value;
      } else {
         return false;
      }
   }

   @Override
   public int hashCode() {
      return this.value;
   }

   public static NioIoOps valueOf(int value) {
      return eventOf(value).ops();
   }

   public boolean isIncludedIn(int ops) {
      return (ops & this.value) != 0;
   }

   public boolean isNotIncludedIn(int ops) {
      return (ops & this.value) == 0;
   }

   static NioIoEvent eventOf(int value) {
      if (value > 0 && value < EVENTS.length) {
         NioIoEvent event = EVENTS[value];
         if (event != null) {
            return event;
         }
      }

      return new NioIoOps.DefaultNioIoEvent(new NioIoOps(value));
   }

   static {
      NioIoOps all = new NioIoOps(NONE.value | ACCEPT.value | CONNECT.value | WRITE.value | READ.value);
      EVENTS = new NioIoEvent[all.value + 1];
      addToArray(EVENTS, NONE);
      addToArray(EVENTS, ACCEPT);
      addToArray(EVENTS, CONNECT);
      addToArray(EVENTS, WRITE);
      addToArray(EVENTS, READ);
      addToArray(EVENTS, READ_AND_ACCEPT);
      addToArray(EVENTS, READ_AND_WRITE);
      addToArray(EVENTS, all);
   }

   private static final class DefaultNioIoEvent implements NioIoEvent {
      private final NioIoOps ops;

      DefaultNioIoEvent(NioIoOps ops) {
         this.ops = ops;
      }

      @Override
      public NioIoOps ops() {
         return this.ops;
      }

      @Override
      public boolean equals(Object o) {
         if (this == o) {
            return true;
         } else if (o != null && this.getClass() == o.getClass()) {
            NioIoEvent event = (NioIoEvent)o;
            return event.ops().equals(this.ops());
         } else {
            return false;
         }
      }

      @Override
      public int hashCode() {
         return this.ops().hashCode();
      }
   }
}
