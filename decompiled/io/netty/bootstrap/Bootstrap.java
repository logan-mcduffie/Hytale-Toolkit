package io.netty.bootstrap;

import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.ChannelPromise;
import io.netty.channel.EventLoop;
import io.netty.channel.EventLoopGroup;
import io.netty.resolver.AddressResolver;
import io.netty.resolver.AddressResolverGroup;
import io.netty.resolver.DefaultAddressResolverGroup;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.FutureListener;
import io.netty.util.internal.ObjectUtil;
import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.Collection;

public class Bootstrap extends AbstractBootstrap<Bootstrap, Channel> {
   private static final InternalLogger logger = InternalLoggerFactory.getInstance(Bootstrap.class);
   private final BootstrapConfig config = new BootstrapConfig(this);
   private Bootstrap.ExternalAddressResolver externalResolver;
   private volatile boolean disableResolver;
   private volatile SocketAddress remoteAddress;

   public Bootstrap() {
   }

   private Bootstrap(Bootstrap bootstrap) {
      super(bootstrap);
      this.externalResolver = bootstrap.externalResolver;
      this.disableResolver = bootstrap.disableResolver;
      this.remoteAddress = bootstrap.remoteAddress;
   }

   public Bootstrap resolver(AddressResolverGroup<?> resolver) {
      this.externalResolver = resolver == null ? null : new Bootstrap.ExternalAddressResolver(resolver);
      this.disableResolver = false;
      return this;
   }

   public Bootstrap disableResolver() {
      this.externalResolver = null;
      this.disableResolver = true;
      return this;
   }

   public Bootstrap remoteAddress(SocketAddress remoteAddress) {
      this.remoteAddress = remoteAddress;
      return this;
   }

   public Bootstrap remoteAddress(String inetHost, int inetPort) {
      this.remoteAddress = InetSocketAddress.createUnresolved(inetHost, inetPort);
      return this;
   }

   public Bootstrap remoteAddress(InetAddress inetHost, int inetPort) {
      this.remoteAddress = new InetSocketAddress(inetHost, inetPort);
      return this;
   }

   public ChannelFuture connect() {
      this.validate();
      SocketAddress remoteAddress = this.remoteAddress;
      if (remoteAddress == null) {
         throw new IllegalStateException("remoteAddress not set");
      } else {
         return this.doResolveAndConnect(remoteAddress, this.config.localAddress());
      }
   }

   public ChannelFuture connect(String inetHost, int inetPort) {
      return this.connect(InetSocketAddress.createUnresolved(inetHost, inetPort));
   }

   public ChannelFuture connect(InetAddress inetHost, int inetPort) {
      return this.connect(new InetSocketAddress(inetHost, inetPort));
   }

   public ChannelFuture connect(SocketAddress remoteAddress) {
      ObjectUtil.checkNotNull(remoteAddress, "remoteAddress");
      this.validate();
      return this.doResolveAndConnect(remoteAddress, this.config.localAddress());
   }

   public ChannelFuture connect(SocketAddress remoteAddress, SocketAddress localAddress) {
      ObjectUtil.checkNotNull(remoteAddress, "remoteAddress");
      this.validate();
      return this.doResolveAndConnect(remoteAddress, localAddress);
   }

   private ChannelFuture doResolveAndConnect(final SocketAddress remoteAddress, final SocketAddress localAddress) {
      ChannelFuture regFuture = this.initAndRegister();
      final Channel channel = regFuture.channel();
      if (regFuture.isDone()) {
         return !regFuture.isSuccess() ? regFuture : this.doResolveAndConnect0(channel, remoteAddress, localAddress, channel.newPromise());
      } else {
         final AbstractBootstrap.PendingRegistrationPromise promise = new AbstractBootstrap.PendingRegistrationPromise(channel);
         regFuture.addListener(new ChannelFutureListener() {
            public void operationComplete(ChannelFuture future) throws Exception {
               Throwable cause = future.cause();
               if (cause != null) {
                  promise.setFailure(cause);
               } else {
                  promise.registered();
                  Bootstrap.this.doResolveAndConnect0(channel, remoteAddress, localAddress, promise);
               }
            }
         });
         return promise;
      }
   }

