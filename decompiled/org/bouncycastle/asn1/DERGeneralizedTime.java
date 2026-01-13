package org.bouncycastle.asn1;

import java.io.IOException;
import java.util.Date;
import org.bouncycastle.util.Strings;

public class DERGeneralizedTime extends ASN1GeneralizedTime {
   public DERGeneralizedTime(byte[] var1) {
      super(var1);
   }

   public DERGeneralizedTime(Date var1) {
      super(var1);
   }

   public DERGeneralizedTime(String var1) {
      super(var1);
   }

   private byte[] getDERTime() {
      if (this.contents[this.contents.length - 1] != 90) {
         return this.contents;
      } else if (!this.hasMinutes()) {
         byte[] var4 = new byte[this.contents.length + 4];
         System.arraycopy(this.contents, 0, var4, 0, this.contents.length - 1);
         System.arraycopy(Strings.toByteArray("0000Z"), 0, var4, this.contents.length - 1, 5);
         return var4;
      } else if (!this.hasSeconds()) {
         byte[] var3 = new byte[this.contents.length + 2];
         System.arraycopy(this.contents, 0, var3, 0, this.contents.length - 1);
         System.arraycopy(Strings.toByteArray("00Z"), 0, var3, this.contents.length - 1, 3);
         return var3;
      } else if (!this.hasFractionalSeconds()) {
         return this.contents;
      } else {
         int var1 = this.contents.length - 2;

         while (var1 > 0 && this.contents[var1] == 48) {
            var1--;
         }

         if (this.contents[var1] == 46) {
            byte[] var5 = new byte[var1 + 1];
            System.arraycopy(this.contents, 0, var5, 0, var1);
            var5[var1] = 90;
            return var5;
         } else {
            byte[] var2 = new byte[var1 + 2];
            System.arraycopy(this.contents, 0, var2, 0, var1 + 1);
            var2[var1 + 1] = 90;
            return var2;
         }
      }
   }

   @Override
   int encodedLength(boolean var1) {
      return ASN1OutputStream.getLengthOfEncodingDL(var1, this.getDERTime().length);
   }

   @Override
   void encode(ASN1OutputStream var1, boolean var2) throws IOException {
      var1.writeEncodingDL(var2, 24, this.getDERTime());
   }

   @Override
   ASN1Primitive toDERObject() {
      return this;
   }

   @Override
   ASN1Primitive toDLObject() {
      return this;
   }
}
