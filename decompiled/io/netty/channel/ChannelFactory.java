package io.netty.channel;

public interface ChannelFactory<T extends Channel> extends io.netty.bootstrap.ChannelFactory<T> {
   @Override
   T newChannel();
}
