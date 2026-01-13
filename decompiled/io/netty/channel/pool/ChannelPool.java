package io.netty.channel.pool;

import io.netty.channel.Channel;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.Promise;
import java.io.Closeable;

public interface ChannelPool extends Closeable {
   Future<Channel> acquire();

   Future<Channel> acquire(Promise<Channel> var1);

   Future<Void> release(Channel var1);

   Future<Void> release(Channel var1, Promise<Void> var2);

   @Override
   void close();
}
