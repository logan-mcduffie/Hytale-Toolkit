package org.bouncycastle.crypto.io;

import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import org.bouncycastle.crypto.BufferedBlockCipher;
import org.bouncycastle.crypto.InvalidCipherTextException;
import org.bouncycastle.crypto.SkippingCipher;
import org.bouncycastle.crypto.StreamCipher;
import org.bouncycastle.crypto.modes.AEADBlockCipher;
import org.bouncycastle.util.Arrays;

public class CipherInputStream extends FilterInputStream {
   private static final int INPUT_BUF_SIZE = 2048;
   private SkippingCipher skippingCipher;
   private byte[] inBuf;
   private BufferedBlockCipher bufferedBlockCipher;
   private StreamCipher streamCipher;
   private AEADBlockCipher aeadBlockCipher;
   private byte[] buf;
   private byte[] markBuf;
   private int bufOff;
   private int maxBuf;
   private boolean finalized;
   private long markPosition;
   private int markBufOff;

   public CipherInputStream(InputStream var1, BufferedBlockCipher var2) {
      this(var1, var2, 2048);
   }

   public CipherInputStream(InputStream var1, StreamCipher var2) {
      this(var1, var2, 2048);
   }

   public CipherInputStream(InputStream var1, AEADBlockCipher var2) {
      this(var1, var2, 2048);
   }

   public CipherInputStream(InputStream var1, BufferedBlockCipher var2, int var3) {
      super(var1);
      this.bufferedBlockCipher = var2;
      this.inBuf = new byte[var3];
      this.skippingCipher = var2 instanceof SkippingCipher ? (SkippingCipher)var2 : null;
   }

   public CipherInputStream(InputStream var1, StreamCipher var2, int var3) {
      super(var1);
      this.streamCipher = var2;
      this.inBuf = new byte[var3];
      this.skippingCipher = var2 instanceof SkippingCipher ? (SkippingCipher)var2 : null;
   }

   public CipherInputStream(InputStream var1, AEADBlockCipher var2, int var3) {
      super(var1);
      this.aeadBlockCipher = var2;
      this.inBuf = new byte[var3];
      this.skippingCipher = var2 instanceof SkippingCipher ? (SkippingCipher)var2 : null;
   }

   private int nextChunk() throws IOException {
      if (this.finalized) {
         return -1;
      } else {
         this.bufOff = 0;
         this.maxBuf = 0;

         while (this.maxBuf == 0) {
            int var1 = this.in.read(this.inBuf);
            if (var1 == -1) {
               this.finaliseCipher();
               if (this.maxBuf == 0) {
                  return -1;
               }

               return this.maxBuf;
            }

            try {
               this.ensureCapacity(var1, false);
               if (this.bufferedBlockCipher != null) {
                  this.maxBuf = this.bufferedBlockCipher.processBytes(this.inBuf, 0, var1, this.buf, 0);
               } else if (this.aeadBlockCipher != null) {
                  this.maxBuf = this.aeadBlockCipher.processBytes(this.inBuf, 0, var1, this.buf, 0);
               } else {
                  this.streamCipher.processBytes(this.inBuf, 0, var1, this.buf, 0);
                  this.maxBuf = var1;
               }
            } catch (Exception var3) {
               throw new CipherIOException("Error processing stream ", var3);
            }
         }

         return this.maxBuf;
      }
   }

   private void finaliseCipher() throws IOException {
      try {
         this.finalized = true;
         this.ensureCapacity(0, true);
         if (this.bufferedBlockCipher != null) {
            this.maxBuf = this.bufferedBlockCipher.doFinal(this.buf, 0);
         } else if (this.aeadBlockCipher != null) {
            this.maxBuf = this.aeadBlockCipher.doFinal(this.buf, 0);
         } else {
            this.maxBuf = 0;
         }
      } catch (InvalidCipherTextException var2) {
         throw new InvalidCipherTextIOException("Error finalising cipher", var2);
      } catch (Exception var3) {
         throw new IOException("Error finalising cipher " + var3);
      }
   }

