package org.bouncycastle.pqc.crypto.lms;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import org.bouncycastle.util.Encodable;
import org.bouncycastle.util.io.Streams;

class LMSSignature implements Encodable {
   private final int q;
   private final LMOtsSignature otsSignature;
   private final LMSigParameters parameter;
   private final byte[][] y;

   public LMSSignature(int var1, LMOtsSignature var2, LMSigParameters var3, byte[][] var4) {
      this.q = var1;
      this.otsSignature = var2;
      this.parameter = var3;
      this.y = var4;
   }

   public static LMSSignature getInstance(Object var0) throws IOException {
      if (var0 instanceof LMSSignature) {
         return (LMSSignature)var0;
      } else if (!(var0 instanceof DataInputStream)) {
         if (var0 instanceof byte[]) {
            DataInputStream var9 = null;

            LMSSignature var10;
            try {
               var9 = new DataInputStream(new ByteArrayInputStream((byte[])var0));
               var10 = getInstance(var9);
            } finally {
               if (var9 != null) {
                  var9.close();
               }
            }

            return var10;
         } else if (var0 instanceof InputStream) {
            return getInstance(Streams.readAll((InputStream)var0));
         } else {
            throw new IllegalArgumentException("cannot parse " + var0);
         }
      } else {
         int var1 = ((DataInputStream)var0).readInt();
         LMOtsSignature var2 = LMOtsSignature.getInstance(var0);
         LMSigParameters var3 = LMSigParameters.getParametersForType(((DataInputStream)var0).readInt());
         byte[][] var4 = new byte[var3.getH()][];

         for (int var5 = 0; var5 < var4.length; var5++) {
            var4[var5] = new byte[var3.getM()];
            ((DataInputStream)var0).readFully(var4[var5]);
         }

         return new LMSSignature(var1, var2, var3, var4);
      }
   }

   @Override
   public boolean equals(Object var1) {
      if (this == var1) {
         return true;
      } else if (var1 != null && this.getClass() == var1.getClass()) {
         LMSSignature var2 = (LMSSignature)var1;
         if (this.q != var2.q) {
            return false;
         } else if (this.otsSignature != null ? this.otsSignature.equals(var2.otsSignature) : var2.otsSignature == null) {
            return (this.parameter != null ? this.parameter.equals(var2.parameter) : var2.parameter == null) ? Arrays.deepEquals(this.y, var2.y) : false;
         } else {
            return false;
         }
      } else {
         return false;
      }
   }

   @Override
   public int hashCode() {
      int var1 = this.q;
      var1 = 31 * var1 + (this.otsSignature != null ? this.otsSignature.hashCode() : 0);
      var1 = 31 * var1 + (this.parameter != null ? this.parameter.hashCode() : 0);
      return 31 * var1 + Arrays.deepHashCode(this.y);
   }

   @Override
   public byte[] getEncoded() throws IOException {
      return Composer.compose().u32str(this.q).bytes(this.otsSignature.getEncoded()).u32str(this.parameter.getType()).bytes(this.y).build();
   }

   public int getQ() {
      return this.q;
   }

   public LMOtsSignature getOtsSignature() {
      return this.otsSignature;
   }

   public LMSigParameters getParameter() {
      return this.parameter;
   }

   public byte[][] getY() {
      return this.y;
   }
}
