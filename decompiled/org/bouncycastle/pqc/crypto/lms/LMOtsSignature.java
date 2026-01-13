package org.bouncycastle.pqc.crypto.lms;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import org.bouncycastle.util.Encodable;
import org.bouncycastle.util.io.Streams;

class LMOtsSignature implements Encodable {
   private final LMOtsParameters type;
   private final byte[] C;
   private final byte[] y;

   public LMOtsSignature(LMOtsParameters var1, byte[] var2, byte[] var3) {
      this.type = var1;
      this.C = var2;
      this.y = var3;
   }

   public static LMOtsSignature getInstance(Object var0) throws IOException {
      if (var0 instanceof LMOtsSignature) {
         return (LMOtsSignature)var0;
      } else if (var0 instanceof DataInputStream) {
         LMOtsParameters var7 = LMOtsParameters.getParametersForType(((DataInputStream)var0).readInt());
         byte[] var8 = new byte[var7.getN()];
         ((DataInputStream)var0).readFully(var8);
         byte[] var3 = new byte[var7.getP() * var7.getN()];
         ((DataInputStream)var0).readFully(var3);
         return new LMOtsSignature(var7, var8, var3);
      } else if (var0 instanceof byte[]) {
         DataInputStream var1 = null;

         LMOtsSignature var2;
         try {
            var1 = new DataInputStream(new ByteArrayInputStream((byte[])var0));
            var2 = getInstance(var1);
         } finally {
            if (var1 != null) {
               var1.close();
            }
         }

         return var2;
      } else if (var0 instanceof InputStream) {
         return getInstance(Streams.readAll((InputStream)var0));
      } else {
         throw new IllegalArgumentException("cannot parse " + var0);
      }
   }

   public LMOtsParameters getType() {
      return this.type;
   }

   public byte[] getC() {
      return this.C;
   }

   public byte[] getY() {
      return this.y;
   }

   @Override
   public boolean equals(Object var1) {
      if (this == var1) {
         return true;
      } else if (var1 != null && this.getClass() == var1.getClass()) {
         LMOtsSignature var2 = (LMOtsSignature)var1;
         if (this.type != null ? this.type.equals(var2.type) : var2.type == null) {
            return !Arrays.equals(this.C, var2.C) ? false : Arrays.equals(this.y, var2.y);
         } else {
            return false;
         }
      } else {
         return false;
      }
   }

   @Override
   public int hashCode() {
      int var1 = this.type != null ? this.type.hashCode() : 0;
      var1 = 31 * var1 + Arrays.hashCode(this.C);
      return 31 * var1 + Arrays.hashCode(this.y);
   }

   @Override
   public byte[] getEncoded() throws IOException {
      return Composer.compose().u32str(this.type.getType()).bytes(this.C).bytes(this.y).build();
   }
}