   @Override
   public int read() throws IOException {
      return this.bufOff >= this.maxBuf && this.nextChunk() < 0 ? -1 : this.buf[this.bufOff++] & 0xFF;
   }

   @Override
   public int read(byte[] var1) throws IOException {
      return this.read(var1, 0, var1.length);
   }

   @Override
   public int read(byte[] var1, int var2, int var3) throws IOException {
      if (this.bufOff >= this.maxBuf && this.nextChunk() < 0) {
         return -1;
      } else {
         int var4 = Math.min(var3, this.available());
         System.arraycopy(this.buf, this.bufOff, var1, var2, var4);
         this.bufOff += var4;
         return var4;
      }
   }

   @Override
   public long skip(long var1) throws IOException {
      if (var1 <= 0L) {
         return 0L;
      } else if (this.skippingCipher != null) {
         int var8 = this.available();
         if (var1 <= var8) {
            this.bufOff = (int)(this.bufOff + var1);
            return var1;
         } else {
            this.bufOff = this.maxBuf;
            long var4 = this.in.skip(var1 - var8);
            long var6 = this.skippingCipher.skip(var4);
            if (var4 != var6) {
               throw new IOException("Unable to skip cipher " + var4 + " bytes.");
            } else {
               return var4 + var8;
            }
         }
      } else {
         int var3 = (int)Math.min(var1, (long)this.available());
         this.bufOff += var3;
         return var3;
      }
   }

   @Override
   public int available() throws IOException {
      return this.maxBuf - this.bufOff;
   }

   private void ensureCapacity(int var1, boolean var2) {
      int var3 = var1;
      if (var2) {
         if (this.bufferedBlockCipher != null) {
            var3 = this.bufferedBlockCipher.getOutputSize(var1);
         } else if (this.aeadBlockCipher != null) {
            var3 = this.aeadBlockCipher.getOutputSize(var1);
         }
      } else if (this.bufferedBlockCipher != null) {
         var3 = this.bufferedBlockCipher.getUpdateOutputSize(var1);
      } else if (this.aeadBlockCipher != null) {
         var3 = this.aeadBlockCipher.getUpdateOutputSize(var1);
      }

      if (this.buf == null || this.buf.length < var3) {
         this.buf = new byte[var3];
      }
   }

   @Override
   public void close() throws IOException {
      try {
         this.in.close();
      } finally {
         if (!this.finalized) {
            this.finaliseCipher();
         }
      }

      this.maxBuf = this.bufOff = 0;
      this.markBufOff = 0;
      this.markPosition = 0L;
      if (this.markBuf != null) {
         Arrays.fill(this.markBuf, (byte)0);
         this.markBuf = null;
      }

      if (this.buf != null) {
         Arrays.fill(this.buf, (byte)0);
         this.buf = null;
      }

      Arrays.fill(this.inBuf, (byte)0);
   }

   @Override
   public void mark(int var1) {
      this.in.mark(var1);
      if (this.skippingCipher != null) {
         this.markPosition = this.skippingCipher.getPosition();
      }

      if (this.buf != null) {
         this.markBuf = new byte[this.buf.length];
         System.arraycopy(this.buf, 0, this.markBuf, 0, this.buf.length);
      }

      this.markBufOff = this.bufOff;
   }

   @Override
   public void reset() throws IOException {
      if (this.skippingCipher == null) {
         throw new IOException("cipher must implement SkippingCipher to be used with reset()");
      } else {
         this.in.reset();
         this.skippingCipher.seekTo(this.markPosition);
         if (this.markBuf != null) {
            this.buf = this.markBuf;
         }

         this.bufOff = this.markBufOff;
      }
   }

   @Override
   public boolean markSupported() {
      return this.skippingCipher != null ? this.in.markSupported() : false;
   }
}
