package it.unimi.dsi.fastutil.shorts;

import java.io.IOException;
import java.nio.Buffer;
import java.nio.ByteOrder;
import java.nio.ShortBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.FileChannel.MapMode;
import java.util.Arrays;

public class ShortMappedBigList extends AbstractShortBigList {
   public static int LOG2_BYTES = 63 - Long.numberOfLeadingZeros(2L);
   @Deprecated
   public static int LOG2_BITS = 63 - Long.numberOfLeadingZeros(2L);
   private static int CHUNK_SHIFT = 30 - LOG2_BYTES;
   public static final long CHUNK_SIZE = 1L << CHUNK_SHIFT;
   private static final long CHUNK_MASK = CHUNK_SIZE - 1L;
   private final ShortBuffer[] buffer;
   private final boolean[] readyToUse;
   private final int n;
   private final long size;

   protected ShortMappedBigList(ShortBuffer[] buffer, long size, boolean[] readyToUse) {
      this.buffer = buffer;
      this.n = buffer.length;
      this.size = size;
      this.readyToUse = readyToUse;

      for (int i = 0; i < this.n; i++) {
         if (i < this.n - 1 && buffer[i].capacity() != CHUNK_SIZE) {
            throw new IllegalArgumentException();
         }
      }
   }

   public static ShortMappedBigList map(FileChannel fileChannel) throws IOException {
      return map(fileChannel, ByteOrder.BIG_ENDIAN);
   }

   public static ShortMappedBigList map(FileChannel fileChannel, ByteOrder byteOrder) throws IOException {
      return map(fileChannel, byteOrder, MapMode.READ_ONLY);
   }

   public static ShortMappedBigList map(FileChannel fileChannel, ByteOrder byteOrder, MapMode mapMode) throws IOException {
      long size = fileChannel.size() / 2L;
      int chunks = (int)((size + (CHUNK_SIZE - 1L)) / CHUNK_SIZE);
      ShortBuffer[] buffer = new ShortBuffer[chunks];

      for (int i = 0; i < chunks; i++) {
         buffer[i] = fileChannel.map(mapMode, i * CHUNK_SIZE * 2L, Math.min(CHUNK_SIZE, size - i * CHUNK_SIZE) * 2L).order(byteOrder).asShortBuffer();
      }

      boolean[] readyToUse = new boolean[chunks];
      Arrays.fill(readyToUse, true);
      return new ShortMappedBigList(buffer, size, readyToUse);
   }

   private ShortBuffer ShortBuffer(int n) {
      if (this.readyToUse[n]) {
         return this.buffer[n];
      } else {
         this.readyToUse[n] = true;
         return this.buffer[n] = this.buffer[n].duplicate();
      }
   }

   public ShortMappedBigList copy() {
      return new ShortMappedBigList((ShortBuffer[])this.buffer.clone(), this.size, new boolean[this.n]);
   }

   @Override
   public short getShort(long index) {
      return this.ShortBuffer((int)(index >>> CHUNK_SHIFT)).get((int)(index & CHUNK_MASK));
   }

   @Override
   public void getElements(long from, short[] a, int offset, int length) {
      int chunk = (int)(from >>> CHUNK_SHIFT);
      int displ = (int)(from & CHUNK_MASK);

      while (length > 0) {
         ShortBuffer b = this.ShortBuffer(chunk);
         int l = Math.min(b.capacity() - displ, length);
         if (l == 0) {
            throw new ArrayIndexOutOfBoundsException();
         }

         ((Buffer)b).position(displ);
         b.get(a, offset, l);
         if ((displ += l) == CHUNK_SIZE) {
            displ = 0;
            chunk++;
         }

         offset += l;
         length -= l;
      }
   }

   @Override
   public short set(long index, short value) {
      ShortBuffer b = this.ShortBuffer((int)(index >>> CHUNK_SHIFT));
      int i = (int)(index & CHUNK_MASK);
      short previousValue = b.get(i);
      b.put(i, value);
      return previousValue;
   }

   @Override
   public long size64() {
      return this.size;
   }
}
