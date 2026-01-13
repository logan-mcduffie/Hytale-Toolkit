package com.hypixel.hytale.server.core.io.netty;

import com.hypixel.hytale.logger.HytaleLogger;
import com.hypixel.hytale.protocol.io.PacketStatsRecorder;
import com.hypixel.hytale.protocol.io.netty.PacketDecoder;
import com.hypixel.hytale.protocol.io.netty.PacketEncoder;
import com.hypixel.hytale.protocol.io.netty.ProtocolUtil;
import com.hypixel.hytale.protocol.packets.connection.Disconnect;
import com.hypixel.hytale.protocol.packets.connection.DisconnectType;
import com.hypixel.hytale.server.core.HytaleServer;
import com.hypixel.hytale.server.core.HytaleServerConfig;
import com.hypixel.hytale.server.core.io.PacketStatsRecorderImpl;
import com.hypixel.hytale.server.core.io.handlers.InitialPacketHandler;
import com.hypixel.hytale.server.core.io.transport.QUICTransport;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelInitializer;
import io.netty.handler.codec.quic.QuicChannel;
import io.netty.handler.codec.quic.QuicStreamChannel;
import io.netty.handler.timeout.ReadTimeoutException;
import io.netty.handler.timeout.ReadTimeoutHandler;
import io.netty.handler.timeout.TimeoutException;
import io.netty.handler.timeout.WriteTimeoutException;
import java.io.IOException;
import java.nio.channels.ClosedChannelException;
import java.security.cert.X509Certificate;
import java.time.Duration;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Level;
import javax.annotation.Nonnull;

public class HytaleChannelInitializer extends ChannelInitializer<Channel> {
   @Override
   protected void initChannel(Channel channel) {
      if (channel instanceof QuicStreamChannel quicStreamChannel) {
         HytaleLogger.getLogger().at(Level.INFO).log("Received stream %s to %s", NettyUtil.formatRemoteAddress(channel), NettyUtil.formatLocalAddress(channel));
         QuicChannel parentChannel = quicStreamChannel.parent();
         X509Certificate clientCert = parentChannel.attr(QUICTransport.CLIENT_CERTIFICATE_ATTR).get();
         if (clientCert != null) {
            channel.attr(QUICTransport.CLIENT_CERTIFICATE_ATTR).set(clientCert);
            HytaleLogger.getLogger().at(Level.FINE).log("Copied client certificate to stream: %s", clientCert.getSubjectX500Principal().getName());
         }
      } else {
         HytaleLogger.getLogger()
            .at(Level.INFO)
            .log("Received connection from %s to %s", NettyUtil.formatRemoteAddress(channel), NettyUtil.formatLocalAddress(channel));
      }

      PacketStatsRecorderImpl statsRecorder = new PacketStatsRecorderImpl();
      channel.attr(PacketStatsRecorder.CHANNEL_KEY).set(statsRecorder);
      Duration initialTimeout = HytaleServer.get().getConfig().getConnectionTimeouts().getInitialTimeout();
      channel.pipeline().addLast("timeOut", new ReadTimeoutHandler(initialTimeout.toMillis(), TimeUnit.MILLISECONDS));
      channel.pipeline().addLast("packetDecoder", new PacketDecoder());
      HytaleServerConfig.RateLimitConfig rateLimitConfig = HytaleServer.get().getConfig().getRateLimitConfig();
      if (rateLimitConfig.isEnabled()) {
         channel.pipeline().addLast("rateLimit", new RateLimitHandler(rateLimitConfig.getBurstCapacity(), rateLimitConfig.getPacketsPerSecond()));
      }

      channel.pipeline().addLast("packetEncoder", new PacketEncoder());
      channel.pipeline().addLast("packetArrayEncoder", NettyUtil.PACKET_ARRAY_ENCODER_INSTANCE);
      if (NettyUtil.PACKET_LOGGER.getLevel() != Level.OFF) {
         channel.pipeline().addLast("logger", NettyUtil.LOGGER);
      }

      InitialPacketHandler playerConnection = new InitialPacketHandler(channel);
      channel.pipeline().addLast("handler", new PlayerChannelHandler(playerConnection));
      channel.pipeline().addLast(new HytaleChannelInitializer.ExceptionHandler());
      playerConnection.registered(null);
   }

   @Override
   public void exceptionCaught(@Nonnull ChannelHandlerContext ctx, Throwable cause) {
      HytaleLogger.getLogger().at(Level.WARNING).withCause(cause).log("Got exception from netty pipeline in HytaleChannelInitializer!");
      if (ctx.channel().isWritable()) {
         ctx.channel().writeAndFlush(new Disconnect("Internal server error!", DisconnectType.Crash)).addListener(ProtocolUtil.CLOSE_ON_COMPLETE);
      } else {
         ProtocolUtil.closeApplicationConnection(ctx.channel());
      }
   }

   @Override
   public void channelInactive(@Nonnull ChannelHandlerContext ctx) throws Exception {
      ProtocolUtil.closeApplicationConnection(ctx.channel());
      super.channelInactive(ctx);
   }

   private static class ExceptionHandler extends ChannelInboundHandlerAdapter {
      private final AtomicBoolean handled = new AtomicBoolean();

      @Override
      public void exceptionCaught(@Nonnull ChannelHandlerContext ctx, Throwable cause) {
         if (!(cause instanceof ClosedChannelException)) {
            ChannelHandler handler = ctx.pipeline().get("handler");
            String identifier;
            if (handler instanceof PlayerChannelHandler) {
               identifier = ((PlayerChannelHandler)handler).getHandler().getIdentifier();
            } else {
               identifier = NettyUtil.formatRemoteAddress(ctx.channel());
            }

            if (this.handled.getAndSet(true)) {
               if (cause instanceof IOException && cause.getMessage() != null) {
                  String var8 = cause.getMessage();
                  switch (var8) {
                     case "Broken pipe":
                     case "Connection reset by peer":
                     case "An existing connection was forcibly closed by the remote host":
                        return;
                  }
               }

               HytaleLogger.getLogger().at(Level.WARNING).withCause(cause).log("Already handled exception in ExceptionHandler but got another!");
            } else if (cause instanceof TimeoutException) {
               boolean readTimeout = cause instanceof ReadTimeoutException;
               boolean writeTimeout = cause instanceof WriteTimeoutException;
               String msg = readTimeout ? "Read timeout for %s" : (writeTimeout ? "Write timeout for %s" : "Connection timeout for %s");
               HytaleLogger.getLogger().at(Level.INFO).log(msg, identifier);
               NettyUtil.CONNECTION_EXCEPTION_LOGGER.at(Level.FINE).withCause(cause).log(msg, identifier);
               if (ctx.channel().isWritable()) {
                  ctx.channel()
                     .writeAndFlush(
                        new Disconnect(readTimeout ? "Read timeout" : (writeTimeout ? "Write timeout" : "Connection timeout"), DisconnectType.Crash)
                     )
                     .addListener(ProtocolUtil.CLOSE_ON_COMPLETE);
               } else {
                  ProtocolUtil.closeApplicationConnection(ctx.channel());
               }
            } else {
               HytaleLogger.getLogger().at(Level.SEVERE).withCause(cause).log("Got exception from netty pipeline in ExceptionHandler: %s", cause.getMessage());
               if (ctx.channel().isWritable()) {
                  ctx.channel().writeAndFlush(new Disconnect("Internal server error!", DisconnectType.Crash)).addListener(ProtocolUtil.CLOSE_ON_COMPLETE);
               } else {
                  ProtocolUtil.closeApplicationConnection(ctx.channel());
               }
            }
         }
      }
   }
}
