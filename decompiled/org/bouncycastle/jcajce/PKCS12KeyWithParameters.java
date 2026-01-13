package org.bouncycastle.jcajce;

import javax.crypto.interfaces.PBEKey;
import org.bouncycastle.util.Arrays;

public class PKCS12KeyWithParameters extends PKCS12Key implements PBEKey {
   private final byte[] salt;
   private final int iterationCount;

   public PKCS12KeyWithParameters(char[] var1, byte[] var2, int var3) {
      super(var1);
      this.salt = Arrays.clone(var2);
      this.iterationCount = var3;
   }

   public PKCS12KeyWithParameters(char[] var1, boolean var2, byte[] var3, int var4) {
      super(var1, var2);
      this.salt = Arrays.clone(var3);
      this.iterationCount = var4;
   }

   @Override
   public byte[] getSalt() {
      return this.salt;
   }

   @Override
   public int getIterationCount() {
      return this.iterationCount;
   }
}
