package com.github.luben.zstd;

import java.nio.ByteBuffer;

public interface BufferPool {
   ByteBuffer get(int var1);

   void release(ByteBuffer var1);
}
