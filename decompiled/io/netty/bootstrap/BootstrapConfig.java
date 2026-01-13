package io.netty.bootstrap;

import io.netty.channel.Channel;
import io.netty.resolver.AddressResolverGroup;
import java.net.SocketAddress;

public final class BootstrapConfig extends AbstractBootstrapConfig<Bootstrap, Channel> {
   BootstrapConfig(Bootstrap bootstrap) {
      super(bootstrap);
   }

   public SocketAddress remoteAddress() {
      return this.bootstrap.remoteAddress();
   }

   public AddressResolverGroup<?> resolver() {
      return this.bootstrap.resolver();
   }

   @Override
   public String toString() {
      StringBuilder buf = new StringBuilder(super.toString());
      buf.setLength(buf.length() - 1);
      AddressResolverGroup<?> resolver = this.resolver();
      if (resolver != null) {
         buf.append(", resolver: ").append(resolver);
      }

      SocketAddress remoteAddress = this.remoteAddress();
      if (remoteAddress != null) {
         buf.append(", remoteAddress: ").append(remoteAddress);
      }

      return buf.append(')').toString();
   }
}
