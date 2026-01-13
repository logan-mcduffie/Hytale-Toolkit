package io.netty.channel.oio;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.channel.Channel;
import io.netty.channel.ChannelConfig;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelMetadata;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelOutboundBuffer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.FileRegion;
import io.netty.channel.RecvByteBufAllocator;
import io.netty.channel.socket.ChannelInputShutdownEvent;
import io.netty.channel.socket.ChannelInputShutdownReadComplete;
import io.netty.util.LeakPresenceDetector;
import io.netty.util.internal.StringUtil;
import java.io.IOException;

/** @deprecated */
public abstract class AbstractOioByteChannel extends AbstractOioChannel {
   private static final ChannelMetadata METADATA = new ChannelMetadata(false);
   private static final String EXPECTED_TYPES = " (expected: "
      + StringUtil.simpleClassName(ByteBuf.class)
      + ", "
      + StringUtil.simpleClassName(FileRegion.class)
      + ')';

   protected AbstractOioByteChannel(Channel parent) {
      super(parent);
   }

   @Override
   public ChannelMetadata metadata() {
      return METADATA;
   }

   protected abstract boolean isInputShutdown();

   protected abstract ChannelFuture shutdownInput();

   private void closeOnRead(ChannelPipeline pipeline) {
      if (this.isOpen()) {
         if (Boolean.TRUE.equals(this.config().getOption(ChannelOption.ALLOW_HALF_CLOSURE))) {
            this.shutdownInput();
            pipeline.fireUserEventTriggered(ChannelInputShutdownEvent.INSTANCE);
         } else {
            this.unsafe().close(this.unsafe().voidPromise());
         }

         pipeline.fireUserEventTriggered(ChannelInputShutdownReadComplete.INSTANCE);
      }
   }

   private void handleReadException(ChannelPipeline pipeline, ByteBuf byteBuf, Throwable cause, boolean close, RecvByteBufAllocator.Handle allocHandle) {
      if (byteBuf != null) {
         if (byteBuf.isReadable()) {
            this.readPending = false;
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
   protected void doRead() {
      ChannelConfig config = this.config();
      if (!this.isInputShutdown() && this.readPending) {
         this.readPending = false;
         ChannelPipeline pipeline = this.pipeline();
         ByteBufAllocator allocator = config.getAllocator();
         RecvByteBufAllocator.Handle allocHandle = this.unsafe().recvBufAllocHandle();
         allocHandle.reset(config);
         ByteBuf byteBuf = null;
         boolean close = false;
         boolean readData = false;

         try {
            byteBuf = allocHandle.allocate(allocator);

            do {
               allocHandle.lastBytesRead(this.doReadBytes(byteBuf));
               if (allocHandle.lastBytesRead() <= 0) {
                  if (!byteBuf.isReadable()) {
                     byteBuf.release();
                     byteBuf = null;
                     close = allocHandle.lastBytesRead() < 0;
                     if (close) {
                        this.readPending = false;
                     }
                  }
                  break;
               }

               readData = true;
               int available = this.available();
               if (available <= 0) {
                  break;
               }

               if (!byteBuf.isWritable()) {
                  int capacity = byteBuf.capacity();
                  int maxCapacity = byteBuf.maxCapacity();
                  if (capacity == maxCapacity) {
                     allocHandle.incMessagesRead(1);
                     this.readPending = false;
                     pipeline.fireChannelRead(byteBuf);
                     byteBuf = allocHandle.allocate(allocator);
                  } else {
                     int writerIndex = byteBuf.writerIndex();
                     if (writerIndex + available > maxCapacity) {
                        byteBuf.capacity(maxCapacity);
                     } else {
                        byteBuf.ensureWritable(available);
                     }
                  }
               }
            } while (allocHandle.continueReading());

            if (byteBuf != null) {
               if (byteBuf.isReadable()) {
                  this.readPending = false;
                  pipeline.fireChannelRead(byteBuf);
               } else {
                  byteBuf.release();
               }

               byteBuf = null;
            }

            if (readData) {
               allocHandle.readComplete();
               pipeline.fireChannelReadComplete();
            }

            if (close) {
               this.closeOnRead(pipeline);
            }
         } catch (Throwable var15) {
            this.handleReadException(pipeline, byteBuf, var15, close, allocHandle);
         } finally {
            if (this.readPending || config.isAutoRead() || !readData && this.isActive()) {
               this.read();
            }
         }
      }
   }

   @Override
   protected void doWrite(ChannelOutboundBuffer in) throws Exception {
      while (true) {
         Object msg = in.current();
         if (msg == null) {
            return;
         }

         if (!(msg instanceof ByteBuf)) {
            if (msg instanceof FileRegion) {
               FileRegion region = (FileRegion)msg;
               long transferred = region.transferred();
               this.doWriteFileRegion(region);
               in.progress(region.transferred() - transferred);
               in.remove();
            } else {
               in.remove(new UnsupportedOperationException("unsupported message type: " + StringUtil.simpleClassName(msg)));
            }
         } else {
            ByteBuf buf = (ByteBuf)msg;
            int readableBytes = buf.readableBytes();

            while (readableBytes > 0) {
               this.doWriteBytes(buf);
               int newReadableBytes = buf.readableBytes();
               in.progress(readableBytes - newReadableBytes);
               readableBytes = newReadableBytes;
            }

            in.remove();
         }
      }
   }

   @Override
   protected final Object filterOutboundMessage(Object msg) throws Exception {
      if (!(msg instanceof ByteBuf) && !(msg instanceof FileRegion)) {
         throw new UnsupportedOperationException("unsupported message type: " + StringUtil.simpleClassName(msg) + EXPECTED_TYPES);
      } else {
         return msg;
      }
   }

   protected abstract int available();

   protected abstract int doReadBytes(ByteBuf var1) throws Exception;

   protected abstract void doWriteBytes(ByteBuf var1) throws Exception;

   protected abstract void doWriteFileRegion(FileRegion var1) throws Exception;
}
