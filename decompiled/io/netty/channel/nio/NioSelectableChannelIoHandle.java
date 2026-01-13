package io.netty.channel.nio;

import io.netty.channel.IoEvent;
import io.netty.channel.IoHandle;
import io.netty.channel.IoRegistration;
import io.netty.util.internal.ObjectUtil;
import java.nio.channels.SelectableChannel;
import java.nio.channels.SelectionKey;

public abstract class NioSelectableChannelIoHandle<S extends SelectableChannel> implements IoHandle, NioIoHandle {
   private final S channel;

   public NioSelectableChannelIoHandle(S channel) {
      this.channel = ObjectUtil.checkNotNull(channel, "channel");
   }

   @Override
   public void handle(IoRegistration registration, IoEvent ioEvent) {
      SelectionKey key = registration.attachment();
      this.handle(this.channel, key);
   }

   @Override
   public void close() throws Exception {
      this.channel.close();
   }

   @Override
   public SelectableChannel selectableChannel() {
      return this.channel;
   }

   protected abstract void handle(S var1, SelectionKey var2);

   protected void deregister(S channel) {
   }
}
