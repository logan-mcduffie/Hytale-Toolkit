package io.netty.buffer;

import jdk.jfr.DataAmount;
import jdk.jfr.Description;
import jdk.jfr.MemoryAddress;

abstract class AbstractBufferEvent extends AbstractAllocatorEvent {
   @DataAmount
   @Description("Configured buffer capacity")
   public int size;
   @DataAmount
   @Description("Actual allocated buffer capacity")
   public int maxFastCapacity;
   @DataAmount
   @Description("Maximum buffer capacity")
   public int maxCapacity;
   @Description("Is this buffer referencing off-heap memory?")
   public boolean direct;
   @Description("The memory address of the off-heap memory, if available")
   @MemoryAddress
   public long address;

   public void fill(AbstractByteBuf buf, Class<? extends AbstractByteBufAllocator> allocatorType) {
      this.allocatorType = allocatorType;
      this.size = buf.capacity();
      this.maxFastCapacity = buf.maxFastWritableBytes() + buf.writerIndex();
      this.maxCapacity = buf.maxCapacity();
      this.direct = buf.isDirect();
      this.address = buf._memoryAddress();
   }
}
