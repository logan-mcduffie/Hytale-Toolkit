package io.netty.util.internal;

import java.nio.ByteBuffer;

public interface CleanableDirectBuffer {
   ByteBuffer buffer();

   void clean();

   default boolean hasMemoryAddress() {
      return false;
   }

   default long memoryAddress() {
      return 0L;
   }
}
