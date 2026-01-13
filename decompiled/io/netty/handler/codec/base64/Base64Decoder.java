package io.netty.handler.codec.base64;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageDecoder;
import io.netty.util.internal.ObjectUtil;
import java.util.List;

@ChannelHandler.Sharable
public class Base64Decoder extends MessageToMessageDecoder<ByteBuf> {
   private final Base64Dialect dialect;

   public Base64Decoder() {
      this(Base64Dialect.STANDARD);
   }

   public Base64Decoder(Base64Dialect dialect) {
      super(ByteBuf.class);
      this.dialect = ObjectUtil.checkNotNull(dialect, "dialect");
   }

   protected void decode(ChannelHandlerContext ctx, ByteBuf msg, List<Object> out) throws Exception {
      out.add(Base64.decode(msg, msg.readerIndex(), msg.readableBytes(), this.dialect));
   }
}
