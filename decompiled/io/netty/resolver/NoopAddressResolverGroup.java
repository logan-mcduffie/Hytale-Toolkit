package io.netty.resolver;

import io.netty.util.concurrent.EventExecutor;
import java.net.SocketAddress;

public final class NoopAddressResolverGroup extends AddressResolverGroup<SocketAddress> {
   public static final NoopAddressResolverGroup INSTANCE = new NoopAddressResolverGroup();

   private NoopAddressResolverGroup() {
   }

   @Override
   protected AddressResolver<SocketAddress> newResolver(EventExecutor executor) throws Exception {
      return new NoopAddressResolver(executor);
   }
}
