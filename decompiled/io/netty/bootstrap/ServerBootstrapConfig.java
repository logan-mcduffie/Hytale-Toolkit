package io.netty.bootstrap;

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.ServerChannel;
import io.netty.util.AttributeKey;
import io.netty.util.internal.StringUtil;
import java.util.Map;

public final class ServerBootstrapConfig extends AbstractBootstrapConfig<ServerBootstrap, ServerChannel> {
   ServerBootstrapConfig(ServerBootstrap bootstrap) {
      super(bootstrap);
   }

   public EventLoopGroup childGroup() {
      return this.bootstrap.childGroup();
   }

   public ChannelHandler childHandler() {
      return this.bootstrap.childHandler();
   }

   public Map<ChannelOption<?>, Object> childOptions() {
      return this.bootstrap.childOptions();
   }

   public Map<AttributeKey<?>, Object> childAttrs() {
      return this.bootstrap.childAttrs();
   }

   @Override
   public String toString() {
      StringBuilder buf = new StringBuilder(super.toString());
      buf.setLength(buf.length() - 1);
      buf.append(", ");
      EventLoopGroup childGroup = this.childGroup();
      if (childGroup != null) {
         buf.append("childGroup: ");
         buf.append(StringUtil.simpleClassName(childGroup));
         buf.append(", ");
      }

      Map<ChannelOption<?>, Object> childOptions = this.childOptions();
      if (!childOptions.isEmpty()) {
         buf.append("childOptions: ");
         buf.append(childOptions);
         buf.append(", ");
      }

      Map<AttributeKey<?>, Object> childAttrs = this.childAttrs();
      if (!childAttrs.isEmpty()) {
         buf.append("childAttrs: ");
         buf.append(childAttrs);
         buf.append(", ");
      }

      ChannelHandler childHandler = this.childHandler();
      if (childHandler != null) {
         buf.append("childHandler: ");
         buf.append(childHandler);
         buf.append(", ");
      }

      if (buf.charAt(buf.length() - 1) == '(') {
         buf.append(')');
      } else {
         buf.setCharAt(buf.length() - 2, ')');
         buf.setLength(buf.length() - 1);
      }

      return buf.toString();
   }
}
