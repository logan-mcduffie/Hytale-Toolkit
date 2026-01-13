package io.netty.buffer;

import io.netty.util.ByteProcessor;
import io.netty.util.IllegalReferenceCountException;
import io.netty.util.ReferenceCountUtil;
import io.netty.util.internal.EmptyArrays;
import io.netty.util.internal.ObjectUtil;
import io.netty.util.internal.RecyclableArrayList;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.channels.FileChannel;
import java.nio.channels.GatheringByteChannel;
import java.nio.channels.ScatteringByteChannel;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

public class CompositeByteBuf extends AbstractReferenceCountedByteBuf implements Iterable<ByteBuf> {
   private static final ByteBuffer EMPTY_NIO_BUFFER = Unpooled.EMPTY_BUFFER.nioBuffer();
   private static final Iterator<ByteBuf> EMPTY_ITERATOR = Collections.<ByteBuf>emptyList().iterator();
   private final ByteBufAllocator alloc;
   private final boolean direct;
   private final int maxNumComponents;
   private int componentCount;
   private CompositeByteBuf.Component[] components;
   private boolean freed;
   static final CompositeByteBuf.ByteWrapper<byte[]> BYTE_ARRAY_WRAPPER = new CompositeByteBuf.ByteWrapper<byte[]>() {
      public ByteBuf wrap(byte[] bytes) {
         return Unpooled.wrappedBuffer(bytes);
      }

      public boolean isEmpty(byte[] bytes) {
         return bytes.length == 0;
      }
   };
   static final CompositeByteBuf.ByteWrapper<ByteBuffer> BYTE_BUFFER_WRAPPER = new CompositeByteBuf.ByteWrapper<ByteBuffer>() {
      public ByteBuf wrap(ByteBuffer bytes) {
         return Unpooled.wrappedBuffer(bytes);
      }

      public boolean isEmpty(ByteBuffer bytes) {
         return !bytes.hasRemaining();
      }
   };
   private CompositeByteBuf.Component lastAccessed;

   private CompositeByteBuf(ByteBufAllocator alloc, boolean direct, int maxNumComponents, int initSize) {
      super(Integer.MAX_VALUE);
      this.alloc = ObjectUtil.checkNotNull(alloc, "alloc");
      if (maxNumComponents < 1) {
         throw new IllegalArgumentException("maxNumComponents: " + maxNumComponents + " (expected: >= 1)");
      } else {
         this.direct = direct;
         this.maxNumComponents = maxNumComponents;
         this.components = newCompArray(initSize, maxNumComponents);
      }
   }

   public CompositeByteBuf(ByteBufAllocator alloc, boolean direct, int maxNumComponents) {
      this(alloc, direct, maxNumComponents, 0);
   }

   public CompositeByteBuf(ByteBufAllocator alloc, boolean direct, int maxNumComponents, ByteBuf... buffers) {
      this(alloc, direct, maxNumComponents, buffers, 0);
   }

   CompositeByteBuf(ByteBufAllocator alloc, boolean direct, int maxNumComponents, ByteBuf[] buffers, int offset) {
      this(alloc, direct, maxNumComponents, buffers.length - offset);
      this.addComponents0(false, 0, buffers, offset);
      this.consolidateIfNeeded();
      this.setIndex0(0, this.capacity());
   }

   public CompositeByteBuf(ByteBufAllocator alloc, boolean direct, int maxNumComponents, Iterable<ByteBuf> buffers) {
      this(alloc, direct, maxNumComponents, buffers instanceof Collection ? ((Collection)buffers).size() : 0);
      this.addComponents(false, 0, buffers);
      this.setIndex(0, this.capacity());
   }

   <T> CompositeByteBuf(ByteBufAllocator alloc, boolean direct, int maxNumComponents, CompositeByteBuf.ByteWrapper<T> wrapper, T[] buffers, int offset) {
      this(alloc, direct, maxNumComponents, buffers.length - offset);
      this.addComponents0(false, 0, wrapper, buffers, offset);
      this.consolidateIfNeeded();
      this.setIndex(0, this.capacity());
   }

   private static CompositeByteBuf.Component[] newCompArray(int initComponents, int maxNumComponents) {
      int capacityGuess = Math.min(16, maxNumComponents);
      return new CompositeByteBuf.Component[Math.max(initComponents, capacityGuess)];
   }

   CompositeByteBuf(ByteBufAllocator alloc) {
      super(Integer.MAX_VALUE);
      this.alloc = alloc;
      this.direct = false;
      this.maxNumComponents = 0;
      this.components = null;
   }

   public CompositeByteBuf addComponent(ByteBuf buffer) {
      return this.addComponent(false, buffer);
   }

   public CompositeByteBuf addComponents(ByteBuf... buffers) {
      return this.addComponents(false, buffers);
   }

   public CompositeByteBuf addComponents(Iterable<ByteBuf> buffers) {
      return this.addComponents(false, buffers);
   }

   public CompositeByteBuf addComponent(int cIndex, ByteBuf buffer) {
      return this.addComponent(false, cIndex, buffer);
   }

   public CompositeByteBuf addComponent(boolean increaseWriterIndex, ByteBuf buffer) {
      return this.addComponent(increaseWriterIndex, this.componentCount, buffer);
   }

   public CompositeByteBuf addComponents(boolean increaseWriterIndex, ByteBuf... buffers) {
      ObjectUtil.checkNotNull(buffers, "buffers");
      this.addComponents0(increaseWriterIndex, this.componentCount, buffers, 0);
      this.consolidateIfNeeded();
      return this;
   }

   public CompositeByteBuf addComponents(boolean increaseWriterIndex, Iterable<ByteBuf> buffers) {
      return this.addComponents(increaseWriterIndex, this.componentCount, buffers);
   }

   public CompositeByteBuf addComponent(boolean increaseWriterIndex, int cIndex, ByteBuf buffer) {
      ObjectUtil.checkNotNull(buffer, "buffer");
      this.addComponent0(increaseWriterIndex, cIndex, buffer);
      this.consolidateIfNeeded();
      return this;
   }

   private static void checkForOverflow(int capacity, int readableBytes) {
      if (capacity + readableBytes < 0) {
         throw new IllegalArgumentException("Can't increase by " + readableBytes + " as capacity(" + capacity + ") would overflow " + Integer.MAX_VALUE);
      }
   }

   private int addComponent0(boolean increaseWriterIndex, int cIndex, ByteBuf buffer) {
      assert buffer != null;

      boolean wasAdded = false;

      int var7;
      try {
         this.checkComponentIndex(cIndex);
         CompositeByteBuf.Component c = this.newComponent(ensureAccessible(buffer), 0);
         int readableBytes = c.length();
         checkForOverflow(this.capacity(), readableBytes);
         this.addComp(cIndex, c);
         wasAdded = true;
         if (readableBytes > 0 && cIndex < this.componentCount - 1) {
            this.updateComponentOffsets(cIndex);
         } else if (cIndex > 0) {
            c.reposition(this.components[cIndex - 1].endOffset);
         }

         if (increaseWriterIndex) {
            this.writerIndex += readableBytes;
         }

         var7 = cIndex;
      } finally {
         if (!wasAdded) {
            buffer.release();
         }
      }

      return var7;
   }

   private static ByteBuf ensureAccessible(ByteBuf buf) {
      if (checkAccessible && !buf.isAccessible()) {
         throw new IllegalReferenceCountException(0);
      } else {
         return buf;
      }
   }

