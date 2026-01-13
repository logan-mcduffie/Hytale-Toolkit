package io.netty.handler.timeout;

import io.netty.util.internal.ObjectUtil;
import io.netty.util.internal.StringUtil;

public class IdleStateEvent {
   public static final IdleStateEvent FIRST_READER_IDLE_STATE_EVENT = new IdleStateEvent.DefaultIdleStateEvent(IdleState.READER_IDLE, true);
   public static final IdleStateEvent READER_IDLE_STATE_EVENT = new IdleStateEvent.DefaultIdleStateEvent(IdleState.READER_IDLE, false);
   public static final IdleStateEvent FIRST_WRITER_IDLE_STATE_EVENT = new IdleStateEvent.DefaultIdleStateEvent(IdleState.WRITER_IDLE, true);
   public static final IdleStateEvent WRITER_IDLE_STATE_EVENT = new IdleStateEvent.DefaultIdleStateEvent(IdleState.WRITER_IDLE, false);
   public static final IdleStateEvent FIRST_ALL_IDLE_STATE_EVENT = new IdleStateEvent.DefaultIdleStateEvent(IdleState.ALL_IDLE, true);
   public static final IdleStateEvent ALL_IDLE_STATE_EVENT = new IdleStateEvent.DefaultIdleStateEvent(IdleState.ALL_IDLE, false);
   private final IdleState state;
   private final boolean first;

   protected IdleStateEvent(IdleState state, boolean first) {
      this.state = ObjectUtil.checkNotNull(state, "state");
      this.first = first;
   }

   public IdleState state() {
      return this.state;
   }

   public boolean isFirst() {
      return this.first;
   }

   @Override
   public String toString() {
      return StringUtil.simpleClassName(this) + '(' + this.state + (this.first ? ", first" : "") + ')';
   }

   private static final class DefaultIdleStateEvent extends IdleStateEvent {
      private final String representation;

      DefaultIdleStateEvent(IdleState state, boolean first) {
         super(state, first);
         this.representation = "IdleStateEvent(" + state + (first ? ", first" : "") + ')';
      }

      @Override
      public String toString() {
         return this.representation;
      }
   }
}
