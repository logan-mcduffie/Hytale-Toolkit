package org.bouncycastle.jcajce.spec;

import javax.crypto.spec.IvParameterSpec;
import org.bouncycastle.util.Arrays;

public class AEADParameterSpec extends IvParameterSpec {
   private final byte[] associatedData;
   private final int macSizeInBits;

   public AEADParameterSpec(byte[] var1, int var2) {
      this(var1, var2, null);
   }

   public AEADParameterSpec(byte[] var1, int var2, byte[] var3) {
      super(var1);
      this.macSizeInBits = var2;
      this.associatedData = Arrays.clone(var3);
   }

   public int getMacSizeInBits() {
      return this.macSizeInBits;
   }

   public byte[] getAssociatedData() {
      return Arrays.clone(this.associatedData);
   }

   public byte[] getNonce() {
      return this.getIV();
   }
}