   private CompositeByteBuf.Component newComponent(ByteBuf buf, int offset) {
      int srcIndex = buf.readerIndex();
      int len = buf.readableBytes();
      ByteBuf unwrapped = buf;
      int unwrappedIndex = srcIndex;

      while (unwrapped instanceof WrappedByteBuf || unwrapped instanceof SwappedByteBuf) {
         unwrapped = unwrapped.unwrap();
      }

      if (unwrapped instanceof AbstractUnpooledSlicedByteBuf) {
         unwrappedIndex = srcIndex + ((AbstractUnpooledSlicedByteBuf)unwrapped).idx(0);
         unwrapped = unwrapped.unwrap();
      } else if (unwrapped instanceof PooledSlicedByteBuf) {
         unwrappedIndex = srcIndex + ((PooledSlicedByteBuf)unwrapped).adjustment;
         unwrapped = unwrapped.unwrap();
      } else if (unwrapped instanceof DuplicatedByteBuf || unwrapped instanceof PooledDuplicatedByteBuf) {
         unwrapped = unwrapped.unwrap();
      }

      ByteBuf slice = buf.capacity() == len ? buf : null;
      return new CompositeByteBuf.Component(
         buf.order(ByteOrder.BIG_ENDIAN), srcIndex, unwrapped.order(ByteOrder.BIG_ENDIAN), unwrappedIndex, offset, len, slice
      );
   }

   public CompositeByteBuf addComponents(int cIndex, ByteBuf... buffers) {
      ObjectUtil.checkNotNull(buffers, "buffers");
      this.addComponents0(false, cIndex, buffers, 0);
      this.consolidateIfNeeded();
      return this;
   }

   private CompositeByteBuf addComponents0(boolean increaseWriterIndex, int cIndex, ByteBuf[] buffers, int arrOffset) {
      int len = buffers.length;
      int count = len - arrOffset;
      int readableBytes = 0;
      int capacity = this.capacity();

      for (int i = arrOffset; i < buffers.length; i++) {
         ByteBuf b = buffers[i];
         if (b == null) {
            break;
         }

         readableBytes += b.readableBytes();
         checkForOverflow(capacity, readableBytes);
      }

      int ci = Integer.MAX_VALUE;

      CompositeByteBuf var18;
      try {
         this.checkComponentIndex(cIndex);
         this.shiftComps(cIndex, count);
         int nextOffset = cIndex > 0 ? this.components[cIndex - 1].endOffset : 0;

         for (ci = cIndex; arrOffset < len; ci++) {
            ByteBuf b = buffers[arrOffset];
            if (b == null) {
               break;
            }

            CompositeByteBuf.Component c = this.newComponent(ensureAccessible(b), nextOffset);
            this.components[ci] = c;
            nextOffset = c.endOffset;
            arrOffset++;
         }

         var18 = this;
      } finally {
         if (ci < this.componentCount) {
            if (ci < cIndex + count) {
               this.removeCompRange(ci, cIndex + count);

               while (arrOffset < len) {
                  ReferenceCountUtil.safeRelease(buffers[arrOffset]);
                  arrOffset++;
               }
            }

            this.updateComponentOffsets(ci);
         }

         if (increaseWriterIndex && ci > cIndex && ci <= this.componentCount) {
            this.writerIndex = this.writerIndex + (this.components[ci - 1].endOffset - this.components[cIndex].offset);
         }
      }

      return var18;
   }

   private <T> int addComponents0(boolean increaseWriterIndex, int cIndex, CompositeByteBuf.ByteWrapper<T> wrapper, T[] buffers, int offset) {
      this.checkComponentIndex(cIndex);
      int i = offset;

      for (int len = buffers.length; i < len; i++) {
         T b = buffers[i];
         if (b == null) {
            break;
         }

         if (!wrapper.isEmpty(b)) {
            cIndex = this.addComponent0(increaseWriterIndex, cIndex, wrapper.wrap(b)) + 1;
            int size = this.componentCount;
            if (cIndex > size) {
               cIndex = size;
            }
         }
      }

      return cIndex;
   }

   public CompositeByteBuf addComponents(int cIndex, Iterable<ByteBuf> buffers) {
      return this.addComponents(false, cIndex, buffers);
   }

   // $VF: Could not verify finally blocks. A semaphore variable has been added to preserve control flow.
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   public CompositeByteBuf addFlattenedComponents(boolean increaseWriterIndex, ByteBuf buffer) {
      ObjectUtil.checkNotNull(buffer, "buffer");
      int ridx = buffer.readerIndex();
      int widx = buffer.writerIndex();
      if (ridx == widx) {
         buffer.release();
         return this;
      } else if (!(buffer instanceof CompositeByteBuf)) {
         this.addComponent0(increaseWriterIndex, this.componentCount, buffer);
         this.consolidateIfNeeded();
         return this;
      } else {
         CompositeByteBuf from;
         if (buffer instanceof WrappedCompositeByteBuf) {
            from = (CompositeByteBuf)buffer.unwrap();
         } else {
            from = (CompositeByteBuf)buffer;
         }

         from.checkIndex(ridx, widx - ridx);
         CompositeByteBuf.Component[] fromComponents = from.components;
         int compCountBefore = this.componentCount;
         int writerIndexBefore = this.writerIndex;
         boolean var19 = false /* VF: Semaphore variable */;

         CompositeByteBuf var21;
         try {
            var19 = true;
            int cidx = from.toComponentIndex0(ridx);
            int cidxx = this.capacity();

            while (true) {
               CompositeByteBuf.Component component = fromComponents[cidx];
               int compOffset = component.offset;
               int fromIdx = Math.max(ridx, compOffset);
               int toIdx = Math.min(widx, component.endOffset);
               int len = toIdx - fromIdx;
               if (len > 0) {
                  this.addComp(
                     this.componentCount,
                     new CompositeByteBuf.Component(
                        component.srcBuf.retain(), component.srcIdx(fromIdx), component.buf, component.idx(fromIdx), cidxx, len, null
                     )
                  );
               }

               if (widx == toIdx) {
                  if (increaseWriterIndex) {
                     this.writerIndex = writerIndexBefore + (widx - ridx);
                  }

                  this.consolidateIfNeeded();
                  buffer.release();
                  buffer = null;
                  var21 = this;
                  var19 = false;
                  break;
               }

               cidxx += len;
               cidx++;
            }
         } finally {
            if (var19) {
               if (buffer != null) {
                  if (increaseWriterIndex) {
                     this.writerIndex = writerIndexBefore;
                  }

                  for (int cidx = this.componentCount - 1; cidx >= compCountBefore; cidx--) {
                     this.components[cidx].free();
                     this.removeComp(cidx);
                  }
               }
            }
         }

         if (buffer != null) {
            if (increaseWriterIndex) {
               this.writerIndex = writerIndexBefore;
            }

            for (int cidx = this.componentCount - 1; cidx >= compCountBefore; cidx--) {
               this.components[cidx].free();
               this.removeComp(cidx);
            }
         }

         return var21;
      }
   }

   private CompositeByteBuf addComponents(boolean increaseIndex, int cIndex, Iterable<ByteBuf> buffers) {
      if (buffers instanceof ByteBuf) {
         return this.addComponent(increaseIndex, cIndex, (ByteBuf)buffers);
      } else {
         ObjectUtil.checkNotNull(buffers, "buffers");
         Iterator<ByteBuf> it = buffers.iterator();

         try {
            this.checkComponentIndex(cIndex);

            while (it.hasNext()) {
               ByteBuf b = it.next();
               if (b == null) {
                  break;
               }

               cIndex = this.addComponent0(increaseIndex, cIndex, b) + 1;
               cIndex = Math.min(cIndex, this.componentCount);
            }
         } finally {
            while (it.hasNext()) {
               ReferenceCountUtil.safeRelease(it.next());
            }
         }

         this.consolidateIfNeeded();
         return this;
      }
   }

   private void consolidateIfNeeded() {
      int size = this.componentCount;
      if (size > this.maxNumComponents) {
         this.consolidate0(0, size);
      }
   }

   private void checkComponentIndex(int cIndex) {
      this.ensureAccessible();
      if (cIndex < 0 || cIndex > this.componentCount) {
         throw new IndexOutOfBoundsException(String.format("cIndex: %d (expected: >= 0 && <= numComponents(%d))", cIndex, this.componentCount));
      }
   }

