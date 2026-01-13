package io.netty.handler.codec.quic;

import io.netty.buffer.ByteBuf;
import java.net.InetSocketAddress;

public interface QuicTokenHandler {
   boolean writeToken(ByteBuf var1, ByteBuf var2, InetSocketAddress var3);

   int validateToken(ByteBuf var1, InetSocketAddress var2);

   int maxTokenLength();
}
