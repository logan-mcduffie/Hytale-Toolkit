package com.hypixel.hytale.server.core.io.transport;

import com.hypixel.hytale.logger.HytaleLogger;
import com.hypixel.hytale.protocol.io.netty.ProtocolUtil;
import com.hypixel.hytale.protocol.packets.connection.Disconnect;
import com.hypixel.hytale.protocol.packets.connection.DisconnectType;
import com.hypixel.hytale.server.core.HytaleServer;
import com.hypixel.hytale.server.core.auth.CertificateUtil;
import com.hypixel.hytale.server.core.auth.ServerAuthManager;
import com.hypixel.hytale.server.core.io.netty.HytaleChannelInitializer;
import com.hypixel.hytale.server.core.io.netty.NettyUtil;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.socket.DatagramChannel;
import io.netty.channel.socket.SocketProtocolFamily;
import io.netty.channel.socket.nio.NioChannelOption;
import io.netty.handler.codec.quic.InsecureQuicTokenHandler;
import io.netty.handler.codec.quic.QuicChannel;
import io.netty.handler.codec.quic.QuicServerCodecBuilder;
import io.netty.handler.codec.quic.QuicSslContext;
import io.netty.handler.codec.quic.QuicSslContextBuilder;
import io.netty.handler.ssl.ClientAuth;
import io.netty.handler.ssl.util.InsecureTrustManagerFactory;
import io.netty.handler.ssl.util.SelfSignedCertificate;
import io.netty.util.AttributeKey;
import java.net.Inet4Address;
import java.net.Inet6Address;
import java.net.InetSocketAddress;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.time.Duration;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.net.ssl.SSLEngine;
import jdk.net.ExtendedSocketOptions;

public class QUICTransport implements Transport {
   private static final HytaleLogger LOGGER = HytaleLogger.forEnclosingClass();
   public static final AttributeKey<X509Certificate> CLIENT_CERTIFICATE_ATTR = AttributeKey.valueOf("CLIENT_CERTIFICATE");
   @Nonnull
   private final EventLoopGroup workerGroup = NettyUtil.getEventLoopGroup("ServerWorkerGroup");
   private final Bootstrap bootstrapIpv4;
   private final Bootstrap bootstrapIpv6;

   public QUICTransport() throws InterruptedException {
      SelfSignedCertificate ssc = null;

      try {
         ssc = new SelfSignedCertificate("localhost");
      } catch (CertificateException var5) {
         throw new RuntimeException(var5);
      }

      ServerAuthManager.getInstance().setServerCertificate(ssc.cert());
      LOGGER.at(Level.INFO).log("Server certificate registered for mutual auth, fingerprint: %s", CertificateUtil.computeCertificateFingerprint(ssc.cert()));
      QuicSslContext sslContext = QuicSslContextBuilder.forServer(ssc.key(), null, ssc.cert())
         .applicationProtocols("hytale/1")
         .earlyData(false)
         .clientAuth(ClientAuth.REQUIRE)
         .trustManager(InsecureTrustManagerFactory.INSTANCE)
         .build();
      NettyUtil.ReflectiveChannelFactory<? extends DatagramChannel> channelFactoryIpv4 = NettyUtil.getDatagramChannelFactory(SocketProtocolFamily.INET);
      LOGGER.at(Level.INFO).log("Using IPv4 Datagram Channel: %s...", channelFactoryIpv4.getSimpleName());
      this.bootstrapIpv4 = new Bootstrap()
         .group(this.workerGroup)
         .channelFactory(channelFactoryIpv4)
         .option(ChannelOption.SO_REUSEADDR, true)
         .option(NioChannelOption.of(ExtendedSocketOptions.IP_DONTFRAGMENT), true)
         .handler(new QUICTransport.QuicChannelInboundHandlerAdapter(sslContext))
         .validate();
      NettyUtil.ReflectiveChannelFactory<? extends DatagramChannel> channelFactoryIpv6 = NettyUtil.getDatagramChannelFactory(SocketProtocolFamily.INET6);
      LOGGER.at(Level.INFO).log("Using IPv6 Datagram Channel: %s...", channelFactoryIpv6.getSimpleName());
      this.bootstrapIpv6 = new Bootstrap()
         .group(this.workerGroup)
         .channelFactory(channelFactoryIpv6)
         .option(ChannelOption.SO_REUSEADDR, true)
         .option(NioChannelOption.of(ExtendedSocketOptions.IP_DONTFRAGMENT), true)
         .handler(new QUICTransport.QuicChannelInboundHandlerAdapter(sslContext))
         .validate();
      this.bootstrapIpv4.register().sync();
      this.bootstrapIpv6.register().sync();
   }

   @Nonnull
   @Override
   public TransportType getType() {
      return TransportType.QUIC;
   }