   private void checkComponentIndex(int cIndex, int numComponents) {
      this.ensureAccessible();
      if (cIndex < 0 || cIndex + numComponents > this.componentCount) {
         throw new IndexOutOfBoundsException(
            String.format(
               "cIndex: %d, numComponents: %d (expected: cIndex >= 0 && cIndex + numComponents <= totalNumComponents(%d))",
               cIndex,
               numComponents,
               this.componentCount
            )
         );
      }
   }

   private void updateComponentOffsets(int cIndex) {
      int size = this.componentCount;
      if (size > cIndex) {
         for (int nextIndex = cIndex > 0 ? this.components[cIndex - 1].endOffset : 0; cIndex < size; cIndex++) {
            CompositeByteBuf.Component c = this.components[cIndex];
            c.reposition(nextIndex);
            nextIndex = c.endOffset;
         }
      }
   }

   public CompositeByteBuf removeComponent(int cIndex) {
      this.checkComponentIndex(cIndex);
      CompositeByteBuf.Component comp = this.components[cIndex];
      if (this.lastAccessed == comp) {
         this.lastAccessed = null;
      }

      comp.free();
      this.removeComp(cIndex);
      if (comp.length() > 0) {
         this.updateComponentOffsets(cIndex);
      }

      return this;
   }

   public CompositeByteBuf removeComponents(int cIndex, int numComponents) {
      this.checkComponentIndex(cIndex, numComponents);
      if (numComponents == 0) {
         return this;
      } else {
         int endIndex = cIndex + numComponents;
         boolean needsUpdate = false;

         for (int i = cIndex; i < endIndex; i++) {
            CompositeByteBuf.Component c = this.components[i];
            if (c.length() > 0) {
               needsUpdate = true;
            }

            if (this.lastAccessed == c) {
               this.lastAccessed = null;
            }

            c.free();
         }

         this.removeCompRange(cIndex, endIndex);
         if (needsUpdate) {
            this.updateComponentOffsets(cIndex);
         }

         return this;
      }
   }

   @Override
   public Iterator<ByteBuf> iterator() {
      this.ensureAccessible();
      return (Iterator<ByteBuf>)(this.componentCount == 0 ? EMPTY_ITERATOR : new CompositeByteBuf.CompositeByteBufIterator());
   }

   @Override
   protected int forEachByteAsc0(int start, int end, ByteProcessor processor) throws Exception {
      if (end <= start) {
         return -1;
      } else {
         int i = this.toComponentIndex0(start);

         for (int length = end - start; length > 0; i++) {
            CompositeByteBuf.Component c = this.components[i];
            if (c.offset != c.endOffset) {
               ByteBuf s = c.buf;
               int localStart = c.idx(start);
               int localLength = Math.min(length, c.endOffset - start);
               int result = s instanceof AbstractByteBuf
                  ? ((AbstractByteBuf)s).forEachByteAsc0(localStart, localStart + localLength, processor)
                  : s.forEachByte(localStart, localLength, processor);
               if (result != -1) {
                  return result - c.adjustment;
               }

               start += localLength;
               length -= localLength;
            }
         }

         return -1;
      }
   }

   @Override
   protected int forEachByteDesc0(int rStart, int rEnd, ByteProcessor processor) throws Exception {
      if (rEnd > rStart) {
         return -1;
      } else {
         int i = this.toComponentIndex0(rStart);

         for (int length = 1 + rStart - rEnd; length > 0; i--) {
            CompositeByteBuf.Component c = this.components[i];
            if (c.offset != c.endOffset) {
               ByteBuf s = c.buf;
               int localRStart = c.idx(length + rEnd);
               int localLength = Math.min(length, localRStart);
               int localIndex = localRStart - localLength;
               int result = s instanceof AbstractByteBuf
                  ? ((AbstractByteBuf)s).forEachByteDesc0(localRStart - 1, localIndex, processor)
                  : s.forEachByteDesc(localIndex, localLength, processor);
               if (result != -1) {
                  return result - c.adjustment;
               }

               length -= localLength;
            }
         }

         return -1;
      }
   }

   public List<ByteBuf> decompose(int offset, int length) {
      this.checkIndex(offset, length);
      if (length == 0) {
         return Collections.emptyList();
      } else {
         int componentId = this.toComponentIndex0(offset);
         CompositeByteBuf.Component firstC = this.components[componentId];
         ByteBuf slice = firstC.srcBuf.slice(firstC.srcIdx(offset), Math.min(firstC.endOffset - offset, length));
         int bytesToSlice = length - slice.readableBytes();
         if (bytesToSlice == 0) {
            return Collections.singletonList(slice);
         } else {
            List<ByteBuf> sliceList = new ArrayList<>(this.componentCount - componentId);
            sliceList.add(slice);

            do {
               CompositeByteBuf.Component component = this.components[++componentId];
               slice = component.srcBuf.slice(component.srcIdx(component.offset), Math.min(component.length(), bytesToSlice));
               bytesToSlice -= slice.readableBytes();
               sliceList.add(slice);
            } while (bytesToSlice > 0);

            return sliceList;
         }
      }
   }

   @Override
   public boolean isDirect() {
      int size = this.componentCount;
      if (size == 0) {
         return false;
      } else {
         for (int i = 0; i < size; i++) {
            if (!this.components[i].buf.isDirect()) {
               return false;
            }
         }

         return true;
      }
   }

   @Override
   public boolean hasArray() {
      switch (this.componentCount) {
         case 0:
            return true;
         case 1:
            return this.components[0].buf.hasArray();
         default:
            return false;
      }
   }

   @Override
   public byte[] array() {
      switch (this.componentCount) {
         case 0:
            return EmptyArrays.EMPTY_BYTES;
         case 1:
            return this.components[0].buf.array();
         default:
            throw new UnsupportedOperationException();
      }
   }

   @Override
   public int arrayOffset() {
      switch (this.componentCount) {
         case 0:
            return 0;
         case 1:
            CompositeByteBuf.Component c = this.components[0];
            return c.idx(c.buf.arrayOffset());
         default:
            throw new UnsupportedOperationException();
      }
   }

   @Override
   public boolean hasMemoryAddress() {
      switch (this.componentCount) {
         case 0:
            return Unpooled.EMPTY_BUFFER.hasMemoryAddress();
         case 1:
            return this.components[0].buf.hasMemoryAddress();
         default:
            return false;
      }
   }

   @Override
   public long memoryAddress() {
      switch (this.componentCount) {
         case 0:
            return Unpooled.EMPTY_BUFFER.memoryAddress();
         case 1:
            CompositeByteBuf.Component c = this.components[0];
            return c.buf.memoryAddress() + c.adjustment;
         default:
            throw new UnsupportedOperationException();
      }
   }

   @Override
   public int capacity() {
      int size = this.componentCount;
      return size > 0 ? this.components[size - 1].endOffset : 0;
   }

   public CompositeByteBuf capacity(int newCapacity) {
      this.checkNewCapacity(newCapacity);
      int size = this.componentCount;
      int oldCapacity = this.capacity();
      if (newCapacity > oldCapacity) {
         int paddingLength = newCapacity - oldCapacity;
         ByteBuf padding = this.allocBuffer(paddingLength).setIndex(0, paddingLength);
         this.addComponent0(false, size, padding);
         if (this.componentCount >= this.maxNumComponents) {
            this.consolidateIfNeeded();
         }
      } else if (newCapacity < oldCapacity) {
         this.lastAccessed = null;
         int i = size - 1;

         for (int bytesToTrim = oldCapacity - newCapacity; i >= 0; i--) {
            CompositeByteBuf.Component c = this.components[i];
            int cLength = c.length();
            if (bytesToTrim < cLength) {
               c.endOffset -= bytesToTrim;
               ByteBuf slice = c.slice;
               if (slice != null) {
                  c.slice = slice.slice(0, c.length());
               }
               break;
            }

            c.free();
            bytesToTrim -= cLength;
         }

         this.removeCompRange(i + 1, size);
         if (this.readerIndex() > newCapacity) {
            this.setIndex0(newCapacity, newCapacity);
         } else if (this.writerIndex > newCapacity) {
            this.writerIndex = newCapacity;
         }
      }

      return this;
   }

