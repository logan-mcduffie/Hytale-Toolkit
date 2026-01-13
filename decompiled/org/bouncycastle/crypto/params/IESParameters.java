package org.bouncycastle.crypto.params;

import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.util.Arrays;

public class IESParameters implements CipherParameters {
   private byte[] derivation;
   private byte[] encoding;
   private int macKeySize;

   public IESParameters(byte[] var1, byte[] var2, int var3) {
      this.derivation = Arrays.clone(var1);
      this.encoding = Arrays.clone(var2);
      this.macKeySize = var3;
   }

   public byte[] getDerivationV() {
      return Arrays.clone(this.derivation);
   }

   public byte[] getEncodingV() {
      return Arrays.clone(this.encoding);
   }

   public int getMacKeySize() {
      return this.macKeySize;
   }
}
