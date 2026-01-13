package org.bouncycastle.jcajce;

import javax.crypto.interfaces.PBEKey;
import org.bouncycastle.crypto.CharToByteConverter;
import org.bouncycastle.util.Arrays;

public class PBKDF1KeyWithParameters extends PBKDF1Key implements PBEKey {
   private final byte[] salt;
   private final int iterationCount;

   public PBKDF1KeyWithParameters(char[] var1, CharToByteConverter var2, byte[] var3, int var4) {
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
