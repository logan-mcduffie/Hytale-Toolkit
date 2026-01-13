package io.netty.channel;

import io.netty.util.internal.ObjectUtil;

public final class ChannelMetadata {
   private final boolean hasDisconnect;
   private final int defaultMaxMessagesPerRead;

   public ChannelMetadata(boolean hasDisconnect) {
      this(hasDisconnect, 16);
   }

   public ChannelMetadata(boolean hasDisconnect, int defaultMaxMessagesPerRead) {
      ObjectUtil.checkPositive(defaultMaxMessagesPerRead, "defaultMaxMessagesPerRead");
      this.hasDisconnect = hasDisconnect;
      this.defaultMaxMessagesPerRead = defaultMaxMessagesPerRead;
   }

   public boolean hasDisconnect() {
      return this.hasDisconnect;
   }

   public int defaultMaxMessagesPerRead() {
      return this.defaultMaxMessagesPerRead;
   }
}
