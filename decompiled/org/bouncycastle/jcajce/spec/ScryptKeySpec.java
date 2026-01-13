package org.bouncycastle.jcajce.spec;

import java.security.spec.KeySpec;
import org.bouncycastle.util.Arrays;

public class ScryptKeySpec implements KeySpec {
   private final char[] password;
   private final byte[] salt;
   private final int costParameter;
   private final int blockSize;
   private final int parallelizationParameter;
   private final int keySize;

   public ScryptKeySpec(char[] var1, byte[] var2, int var3, int var4, int var5, int var6) {
      this.password = var1;
      this.salt = Arrays.clone(var2);
      this.costParameter = var3;
      this.blockSize = var4;
      this.parallelizationParameter = var5;
      this.keySize = var6;
   }

   public char[] getPassword() {
      return this.password;
   }

   public byte[] getSalt() {
      return Arrays.clone(this.salt);
   }

   public int getCostParameter() {
      return this.costParameter;
   }

   public int getBlockSize() {
      return this.blockSize;
   }

   public int getParallelizationParameter() {
      return this.parallelizationParameter;
   }

   public int getKeyLength() {
      return this.keySize;
   }
}
