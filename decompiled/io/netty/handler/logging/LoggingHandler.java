package io.netty.handler.logging;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufHolder;
import io.netty.buffer.ByteBufUtil;
import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPromise;
import io.netty.util.internal.ObjectUtil;
import io.netty.util.internal.StringUtil;
import io.netty.util.internal.logging.InternalLogLevel;
import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;
import java.net.SocketAddress;

@ChannelHandler.Sharable
public class LoggingHandler extends ChannelDuplexHandler {
   private static final LogLevel DEFAULT_LEVEL = LogLevel.DEBUG;
   protected final InternalLogger logger;
   protected final InternalLogLevel internalLevel;
   private final LogLevel level;
   private final ByteBufFormat byteBufFormat;

   public LoggingHandler() {
      this(DEFAULT_LEVEL);
   }

   public LoggingHandler(ByteBufFormat format) {
      this(DEFAULT_LEVEL, format);
   }

   public LoggingHandler(LogLevel level) {
      this(level, ByteBufFormat.HEX_DUMP);
   }

   public LoggingHandler(LogLevel level, ByteBufFormat byteBufFormat) {
      this.level = ObjectUtil.checkNotNull(level, "level");
      this.byteBufFormat = ObjectUtil.checkNotNull(byteBufFormat, "byteBufFormat");
      this.logger = InternalLoggerFactory.getInstance(this.getClass());
      this.internalLevel = level.toInternalLevel();
   }

   public LoggingHandler(Class<?> clazz) {
      this(clazz, DEFAULT_LEVEL);
   }

   public LoggingHandler(Class<?> clazz, LogLevel level) {
      this(clazz, level, ByteBufFormat.HEX_DUMP);
   }

   public LoggingHandler(Class<?> clazz, LogLevel level, ByteBufFormat byteBufFormat) {
      ObjectUtil.checkNotNull(clazz, "clazz");
      this.level = ObjectUtil.checkNotNull(level, "level");
      this.byteBufFormat = ObjectUtil.checkNotNull(byteBufFormat, "byteBufFormat");
      this.logger = InternalLoggerFactory.getInstance(clazz);
      this.internalLevel = level.toInternalLevel();
   }

   public LoggingHandler(String name) {
      this(name, DEFAULT_LEVEL);
   }

   public LoggingHandler(String name, LogLevel level) {
      this(name, level, ByteBufFormat.HEX_DUMP);
   }

   public LoggingHandler(String name, LogLevel level, ByteBufFormat byteBufFormat) {
      ObjectUtil.checkNotNull(name, "name");
      this.level = ObjectUtil.checkNotNull(level, "level");
      this.byteBufFormat = ObjectUtil.checkNotNull(byteBufFormat, "byteBufFormat");
      this.logger = InternalLoggerFactory.getInstance(name);
      this.internalLevel = level.toInternalLevel();
   }

   public LogLevel level() {
      return this.level;
   }

   public ByteBufFormat byteBufFormat() {
      return this.byteBufFormat;
   }

   @Override
   public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
      if (this.logger.isEnabled(this.internalLevel)) {
         this.logger.log(this.internalLevel, this.format(ctx, "REGISTERED"));
      }

