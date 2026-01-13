package io.netty.handler.codec.bytes;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageDecoder;
import java.util.List;

public class ByteArrayDecoder extends MessageToMessageDecoder<ByteBuf> {
   public ByteArrayDecoder() {
      super(ByteBuf.class);
   }

   protected void decode(ChannelHandlerContext ctx, ByteBuf msg, List<Object> out) throws Exception {
      out.add(ByteBufUtil.getBytes(msg));
   }
}
