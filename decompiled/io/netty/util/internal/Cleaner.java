package io.netty.util.internal;

import java.nio.ByteBuffer;

interface Cleaner {
   CleanableDirectBuffer allocate(int var1);

   @Deprecated
   void freeDirectBuffer(ByteBuffer var1);
}
