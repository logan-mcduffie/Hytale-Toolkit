package io.netty.buffer;

import jdk.jfr.DataAmount;
import jdk.jfr.Description;
import jdk.jfr.MemoryAddress;

abstract class AbstractChunkEvent extends AbstractAllocatorEvent {
   @DataAmount
   @Description("Size of the chunk")
   public int capacity;
   @Description("Is this chunk referencing off-heap memory?")
   public boolean direct;
   @Description("The memory address of the off-heap memory, if available")
   @MemoryAddress
   public long address;

   public void fill(ChunkInfo chunk, Class<? extends AbstractByteBufAllocator> allocatorType) {
      this.allocatorType = allocatorType;
      this.capacity = chunk.capacity();
      this.direct = chunk.isDirect();
      this.address = chunk.memoryAddress();
   }
}
