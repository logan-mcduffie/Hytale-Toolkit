package io.netty.handler.ssl;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufHolder;

interface PemEncoded extends ByteBufHolder {
   boolean isSensitive();

   PemEncoded copy();

   PemEncoded duplicate();

   PemEncoded retainedDuplicate();

   PemEncoded replace(ByteBuf var1);

   PemEncoded retain();

   PemEncoded retain(int var1);

   PemEncoded touch();

   PemEncoded touch(Object var1);
}