   @Override
   public ChannelFuture bind(@Nonnull InetSocketAddress address) throws InterruptedException {
      if (address.getAddress() instanceof Inet4Address) {
         return this.bootstrapIpv4.bind(address).sync();
      } else if (address.getAddress() instanceof Inet6Address) {
         return this.bootstrapIpv6.bind(address).sync();
      } else {
         throw new UnsupportedOperationException("Unsupported address type: " + address.getAddress().getClass());
      }
   }

   @Override
   public void shutdown() {
      LOGGER.at(Level.INFO).log("Shutting down workerGroup...");

      try {
         this.workerGroup.shutdownGracefully(0L, 1L, TimeUnit.SECONDS).await(1L, TimeUnit.SECONDS);
      } catch (InterruptedException var2) {
         LOGGER.at(Level.SEVERE).withCause(var2).log("Failed to await for listener to close!");
         Thread.currentThread().interrupt();
      }
   }

   private static class QuicChannelInboundHandlerAdapter extends ChannelInboundHandlerAdapter {
      private final QuicSslContext sslContext;

      public QuicChannelInboundHandlerAdapter(QuicSslContext sslContext) {
         this.sslContext = sslContext;
      }

      @Override
      public boolean isSharable() {
         return true;
      }

      @Override
      public void channelActive(@Nonnull ChannelHandlerContext ctx) throws Exception {
         Duration playTimeout = HytaleServer.get().getConfig().getConnectionTimeouts().getPlayTimeout();
         ChannelHandler quicHandler = new QuicServerCodecBuilder()
            .sslContext(this.sslContext)
            .tokenHandler(InsecureQuicTokenHandler.INSTANCE)
            .maxIdleTimeout(playTimeout.toMillis(), TimeUnit.MILLISECONDS)
            .ackDelayExponent(3L)
            .initialMaxData(524288L)
            .initialMaxStreamDataUnidirectional(0L)
            .initialMaxStreamsUnidirectional(0L)
            .initialMaxStreamDataBidirectionalLocal(131072L)
            .initialMaxStreamDataBidirectionalRemote(131072L)
            .initialMaxStreamsBidirectional(1L)
            .handler(
               new ChannelInboundHandlerAdapter() {
                  @Override
                  public boolean isSharable() {
                     return true;
                  }

                  @Override
                  public void channelActive(@Nonnull ChannelHandlerContext ctx) throws Exception {
                     QuicChannel channel = (QuicChannel)ctx.channel();
                     QUICTransport.LOGGER
                        .at(Level.INFO)
                        .log("Received connection from %s to %s", NettyUtil.formatRemoteAddress(channel), NettyUtil.formatLocalAddress(channel));
                     X509Certificate clientCert = QuicChannelInboundHandlerAdapter.this.extractClientCertificate(channel);
                     if (clientCert == null) {
                        QUICTransport.LOGGER
                           .at(Level.WARNING)
                           .log("Connection rejected: no client certificate from %s", NettyUtil.formatRemoteAddress(channel));
                        ProtocolUtil.closeConnection(channel);
                     } else {
                        channel.attr(QUICTransport.CLIENT_CERTIFICATE_ATTR).set(clientCert);
                        QUICTransport.LOGGER.at(Level.FINE).log("Client certificate: %s", clientCert.getSubjectX500Principal().getName());
                     }
                  }

                  @Override
                  public void channelInactive(@Nonnull ChannelHandlerContext ctx) {
                     ((QuicChannel)ctx.channel()).collectStats().addListener(f -> {
                        if (f.isSuccess()) {
                           QUICTransport.LOGGER.at(Level.INFO).log("Connection closed: %s", f.getNow());
                        }
                     });
                  }

                  @Override
                  public void exceptionCaught(@Nonnull ChannelHandlerContext ctx, Throwable cause) {
                     QUICTransport.LOGGER.at(Level.WARNING).withCause(cause).log("Got exception from netty pipeline in ChannelInitializer!");
                     Channel channel = ctx.channel();
                     if (channel.isWritable()) {
                        channel.writeAndFlush(new Disconnect("Internal server error!", DisconnectType.Crash)).addListener(ProtocolUtil.CLOSE_ON_COMPLETE);
                     } else {
                        ProtocolUtil.closeApplicationConnection(channel);
                     }
                  }
               }
            )
            .streamHandler(new HytaleChannelInitializer())
            .build();
         ctx.channel().pipeline().addLast(quicHandler);
      }

      @Nullable
      private X509Certificate extractClientCertificate(QuicChannel channel) {
         try {
            SSLEngine sslEngine = channel.sslEngine();
            if (sslEngine == null) {
               return null;
            }

            Certificate[] peerCerts = sslEngine.getSession().getPeerCertificates();
            if (peerCerts != null && peerCerts.length > 0 && peerCerts[0] instanceof X509Certificate) {
               return (X509Certificate)peerCerts[0];
            }
         } catch (Exception var4) {
            QUICTransport.LOGGER.at(Level.FINEST).log("No peer certificate available: %s", var4.getMessage());
         }

         return null;
      }
   }
}
