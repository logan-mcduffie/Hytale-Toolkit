package org.bouncycastle.asn1;

import java.io.IOException;

public class BERBitString extends ASN1BitString {
   private static final int DEFAULT_SEGMENT_LIMIT = 1000;
   private final int segmentLimit;
   private final ASN1BitString[] elements;

   static byte[] flattenBitStrings(ASN1BitString[] var0) {
      int var1 = var0.length;
      switch (var1) {
         case 0:
            return new byte[]{0};
         case 1:
            return var0[0].contents;
         default:
            int var2 = var1 - 1;
            int var3 = 0;

            for (int var4 = 0; var4 < var2; var4++) {
               byte[] var5 = var0[var4].contents;
               if (var5[0] != 0) {
                  throw new IllegalArgumentException("only the last nested bitstring can have padding");
               }

               var3 += var5.length - 1;
            }

            byte[] var12 = var0[var2].contents;
            byte var13 = var12[0];
            var3 += var12.length;
            byte[] var6 = new byte[var3];
            var6[0] = var13;
            int var7 = 1;

            for (int var8 = 0; var8 < var1; var8++) {
               byte[] var9 = var0[var8].contents;
               int var10 = var9.length - 1;
               System.arraycopy(var9, 1, var6, var7, var10);
               var7 += var10;
            }

            return var6;
      }
   }

   public BERBitString(byte[] var1) {
      this(var1, 0);
   }

   public BERBitString(byte var1, int var2) {
      super(var1, var2);
      this.elements = null;
      this.segmentLimit = 1000;
   }

   public BERBitString(byte[] var1, int var2) {
      this(var1, var2, 1000);
   }

   public BERBitString(byte[] var1, int var2, int var3) {
      super(var1, var2);
      this.elements = null;
      this.segmentLimit = var3;
   }

   public BERBitString(ASN1Encodable var1) throws IOException {
      this(var1.toASN1Primitive().getEncoded("DER"), 0);
   }

   public BERBitString(ASN1BitString[] var1) {
      this(var1, 1000);
   }

   public BERBitString(ASN1BitString[] var1, int var2) {
      super(flattenBitStrings(var1), false);
      this.elements = var1;
      this.segmentLimit = var2;
   }

   BERBitString(byte[] var1, boolean var2) {
      super(var1, var2);
      this.elements = null;
      this.segmentLimit = 1000;
   }

   @Override
   boolean encodeConstructed() {
      return null != this.elements || this.contents.length > this.segmentLimit;
   }

   @Override
   int encodedLength(boolean var1) throws IOException {
      if (!this.encodeConstructed()) {
         return DLBitString.encodedLength(var1, this.contents.length);
      } else {
         int var2 = var1 ? 4 : 3;
         if (null != this.elements) {
            for (int var3 = 0; var3 < this.elements.length; var3++) {
               var2 += this.elements[var3].encodedLength(true);
            }
         } else if (this.contents.length >= 2) {
            int var6 = (this.contents.length - 2) / (this.segmentLimit - 1);
            var2 += var6 * DLBitString.encodedLength(true, this.segmentLimit);
            int var4 = this.contents.length - var6 * (this.segmentLimit - 1);
            var2 += DLBitString.encodedLength(true, var4);
         }

         return var2;
      }
   }

   @Override
   void encode(ASN1OutputStream var1, boolean var2) throws IOException {
      if (!this.encodeConstructed()) {
         DLBitString.encode(var1, var2, this.contents, 0, this.contents.length);
      } else {
         var1.writeIdentifier(var2, 35);
         var1.write(128);
         if (null != this.elements) {
            var1.writePrimitives(this.elements);
         } else if (this.contents.length >= 2) {
            byte var3 = this.contents[0];
            int var4 = this.contents.length;
            int var5 = var4 - 1;

            for (int var6 = this.segmentLimit - 1; var5 > var6; var5 -= var6) {
               DLBitString.encode(var1, true, (byte)0, this.contents, var4 - var5, var6);
            }

            DLBitString.encode(var1, true, var3, this.contents, var4 - var5, var5);
         }

         var1.write(0);
         var1.write(0);
      }
   }
}
