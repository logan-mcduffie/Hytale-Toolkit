package io.netty.buffer;

import io.netty.util.ReferenceCounted;

public interface ByteBufHolder extends ReferenceCounted {
   ByteBuf content();

   ByteBufHolder copy();

   ByteBufHolder duplicate();

   ByteBufHolder retainedDuplicate();

   ByteBufHolder replace(ByteBuf var1);

   ByteBufHolder retain();

   ByteBufHolder retain(int var1);

   ByteBufHolder touch();

   ByteBufHolder touch(Object var1);
}
