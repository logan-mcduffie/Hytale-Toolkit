package org.bouncycastle.asn1;

import java.io.IOException;
import java.io.OutputStream;

public class BEROctetStringGenerator extends BERGenerator {
   public BEROctetStringGenerator(OutputStream var1) throws IOException {
      super(var1);
      this.writeBERHeader(36);
   }

   public BEROctetStringGenerator(OutputStream var1, int var2, boolean var3) throws IOException {
      super(var1, var2, var3);
      this.writeBERHeader(36);
   }

   public OutputStream getOctetOutputStream() {
      return this.getOctetOutputStream(new byte[1000]);
   }

   public OutputStream getOctetOutputStream(byte[] var1) {
      return new BEROctetStringGenerator.BufferedBEROctetStream(var1);
   }

   private class BufferedBEROctetStream extends OutputStream {
      private byte[] _buf;
      private int _off;
      private DEROutputStream _derOut;

      BufferedBEROctetStream(byte[] nullx) {
         this._buf = nullx;
         this._off = 0;
         this._derOut = new DEROutputStream(BEROctetStringGenerator.this._out);
      }

      @Override
      public void write(int var1) throws IOException {
         this._buf[this._off++] = (byte)var1;
         if (this._off == this._buf.length) {
            DEROctetString.encode(this._derOut, true, this._buf, 0, this._buf.length);
            this._off = 0;
         }
      }

      @Override
      public void write(byte[] var1, int var2, int var3) throws IOException {
         int var4 = this._buf.length;
         int var5 = var4 - this._off;
         if (var3 < var5) {
            System.arraycopy(var1, var2, this._buf, this._off, var3);
            this._off += var3;
         } else {
            int var6 = 0;
            if (this._off > 0) {
               System.arraycopy(var1, var2, this._buf, this._off, var5);
               var6 += var5;
               DEROctetString.encode(this._derOut, true, this._buf, 0, var4);
            }

            int var7;
            while ((var7 = var3 - var6) >= var4) {
               DEROctetString.encode(this._derOut, true, var1, var2 + var6, var4);
               var6 += var4;
            }

            System.arraycopy(var1, var2 + var6, this._buf, 0, var7);
            this._off = var7;
         }
      }

      @Override
      public void close() throws IOException {
         if (this._off != 0) {
            DEROctetString.encode(this._derOut, true, this._buf, 0, this._off);
         }

         this._derOut.flushInternal();
         BEROctetStringGenerator.this.writeBEREnd();
      }
   }
}
