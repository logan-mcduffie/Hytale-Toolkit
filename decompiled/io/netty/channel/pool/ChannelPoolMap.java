package io.netty.channel.pool;

public interface ChannelPoolMap<K, P extends ChannelPool> {
   P get(K var1);

   boolean contains(K var1);
}
