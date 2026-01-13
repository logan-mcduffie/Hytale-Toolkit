package org.bouncycastle.asn1;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;

public abstract class DERGenerator extends ASN1Generator {
   private boolean _tagged = false;
   private boolean _isExplicit;
   private int _tagNo;

   protected DERGenerator(OutputStream var1) {
      super(var1);
   }

   public DERGenerator(OutputStream var1, int var2, boolean var3) {
      super(var1);
      this._tagged = true;
      this._isExplicit = var3;
      this._tagNo = var2;
   }

   private void writeLength(OutputStream var1, int var2) throws IOException {
      if (var2 > 127) {
         int var3 = 1;
         int var4 = var2;

         while ((var4 >>>= 8) != 0) {
            var3++;
         }

         var1.write((byte)(var3 | 128));

         for (int var5 = (var3 - 1) * 8; var5 >= 0; var5 -= 8) {
            var1.write((byte)(var2 >> var5));
         }
      } else {
         var1.write((byte)var2);
      }
   }

   void writeDEREncoded(OutputStream var1, int var2, byte[] var3) throws IOException {
      var1.write(var2);
      this.writeLength(var1, var3.length);
      var1.write(var3);
   }

   void writeDEREncoded(int var1, byte[] var2) throws IOException {
      if (this._tagged) {
         int var3 = this._tagNo | 128;
         if (this._isExplicit) {
            int var4 = this._tagNo | 32 | 128;
            ByteArrayOutputStream var5 = new ByteArrayOutputStream();
            this.writeDEREncoded(var5, var1, var2);
            this.writeDEREncoded(this._out, var4, var5.toByteArray());
         } else if ((var1 & 32) != 0) {
            this.writeDEREncoded(this._out, var3 | 32, var2);
         } else {
            this.writeDEREncoded(this._out, var3, var2);
         }
      } else {
         this.writeDEREncoded(this._out, var1, var2);
      }
   }
}
