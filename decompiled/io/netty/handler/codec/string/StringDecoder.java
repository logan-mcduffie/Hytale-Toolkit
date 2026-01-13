package io.netty.handler.codec.string;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageDecoder;
import io.netty.util.internal.ObjectUtil;
import java.nio.charset.Charset;
import java.util.List;

@ChannelHandler.Sharable
public class StringDecoder extends MessageToMessageDecoder<ByteBuf> {
   private final Charset charset;

   public StringDecoder() {
      this(Charset.defaultCharset());
   }

   public StringDecoder(Charset charset) {
      super(ByteBuf.class);
      this.charset = ObjectUtil.checkNotNull(charset, "charset");
   }

   protected void decode(ChannelHandlerContext ctx, ByteBuf msg, List<Object> out) throws Exception {
      out.add(msg.toString(this.charset));
   }
}
