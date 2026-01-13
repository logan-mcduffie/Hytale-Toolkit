package org.bouncycastle.pqc.crypto.lms;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import org.bouncycastle.crypto.Digest;
import org.bouncycastle.util.Arrays;
import org.bouncycastle.util.Encodable;
import org.bouncycastle.util.Objects;
import org.bouncycastle.util.io.Streams;

class LMOtsPublicKey implements Encodable {
   private final LMOtsParameters parameter;
   private final byte[] I;
   private final int q;
   private final byte[] K;

   LMOtsPublicKey(LMOtsParameters var1, byte[] var2, int var3, byte[] var4) {
      this.parameter = var1;
      this.I = var2;
      this.q = var3;
      this.K = var4;
   }

   public static LMOtsPublicKey getInstance(Object var0) throws Exception {
      if (var0 instanceof LMOtsPublicKey) {
         return (LMOtsPublicKey)var0;
      } else if (var0 instanceof DataInputStream) {
         LMOtsParameters var8 = LMOtsParameters.getParametersForType(((DataInputStream)var0).readInt());
         byte[] var9 = new byte[16];
         ((DataInputStream)var0).readFully(var9);
         int var3 = ((DataInputStream)var0).readInt();
         byte[] var4 = new byte[var8.getN()];
         ((DataInputStream)var0).readFully(var4);
         return new LMOtsPublicKey(var8, var9, var3, var4);
      } else if (var0 instanceof byte[]) {
         DataInputStream var1 = null;

         LMOtsPublicKey var2;
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

   public LMOtsParameters getParameter() {
      return this.parameter;
   }

   public byte[] getI() {
      return this.I;
   }

   public int getQ() {
      return this.q;
   }

   public byte[] getK() {
      return this.K;
   }

   @Override
   public boolean equals(Object var1) {
      if (this == var1) {
         return true;
      } else if (var1 != null && this.getClass() == var1.getClass()) {
         LMOtsPublicKey var2 = (LMOtsPublicKey)var1;
         return this.q == var2.q && Objects.areEqual(this.parameter, var2.parameter) && Arrays.areEqual(this.I, var2.I) && Arrays.areEqual(this.K, var2.K);
      } else {
         return false;
      }
   }

   @Override
   public int hashCode() {
      int var1 = this.q;
      var1 = 31 * var1 + Objects.hashCode(this.parameter);
      var1 = 31 * var1 + Arrays.hashCode(this.I);
      return 31 * var1 + Arrays.hashCode(this.K);
   }

   @Override
   public byte[] getEncoded() throws IOException {
      return Composer.compose().u32str(this.parameter.getType()).bytes(this.I).u32str(this.q).bytes(this.K).build();
   }

   LMSContext createOtsContext(LMOtsSignature var1) {
      Digest var2 = DigestUtil.getDigest(this.parameter);
      LmsUtils.byteArray(this.I, var2);
      LmsUtils.u32str(this.q, var2);
      LmsUtils.u16str((short)-32383, var2);
      LmsUtils.byteArray(var1.getC(), var2);
      return new LMSContext(this, var1, var2);
   }

   LMSContext createOtsContext(LMSSignature var1) {
      Digest var2 = DigestUtil.getDigest(this.parameter);
      LmsUtils.byteArray(this.I, var2);
      LmsUtils.u32str(this.q, var2);
      LmsUtils.u16str((short)-32383, var2);
      LmsUtils.byteArray(var1.getOtsSignature().getC(), var2);
      return new LMSContext(this, var1, var2);
   }
}
