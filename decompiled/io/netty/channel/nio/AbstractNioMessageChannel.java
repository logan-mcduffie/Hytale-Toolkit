package io.netty.channel.nio;

import io.netty.channel.Channel;
import io.netty.channel.ChannelConfig;
import io.netty.channel.ChannelOutboundBuffer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.RecvByteBufAllocator;
import io.netty.channel.ServerChannel;
import java.io.IOException;
import java.net.PortUnreachableException;
import java.nio.channels.SelectableChannel;
import java.util.ArrayList;
import java.util.List;

public abstract class AbstractNioMessageChannel extends AbstractNioChannel {
   boolean inputShutdown;

   protected AbstractNioMessageChannel(Channel parent, SelectableChannel ch, int readInterestOp) {
      super(parent, ch, readInterestOp);
   }

   protected AbstractNioMessageChannel(Channel parent, SelectableChannel ch, NioIoOps readOps) {
      super(parent, ch, readOps);
   }

   protected AbstractNioChannel.AbstractNioUnsafe newUnsafe() {
      return new AbstractNioMessageChannel.NioMessageUnsafe();
   }

   @Override
   protected void doBeginRead() throws Exception {
      if (!this.inputShutdown) {
         super.doBeginRead();
      }
   }

   protected boolean continueReading(RecvByteBufAllocator.Handle allocHandle) {
      return allocHandle.continueReading();
   }

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

            for (int i = this.config().getWriteSpinCount() - 1; i >= 0; i--) {
               if (this.doWriteMessage(msg, in)) {
                  done = true;
                  break;
               }
            }

            if (!done) {
               break;
            }

            maxMessagesPerWrite--;
            in.remove();
         } catch (Exception var6) {
            if (!this.continueOnWriteError()) {
               throw var6;
            }

            maxMessagesPerWrite--;
            in.remove(var6);
         }
      }

      if (in.isEmpty()) {
         this.removeAndSubmit(NioIoOps.WRITE);
      } else {
         this.addAndSubmit(NioIoOps.WRITE);
      }
   }

   protected boolean continueOnWriteError() {
      return false;
   }

   protected boolean closeOnReadError(Throwable cause) {
      if (!this.isActive()) {
         return true;
      } else if (cause instanceof PortUnreachableException) {
         return false;
      } else {
         return cause instanceof IOException ? !(this instanceof ServerChannel) : true;
      }
   }

   protected abstract int doReadMessages(List<Object> var1) throws Exception;

   protected abstract boolean doWriteMessage(Object var1, ChannelOutboundBuffer var2) throws Exception;

   private final class NioMessageUnsafe extends AbstractNioChannel.AbstractNioUnsafe {
      private final List<Object> readBuf = new ArrayList<>();

      private NioMessageUnsafe() {
      }

      @Override
      public void read() {
         assert AbstractNioMessageChannel.this.eventLoop().inEventLoop();

         ChannelConfig config = AbstractNioMessageChannel.this.config();
         ChannelPipeline pipeline = AbstractNioMessageChannel.this.pipeline();
         RecvByteBufAllocator.Handle allocHandle = AbstractNioMessageChannel.this.unsafe().recvBufAllocHandle();
         allocHandle.reset(config);
         boolean closed = false;
         Throwable exception = null;

         try {
            try {
               do {
                  int localRead = AbstractNioMessageChannel.this.doReadMessages(this.readBuf);
                  if (localRead == 0) {
                     break;
                  }

                  if (localRead < 0) {
                     closed = true;
                     break;
                  }

                  allocHandle.incMessagesRead(localRead);
               } while (AbstractNioMessageChannel.this.continueReading(allocHandle));
            } catch (Throwable var11) {
               exception = var11;
            }

            int size = this.readBuf.size();

            for (int i = 0; i < size; i++) {
               AbstractNioMessageChannel.this.readPending = false;
               pipeline.fireChannelRead(this.readBuf.get(i));
            }

            this.readBuf.clear();
            allocHandle.readComplete();
            pipeline.fireChannelReadComplete();
            if (exception != null) {
               closed = AbstractNioMessageChannel.this.closeOnReadError(exception);
               pipeline.fireExceptionCaught(exception);
            }

            if (closed) {
               AbstractNioMessageChannel.this.inputShutdown = true;
               if (AbstractNioMessageChannel.this.isOpen()) {
                  this.close(this.voidPromise());
               }
            }
         } finally {
            if (!AbstractNioMessageChannel.this.readPending && !config.isAutoRead()) {
               this.removeReadOp();
            }
         }
      }
   }
}
