package org.bouncycastle.asn1;

import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import org.bouncycastle.util.io.Streams;

class DefiniteLengthInputStream extends LimitedInputStream {
   private static final byte[] EMPTY_BYTES = new byte[0];
   private final int _originalLength;
   private int _remaining;

   DefiniteLengthInputStream(InputStream var1, int var2, int var3) {
      super(var1, var3);
      if (var2 <= 0) {
         if (var2 < 0) {
            throw new IllegalArgumentException("negative lengths not allowed");
         }

         this.setParentEofDetect(true);
      }

      this._originalLength = var2;
      this._remaining = var2;
   }

   int getRemaining() {
      return this._remaining;
   }

   @Override
   public int read() throws IOException {
      if (this._remaining == 0) {
         return -1;
      } else {
         int var1 = this._in.read();
         if (var1 < 0) {
            throw new EOFException("DEF length " + this._originalLength + " object truncated by " + this._remaining);
         } else {
            if (--this._remaining == 0) {
               this.setParentEofDetect(true);
            }

            return var1;
         }
      }
   }

   @Override
   public int read(byte[] var1, int var2, int var3) throws IOException {
      if (this._remaining == 0) {
         return -1;
      } else {
         int var4 = Math.min(var3, this._remaining);
         int var5 = this._in.read(var1, var2, var4);
         if (var5 < 0) {
            throw new EOFException("DEF length " + this._originalLength + " object truncated by " + this._remaining);
         } else {
            if ((this._remaining -= var5) == 0) {
               this.setParentEofDetect(true);
            }

            return var5;
         }
      }
   }

   void readAllIntoByteArray(byte[] var1) throws IOException {
      if (this._remaining != var1.length) {
         throw new IllegalArgumentException("buffer length not right for data");
      } else if (this._remaining != 0) {
         int var2 = this.getLimit();
         if (this._remaining >= var2) {
            throw new IOException("corrupted stream - out of bounds length found: " + this._remaining + " >= " + var2);
         } else if ((this._remaining = this._remaining - Streams.readFully(this._in, var1, 0, var1.length)) != 0) {
            throw new EOFException("DEF length " + this._originalLength + " object truncated by " + this._remaining);
         } else {
            this.setParentEofDetect(true);
         }
      }
   }

   byte[] toByteArray() throws IOException {
      if (this._remaining == 0) {
         return EMPTY_BYTES;
      } else {
         int var1 = this.getLimit();
         if (this._remaining >= var1) {
            throw new IOException("corrupted stream - out of bounds length found: " + this._remaining + " >= " + var1);
         } else {
            byte[] var2 = new byte[this._remaining];
            if ((this._remaining = this._remaining - Streams.readFully(this._in, var2, 0, var2.length)) != 0) {
               throw new EOFException("DEF length " + this._originalLength + " object truncated by " + this._remaining);
            } else {
               this.setParentEofDetect(true);
               return var2;
            }
         }
      }
   }
}
