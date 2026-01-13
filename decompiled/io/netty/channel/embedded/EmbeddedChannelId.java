package io.netty.channel.embedded;

import io.netty.channel.ChannelId;

final class EmbeddedChannelId implements ChannelId {
   private static final long serialVersionUID = -251711922203466130L;
   static final ChannelId INSTANCE = new EmbeddedChannelId();

   private EmbeddedChannelId() {
   }

   @Override
   public String asShortText() {
      return this.toString();
   }

   @Override
   public String asLongText() {
      return this.toString();
   }

   public int compareTo(ChannelId o) {
      return o instanceof EmbeddedChannelId ? 0 : this.asLongText().compareTo(o.asLongText());
   }

   @Override
   public int hashCode() {
      return 0;
   }

   @Override
   public boolean equals(Object obj) {
      return obj instanceof EmbeddedChannelId;
   }

   @Override
   public String toString() {
      return "embedded";
   }
}
