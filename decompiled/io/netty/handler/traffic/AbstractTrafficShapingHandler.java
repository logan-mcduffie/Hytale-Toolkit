package io.netty.handler.traffic;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufHolder;
import io.netty.channel.Channel;
import io.netty.channel.ChannelConfig;
import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelOutboundBuffer;
import io.netty.channel.ChannelPromise;
import io.netty.channel.FileRegion;
import io.netty.util.Attribute;
import io.netty.util.AttributeKey;
import io.netty.util.internal.ObjectUtil;
import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;
import java.util.concurrent.TimeUnit;

public abstract class AbstractTrafficShapingHandler extends ChannelDuplexHandler {
   private static final InternalLogger logger = InternalLoggerFactory.getInstance(AbstractTrafficShapingHandler.class);
   public static final long DEFAULT_CHECK_INTERVAL = 1000L;
   public static final long DEFAULT_MAX_TIME = 15000L;
   static final long DEFAULT_MAX_SIZE = 4194304L;
   static final long MINIMAL_WAIT = 10L;
   protected TrafficCounter trafficCounter;
   private volatile long writeLimit;
   private volatile long readLimit;
   protected volatile long maxTime = 15000L;
   protected volatile long checkInterval = 1000L;
   static final AttributeKey<Boolean> READ_SUSPENDED = AttributeKey.valueOf(AbstractTrafficShapingHandler.class.getName() + ".READ_SUSPENDED");
   static final AttributeKey<Runnable> REOPEN_TASK = AttributeKey.valueOf(AbstractTrafficShapingHandler.class.getName() + ".REOPEN_TASK");
   volatile long maxWriteDelay = 4000L;
   volatile long maxWriteSize = 4194304L;
   final int userDefinedWritabilityIndex;
   static final int CHANNEL_DEFAULT_USER_DEFINED_WRITABILITY_INDEX = 1;
   static final int GLOBAL_DEFAULT_USER_DEFINED_WRITABILITY_INDEX = 2;
   static final int GLOBALCHANNEL_DEFAULT_USER_DEFINED_WRITABILITY_INDEX = 3;

   void setTrafficCounter(TrafficCounter newTrafficCounter) {
      this.trafficCounter = newTrafficCounter;
   }

   protected int userDefinedWritabilityIndex() {
      return 1;
   }

   protected AbstractTrafficShapingHandler(long writeLimit, long readLimit, long checkInterval, long maxTime) {
      this.maxTime = ObjectUtil.checkPositive(maxTime, "maxTime");
      this.userDefinedWritabilityIndex = this.userDefinedWritabilityIndex();
      this.writeLimit = writeLimit;
      this.readLimit = readLimit;
      this.checkInterval = checkInterval;
   }

   protected AbstractTrafficShapingHandler(long writeLimit, long readLimit, long checkInterval) {
      this(writeLimit, readLimit, checkInterval, 15000L);
   }

   protected AbstractTrafficShapingHandler(long writeLimit, long readLimit) {
      this(writeLimit, readLimit, 1000L, 15000L);
   }

   protected AbstractTrafficShapingHandler() {
      this(0L, 0L, 1000L, 15000L);
   }

   protected AbstractTrafficShapingHandler(long checkInterval) {
      this(0L, 0L, checkInterval, 15000L);
   }

   public void configure(long newWriteLimit, long newReadLimit, long newCheckInterval) {
      this.configure(newWriteLimit, newReadLimit);
      this.configure(newCheckInterval);
   }

   public void configure(long newWriteLimit, long newReadLimit) {
      this.writeLimit = newWriteLimit;
      this.readLimit = newReadLimit;
      if (this.trafficCounter != null) {
         this.trafficCounter.resetAccounting(TrafficCounter.milliSecondFromNano());
      }
   }

   public void configure(long newCheckInterval) {
      this.checkInterval = newCheckInterval;
      if (this.trafficCounter != null) {
         this.trafficCounter.configure(this.checkInterval);
      }
   }

   public long getWriteLimit() {
      return this.writeLimit;
   }

   public void setWriteLimit(long writeLimit) {
      this.writeLimit = writeLimit;
      if (this.trafficCounter != null) {
         this.trafficCounter.resetAccounting(TrafficCounter.milliSecondFromNano());
      }
   }

   public long getReadLimit() {
      return this.readLimit;
   }