   @Override
   public ByteBufAllocator alloc() {
      return this.alloc;
   }

   @Override
   public ByteOrder order() {
      return ByteOrder.BIG_ENDIAN;
   }

   public int numComponents() {
      return this.componentCount;
   }

   public int maxNumComponents() {
      return this.maxNumComponents;
   }

   public int toComponentIndex(int offset) {
      this.checkIndex(offset);
      return this.toComponentIndex0(offset);
   }

   private int toComponentIndex0(int offset) {
      int size = this.componentCount;
      if (offset == 0) {
         for (int i = 0; i < size; i++) {
            if (this.components[i].endOffset > 0) {
               return i;
            }
         }
      }

      if (size > 2) {
         int low = 0;
         int high = size;

         while (low <= high) {
            int mid = low + high >>> 1;
            CompositeByteBuf.Component c = this.components[mid];
            if (offset >= c.endOffset) {
               low = mid + 1;
            } else {
               if (offset >= c.offset) {
                  return mid;
               }

               high = mid - 1;
            }
         }

         throw new Error("should not reach here");
      } else {
         return size != 1 && offset >= this.components[0].endOffset ? 1 : 0;
      }
   }

   public int toByteIndex(int cIndex) {
      this.checkComponentIndex(cIndex);
      return this.components[cIndex].offset;
   }

   @Override
   public byte getByte(int index) {
      CompositeByteBuf.Component c = this.findComponent(index);
      return c.buf.getByte(c.idx(index));
   }

   @Override
   protected byte _getByte(int index) {
      CompositeByteBuf.Component c = this.findComponent0(index);
      return c.buf.getByte(c.idx(index));
   }

   @Override
   protected short _getShort(int index) {
      CompositeByteBuf.Component c = this.findComponent0(index);
      if (index + 2 <= c.endOffset) {
         return c.buf.getShort(c.idx(index));
      } else {
         return this.order() == ByteOrder.BIG_ENDIAN
            ? (short)((this._getByte(index) & 255) << 8 | this._getByte(index + 1) & 255)
            : (short)(this._getByte(index) & 255 | (this._getByte(index + 1) & 255) << 8);
      }
   }

   @Override
   protected short _getShortLE(int index) {
      CompositeByteBuf.Component c = this.findComponent0(index);
      if (index + 2 <= c.endOffset) {
         return c.buf.getShortLE(c.idx(index));
      } else {
         return this.order() == ByteOrder.BIG_ENDIAN
            ? (short)(this._getByte(index) & 255 | (this._getByte(index + 1) & 255) << 8)
            : (short)((this._getByte(index) & 255) << 8 | this._getByte(index + 1) & 255);
      }
   }

   @Override
   protected int _getUnsignedMedium(int index) {
      CompositeByteBuf.Component c = this.findComponent0(index);
      if (index + 3 <= c.endOffset) {
         return c.buf.getUnsignedMedium(c.idx(index));
      } else {
         return this.order() == ByteOrder.BIG_ENDIAN
            ? (this._getShort(index) & 65535) << 8 | this._getByte(index + 2) & 0xFF
            : this._getShort(index) & 65535 | (this._getByte(index + 2) & 0xFF) << 16;
      }
   }

   @Override
   protected int _getUnsignedMediumLE(int index) {
      CompositeByteBuf.Component c = this.findComponent0(index);
      if (index + 3 <= c.endOffset) {
         return c.buf.getUnsignedMediumLE(c.idx(index));
      } else {
         return this.order() == ByteOrder.BIG_ENDIAN
            ? this._getShortLE(index) & 65535 | (this._getByte(index + 2) & 0xFF) << 16
            : (this._getShortLE(index) & 65535) << 8 | this._getByte(index + 2) & 0xFF;
      }
   }

   @Override
   protected int _getInt(int index) {
      CompositeByteBuf.Component c = this.findComponent0(index);
      if (index + 4 <= c.endOffset) {
         return c.buf.getInt(c.idx(index));
      } else {
         return this.order() == ByteOrder.BIG_ENDIAN
            ? (this._getShort(index) & 65535) << 16 | this._getShort(index + 2) & 65535
            : this._getShort(index) & 65535 | (this._getShort(index + 2) & 65535) << 16;
      }
   }

   @Override
   protected int _getIntLE(int index) {
      CompositeByteBuf.Component c = this.findComponent0(index);
      if (index + 4 <= c.endOffset) {
         return c.buf.getIntLE(c.idx(index));
      } else {
         return this.order() == ByteOrder.BIG_ENDIAN
            ? this._getShortLE(index) & 65535 | (this._getShortLE(index + 2) & 65535) << 16
            : (this._getShortLE(index) & 65535) << 16 | this._getShortLE(index + 2) & 65535;
      }
   }

   @Override
   protected long _getLong(int index) {
      CompositeByteBuf.Component c = this.findComponent0(index);
      if (index + 8 <= c.endOffset) {
         return c.buf.getLong(c.idx(index));
      } else {
         return this.order() == ByteOrder.BIG_ENDIAN
            ? (this._getInt(index) & 4294967295L) << 32 | this._getInt(index + 4) & 4294967295L
            : this._getInt(index) & 4294967295L | (this._getInt(index + 4) & 4294967295L) << 32;
      }
   }

   @Override
   protected long _getLongLE(int index) {
      CompositeByteBuf.Component c = this.findComponent0(index);
      if (index + 8 <= c.endOffset) {
         return c.buf.getLongLE(c.idx(index));
      } else {
         return this.order() == ByteOrder.BIG_ENDIAN
            ? this._getIntLE(index) & 4294967295L | (this._getIntLE(index + 4) & 4294967295L) << 32
            : (this._getIntLE(index) & 4294967295L) << 32 | this._getIntLE(index + 4) & 4294967295L;
      }
   }

   public CompositeByteBuf getBytes(int index, byte[] dst, int dstIndex, int length) {
      this.checkDstIndex(index, length, dstIndex, dst.length);
      if (length == 0) {
         return this;
      } else {
         for (int i = this.toComponentIndex0(index); length > 0; i++) {
            CompositeByteBuf.Component c = this.components[i];
            int localLength = Math.min(length, c.endOffset - index);
            c.buf.getBytes(c.idx(index), dst, dstIndex, localLength);
            index += localLength;
            dstIndex += localLength;
            length -= localLength;
         }

         return this;
      }
   }

   public CompositeByteBuf getBytes(int index, ByteBuffer dst) {
      int limit = dst.limit();
      int length = dst.remaining();
      this.checkIndex(index, length);
      if (length == 0) {
         return this;
      } else {
         int i = this.toComponentIndex0(index);

         try {
            while (length > 0) {
               CompositeByteBuf.Component c = this.components[i];
               int localLength = Math.min(length, c.endOffset - index);
               ((Buffer)dst).limit(dst.position() + localLength);
               c.buf.getBytes(c.idx(index), dst);
               index += localLength;
               length -= localLength;
               i++;
            }
         } finally {
            ((Buffer)dst).limit(limit);
         }

         return this;
      }
   }

   public CompositeByteBuf getBytes(int index, ByteBuf dst, int dstIndex, int length) {
      this.checkDstIndex(index, length, dstIndex, dst.capacity());
      if (length == 0) {
         return this;
      } else {
         for (int i = this.toComponentIndex0(index); length > 0; i++) {
            CompositeByteBuf.Component c = this.components[i];
            int localLength = Math.min(length, c.endOffset - index);
            c.buf.getBytes(c.idx(index), dst, dstIndex, localLength);
            index += localLength;
            dstIndex += localLength;
            length -= localLength;
         }

         return this;
      }
   }

