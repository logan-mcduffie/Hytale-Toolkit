package io.netty.handler.codec.quic;

import java.nio.ByteBuffer;
import java.security.SecureRandom;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

final class ConnectionIdChannelMap {
   private static final SecureRandom random = new SecureRandom();
   private final Map<ConnectionIdChannelMap.ConnectionIdKey, QuicheQuicChannel> channelMap = new HashMap<>();
   private final SipHash sipHash;

   ConnectionIdChannelMap() {
      byte[] seed = new byte[16];
      random.nextBytes(seed);
      this.sipHash = new SipHash(1, 3, seed);
   }

   private ConnectionIdChannelMap.ConnectionIdKey key(ByteBuffer cid) {
      long hash = this.sipHash.macHash(cid);
      return new ConnectionIdChannelMap.ConnectionIdKey(hash, cid);
   }

   @Nullable
   QuicheQuicChannel put(ByteBuffer cid, QuicheQuicChannel channel) {
      return this.channelMap.put(this.key(cid), channel);
   }

   @Nullable
   QuicheQuicChannel remove(ByteBuffer cid) {
      return this.channelMap.remove(this.key(cid));
   }

   @Nullable
   QuicheQuicChannel get(ByteBuffer cid) {
      return this.channelMap.get(this.key(cid));
   }

   void clear() {
      this.channelMap.clear();
   }

   private static final class ConnectionIdKey implements Comparable<ConnectionIdChannelMap.ConnectionIdKey> {
      private final long hash;
      private final ByteBuffer key;

      ConnectionIdKey(long hash, ByteBuffer key) {
         this.hash = hash;
         this.key = key;
      }

      @Override
      public boolean equals(Object o) {
         if (this == o) {
            return true;
         } else if (o != null && this.getClass() == o.getClass()) {
            ConnectionIdChannelMap.ConnectionIdKey that = (ConnectionIdChannelMap.ConnectionIdKey)o;
            return this.hash == that.hash && Objects.equals(this.key, that.key);
         } else {
            return false;
         }
      }

      @Override
      public int hashCode() {
         return (int)this.hash;
      }

      public int compareTo(@NotNull ConnectionIdChannelMap.ConnectionIdKey o) {
         int result = Long.compare(this.hash, o.hash);
         return result != 0 ? result : this.key.compareTo(o.key);
      }
   }
}
