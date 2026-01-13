package org.bouncycastle.jcajce.spec;

/** @deprecated */
public class KEMParameterSpec extends KTSParameterSpec {
   public KEMParameterSpec(String var1) {
      this(var1, 256);
   }

   public KEMParameterSpec(String var1, int var2) {
      super(var1, var2, null, null, null);
   }

   public int getKeySizeInBits() {
      return this.getKeySize();
   }
}