   @Override
   public int getBytes(int index, GatheringByteChannel out, int length) throws IOException {
      int count = this.nioBufferCount();
      if (count == 1) {
         return out.write(this.internalNioBuffer(index, length));
      } else {
         long writtenBytes = out.write(this.nioBuffers(index, length));
         return writtenBytes > 2147483647L ? Integer.MAX_VALUE : (int)writtenBytes;
      }
   }

   @Override
   public int getBytes(int index, FileChannel out, long position, int length) throws IOException {
      int count = this.nioBufferCount();
      if (count == 1) {
         return out.write(this.internalNioBuffer(index, length), position);
      } else {
         long writtenBytes = 0L;

         for (ByteBuffer buf : this.nioBuffers(index, length)) {
            writtenBytes += out.write(buf, position + writtenBytes);
         }

         return writtenBytes > 2147483647L ? Integer.MAX_VALUE : (int)writtenBytes;
      }
   }

   public CompositeByteBuf getBytes(int index, OutputStream out, int length) throws IOException {
      this.checkIndex(index, length);
      if (length == 0) {
         return this;
      } else {
         for (int i = this.toComponentIndex0(index); length > 0; i++) {
            CompositeByteBuf.Component c = this.components[i];
            int localLength = Math.min(length, c.endOffset - index);
            c.buf.getBytes(c.idx(index), out, localLength);
            index += localLength;
            length -= localLength;
         }

         return this;
      }
   }

   public CompositeByteBuf setByte(int index, int value) {
      CompositeByteBuf.Component c = this.findComponent(index);
      c.buf.setByte(c.idx(index), value);
      return this;
   }

   @Override
   protected void _setByte(int index, int value) {
      CompositeByteBuf.Component c = this.findComponent0(index);
      c.buf.setByte(c.idx(index), value);
   }

   public CompositeByteBuf setShort(int index, int value) {
      this.checkIndex(index, 2);
      this._setShort(index, value);
      return this;
   }

   @Override
   protected void _setShort(int index, int value) {
      CompositeByteBuf.Component c = this.findComponent0(index);
      if (index + 2 <= c.endOffset) {
         c.buf.setShort(c.idx(index), value);
      } else if (this.order() == ByteOrder.BIG_ENDIAN) {
         this._setByte(index, (byte)(value >>> 8));
         this._setByte(index + 1, (byte)value);
      } else {
         this._setByte(index, (byte)value);
         this._setByte(index + 1, (byte)(value >>> 8));
      }
   }

   @Override
   protected void _setShortLE(int index, int value) {
      CompositeByteBuf.Component c = this.findComponent0(index);
      if (index + 2 <= c.endOffset) {
         c.buf.setShortLE(c.idx(index), value);
      } else if (this.order() == ByteOrder.BIG_ENDIAN) {
         this._setByte(index, (byte)value);
         this._setByte(index + 1, (byte)(value >>> 8));
      } else {
         this._setByte(index, (byte)(value >>> 8));
         this._setByte(index + 1, (byte)value);
      }
   }

   public CompositeByteBuf setMedium(int index, int value) {
      this.checkIndex(index, 3);
      this._setMedium(index, value);
      return this;
   }

   @Override
   protected void _setMedium(int index, int value) {
      CompositeByteBuf.Component c = this.findComponent0(index);
      if (index + 3 <= c.endOffset) {
         c.buf.setMedium(c.idx(index), value);
      } else if (this.order() == ByteOrder.BIG_ENDIAN) {
         this._setShort(index, (short)(value >> 8));
         this._setByte(index + 2, (byte)value);
      } else {
         this._setShort(index, (short)value);
         this._setByte(index + 2, (byte)(value >>> 16));
      }
   }

   @Override
   protected void _setMediumLE(int index, int value) {
      CompositeByteBuf.Component c = this.findComponent0(index);
      if (index + 3 <= c.endOffset) {
         c.buf.setMediumLE(c.idx(index), value);
      } else if (this.order() == ByteOrder.BIG_ENDIAN) {
         this._setShortLE(index, (short)value);
         this._setByte(index + 2, (byte)(value >>> 16));
      } else {
         this._setShortLE(index, (short)(value >> 8));
         this._setByte(index + 2, (byte)value);
      }
   }

   public CompositeByteBuf setInt(int index, int value) {
      this.checkIndex(index, 4);
      this._setInt(index, value);
      return this;
   }

   @Override
   protected void _setInt(int index, int value) {
      CompositeByteBuf.Component c = this.findComponent0(index);
      if (index + 4 <= c.endOffset) {
         c.buf.setInt(c.idx(index), value);
      } else if (this.order() == ByteOrder.BIG_ENDIAN) {
         this._setShort(index, (short)(value >>> 16));
         this._setShort(index + 2, (short)value);
      } else {
         this._setShort(index, (short)value);
         this._setShort(index + 2, (short)(value >>> 16));
      }
   }

   @Override
   protected void _setIntLE(int index, int value) {
      CompositeByteBuf.Component c = this.findComponent0(index);
      if (index + 4 <= c.endOffset) {
         c.buf.setIntLE(c.idx(index), value);
      } else if (this.order() == ByteOrder.BIG_ENDIAN) {
         this._setShortLE(index, (short)value);
         this._setShortLE(index + 2, (short)(value >>> 16));
      } else {
         this._setShortLE(index, (short)(value >>> 16));
         this._setShortLE(index + 2, (short)value);
      }
   }

   public CompositeByteBuf setLong(int index, long value) {
      this.checkIndex(index, 8);
      this._setLong(index, value);
      return this;
   }

   @Override
   protected void _setLong(int index, long value) {
      CompositeByteBuf.Component c = this.findComponent0(index);
      if (index + 8 <= c.endOffset) {
         c.buf.setLong(c.idx(index), value);
      } else if (this.order() == ByteOrder.BIG_ENDIAN) {
         this._setInt(index, (int)(value >>> 32));
         this._setInt(index + 4, (int)value);
      } else {
         this._setInt(index, (int)value);
         this._setInt(index + 4, (int)(value >>> 32));
      }
   }

   @Override
   protected void _setLongLE(int index, long value) {
      CompositeByteBuf.Component c = this.findComponent0(index);
      if (index + 8 <= c.endOffset) {
         c.buf.setLongLE(c.idx(index), value);
      } else if (this.order() == ByteOrder.BIG_ENDIAN) {
         this._setIntLE(index, (int)value);
         this._setIntLE(index + 4, (int)(value >>> 32));
      } else {
         this._setIntLE(index, (int)(value >>> 32));
         this._setIntLE(index + 4, (int)value);
      }
   }

   public CompositeByteBuf setBytes(int index, byte[] src, int srcIndex, int length) {
      this.checkSrcIndex(index, length, srcIndex, src.length);
      if (length == 0) {
         return this;
      } else {
         for (int i = this.toComponentIndex0(index); length > 0; i++) {
            CompositeByteBuf.Component c = this.components[i];
            int localLength = Math.min(length, c.endOffset - index);
            c.buf.setBytes(c.idx(index), src, srcIndex, localLength);
            index += localLength;
            srcIndex += localLength;
            length -= localLength;
         }

         return this;
      }
   }

   public CompositeByteBuf setBytes(int index, ByteBuffer src) {
      int limit = src.limit();
      int length = src.remaining();
      this.checkIndex(index, length);
      if (length == 0) {
         return this;
      } else {
         int i = this.toComponentIndex0(index);

         try {
            while (length > 0) {
               CompositeByteBuf.Component c = this.components[i];
               int localLength = Math.min(length, c.endOffset - index);
               ((Buffer)src).limit(src.position() + localLength);
               c.buf.setBytes(c.idx(index), src);
               index += localLength;
               length -= localLength;
               i++;
            }
         } finally {
            ((Buffer)src).limit(limit);
         }

         return this;
      }
   }

