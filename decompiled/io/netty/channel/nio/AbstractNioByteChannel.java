package io.netty.channel.nio;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.channel.Channel;
import io.netty.channel.ChannelConfig;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelMetadata;
import io.netty.channel.ChannelOutboundBuffer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.FileRegion;
import io.netty.channel.IoRegistration;
import io.netty.channel.RecvByteBufAllocator;
import io.netty.channel.socket.ChannelInputShutdownEvent;
import io.netty.channel.socket.ChannelInputShutdownReadComplete;
import io.netty.channel.socket.SocketChannelConfig;
import io.netty.util.LeakPresenceDetector;
import io.netty.util.internal.StringUtil;
import java.io.IOException;
import java.nio.channels.SelectableChannel;

public abstract class AbstractNioByteChannel extends AbstractNioChannel {
   private static final ChannelMetadata METADATA = new ChannelMetadata(false, 16);
   private static final String EXPECTED_TYPES = " (expected: "
      + StringUtil.simpleClassName(ByteBuf.class)
      + ", "
      + StringUtil.simpleClassName(FileRegion.class)
      + ')';
   private final Runnable flushTask = new Runnable() {
      @Override
      public void run() {
         ((AbstractNioChannel.AbstractNioUnsafe)AbstractNioByteChannel.this.unsafe()).flush0();
      }
   };
   private boolean inputClosedSeenErrorOnRead;

   protected AbstractNioByteChannel(Channel parent, SelectableChannel ch) {
      super(parent, ch, 1);
   }

   protected abstract ChannelFuture shutdownInput();

   protected boolean isInputShutdown0() {
      return false;
   }

   protected AbstractNioChannel.AbstractNioUnsafe newUnsafe() {
      return new AbstractNioByteChannel.NioByteUnsafe();
   }

   @Override
   public ChannelMetadata metadata() {
      return METADATA;
   }

   final boolean shouldBreakReadReady(ChannelConfig config) {
      return this.isInputShutdown0() && (this.inputClosedSeenErrorOnRead || !isAllowHalfClosure(config));
   }

   private static boolean isAllowHalfClosure(ChannelConfig config) {
      return config instanceof SocketChannelConfig && ((SocketChannelConfig)config).isAllowHalfClosure();
   }

   protected final int doWrite0(ChannelOutboundBuffer in) throws Exception {
      Object msg = in.current();
      return msg == null ? 0 : this.doWriteInternal(in, in.current());
   }

   private int doWriteInternal(ChannelOutboundBuffer in, Object msg) throws Exception {
      if (msg instanceof ByteBuf) {
         ByteBuf buf = (ByteBuf)msg;
         if (!buf.isReadable()) {
            in.remove();
            return 0;
         }

         int localFlushedAmount = this.doWriteBytes(buf);
         if (localFlushedAmount > 0) {
            in.progress(localFlushedAmount);
            if (!buf.isReadable()) {
               in.remove();
            }

            return 1;
         }
      } else {
         if (!(msg instanceof FileRegion)) {
            throw new Error("Unexpected message type: " + StringUtil.className(msg));
         }

         FileRegion region = (FileRegion)msg;
         if (region.transferred() >= region.count()) {
            in.remove();
            return 0;
         }

         long localFlushedAmount = this.doWriteFileRegion(region);
         if (localFlushedAmount > 0L) {
            in.progress(localFlushedAmount);
            if (region.transferred() >= region.count()) {
               in.remove();
            }

            return 1;
         }
      }

      return Integer.MAX_VALUE;
   }

   @Override
   protected void doWrite(ChannelOutboundBuffer in) throws Exception {
      int writeSpinCount = this.config().getWriteSpinCount();

      do {
         Object msg = in.current();
         if (msg == null) {
            this.clearOpWrite();
            return;
         }

         writeSpinCount -= this.doWriteInternal(in, msg);
      } while (writeSpinCount > 0);

      this.incompleteWrite(writeSpinCount < 0);
   }

   @Override
   protected final Object filterOutboundMessage(Object msg) {
      if (msg instanceof ByteBuf) {
         ByteBuf buf = (ByteBuf)msg;
         return buf.isDirect() ? msg : this.newDirectBuffer(buf);
      } else if (msg instanceof FileRegion) {
         return msg;
      } else {
         throw new UnsupportedOperationException("unsupported message type: " + StringUtil.simpleClassName(msg) + EXPECTED_TYPES);
      }
   }

