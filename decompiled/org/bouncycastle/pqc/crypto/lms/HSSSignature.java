package org.bouncycastle.pqc.crypto.lms;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import org.bouncycastle.util.Arrays;
import org.bouncycastle.util.Encodable;
import org.bouncycastle.util.Objects;
import org.bouncycastle.util.io.Streams;

class HSSSignature implements Encodable {
   private final int lMinus1;
   private final LMSSignedPubKey[] signedPubKey;
   private final LMSSignature signature;

   public HSSSignature(int var1, LMSSignedPubKey[] var2, LMSSignature var3) {
      this.lMinus1 = var1;
      this.signedPubKey = var2;
      this.signature = var3;
   }

   public static HSSSignature getInstance(Object var0, int var1) throws IOException {
      if (var0 instanceof HSSSignature) {
         return (HSSSignature)var0;
      } else if (var0 instanceof DataInputStream) {
         int var8 = ((DataInputStream)var0).readInt();
         if (var8 != var1 - 1) {
            throw new IllegalStateException("nspk exceeded maxNspk");
         } else {
            LMSSignedPubKey[] var9 = new LMSSignedPubKey[var8];
            if (var8 != 0) {
               for (int var4 = 0; var4 < var9.length; var4++) {
                  var9[var4] = new LMSSignedPubKey(LMSSignature.getInstance(var0), LMSPublicKeyParameters.getInstance(var0));
               }
            }

            LMSSignature var10 = LMSSignature.getInstance(var0);
            return new HSSSignature(var8, var9, var10);
         }
      } else if (var0 instanceof byte[]) {
         DataInputStream var2 = null;

         HSSSignature var3;
         try {
            var2 = new DataInputStream(new ByteArrayInputStream((byte[])var0));
            var3 = getInstance(var2, var1);
         } finally {
            if (var2 != null) {
               var2.close();
            }
         }

         return var3;
      } else if (var0 instanceof InputStream) {
         return getInstance(Streams.readAll((InputStream)var0), var1);
      } else {
         throw new IllegalArgumentException("cannot parse " + var0);
      }
   }

   public int getlMinus1() {
      return this.lMinus1;
   }

   public LMSSignedPubKey[] getSignedPubKey() {
      return this.signedPubKey;
   }

   public LMSSignature getSignature() {
      return this.signature;
   }

   @Override
   public boolean equals(Object var1) {
      if (this == var1) {
         return true;
      } else if (var1 != null && this.getClass() == var1.getClass()) {
         HSSSignature var2 = (HSSSignature)var1;
         return this.lMinus1 == var2.lMinus1
            && Arrays.areEqual((Object[])this.signedPubKey, (Object[])var2.signedPubKey)
            && Objects.areEqual(this.signature, var2.signature);
      } else {
         return false;
      }
   }

   @Override
   public int hashCode() {
      int var1 = this.lMinus1;
      var1 = 31 * var1 + Arrays.hashCode((Object[])this.signedPubKey);
      return 31 * var1 + Objects.hashCode(this.signature);
   }

   @Override
   public byte[] getEncoded() throws IOException {
      Composer var1 = Composer.compose();
      var1.u32str(this.lMinus1);
      if (this.signedPubKey != null) {
         for (LMSSignedPubKey var5 : this.signedPubKey) {
            var1.bytes(var5);
         }
      }

      var1.bytes(this.signature);
      return var1.build();
   }
}
