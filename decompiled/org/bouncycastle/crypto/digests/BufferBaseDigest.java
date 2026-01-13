package org.bouncycastle.crypto.digests;

import org.bouncycastle.crypto.DataLengthException;
import org.bouncycastle.crypto.ExtendedDigest;
import org.bouncycastle.crypto.OutputLengthException;
import org.bouncycastle.util.Arrays;

abstract class BufferBaseDigest implements ExtendedDigest {
   protected int DigestSize;
   protected int BlockSize;
   protected byte[] m_buf;
   protected int m_bufPos;
   protected String algorithmName;
   protected BufferBaseDigest.ProcessingBuffer processor;

   protected BufferBaseDigest(BufferBaseDigest.ProcessingBufferType var1, int var2) {
      this.BlockSize = var2;
      this.m_buf = new byte[var2];
      switch (var1.ord) {
         case 0:
            this.processor = new BufferBaseDigest.BufferedProcessor();
            break;
         case 1:
            this.processor = new BufferBaseDigest.ImmediateProcessor();
      }
   }

   @Override
   public String getAlgorithmName() {
      return this.algorithmName;
   }

   @Override
   public int getDigestSize() {
      return this.DigestSize;
   }

   @Override
   public int getByteLength() {
      return this.BlockSize;
   }

   @Override
   public void update(byte var1) {
      this.processor.update(var1);
   }

   @Override
   public void update(byte[] var1, int var2, int var3) {
      this.ensureSufficientInputBuffer(var1, var2, var3);
      int var4 = this.BlockSize - this.m_bufPos;
      if (this.processor.isLengthWithinAvailableSpace(var3, var4)) {
         System.arraycopy(var1, var2, this.m_buf, this.m_bufPos, var3);
         this.m_bufPos += var3;
      } else {
         if (this.m_bufPos > 0) {
            System.arraycopy(var1, var2, this.m_buf, this.m_bufPos, var4);
            var2 += var4;
            var3 -= var4;
            this.processBytes(this.m_buf, 0);
         }

         while (this.processor.isLengthExceedingBlockSize(var3, this.BlockSize)) {
            this.processBytes(var1, var2);
            var2 += this.BlockSize;
            var3 -= this.BlockSize;
         }

         System.arraycopy(var1, var2, this.m_buf, 0, var3);
         this.m_bufPos = var3;
      }
   }

   @Override
   public int doFinal(byte[] var1, int var2) {
      this.ensureSufficientOutputBuffer(var1, var2);
      this.finish(var1, var2);
      this.reset();
      return this.DigestSize;
   }

   @Override
   public void reset() {
      Arrays.clear(this.m_buf);
      this.m_bufPos = 0;
   }

   protected void ensureSufficientInputBuffer(byte[] var1, int var2, int var3) {
      if (var2 + var3 > var1.length) {
         throw new DataLengthException("input buffer too short");
      }
   }

   protected void ensureSufficientOutputBuffer(byte[] var1, int var2) {
      if (this.DigestSize + var2 > var1.length) {
         throw new OutputLengthException("output buffer is too short");
      }
   }

   protected abstract void processBytes(byte[] var1, int var2);

   protected abstract void finish(byte[] var1, int var2);

   private class BufferedProcessor implements BufferBaseDigest.ProcessingBuffer {
      private BufferedProcessor() {
      }

      @Override
      public void update(byte var1) {
         if (BufferBaseDigest.this.m_bufPos == BufferBaseDigest.this.BlockSize) {
            BufferBaseDigest.this.processBytes(BufferBaseDigest.this.m_buf, 0);
            BufferBaseDigest.this.m_bufPos = 0;
         }

         BufferBaseDigest.this.m_buf[BufferBaseDigest.this.m_bufPos++] = var1;
      }

      @Override
      public boolean isLengthWithinAvailableSpace(int var1, int var2) {
         return var1 <= var2;
      }

      @Override
      public boolean isLengthExceedingBlockSize(int var1, int var2) {
         return var1 > var2;
      }
   }

   private class ImmediateProcessor implements BufferBaseDigest.ProcessingBuffer {
      private ImmediateProcessor() {
      }

      @Override
      public void update(byte var1) {
         BufferBaseDigest.this.m_buf[BufferBaseDigest.this.m_bufPos] = var1;
         if (++BufferBaseDigest.this.m_bufPos == BufferBaseDigest.this.BlockSize) {
            BufferBaseDigest.this.processBytes(BufferBaseDigest.this.m_buf, 0);
            BufferBaseDigest.this.m_bufPos = 0;
         }
      }

      @Override
      public boolean isLengthWithinAvailableSpace(int var1, int var2) {
         return var1 < var2;
      }

      @Override
      public boolean isLengthExceedingBlockSize(int var1, int var2) {
         return var1 >= var2;
      }
   }

   protected interface ProcessingBuffer {
      void update(byte var1);

      boolean isLengthWithinAvailableSpace(int var1, int var2);

      boolean isLengthExceedingBlockSize(int var1, int var2);
   }

   protected static class ProcessingBufferType {
      public static final int BUFFERED = 0;
      public static final int IMMEDIATE = 1;
      public static final BufferBaseDigest.ProcessingBufferType Buffered = new BufferBaseDigest.ProcessingBufferType(0);
      public static final BufferBaseDigest.ProcessingBufferType Immediate = new BufferBaseDigest.ProcessingBufferType(1);
      private final int ord;

      ProcessingBufferType(int var1) {
         this.ord = var1;
      }
   }
}
