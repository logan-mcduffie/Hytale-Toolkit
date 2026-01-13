package io.netty.channel.epoll;

import io.netty.channel.IoOps;

public final class EpollIoOps implements IoOps {
   public static final EpollIoOps EPOLLOUT = new EpollIoOps(Native.EPOLLOUT);
   public static final EpollIoOps EPOLLIN = new EpollIoOps(Native.EPOLLIN);
   public static final EpollIoOps EPOLLERR = new EpollIoOps(Native.EPOLLERR);
   public static final EpollIoOps EPOLLRDHUP = new EpollIoOps(Native.EPOLLRDHUP);
   public static final EpollIoOps EPOLLET = new EpollIoOps(Native.EPOLLET);
   static final int EPOLL_ERR_OUT_MASK;
   static final int EPOLL_ERR_IN_MASK;
   static final int EPOLL_RDHUP_MASK;
   private static final EpollIoEvent[] EVENTS;
   final int value;

   private static void addToArray(EpollIoEvent[] array, EpollIoOps ops) {
      array[ops.value] = new EpollIoOps.DefaultEpollIoEvent(ops);
   }

   private EpollIoOps(int value) {
      this.value = value;
   }

   public boolean contains(EpollIoOps ops) {
      return (this.value & ops.value) != 0;
   }

   boolean contains(int value) {
      return (this.value & value) != 0;
   }

   public EpollIoOps with(EpollIoOps ops) {
      return this.contains(ops) ? this : valueOf(this.value | ops.value());
   }

   public EpollIoOps without(EpollIoOps ops) {
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
         EpollIoOps nioOps = (EpollIoOps)o;
         return this.value == nioOps.value;
      } else {
         return false;
      }
   }

   @Override
   public int hashCode() {
      return this.value;
   }

   public static EpollIoOps valueOf(int value) {
      return eventOf(value).ops();
   }

   @Override
   public String toString() {
      return "EpollIoOps{value=" + this.value + '}';
   }

   static EpollIoEvent eventOf(int value) {
      if (value > 0 && value < EVENTS.length) {
         EpollIoEvent event = EVENTS[value];
         if (event != null) {
            return event;
         }
      }

      return new EpollIoOps.DefaultEpollIoEvent(new EpollIoOps(value));
   }

   static {
      Epoll.ensureAvailability();
      EPOLL_ERR_OUT_MASK = EPOLLERR.value | EPOLLOUT.value;
      EPOLL_ERR_IN_MASK = EPOLLERR.value | EPOLLIN.value;
      EPOLL_RDHUP_MASK = EPOLLRDHUP.value;
      EpollIoOps all = new EpollIoOps(EPOLLOUT.value | EPOLLIN.value | EPOLLERR.value | EPOLLRDHUP.value);
      EVENTS = new EpollIoEvent[all.value + 1];
      addToArray(EVENTS, EPOLLOUT);
      addToArray(EVENTS, EPOLLIN);
      addToArray(EVENTS, EPOLLERR);
      addToArray(EVENTS, EPOLLRDHUP);
      addToArray(EVENTS, all);
   }

   private static final class DefaultEpollIoEvent implements EpollIoEvent {
      private final EpollIoOps ops;

      DefaultEpollIoEvent(EpollIoOps ops) {
         this.ops = ops;
      }

      @Override
      public EpollIoOps ops() {
         return this.ops;
      }

      @Override
      public boolean equals(Object o) {
         if (this == o) {
            return true;
         } else if (o != null && this.getClass() == o.getClass()) {
            EpollIoEvent event = (EpollIoEvent)o;
            return event.ops().equals(this.ops());
         } else {
            return false;
         }
      }

      @Override
      public int hashCode() {
         return this.ops().hashCode();
      }

      @Override
      public String toString() {
         return "DefaultEpollIoEvent{ops=" + this.ops + '}';
      }
   }
}
