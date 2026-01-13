package org.bouncycastle.pqc.crypto.lms;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import org.bouncycastle.util.io.Streams;

public class HSSPublicKeyParameters extends LMSKeyParameters implements LMSContextBasedVerifier {
   private final int l;
   private final LMSPublicKeyParameters lmsPublicKey;

   public HSSPublicKeyParameters(int var1, LMSPublicKeyParameters var2) {
      super(false);
      if (var2 == null) {
         throw new NullPointerException("lmsPublicKey");
      } else {
         this.l = var1;
         this.lmsPublicKey = var2;
      }
   }

   public static HSSPublicKeyParameters getInstance(Object var0) throws IOException {
      if (var0 instanceof HSSPublicKeyParameters) {
         return (HSSPublicKeyParameters)var0;
      } else if (var0 instanceof DataInputStream) {
         int var6 = ((DataInputStream)var0).readInt();
         LMSPublicKeyParameters var7 = LMSPublicKeyParameters.getInstance(var0);
         return new HSSPublicKeyParameters(var6, var7);
      } else if (var0 instanceof byte[]) {
         DataInputStream var1 = null;

         HSSPublicKeyParameters var2;
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

   public int getL() {
      return this.l;
   }

   public LMSPublicKeyParameters getLMSPublicKey() {
      return this.lmsPublicKey;
   }

   @Override
   public boolean equals(Object var1) {
      if (this == var1) {
         return true;
      } else if (var1 != null && this.getClass() == var1.getClass()) {
         HSSPublicKeyParameters var2 = (HSSPublicKeyParameters)var1;
         return this.l != var2.l ? false : this.lmsPublicKey.equals(var2.lmsPublicKey);
      } else {
         return false;
      }
   }

   @Override
   public int hashCode() {
      int var1 = this.l;
      return 31 * var1 + this.lmsPublicKey.hashCode();
   }

   @Override
   public byte[] getEncoded() throws IOException {
      return Composer.compose().u32str(this.l).bytes(this.lmsPublicKey.getEncoded()).build();
   }

   @Override
   public LMSContext generateLMSContext(byte[] var1) {
      HSSSignature var2;
      try {
         var2 = HSSSignature.getInstance(var1, this.getL());
      } catch (IOException var5) {
         throw new IllegalStateException("cannot parse signature: " + var5.getMessage());
      }

      LMSSignedPubKey[] var3 = var2.getSignedPubKey();
      LMSPublicKeyParameters var4;
      if (var3.length != 0) {
         var4 = var3[var3.length - 1].getPublicKey();
      } else {
         var4 = this.getLMSPublicKey();
      }

      return var4.generateOtsContext(var2.getSignature()).withSignedPublicKeys(var3);
   }

   @Override
   public boolean verify(LMSContext var1) {
      boolean var2 = true;
      LMSSignedPubKey[] var3 = var1.getSignedPubKeys();
      if (var3.length != this.getL() - 1) {
         return false;
      } else {
         LMSPublicKeyParameters var4 = this.getLMSPublicKey();

         for (int var5 = 0; var5 < var3.length; var5++) {
            LMSSignature var6 = var3[var5].getSignature();
            byte[] var7 = var3[var5].getPublicKey().toByteArray();
            var2 &= LMS.verifySignature(var4, var6, var7);
            var4 = var3[var5].getPublicKey();
         }

         return var2 & var4.verify(var1);
      }
   }
}
