package it.unimi.dsi.fastutil.floats;

import java.io.IOException;
import java.nio.Buffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.FileChannel.MapMode;
import java.util.Arrays;

public class FloatMappedBigList extends AbstractFloatBigList {
   public static int LOG2_BYTES = 63 - Long.numberOfLeadingZeros(4L);
   @Deprecated
   public static int LOG2_BITS = 63 - Long.numberOfLeadingZeros(4L);
   private static int CHUNK_SHIFT = 30 - LOG2_BYTES;
   public static final long CHUNK_SIZE = 1L << CHUNK_SHIFT;
   private static final long CHUNK_MASK = CHUNK_SIZE - 1L;
   private final FloatBuffer[] buffer;
   private final boolean[] readyToUse;
   private final int n;
   private final long size;

   protected FloatMappedBigList(FloatBuffer[] buffer, long size, boolean[] readyToUse) {
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

   public static FloatMappedBigList map(FileChannel fileChannel) throws IOException {
      return map(fileChannel, ByteOrder.BIG_ENDIAN);
   }

   public static FloatMappedBigList map(FileChannel fileChannel, ByteOrder byteOrder) throws IOException {
      return map(fileChannel, byteOrder, MapMode.READ_ONLY);
   }

   public static FloatMappedBigList map(FileChannel fileChannel, ByteOrder byteOrder, MapMode mapMode) throws IOException {
      long size = fileChannel.size() / 4L;
      int chunks = (int)((size + (CHUNK_SIZE - 1L)) / CHUNK_SIZE);
      FloatBuffer[] buffer = new FloatBuffer[chunks];

      for (int i = 0; i < chunks; i++) {
         buffer[i] = fileChannel.map(mapMode, i * CHUNK_SIZE * 4L, Math.min(CHUNK_SIZE, size - i * CHUNK_SIZE) * 4L).order(byteOrder).asFloatBuffer();
      }

      boolean[] readyToUse = new boolean[chunks];
      Arrays.fill(readyToUse, true);
      return new FloatMappedBigList(buffer, size, readyToUse);
   }

   private FloatBuffer FloatBuffer(int n) {
      if (this.readyToUse[n]) {
         return this.buffer[n];
      } else {
         this.readyToUse[n] = true;
         return this.buffer[n] = this.buffer[n].duplicate();
      }
   }

   public FloatMappedBigList copy() {
      return new FloatMappedBigList((FloatBuffer[])this.buffer.clone(), this.size, new boolean[this.n]);
   }

   @Override
   public float getFloat(long index) {
      return this.FloatBuffer((int)(index >>> CHUNK_SHIFT)).get((int)(index & CHUNK_MASK));
   }

   @Override
   public void getElements(long from, float[] a, int offset, int length) {
      int chunk = (int)(from >>> CHUNK_SHIFT);
      int displ = (int)(from & CHUNK_MASK);

      while (length > 0) {
         FloatBuffer b = this.FloatBuffer(chunk);
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
   public float set(long index, float value) {
      FloatBuffer b = this.FloatBuffer((int)(index >>> CHUNK_SHIFT));
      int i = (int)(index & CHUNK_MASK);
      float previousValue = b.get(i);
      b.put(i, value);
      return previousValue;
   }

   @Override
   public long size64() {
      return this.size;
   }
}