   protected final void incompleteWrite(boolean setOpWrite) {
      if (setOpWrite) {
         this.setOpWrite();
      } else {
         this.clearOpWrite();
         this.eventLoop().execute(this.flushTask);
      }
   }

   protected abstract long doWriteFileRegion(FileRegion var1) throws Exception;

   protected abstract int doReadBytes(ByteBuf var1) throws Exception;

   protected abstract int doWriteBytes(ByteBuf var1) throws Exception;

   protected final void setOpWrite() {
      IoRegistration registration = this.registration();
      if (registration.isValid()) {
         this.addAndSubmit(NioIoOps.WRITE);
      }
   }

   protected final void clearOpWrite() {
      IoRegistration registration = this.registration();
      if (registration.isValid()) {
         this.removeAndSubmit(NioIoOps.WRITE);
      }
   }

   protected class NioByteUnsafe extends AbstractNioChannel.AbstractNioUnsafe {
      private void closeOnRead(ChannelPipeline pipeline) {
         if (!AbstractNioByteChannel.this.isInputShutdown0()) {
            if (AbstractNioByteChannel.isAllowHalfClosure(AbstractNioByteChannel.this.config())) {
               AbstractNioByteChannel.this.shutdownInput();
               pipeline.fireUserEventTriggered(ChannelInputShutdownEvent.INSTANCE);
            } else {
               this.close(this.voidPromise());
            }
         } else if (!AbstractNioByteChannel.this.inputClosedSeenErrorOnRead) {
            AbstractNioByteChannel.this.inputClosedSeenErrorOnRead = true;
            pipeline.fireUserEventTriggered(ChannelInputShutdownReadComplete.INSTANCE);
         }
      }

      private void handleReadException(ChannelPipeline pipeline, ByteBuf byteBuf, Throwable cause, boolean close, RecvByteBufAllocator.Handle allocHandle) {
         if (byteBuf != null) {
            if (byteBuf.isReadable()) {
               AbstractNioByteChannel.this.readPending = false;
               pipeline.fireChannelRead(byteBuf);
            } else {
               byteBuf.release();
            }
         }

         allocHandle.readComplete();
         pipeline.fireChannelReadComplete();
         pipeline.fireExceptionCaught(cause);
         if (close || cause instanceof OutOfMemoryError || cause instanceof LeakPresenceDetector.AllocationProhibitedException || cause instanceof IOException) {
            this.closeOnRead(pipeline);
         }
      }

      @Override
      public final void read() {
         ChannelConfig config = AbstractNioByteChannel.this.config();
         if (AbstractNioByteChannel.this.shouldBreakReadReady(config)) {
            AbstractNioByteChannel.this.clearReadPending();
         } else {
            ChannelPipeline pipeline = AbstractNioByteChannel.this.pipeline();
            ByteBufAllocator allocator = config.getAllocator();
            RecvByteBufAllocator.Handle allocHandle = this.recvBufAllocHandle();
            allocHandle.reset(config);
            ByteBuf byteBuf = null;
            boolean close = false;

            try {
               do {
                  byteBuf = allocHandle.allocate(allocator);
                  allocHandle.lastBytesRead(AbstractNioByteChannel.this.doReadBytes(byteBuf));
                  if (allocHandle.lastBytesRead() <= 0) {
                     byteBuf.release();
                     byteBuf = null;
                     close = allocHandle.lastBytesRead() < 0;
                     if (close) {
                        AbstractNioByteChannel.this.readPending = false;
                     }
                     break;
                  }

                  allocHandle.incMessagesRead(1);
                  AbstractNioByteChannel.this.readPending = false;
                  pipeline.fireChannelRead(byteBuf);
                  byteBuf = null;
               } while (allocHandle.continueReading());

               allocHandle.readComplete();
               pipeline.fireChannelReadComplete();
               if (close) {
                  this.closeOnRead(pipeline);
               }
            } catch (Throwable var11) {
               this.handleReadException(pipeline, byteBuf, var11, close, allocHandle);
            } finally {
               if (!AbstractNioByteChannel.this.readPending && !config.isAutoRead()) {
                  this.removeReadOp();
               }
            }
         }
      }
   }
}
