package org.bouncycastle.crypto.params;

import org.bouncycastle.crypto.DerivationParameters;
import org.bouncycastle.util.Arrays;

public class HKDFParameters implements DerivationParameters {
   private final byte[] ikm;
   private final boolean skipExtract;
   private final byte[] salt;
   private final byte[] info;

   private HKDFParameters(byte[] var1, boolean var2, byte[] var3, byte[] var4) {
      if (var1 == null) {
         throw new IllegalArgumentException("IKM (input keying material) should not be null");
      } else {
         this.ikm = Arrays.clone(var1);
         this.skipExtract = var2;
         if (var3 != null && var3.length != 0) {
            this.salt = Arrays.clone(var3);
         } else {
            this.salt = null;
         }

         if (var4 == null) {
            this.info = new byte[0];
         } else {
            this.info = Arrays.clone(var4);
         }
      }
   }

   public HKDFParameters(byte[] var1, byte[] var2, byte[] var3) {
      this(var1, false, var2, var3);
   }

   public static HKDFParameters skipExtractParameters(byte[] var0, byte[] var1) {
      return new HKDFParameters(var0, true, null, var1);
   }

   public static HKDFParameters defaultParameters(byte[] var0) {
      return new HKDFParameters(var0, false, null, null);
   }

   public byte[] getIKM() {
      return Arrays.clone(this.ikm);
   }

   public boolean skipExtract() {
      return this.skipExtract;
   }

   public byte[] getSalt() {
      return Arrays.clone(this.salt);
   }

   public byte[] getInfo() {
      return Arrays.clone(this.info);
   }
}
