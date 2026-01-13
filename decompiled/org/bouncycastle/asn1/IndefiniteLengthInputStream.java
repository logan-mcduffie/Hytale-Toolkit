package org.bouncycastle.asn1;

import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;

class IndefiniteLengthInputStream extends LimitedInputStream {
   private int _b1;
   private int _b2;
   private boolean _eofReached = false;
   private boolean _eofOn00 = true;

   IndefiniteLengthInputStream(InputStream var1, int var2) throws IOException {
      super(var1, var2);
      this._b1 = var1.read();
      this._b2 = var1.read();
      if (this._b2 < 0) {
         throw new EOFException();
      } else {
         this.checkForEof();
      }
   }

   void setEofOn00(boolean var1) {
      this._eofOn00 = var1;
      this.checkForEof();
   }

   private boolean checkForEof() {
      if (!this._eofReached && this._eofOn00 && this._b1 == 0 && this._b2 == 0) {
         this._eofReached = true;
         this.setParentEofDetect(true);
      }

      return this._eofReached;
   }

   @Override
   public int read(byte[] var1, int var2, int var3) throws IOException {
      if (this._eofOn00 || var3 < 3) {
         return super.read(var1, var2, var3);
      } else if (this._eofReached) {
         return -1;
      } else {
         int var4 = this._in.read(var1, var2 + 2, var3 - 2);
         if (var4 < 0) {
            throw new EOFException();
         } else {
            var1[var2] = (byte)this._b1;
            var1[var2 + 1] = (byte)this._b2;
            this._b1 = this._in.read();
            this._b2 = this._in.read();
            if (this._b2 < 0) {
               throw new EOFException();
            } else {
               return var4 + 2;
            }
         }
      }
   }

   @Override
   public int read() throws IOException {
      if (this.checkForEof()) {
         return -1;
      } else {
         int var1 = this._in.read();
         if (var1 < 0) {
            throw new EOFException();
         } else {
            int var2 = this._b1;
            this._b1 = this._b2;
            this._b2 = var1;
            return var2;
         }
      }
   }
}