   public CompositeByteBuf setBytes(int index, ByteBuf src, int srcIndex, int length) {
      this.checkSrcIndex(index, length, srcIndex, src.capacity());
      if (length == 0) {
         return this;
      } else {
         for (int i = this.toComponentIndex0(index); length > 0; i++) {
            CompositeByteBuf.Component c = this.components[i];
            int localLength = Math.min(length, c.endOffset - index);
            c.buf.setBytes(c.idx(index), src, srcIndex, localLength);
            index += localLength;
            srcIndex += localLength;
            length -= localLength;
         }

         return this;
      }
   }

   @Override
   public int setBytes(int index, InputStream in, int length) throws IOException {
      this.checkIndex(index, length);
      if (length == 0) {
         return in.read(EmptyArrays.EMPTY_BYTES);
      } else {
         int i = this.toComponentIndex0(index);
         int readBytes = 0;

         do {
            CompositeByteBuf.Component c = this.components[i];
            int localLength = Math.min(length, c.endOffset - index);
            if (localLength == 0) {
               i++;
            } else {
               int localReadBytes = c.buf.setBytes(c.idx(index), in, localLength);
               if (localReadBytes < 0) {
                  if (readBytes == 0) {
                     return -1;
                  }
                  break;
               }

               index += localReadBytes;
               length -= localReadBytes;
               readBytes += localReadBytes;
               if (localReadBytes == localLength) {
                  i++;
               }
            }
         } while (length > 0);

         return readBytes;
      }
   }

   @Override
   public int setBytes(int index, ScatteringByteChannel in, int length) throws IOException {
      this.checkIndex(index, length);
      if (length == 0) {
         return in.read(EMPTY_NIO_BUFFER);
      } else {
         int i = this.toComponentIndex0(index);
         int readBytes = 0;

         do {
            CompositeByteBuf.Component c = this.components[i];
            int localLength = Math.min(length, c.endOffset - index);
            if (localLength == 0) {
               i++;
            } else {
               int localReadBytes = c.buf.setBytes(c.idx(index), in, localLength);
               if (localReadBytes == 0) {
                  break;
               }

               if (localReadBytes < 0) {
                  if (readBytes == 0) {
                     return -1;
                  }
                  break;
               }

               index += localReadBytes;
               length -= localReadBytes;
               readBytes += localReadBytes;
               if (localReadBytes == localLength) {
                  i++;
               }
            }
         } while (length > 0);

         return readBytes;
      }
   }

   @Override
   public int setBytes(int index, FileChannel in, long position, int length) throws IOException {
      this.checkIndex(index, length);
      if (length == 0) {
         return in.read(EMPTY_NIO_BUFFER, position);
      } else {
         int i = this.toComponentIndex0(index);
         int readBytes = 0;

         do {
            CompositeByteBuf.Component c = this.components[i];
            int localLength = Math.min(length, c.endOffset - index);
            if (localLength == 0) {
               i++;
            } else {
               int localReadBytes = c.buf.setBytes(c.idx(index), in, position + readBytes, localLength);
               if (localReadBytes == 0) {
                  break;
               }

               if (localReadBytes < 0) {
                  if (readBytes == 0) {
                     return -1;
                  }
                  break;
               }

               index += localReadBytes;
               length -= localReadBytes;
               readBytes += localReadBytes;
               if (localReadBytes == localLength) {
                  i++;
               }
            }
         } while (length > 0);

         return readBytes;
      }
   }

   @Override
   public ByteBuf copy(int index, int length) {
      this.checkIndex(index, length);
      ByteBuf dst = this.allocBuffer(length);
      if (length != 0) {
         this.copyTo(index, length, this.toComponentIndex0(index), dst);
      }

      return dst;
   }

   private void copyTo(int index, int length, int componentId, ByteBuf dst) {
      int dstIndex = 0;

      for (int i = componentId; length > 0; i++) {
         CompositeByteBuf.Component c = this.components[i];
         int localLength = Math.min(length, c.endOffset - index);
         c.buf.getBytes(c.idx(index), dst, dstIndex, localLength);
         index += localLength;
         dstIndex += localLength;
         length -= localLength;
      }

      dst.writerIndex(dst.capacity());
   }

   public ByteBuf component(int cIndex) {
      this.checkComponentIndex(cIndex);
      return this.components[cIndex].duplicate();
   }

   public ByteBuf componentSlice(int cIndex) {
      this.checkComponentIndex(cIndex);
      return this.components[cIndex].slice();
   }

   public ByteBuf componentAtOffset(int offset) {
      return this.findComponent(offset).duplicate();
   }

   public ByteBuf internalComponent(int cIndex) {
      this.checkComponentIndex(cIndex);
      return this.components[cIndex].slice();
   }

   public ByteBuf internalComponentAtOffset(int offset) {
      return this.findComponent(offset).slice();
   }

   private CompositeByteBuf.Component findComponent(int offset) {
      CompositeByteBuf.Component la = this.lastAccessed;
      if (la != null && offset >= la.offset && offset < la.endOffset) {
         this.ensureAccessible();
         return la;
      } else {
         this.checkIndex(offset);
         return this.findIt(offset);
      }
   }

   private CompositeByteBuf.Component findComponent0(int offset) {
      CompositeByteBuf.Component la = this.lastAccessed;
      return la != null && offset >= la.offset && offset < la.endOffset ? la : this.findIt(offset);
   }

   private CompositeByteBuf.Component findIt(int offset) {
      int low = 0;
      int high = this.componentCount;

      while (low <= high) {
         int mid = low + high >>> 1;
         CompositeByteBuf.Component c = this.components[mid];
         if (c == null) {
            throw new IllegalStateException("No component found for offset. Composite buffer layout might be outdated, e.g. from a discardReadBytes call.");
         }

         if (offset >= c.endOffset) {
            low = mid + 1;
         } else {
            if (offset >= c.offset) {
               this.lastAccessed = c;
               return c;
            }

            high = mid - 1;
         }
      }

      throw new Error("should not reach here");
   }

   @Override
   public int nioBufferCount() {
      int size = this.componentCount;
      switch (size) {
         case 0:
            return 1;
         case 1:
            return this.components[0].buf.nioBufferCount();
         default:
            int count = 0;

            for (int i = 0; i < size; i++) {
               count += this.components[i].buf.nioBufferCount();
            }

            return count;
      }
   }

   @Override
   public ByteBuffer internalNioBuffer(int index, int length) {
      switch (this.componentCount) {
         case 0:
            return EMPTY_NIO_BUFFER;
         case 1:
            return this.components[0].internalNioBuffer(index, length);
         default:
            throw new UnsupportedOperationException();
      }
   }

   @Override
   public ByteBuffer nioBuffer(int index, int length) {
      this.checkIndex(index, length);
      switch (this.componentCount) {
         case 0:
            return EMPTY_NIO_BUFFER;
         case 1:
            CompositeByteBuf.Component c = this.components[0];
            ByteBuf buf = c.buf;
            if (buf.nioBufferCount() == 1) {
               return buf.nioBuffer(c.idx(index), length);
            }
         default:
            ByteBuffer[] buffers = this.nioBuffers(index, length);
            if (buffers.length == 1) {
               return buffers[0];
            } else {
               ByteBuffer merged = ByteBuffer.allocate(length).order(this.order());

               for (ByteBuffer buf : buffers) {
                  merged.put(buf);
               }

               ((Buffer)merged).flip();
               return merged;
            }
      }
   }