   private ChannelFuture doResolveAndConnect0(
      final Channel channel, SocketAddress remoteAddress, final SocketAddress localAddress, final ChannelPromise promise
   ) {
      try {
         if (this.disableResolver) {
            doConnect(remoteAddress, localAddress, promise);
            return promise;
         }

         EventLoop eventLoop = channel.eventLoop();

         AddressResolver<SocketAddress> resolver;
         try {
            resolver = Bootstrap.ExternalAddressResolver.getOrDefault(this.externalResolver).getResolver(eventLoop);
         } catch (Throwable var9) {
            channel.close();
            return promise.setFailure(var9);
         }

         if (!resolver.isSupported(remoteAddress) || resolver.isResolved(remoteAddress)) {
            doConnect(remoteAddress, localAddress, promise);
            return promise;
         }

         Future<SocketAddress> resolveFuture = resolver.resolve(remoteAddress);
         if (resolveFuture.isDone()) {
            Throwable resolveFailureCause = resolveFuture.cause();
            if (resolveFailureCause != null) {
               channel.close();
               promise.setFailure(resolveFailureCause);
            } else {
               doConnect(resolveFuture.getNow(), localAddress, promise);
            }

            return promise;
         }

         resolveFuture.addListener(new FutureListener<SocketAddress>() {
            @Override
            public void operationComplete(Future<SocketAddress> future) throws Exception {
               if (future.cause() != null) {
                  channel.close();
                  promise.setFailure(future.cause());
               } else {
                  Bootstrap.doConnect(future.getNow(), localAddress, promise);
               }
            }
         });
      } catch (Throwable var10) {
         promise.tryFailure(var10);
      }

      return promise;
   }

   private static void doConnect(final SocketAddress remoteAddress, final SocketAddress localAddress, final ChannelPromise connectPromise) {
      final Channel channel = connectPromise.channel();
      channel.eventLoop().execute(new Runnable() {
         @Override
         public void run() {
            if (localAddress == null) {
               channel.connect(remoteAddress, connectPromise);
            } else {
               channel.connect(remoteAddress, localAddress, connectPromise);
            }

            connectPromise.addListener(ChannelFutureListener.CLOSE_ON_FAILURE);
         }
      });
   }

   @Override
   void init(Channel channel) throws Throwable {
      ChannelPipeline p = channel.pipeline();
      p.addLast(this.config.handler());
      setChannelOptions(channel, this.newOptionsArray(), logger);
      setAttributes(channel, this.newAttributesArray());
      Collection<ChannelInitializerExtension> extensions = this.getInitializerExtensions();
      if (!extensions.isEmpty()) {
         for (ChannelInitializerExtension extension : extensions) {
            try {
               extension.postInitializeClientChannel(channel);
            } catch (Exception var7) {
               logger.warn("Exception thrown from postInitializeClientChannel", (Throwable)var7);
            }
         }
      }
   }

   public Bootstrap validate() {
      super.validate();
      if (this.config.handler() == null) {
         throw new IllegalStateException("handler not set");
      } else {
         return this;
      }
   }

   public Bootstrap clone() {
      return new Bootstrap(this);
   }

   public Bootstrap clone(EventLoopGroup group) {
      Bootstrap bs = new Bootstrap(this);
      bs.group = group;
      return bs;
   }

   public final BootstrapConfig config() {
      return this.config;
   }

   final SocketAddress remoteAddress() {
      return this.remoteAddress;
   }

   final AddressResolverGroup<?> resolver() {
      return this.disableResolver ? null : Bootstrap.ExternalAddressResolver.getOrDefault(this.externalResolver);
   }

   static final class ExternalAddressResolver {
      final AddressResolverGroup<SocketAddress> resolverGroup;

      ExternalAddressResolver(AddressResolverGroup<?> resolverGroup) {
         this.resolverGroup = (AddressResolverGroup<SocketAddress>)resolverGroup;
      }

      static AddressResolverGroup<SocketAddress> getOrDefault(Bootstrap.ExternalAddressResolver externalResolver) {
         if (externalResolver == null) {
            AddressResolverGroup<?> defaultResolverGroup = DefaultAddressResolverGroup.INSTANCE;
            return (AddressResolverGroup<SocketAddress>)defaultResolverGroup;
         } else {
            return externalResolver.resolverGroup;
         }
      }
   }
}