   public void setReadLimit(long readLimit) {
      this.readLimit = readLimit;
      if (this.trafficCounter != null) {
         this.trafficCounter.resetAccounting(TrafficCounter.milliSecondFromNano());
      }
   }

   public long getCheckInterval() {
      return this.checkInterval;
   }

   public void setCheckInterval(long checkInterval) {
      this.checkInterval = checkInterval;
      if (this.trafficCounter != null) {
         this.trafficCounter.configure(checkInterval);
      }
   }

   public void setMaxTimeWait(long maxTime) {
      this.maxTime = ObjectUtil.checkPositive(maxTime, "maxTime");
   }

   public long getMaxTimeWait() {
      return this.maxTime;
   }

   public long getMaxWriteDelay() {
      return this.maxWriteDelay;
   }

   public void setMaxWriteDelay(long maxWriteDelay) {
      this.maxWriteDelay = ObjectUtil.checkPositive(maxWriteDelay, "maxWriteDelay");
   }

   public long getMaxWriteSize() {
      return this.maxWriteSize;
   }

   public void setMaxWriteSize(long maxWriteSize) {
      this.maxWriteSize = maxWriteSize;
   }

   protected void doAccounting(TrafficCounter counter) {
   }

   void releaseReadSuspended(ChannelHandlerContext ctx) {
      Channel channel = ctx.channel();
      channel.attr(READ_SUSPENDED).set(false);
      channel.config().setAutoRead(true);
   }

   @Override
   public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
      long size = this.calculateSize(msg);
      long now = TrafficCounter.milliSecondFromNano();
      if (size > 0L) {
         long wait = this.trafficCounter.readTimeToWait(size, this.readLimit, this.maxTime, now);
         wait = this.checkWaitReadTime(ctx, wait, now);
         if (wait >= 10L) {
            Channel channel = ctx.channel();
            ChannelConfig config = channel.config();
            if (logger.isDebugEnabled()) {
               logger.debug("Read suspend: " + wait + ':' + config.isAutoRead() + ':' + isHandlerActive(ctx));
            }

            if (config.isAutoRead() && isHandlerActive(ctx)) {
               config.setAutoRead(false);
               channel.attr(READ_SUSPENDED).set(true);
               Attribute<Runnable> attr = channel.attr(REOPEN_TASK);
               Runnable reopenTask = attr.get();
               if (reopenTask == null) {
                  reopenTask = new AbstractTrafficShapingHandler.ReopenReadTimerTask(ctx);
                  attr.set(reopenTask);
               }

               ctx.executor().schedule(reopenTask, wait, TimeUnit.MILLISECONDS);
               if (logger.isDebugEnabled()) {
                  logger.debug("Suspend final status => " + config.isAutoRead() + ':' + isHandlerActive(ctx) + " will reopened at: " + wait);
               }
            }
         }
      }

