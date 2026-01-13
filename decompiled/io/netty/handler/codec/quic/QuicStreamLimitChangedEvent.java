package io.netty.handler.codec.quic;

public final class QuicStreamLimitChangedEvent implements QuicEvent {
   static final QuicStreamLimitChangedEvent INSTANCE = new QuicStreamLimitChangedEvent();

   private QuicStreamLimitChangedEvent() {
   }
}
