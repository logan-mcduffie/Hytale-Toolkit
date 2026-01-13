package io.netty.channel.kqueue;

import io.netty.channel.Channel;
import io.netty.channel.ChannelMetadata;
import io.netty.channel.ChannelOutboundBuffer;
import java.io.IOException;

abstract class AbstractKQueueDatagramChannel extends AbstractKQueueChannel {
   private static final ChannelMetadata METADATA = new ChannelMetadata(true, 16);

   AbstractKQueueDatagramChannel(Channel parent, BsdSocket fd, boolean active) {
      super(parent, fd, active);
   }

   @Override
   public ChannelMetadata metadata() {
      return METADATA;
   }

   protected abstract boolean doWriteMessage(Object var1) throws Exception;

   @Override
   protected void doWrite(ChannelOutboundBuffer in) throws Exception {
      int maxMessagesPerWrite = this.maxMessagesPerWrite();

      while (maxMessagesPerWrite > 0) {
         Object msg = in.current();
         if (msg == null) {
            break;
         }

         try {
            boolean done = false;

            for (int i = this.config().getWriteSpinCount(); i > 0; i--) {
               if (this.doWriteMessage(msg)) {
                  done = true;
                  break;
               }
            }

            if (!done) {
               break;
            }

            in.remove();
            maxMessagesPerWrite--;
         } catch (IOException var6) {
            maxMessagesPerWrite--;
            in.remove(var6);
         }
      }

      this.writeFilter(!in.isEmpty());
   }
}
