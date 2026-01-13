package org.bouncycastle.jcajce;

import javax.crypto.SecretKey;
import org.bouncycastle.util.Arrays;

public final class SecretKeyWithEncapsulation implements SecretKey {
   private final SecretKey secretKey;
   private final byte[] encapsulation;

   public SecretKeyWithEncapsulation(SecretKey var1, byte[] var2) {
      this.secretKey = var1;
      this.encapsulation = Arrays.clone(var2);
   }

   @Override
   public String getAlgorithm() {
      return this.secretKey.getAlgorithm();
   }

   @Override
   public String getFormat() {
      return this.secretKey.getFormat();
   }

   @Override
   public byte[] getEncoded() {
      return this.secretKey.getEncoded();
   }

   public byte[] getEncapsulation() {
      return Arrays.clone(this.encapsulation);
   }

   @Override
   public boolean equals(Object var1) {
      return this.secretKey.equals(var1);
   }

   @Override
   public int hashCode() {
      return this.secretKey.hashCode();
   }
}
