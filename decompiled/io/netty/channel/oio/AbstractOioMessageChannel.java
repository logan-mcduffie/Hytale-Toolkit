package io.netty.channel.oio;

import io.netty.channel.Channel;
import io.netty.channel.ChannelConfig;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.RecvByteBufAllocator;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Deprecated
public abstract class AbstractOioMessageChannel extends AbstractOioChannel {
   private final List<Object> readBuf = new ArrayList<>();

   protected AbstractOioMessageChannel(Channel parent) {
      super(parent);
   }

   @Override
   protected void doRead() {
      if (this.readPending) {
         this.readPending = false;
         ChannelConfig config = this.config();
         ChannelPipeline pipeline = this.pipeline();
         RecvByteBufAllocator.Handle allocHandle = this.unsafe().recvBufAllocHandle();
         allocHandle.reset(config);
         boolean closed = false;
         Throwable exception = null;

         try {
            do {
               int localRead = this.doReadMessages(this.readBuf);
               if (localRead == 0) {
                  break;
               }

               if (localRead < 0) {
                  closed = true;
                  break;
               }

               allocHandle.incMessagesRead(localRead);
            } while (allocHandle.continueReading());
         } catch (Throwable var9) {
            exception = var9;
         }

         boolean readData = false;
         int size = this.readBuf.size();
         if (size > 0) {
            readData = true;

            for (int i = 0; i < size; i++) {
               this.readPending = false;
               pipeline.fireChannelRead(this.readBuf.get(i));
            }

            this.readBuf.clear();
            allocHandle.readComplete();
            pipeline.fireChannelReadComplete();
         }

         if (exception != null) {
            if (exception instanceof IOException) {
               closed = true;
            }

            pipeline.fireExceptionCaught(exception);
         }

         if (closed) {
            if (this.isOpen()) {
               this.unsafe().close(this.unsafe().voidPromise());
            }
         } else if (this.readPending || config.isAutoRead() || !readData && this.isActive()) {
            this.read();
         }
      }
   }

   protected abstract int doReadMessages(List<Object> var1) throws Exception;
}
