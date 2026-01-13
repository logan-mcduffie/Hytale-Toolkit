package io.netty.channel.unix;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelOutboundBuffer;
import io.netty.util.internal.CleanableDirectBuffer;
import io.netty.util.internal.ObjectUtil;
import io.netty.util.internal.PlatformDependent;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public final class IovArray implements ChannelOutboundBuffer.MessageProcessor {
   private static final int ADDRESS_SIZE = Buffer.addressSize();
   public static final int IOV_SIZE = 2 * ADDRESS_SIZE;
   private static final int MAX_CAPACITY = Limits.IOV_MAX * IOV_SIZE;
   private final long memoryAddress;
   private final ByteBuf memory;
   private final CleanableDirectBuffer cleanable;
   private int count;
   private long size;
   private long maxBytes = Limits.SSIZE_MAX;
   private int maxCount;

   @Deprecated
   public IovArray() {
      this(Limits.IOV_MAX);
   }

   public IovArray(int numEntries) {
      int sizeBytes = Math.multiplyExact(ObjectUtil.checkPositive(numEntries, "numEntries"), IOV_SIZE);
      this.cleanable = Buffer.allocateDirectBufferWithNativeOrder(sizeBytes);
      ByteBuf bbuf = Unpooled.wrappedBuffer(this.cleanable.buffer()).setIndex(0, 0);
      this.memory = PlatformDependent.hasUnsafe()
         ? bbuf
         : bbuf.order(PlatformDependent.BIG_ENDIAN_NATIVE_ORDER ? ByteOrder.BIG_ENDIAN : ByteOrder.LITTLE_ENDIAN);
      if (this.memory.hasMemoryAddress()) {
         this.memoryAddress = this.memory.memoryAddress();
      } else {
         ByteBuffer byteBuffer = this.memory.internalNioBuffer(0, this.memory.capacity());
         this.memoryAddress = Buffer.memoryAddress(byteBuffer) + byteBuffer.position();
      }

      this.maxCount = Limits.IOV_MAX;
   }

   @Deprecated
   public IovArray(ByteBuf memory) {
      assert memory.writerIndex() == 0;

      assert memory.readerIndex() == 0;

      this.memory = PlatformDependent.hasUnsafe()
         ? memory
         : memory.order(PlatformDependent.BIG_ENDIAN_NATIVE_ORDER ? ByteOrder.BIG_ENDIAN : ByteOrder.LITTLE_ENDIAN);
      if (memory.hasMemoryAddress()) {
         this.memoryAddress = memory.memoryAddress();
      } else {
         ByteBuffer byteBuffer = memory.internalNioBuffer(0, memory.capacity());
         this.memoryAddress = Buffer.memoryAddress(byteBuffer) + byteBuffer.position();
      }

      this.cleanable = null;
      this.maxCount = Limits.IOV_MAX;
   }

   public void clear() {
      this.count = 0;
      this.size = 0L;
      this.maxCount = Limits.IOV_MAX;
   }

   @Deprecated
   public boolean add(ByteBuf buf) {
      return this.add(buf, buf.readerIndex(), buf.readableBytes());
   }

   public boolean add(ByteBuf buf, int offset, int len) {
      if (this.count == this.maxCount) {
         return false;
      } else if (buf.nioBufferCount() == 1) {
         if (len == 0) {
            return true;
         } else if (buf.hasMemoryAddress()) {
            return this.add(this.memoryAddress, buf.memoryAddress() + offset, len);
         } else {
            ByteBuffer nioBuffer = buf.internalNioBuffer(offset, len);
            return this.add(this.memoryAddress, Buffer.memoryAddress(nioBuffer) + nioBuffer.position(), len);
         }
      } else {
         ByteBuffer[] buffers = buf.nioBuffers(offset, len);

         for (ByteBuffer nioBuffer : buffers) {
            int remaining = nioBuffer.remaining();
            if (remaining != 0
               && (!this.add(this.memoryAddress, Buffer.memoryAddress(nioBuffer) + nioBuffer.position(), remaining) || this.count == Limits.IOV_MAX)) {
               return false;
            }
         }

         return true;
      }
   }

   public boolean isFull() {
      return this.memory.capacity() < (this.count + 1) * IOV_SIZE || this.size >= this.maxBytes;
   }

   private boolean add(long memoryAddress, long addr, int len) {
      assert addr != 0L;

      if ((this.maxBytes - len >= this.size || this.count <= 0) && this.memory.capacity() >= (this.count + 1) * IOV_SIZE) {
         int baseOffset = idx(this.count);
         int lengthOffset = baseOffset + ADDRESS_SIZE;
         this.size += len;
         this.count++;
         if (ADDRESS_SIZE == 8) {
            if (PlatformDependent.hasUnsafe()) {
               PlatformDependent.putLong(baseOffset + memoryAddress, addr);
               PlatformDependent.putLong(lengthOffset + memoryAddress, len);
            } else {
               this.memory.setLong(baseOffset, addr);
               this.memory.setLong(lengthOffset, len);
            }
         } else {
            assert ADDRESS_SIZE == 4;

            if (PlatformDependent.hasUnsafe()) {
               PlatformDependent.putInt(baseOffset + memoryAddress, (int)addr);
               PlatformDependent.putInt(lengthOffset + memoryAddress, len);
            } else {
               this.memory.setInt(baseOffset, (int)addr);
               this.memory.setInt(lengthOffset, len);
            }
         }

         return true;
      } else {
         return false;
      }
   }

   public int count() {
      return this.count;
   }

   public long size() {
      return this.size;
   }

   public void maxBytes(long maxBytes) {
      this.maxBytes = Math.min(Limits.SSIZE_MAX, ObjectUtil.checkPositive(maxBytes, "maxBytes"));
   }

   public void maxCount(int maxCount) {
      this.maxCount = Math.min(Limits.IOV_MAX, ObjectUtil.checkPositive(maxCount, "maxCount"));
   }

   public long maxBytes() {
      return this.maxBytes;
   }

   public int maxCount() {
      return this.maxCount;
   }

   public long memoryAddress(int offset) {
      return this.memoryAddress + idx(offset);
   }

   public void release() {
      this.memory.release();
      if (this.cleanable != null) {
         this.cleanable.clean();
      }
   }

   @Override
   public boolean processMessage(Object msg) throws Exception {
      if (msg instanceof ByteBuf) {
         ByteBuf buffer = (ByteBuf)msg;
         return this.add(buffer, buffer.readerIndex(), buffer.readableBytes());
      } else {
         return false;
      }
   }

   private static int idx(int index) {
      return IOV_SIZE * index;
   }
}