   @Override
   public ByteBuffer[] nioBuffers(int index, int length) {
      this.checkIndex(index, length);
      if (length == 0) {
         return new ByteBuffer[]{EMPTY_NIO_BUFFER};
      } else {
         RecyclableArrayList buffers = RecyclableArrayList.newInstance(this.componentCount);

         ByteBuffer[] var11;
         try {
            for (int i = this.toComponentIndex0(index); length > 0; i++) {
               CompositeByteBuf.Component c = this.components[i];
               ByteBuf s = c.buf;
               int localLength = Math.min(length, c.endOffset - index);
               switch (s.nioBufferCount()) {
                  case 0:
                     throw new UnsupportedOperationException();
                  case 1:
                     buffers.add(s.nioBuffer(c.idx(index), localLength));
                     break;
                  default:
                     Collections.addAll(buffers, s.nioBuffers(c.idx(index), localLength));
               }

               index += localLength;
               length -= localLength;
            }

            var11 = buffers.toArray(EmptyArrays.EMPTY_BYTE_BUFFERS);
         } finally {
            buffers.recycle();
         }

         return var11;
      }
   }

   public CompositeByteBuf consolidate() {
      this.ensureAccessible();
      this.consolidate0(0, this.componentCount);
      return this;
   }

   public CompositeByteBuf consolidate(int cIndex, int numComponents) {
      this.checkComponentIndex(cIndex, numComponents);
      this.consolidate0(cIndex, numComponents);
      return this;
   }

   private void consolidate0(int cIndex, int numComponents) {
      if (numComponents > 1) {
         int endCIndex = cIndex + numComponents;
         int startOffset = cIndex != 0 ? this.components[cIndex].offset : 0;
         int capacity = this.components[endCIndex - 1].endOffset - startOffset;
         ByteBuf consolidated = this.allocBuffer(capacity);

         for (int i = cIndex; i < endCIndex; i++) {
            this.components[i].transferTo(consolidated);
         }

         this.lastAccessed = null;
         this.removeCompRange(cIndex + 1, endCIndex);
         this.components[cIndex] = this.newComponent(consolidated, 0);
         if (cIndex != 0 || numComponents != this.componentCount) {
            this.updateComponentOffsets(cIndex);
         }
      }
   }

   public CompositeByteBuf discardReadComponents() {
      this.ensureAccessible();
      int readerIndex = this.readerIndex();
      if (readerIndex == 0) {
         return this;
      } else {
         int writerIndex = this.writerIndex();
         if (readerIndex == writerIndex && writerIndex == this.capacity()) {
            int i = 0;

            for (int size = this.componentCount; i < size; i++) {
               this.components[i].free();
            }

            this.lastAccessed = null;
            this.clearComps();
            this.setIndex(0, 0);
            this.adjustMarkers(readerIndex);
            return this;
         } else {
            int firstComponentId = 0;
            CompositeByteBuf.Component c = null;

            for (int size = this.componentCount; firstComponentId < size; firstComponentId++) {
               c = this.components[firstComponentId];
               if (c.endOffset > readerIndex) {
                  break;
               }

               c.free();
            }

            if (firstComponentId == 0) {
               return this;
            } else {
               CompositeByteBuf.Component la = this.lastAccessed;
               if (la != null && la.endOffset <= readerIndex) {
                  this.lastAccessed = null;
               }

               this.removeCompRange(0, firstComponentId);
               int offset = c.offset;
               this.updateComponentOffsets(0);
               this.setIndex(readerIndex - offset, writerIndex - offset);
               this.adjustMarkers(offset);
               return this;
            }
         }
      }
   }

   public CompositeByteBuf discardReadBytes() {
      this.ensureAccessible();
      int readerIndex = this.readerIndex();
      if (readerIndex == 0) {
         return this;
      } else {
         int writerIndex = this.writerIndex();
         if (readerIndex == writerIndex && writerIndex == this.capacity()) {
            int i = 0;

            for (int size = this.componentCount; i < size; i++) {
               this.components[i].free();
            }

            this.lastAccessed = null;
            this.clearComps();
            this.setIndex(0, 0);
            this.adjustMarkers(readerIndex);
            return this;
         } else {
            int firstComponentId = 0;
            CompositeByteBuf.Component c = null;

            for (int size = this.componentCount; firstComponentId < size; firstComponentId++) {
               c = this.components[firstComponentId];
               if (c.endOffset > readerIndex) {
                  break;
               }

               c.free();
            }

            int trimmedBytes = readerIndex - c.offset;
            c.offset = 0;
            c.endOffset -= readerIndex;
            c.srcAdjustment += readerIndex;
            c.adjustment += readerIndex;
            ByteBuf slice = c.slice;
            if (slice != null) {
               c.slice = slice.slice(trimmedBytes, c.length());
            }

            CompositeByteBuf.Component la = this.lastAccessed;
            if (la != null && la.endOffset <= readerIndex) {
               this.lastAccessed = null;
            }

            this.removeCompRange(0, firstComponentId);
            this.updateComponentOffsets(0);
            this.setIndex(0, writerIndex - readerIndex);
            this.adjustMarkers(readerIndex);
            return this;
         }
      }
   }

   private ByteBuf allocBuffer(int capacity) {
      return this.direct ? this.alloc().directBuffer(capacity) : this.alloc().heapBuffer(capacity);
   }

   @Override
   public String toString() {
      String result = super.toString();
      result = result.substring(0, result.length() - 1);
      return result + ", components=" + this.componentCount + ')';
   }

   public CompositeByteBuf readerIndex(int readerIndex) {
      super.readerIndex(readerIndex);
      return this;
   }

   public CompositeByteBuf writerIndex(int writerIndex) {
      super.writerIndex(writerIndex);
      return this;
   }

   public CompositeByteBuf setIndex(int readerIndex, int writerIndex) {
      super.setIndex(readerIndex, writerIndex);
      return this;
   }

   public CompositeByteBuf clear() {
      super.clear();
      return this;
   }

   public CompositeByteBuf markReaderIndex() {
      super.markReaderIndex();
      return this;
   }

   public CompositeByteBuf resetReaderIndex() {
      super.resetReaderIndex();
      return this;
   }

   public CompositeByteBuf markWriterIndex() {
      super.markWriterIndex();
      return this;
   }

   public CompositeByteBuf resetWriterIndex() {
      super.resetWriterIndex();
      return this;
   }

   public CompositeByteBuf ensureWritable(int minWritableBytes) {
      super.ensureWritable(minWritableBytes);
      return this;
   }

   public CompositeByteBuf getBytes(int index, ByteBuf dst) {
      return this.getBytes(index, dst, dst.writableBytes());
   }

   public CompositeByteBuf getBytes(int index, ByteBuf dst, int length) {
      this.getBytes(index, dst, dst.writerIndex(), length);
      dst.writerIndex(dst.writerIndex() + length);
      return this;
   }

   public CompositeByteBuf getBytes(int index, byte[] dst) {
      return this.getBytes(index, dst, 0, dst.length);
   }

   public CompositeByteBuf setBoolean(int index, boolean value) {
      return this.setByte(index, value ? 1 : 0);
   }

   public CompositeByteBuf setChar(int index, int value) {
      return this.setShort(index, value);
   }

   public CompositeByteBuf setFloat(int index, float value) {
      return this.setInt(index, Float.floatToRawIntBits(value));
   }

   public CompositeByteBuf setDouble(int index, double value) {
      return this.setLong(index, Double.doubleToRawLongBits(value));
   }

   public CompositeByteBuf setBytes(int index, ByteBuf src) {
      super.setBytes(index, src, src.readableBytes());
      return this;
   }

   public CompositeByteBuf setBytes(int index, ByteBuf src, int length) {
      super.setBytes(index, src, length);
      return this;
   }

   public CompositeByteBuf setBytes(int index, byte[] src) {
      return this.setBytes(index, src, 0, src.length);
   }

   public CompositeByteBuf setZero(int index, int length) {
      super.setZero(index, length);
      return this;
   }

   public CompositeByteBuf readBytes(ByteBuf dst) {
      super.readBytes(dst, dst.writableBytes());
      return this;
   }

   public CompositeByteBuf readBytes(ByteBuf dst, int length) {
      super.readBytes(dst, length);
      return this;
   }

