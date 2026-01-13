package io.netty.channel;

import io.netty.util.internal.ObjectUtil;

abstract class PendingBytesTracker implements MessageSizeEstimator.Handle {
   private final MessageSizeEstimator.Handle estimatorHandle;

   private PendingBytesTracker(MessageSizeEstimator.Handle estimatorHandle) {
      this.estimatorHandle = ObjectUtil.checkNotNull(estimatorHandle, "estimatorHandle");
   }

   @Override
   public final int size(Object msg) {
      return this.estimatorHandle.size(msg);
   }

   public abstract void incrementPendingOutboundBytes(long var1);

   public abstract void decrementPendingOutboundBytes(long var1);

   static PendingBytesTracker newTracker(Channel channel) {
      if (channel.pipeline() instanceof DefaultChannelPipeline) {
         return new PendingBytesTracker.DefaultChannelPipelinePendingBytesTracker((DefaultChannelPipeline)channel.pipeline());
      } else {
         ChannelOutboundBuffer buffer = channel.unsafe().outboundBuffer();
         MessageSizeEstimator.Handle handle = channel.config().getMessageSizeEstimator().newHandle();
         return (PendingBytesTracker)(buffer == null
            ? new PendingBytesTracker.NoopPendingBytesTracker(handle)
            : new PendingBytesTracker.ChannelOutboundBufferPendingBytesTracker(buffer, handle));
      }
   }

   private static final class ChannelOutboundBufferPendingBytesTracker extends PendingBytesTracker {
      private final ChannelOutboundBuffer buffer;

      ChannelOutboundBufferPendingBytesTracker(ChannelOutboundBuffer buffer, MessageSizeEstimator.Handle estimatorHandle) {
         super(estimatorHandle);
         this.buffer = buffer;
      }

      @Override
      public void incrementPendingOutboundBytes(long bytes) {
         this.buffer.incrementPendingOutboundBytes(bytes);
      }

      @Override
      public void decrementPendingOutboundBytes(long bytes) {
         this.buffer.decrementPendingOutboundBytes(bytes);
      }
   }

   private static final class DefaultChannelPipelinePendingBytesTracker extends PendingBytesTracker {
      private final DefaultChannelPipeline pipeline;

      DefaultChannelPipelinePendingBytesTracker(DefaultChannelPipeline pipeline) {
         super(pipeline.estimatorHandle());
         this.pipeline = pipeline;
      }

      @Override
      public void incrementPendingOutboundBytes(long bytes) {
         this.pipeline.incrementPendingOutboundBytes(bytes);
      }

      @Override
      public void decrementPendingOutboundBytes(long bytes) {
         this.pipeline.decrementPendingOutboundBytes(bytes);
      }
   }

   private static final class NoopPendingBytesTracker extends PendingBytesTracker {
      NoopPendingBytesTracker(MessageSizeEstimator.Handle estimatorHandle) {
         super(estimatorHandle);
      }

      @Override
      public void incrementPendingOutboundBytes(long bytes) {
      }

      @Override
      public void decrementPendingOutboundBytes(long bytes) {
      }
   }
}