      this.informReadOperation(ctx, now);
      ctx.fireChannelRead(msg);
   }

   @Override
   public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
      Channel channel = ctx.channel();
      if (channel.hasAttr(REOPEN_TASK)) {
         channel.attr(REOPEN_TASK).set(null);
      }

      super.handlerRemoved(ctx);
   }

   long checkWaitReadTime(ChannelHandlerContext ctx, long wait, long now) {
      return wait;
   }

   void informReadOperation(ChannelHandlerContext ctx, long now) {
   }

   protected static boolean isHandlerActive(ChannelHandlerContext ctx) {
      Boolean suspended = ctx.channel().attr(READ_SUSPENDED).get();
      return suspended == null || Boolean.FALSE.equals(suspended);
   }

   @Override
   public void read(ChannelHandlerContext ctx) {
      if (isHandlerActive(ctx)) {
         ctx.read();
      }
   }

   @Override
   public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
      long size = this.calculateSize(msg);
      long now = TrafficCounter.milliSecondFromNano();
      if (size > 0L) {
         long wait = this.trafficCounter.writeTimeToWait(size, this.writeLimit, this.maxTime, now);
         if (wait >= 10L) {
            if (logger.isDebugEnabled()) {
               logger.debug("Write suspend: " + wait + ':' + ctx.channel().config().isAutoRead() + ':' + isHandlerActive(ctx));
            }

            this.submitWrite(ctx, msg, size, wait, now, promise);
            return;
         }
      }

      this.submitWrite(ctx, msg, size, 0L, now, promise);
   }

   @Deprecated
   protected void submitWrite(ChannelHandlerContext ctx, Object msg, long delay, ChannelPromise promise) {
      this.submitWrite(ctx, msg, this.calculateSize(msg), delay, TrafficCounter.milliSecondFromNano(), promise);
   }

   abstract void submitWrite(ChannelHandlerContext var1, Object var2, long var3, long var5, long var7, ChannelPromise var9);

   @Override
   public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
      this.setUserDefinedWritability(ctx, true);
      super.channelRegistered(ctx);
   }

   void setUserDefinedWritability(ChannelHandlerContext ctx, boolean writable) {
      ChannelOutboundBuffer cob = ctx.channel().unsafe().outboundBuffer();
      if (cob != null) {
         cob.setUserDefinedWritability(this.userDefinedWritabilityIndex, writable);
      }
   }

   void checkWriteSuspend(ChannelHandlerContext ctx, long delay, long queueSize) {
      if (queueSize > this.maxWriteSize || delay > this.maxWriteDelay) {
         this.setUserDefinedWritability(ctx, false);
      }
   }

   void releaseWriteSuspended(ChannelHandlerContext ctx) {
      this.setUserDefinedWritability(ctx, true);
   }

   public TrafficCounter trafficCounter() {
      return this.trafficCounter;
   }

   @Override
   public String toString() {
      StringBuilder builder = new StringBuilder(290)
         .append("TrafficShaping with Write Limit: ")
         .append(this.writeLimit)
         .append(" Read Limit: ")
         .append(this.readLimit)
         .append(" CheckInterval: ")
         .append(this.checkInterval)
         .append(" maxDelay: ")
         .append(this.maxWriteDelay)
         .append(" maxSize: ")
         .append(this.maxWriteSize)
         .append(" and Counter: ");
      if (this.trafficCounter != null) {
         builder.append(this.trafficCounter);
      } else {
         builder.append("none");
      }

      return builder.toString();
   }

   protected long calculateSize(Object msg) {
      if (msg instanceof ByteBuf) {
         return ((ByteBuf)msg).readableBytes();
      } else if (msg instanceof ByteBufHolder) {
         return ((ByteBufHolder)msg).content().readableBytes();
      } else {
         return msg instanceof FileRegion ? ((FileRegion)msg).count() : -1L;
      }
   }

   static final class ReopenReadTimerTask implements Runnable {
      final ChannelHandlerContext ctx;

      ReopenReadTimerTask(ChannelHandlerContext ctx) {
         this.ctx = ctx;
      }

      @Override
      public void run() {
         Channel channel = this.ctx.channel();
         ChannelConfig config = channel.config();
         if (!config.isAutoRead() && AbstractTrafficShapingHandler.isHandlerActive(this.ctx)) {
            if (AbstractTrafficShapingHandler.logger.isDebugEnabled()) {
               AbstractTrafficShapingHandler.logger
                  .debug("Not unsuspend: " + config.isAutoRead() + ':' + AbstractTrafficShapingHandler.isHandlerActive(this.ctx));
            }

            channel.attr(AbstractTrafficShapingHandler.READ_SUSPENDED).set(false);
         } else {
            if (AbstractTrafficShapingHandler.logger.isDebugEnabled()) {
               if (config.isAutoRead() && !AbstractTrafficShapingHandler.isHandlerActive(this.ctx)) {
                  if (AbstractTrafficShapingHandler.logger.isDebugEnabled()) {
                     AbstractTrafficShapingHandler.logger
                        .debug("Unsuspend: " + config.isAutoRead() + ':' + AbstractTrafficShapingHandler.isHandlerActive(this.ctx));
                  }
               } else if (AbstractTrafficShapingHandler.logger.isDebugEnabled()) {
                  AbstractTrafficShapingHandler.logger
                     .debug("Normal unsuspend: " + config.isAutoRead() + ':' + AbstractTrafficShapingHandler.isHandlerActive(this.ctx));
               }
            }

            channel.attr(AbstractTrafficShapingHandler.READ_SUSPENDED).set(false);
            config.setAutoRead(true);
            channel.read();
         }

         if (AbstractTrafficShapingHandler.logger.isDebugEnabled()) {
            AbstractTrafficShapingHandler.logger
               .debug("Unsuspend final status => " + config.isAutoRead() + ':' + AbstractTrafficShapingHandler.isHandlerActive(this.ctx));
         }
      }
   }
}