      ctx.fireChannelRegistered();
   }

   @Override
   public void channelUnregistered(ChannelHandlerContext ctx) throws Exception {
      if (this.logger.isEnabled(this.internalLevel)) {
         this.logger.log(this.internalLevel, this.format(ctx, "UNREGISTERED"));
      }

      ctx.fireChannelUnregistered();
   }

   @Override
   public void channelActive(ChannelHandlerContext ctx) throws Exception {
      if (this.logger.isEnabled(this.internalLevel)) {
         this.logger.log(this.internalLevel, this.format(ctx, "ACTIVE"));
      }

      ctx.fireChannelActive();
   }

   @Override
   public void channelInactive(ChannelHandlerContext ctx) throws Exception {
      if (this.logger.isEnabled(this.internalLevel)) {
         this.logger.log(this.internalLevel, this.format(ctx, "INACTIVE"));
      }

      ctx.fireChannelInactive();
   }

   @Override
   public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
      if (this.logger.isEnabled(this.internalLevel)) {
         this.logger.log(this.internalLevel, this.format(ctx, "EXCEPTION", cause), cause);
      }

      ctx.fireExceptionCaught(cause);
   }

   @Override
   public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
      if (this.logger.isEnabled(this.internalLevel)) {
         this.logger.log(this.internalLevel, this.format(ctx, "USER_EVENT", evt));
      }

      ctx.fireUserEventTriggered(evt);
   }

   @Override
   public void bind(ChannelHandlerContext ctx, SocketAddress localAddress, ChannelPromise promise) throws Exception {
      if (this.logger.isEnabled(this.internalLevel)) {
         this.logger.log(this.internalLevel, this.format(ctx, "BIND", localAddress));
      }

      ctx.bind(localAddress, promise);
   }

   @Override
   public void connect(ChannelHandlerContext ctx, SocketAddress remoteAddress, SocketAddress localAddress, ChannelPromise promise) throws Exception {
      if (this.logger.isEnabled(this.internalLevel)) {
         this.logger.log(this.internalLevel, this.format(ctx, "CONNECT", remoteAddress, localAddress));
      }

      ctx.connect(remoteAddress, localAddress, promise);
   }

   @Override
   public void disconnect(ChannelHandlerContext ctx, ChannelPromise promise) throws Exception {
      if (this.logger.isEnabled(this.internalLevel)) {
         this.logger.log(this.internalLevel, this.format(ctx, "DISCONNECT"));
      }

      ctx.disconnect(promise);
   }

   @Override
   public void close(ChannelHandlerContext ctx, ChannelPromise promise) throws Exception {
      if (this.logger.isEnabled(this.internalLevel)) {
         this.logger.log(this.internalLevel, this.format(ctx, "CLOSE"));
      }

      ctx.close(promise);
   }

   @Override
   public void deregister(ChannelHandlerContext ctx, ChannelPromise promise) throws Exception {
      if (this.logger.isEnabled(this.internalLevel)) {
         this.logger.log(this.internalLevel, this.format(ctx, "DEREGISTER"));
      }

      ctx.deregister(promise);
   }

   @Override
   public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
      if (this.logger.isEnabled(this.internalLevel)) {
         this.logger.log(this.internalLevel, this.format(ctx, "READ COMPLETE"));
      }

      ctx.fireChannelReadComplete();
   }

   @Override
   public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
      if (this.logger.isEnabled(this.internalLevel)) {
         this.logger.log(this.internalLevel, this.format(ctx, "READ", msg));
      }

      ctx.fireChannelRead(msg);
   }

   @Override
   public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
      if (this.logger.isEnabled(this.internalLevel)) {
         this.logger.log(this.internalLevel, this.format(ctx, "WRITE", msg));
      }

      ctx.write(msg, promise);
   }

   @Override
   public void channelWritabilityChanged(ChannelHandlerContext ctx) throws Exception {
      if (this.logger.isEnabled(this.internalLevel)) {
         this.logger.log(this.internalLevel, this.format(ctx, "WRITABILITY CHANGED"));
      }

      ctx.fireChannelWritabilityChanged();
   }

   @Override
   public void flush(ChannelHandlerContext ctx) throws Exception {
      if (this.logger.isEnabled(this.internalLevel)) {
         this.logger.log(this.internalLevel, this.format(ctx, "FLUSH"));
      }

      ctx.flush();
   }

   protected String format(ChannelHandlerContext ctx, String eventName) {
      String chStr = ctx.channel().toString();
      return new StringBuilder(chStr.length() + 1 + eventName.length()).append(chStr).append(' ').append(eventName).toString();
   }

   protected String format(ChannelHandlerContext ctx, String eventName, Object arg) {
      if (arg instanceof ByteBuf) {
         return this.formatByteBuf(ctx, eventName, (ByteBuf)arg);
      } else {
         return arg instanceof ByteBufHolder ? this.formatByteBufHolder(ctx, eventName, (ByteBufHolder)arg) : formatSimple(ctx, eventName, arg);
      }
   }

   protected String format(ChannelHandlerContext ctx, String eventName, Object firstArg, Object secondArg) {
      if (secondArg == null) {
         return formatSimple(ctx, eventName, firstArg);
      } else {
         String chStr = ctx.channel().toString();
         String arg1Str = String.valueOf(firstArg);
         String arg2Str = secondArg.toString();
         StringBuilder buf = new StringBuilder(chStr.length() + 1 + eventName.length() + 2 + arg1Str.length() + 2 + arg2Str.length());
         buf.append(chStr).append(' ').append(eventName).append(": ").append(arg1Str).append(", ").append(arg2Str);
         return buf.toString();
      }
   }

   private String formatByteBuf(ChannelHandlerContext ctx, String eventName, ByteBuf msg) {
      String chStr = ctx.channel().toString();
      int length = msg.readableBytes();
      if (length == 0) {
         StringBuilder buf = new StringBuilder(chStr.length() + 1 + eventName.length() + 4);
         buf.append(chStr).append(' ').append(eventName).append(": 0B");
         return buf.toString();
      } else {
         int outputLength = chStr.length() + 1 + eventName.length() + 2 + 10 + 1;
         if (this.byteBufFormat == ByteBufFormat.HEX_DUMP) {
            int rows = length / 16 + (length % 15 == 0 ? 0 : 1) + 4;
            int hexDumpLength = 2 + rows * 80;
            outputLength += hexDumpLength;
         }

         StringBuilder buf = new StringBuilder(outputLength);
         buf.append(chStr).append(' ').append(eventName).append(": ").append(length).append('B');
         if (this.byteBufFormat == ByteBufFormat.HEX_DUMP) {
            buf.append(StringUtil.NEWLINE);
            ByteBufUtil.appendPrettyHexDump(buf, msg);
         }

         return buf.toString();
      }
   }

   private String formatByteBufHolder(ChannelHandlerContext ctx, String eventName, ByteBufHolder msg) {
      String chStr = ctx.channel().toString();
      String msgStr = msg.toString();
      ByteBuf content = msg.content();
      int length = content.readableBytes();
      if (length == 0) {
         StringBuilder buf = new StringBuilder(chStr.length() + 1 + eventName.length() + 2 + msgStr.length() + 4);
         buf.append(chStr).append(' ').append(eventName).append(", ").append(msgStr).append(", 0B");
         return buf.toString();
      } else {
         int outputLength = chStr.length() + 1 + eventName.length() + 2 + msgStr.length() + 2 + 10 + 1;
         if (this.byteBufFormat == ByteBufFormat.HEX_DUMP) {
            int rows = length / 16 + (length % 15 == 0 ? 0 : 1) + 4;
            int hexDumpLength = 2 + rows * 80;
            outputLength += hexDumpLength;
         }

         StringBuilder buf = new StringBuilder(outputLength);
         buf.append(chStr).append(' ').append(eventName).append(": ").append(msgStr).append(", ").append(length).append('B');
         if (this.byteBufFormat == ByteBufFormat.HEX_DUMP) {
            buf.append(StringUtil.NEWLINE);
            ByteBufUtil.appendPrettyHexDump(buf, content);
         }

         return buf.toString();
      }
   }

   private static String formatSimple(ChannelHandlerContext ctx, String eventName, Object msg) {
      String chStr = ctx.channel().toString();
      String msgStr = String.valueOf(msg);
      StringBuilder buf = new StringBuilder(chStr.length() + 1 + eventName.length() + 2 + msgStr.length());
      return buf.append(chStr).append(' ').append(eventName).append(": ").append(msgStr).toString();
   }
}
