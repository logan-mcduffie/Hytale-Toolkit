package io.netty.channel.socket;

public final class ChannelOutputShutdownEvent {
   public static final ChannelOutputShutdownEvent INSTANCE = new ChannelOutputShutdownEvent();

   private ChannelOutputShutdownEvent() {
   }
}