   public CompositeByteBuf readBytes(ByteBuf dst, int dstIndex, int length) {
      super.readBytes(dst, dstIndex, length);
      return this;
   }

   public CompositeByteBuf readBytes(byte[] dst) {
      super.readBytes(dst, 0, dst.length);
      return this;
   }

   public CompositeByteBuf readBytes(byte[] dst, int dstIndex, int length) {
      super.readBytes(dst, dstIndex, length);
      return this;
   }

   public CompositeByteBuf readBytes(ByteBuffer dst) {
      super.readBytes(dst);
      return this;
   }

   public CompositeByteBuf readBytes(OutputStream out, int length) throws IOException {
      super.readBytes(out, length);
      return this;
   }

   public CompositeByteBuf skipBytes(int length) {
      super.skipBytes(length);
      return this;
   }

   public CompositeByteBuf writeBoolean(boolean value) {
      this.writeByte(value ? 1 : 0);
      return this;
   }

   public CompositeByteBuf writeByte(int value) {
      this.ensureWritable0(1);
      this._setByte(this.writerIndex++, value);
      return this;
   }

   public CompositeByteBuf writeShort(int value) {
      super.writeShort(value);
      return this;
   }

   public CompositeByteBuf writeMedium(int value) {
      super.writeMedium(value);
      return this;
   }

   public CompositeByteBuf writeInt(int value) {
      super.writeInt(value);
      return this;
   }

   public CompositeByteBuf writeLong(long value) {
      super.writeLong(value);
      return this;
   }

   public CompositeByteBuf writeChar(int value) {
      super.writeShort(value);
      return this;
   }

   public CompositeByteBuf writeFloat(float value) {
      super.writeInt(Float.floatToRawIntBits(value));
      return this;
   }

   public CompositeByteBuf writeDouble(double value) {
      super.writeLong(Double.doubleToRawLongBits(value));
      return this;
   }

   public CompositeByteBuf writeBytes(ByteBuf src) {
      super.writeBytes(src, src.readableBytes());
      return this;
   }

   public CompositeByteBuf writeBytes(ByteBuf src, int length) {
      super.writeBytes(src, length);
      return this;
   }

   public CompositeByteBuf writeBytes(ByteBuf src, int srcIndex, int length) {
      super.writeBytes(src, srcIndex, length);
      return this;
   }

   public CompositeByteBuf writeBytes(byte[] src) {
      super.writeBytes(src, 0, src.length);
      return this;
   }

   public CompositeByteBuf writeBytes(byte[] src, int srcIndex, int length) {
      super.writeBytes(src, srcIndex, length);
      return this;
   }

   public CompositeByteBuf writeBytes(ByteBuffer src) {
      super.writeBytes(src);
      return this;
   }

   public CompositeByteBuf writeZero(int length) {
      super.writeZero(length);
      return this;
   }

   public CompositeByteBuf retain(int increment) {
      super.retain(increment);
      return this;
   }

   public CompositeByteBuf retain() {
      super.retain();
      return this;
   }

   public CompositeByteBuf touch() {
      return this;
   }

   public CompositeByteBuf touch(Object hint) {
      return this;
   }

   @Override
   public ByteBuffer[] nioBuffers() {
      return this.nioBuffers(this.readerIndex(), this.readableBytes());
   }

   public CompositeByteBuf discardSomeReadBytes() {
      return this.discardReadComponents();
   }

   @Override
   protected void deallocate() {
      if (!this.freed) {
         this.freed = true;
         int i = 0;

         for (int size = this.componentCount; i < size; i++) {
            this.components[i].free();
         }
      }
   }

   @Override
   boolean isAccessible() {
      return !this.freed;
   }

   @Override
   public ByteBuf unwrap() {
      return null;
   }

   private void clearComps() {
      this.removeCompRange(0, this.componentCount);
   }

   private void removeComp(int i) {
      this.removeCompRange(i, i + 1);
   }

   private void removeCompRange(int from, int to) {
      if (from < to) {
         int size = this.componentCount;

         assert from >= 0 && to <= size;

         if (to < size) {
            System.arraycopy(this.components, to, this.components, from, size - to);
         }

         int newSize = size - to + from;

         for (int i = newSize; i < size; i++) {
            this.components[i] = null;
         }

         this.componentCount = newSize;
      }
   }

   private void addComp(int i, CompositeByteBuf.Component c) {
      this.shiftComps(i, 1);
      this.components[i] = c;
   }

   private void shiftComps(int i, int count) {
      int size = this.componentCount;
      int newSize = size + count;

      assert i >= 0 && i <= size && count > 0;

      if (newSize > this.components.length) {
         int newArrSize = Math.max(size + (size >> 1), newSize);
         CompositeByteBuf.Component[] newArr;
         if (i == size) {
            newArr = Arrays.copyOf(this.components, newArrSize, CompositeByteBuf.Component[].class);
         } else {
            newArr = new CompositeByteBuf.Component[newArrSize];
            if (i > 0) {
               System.arraycopy(this.components, 0, newArr, 0, i);
            }

            if (i < size) {
               System.arraycopy(this.components, i, newArr, i + count, size - i);
            }
         }

         this.components = newArr;
      } else if (i < size) {
         System.arraycopy(this.components, i, this.components, i + count, size - i);
      }

      this.componentCount = newSize;
   }

   @Override
   public boolean release(int decrement) {
      return super.release(decrement);
   }

   interface ByteWrapper<T> {
      ByteBuf wrap(T var1);

      boolean isEmpty(T var1);
   }

   private static final class Component {
      final ByteBuf srcBuf;
      final ByteBuf buf;
      int srcAdjustment;
      int adjustment;
      int offset;
      int endOffset;
      private ByteBuf slice;

      Component(ByteBuf srcBuf, int srcOffset, ByteBuf buf, int bufOffset, int offset, int len, ByteBuf slice) {
         this.srcBuf = srcBuf;
         this.srcAdjustment = srcOffset - offset;
         this.buf = buf;
         this.adjustment = bufOffset - offset;
         this.offset = offset;
         this.endOffset = offset + len;
         this.slice = slice;
      }

      int srcIdx(int index) {
         return index + this.srcAdjustment;
      }

      int idx(int index) {
         return index + this.adjustment;
      }

      int length() {
         return this.endOffset - this.offset;
      }

      void reposition(int newOffset) {
         int move = newOffset - this.offset;
         this.endOffset += move;
         this.srcAdjustment -= move;
         this.adjustment -= move;
         this.offset = newOffset;
      }

      void transferTo(ByteBuf dst) {
         dst.writeBytes(this.buf, this.idx(this.offset), this.length());
         this.free();
      }

      ByteBuf slice() {
         ByteBuf s = this.slice;
         if (s == null) {
            this.slice = s = this.srcBuf.slice(this.srcIdx(this.offset), this.length());
         }

         return s;
      }

      ByteBuf duplicate() {
         return this.srcBuf.duplicate();
      }

      ByteBuffer internalNioBuffer(int index, int length) {
         return this.srcBuf.internalNioBuffer(this.srcIdx(index), length);
      }

      void free() {
         this.slice = null;
         this.srcBuf.release();
      }
   }

   private final class CompositeByteBufIterator implements Iterator<ByteBuf> {
      private final int size = CompositeByteBuf.this.numComponents();
      private int index;

      private CompositeByteBufIterator() {
      }

      @Override
      public boolean hasNext() {
         return this.size > this.index;
      }

      public ByteBuf next() {
         if (this.size != CompositeByteBuf.this.numComponents()) {
            throw new ConcurrentModificationException();
         } else if (!this.hasNext()) {
            throw new NoSuchElementException();
         } else {
            try {
               return CompositeByteBuf.this.components[this.index++].slice();
            } catch (IndexOutOfBoundsException var2) {
               throw new ConcurrentModificationException();
            }
         }
      }

      @Override
      public void remove() {
         throw new UnsupportedOperationException("Read-Only");
      }
   }
}
