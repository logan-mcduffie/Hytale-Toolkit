package io.netty.handler.codec.marshalling;

import io.netty.channel.ChannelHandlerContext;
import org.jboss.marshalling.Marshaller;
import org.jboss.marshalling.MarshallerFactory;
import org.jboss.marshalling.MarshallingConfiguration;

public class DefaultMarshallerProvider implements MarshallerProvider {
   private final MarshallerFactory factory;
   private final MarshallingConfiguration config;

   public DefaultMarshallerProvider(MarshallerFactory factory, MarshallingConfiguration config) {
      this.factory = factory;
      this.config = config;
   }

   @Override
   public Marshaller getMarshaller(ChannelHandlerContext ctx) throws Exception {
      return this.factory.createMarshaller(this.config);
   }
}
