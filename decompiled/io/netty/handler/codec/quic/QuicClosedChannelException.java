package io.netty.handler.codec.quic;

import java.nio.channels.ClosedChannelException;
import org.jetbrains.annotations.Nullable;

public final class QuicClosedChannelException extends ClosedChannelException {
   private final QuicConnectionCloseEvent event;

   QuicClosedChannelException(@Nullable QuicConnectionCloseEvent event) {
      this.event = event;
   }

   @Nullable
   public QuicConnectionCloseEvent event() {
      return this.event;
   }
}
