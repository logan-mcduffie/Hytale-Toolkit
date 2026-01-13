package io.netty.channel;

import io.netty.util.concurrent.PromiseAggregator;

@Deprecated
public final class ChannelPromiseAggregator extends PromiseAggregator<Void, ChannelFuture> implements ChannelFutureListener {
   public ChannelPromiseAggregator(ChannelPromise aggregatePromise) {
      super(aggregatePromise);
   }
}
