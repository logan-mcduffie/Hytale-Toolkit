package org.bouncycastle.crypto;

import org.bouncycastle.crypto.params.AsymmetricKeyParameter;

public class AsymmetricCipherKeyPair {
   private AsymmetricKeyParameter publicParam;
   private AsymmetricKeyParameter privateParam;

   public AsymmetricCipherKeyPair(AsymmetricKeyParameter var1, AsymmetricKeyParameter var2) {
      this.publicParam = var1;
      this.privateParam = var2;
   }

   @Deprecated
   public AsymmetricCipherKeyPair(CipherParameters var1, CipherParameters var2) {
      this.publicParam = (AsymmetricKeyParameter)var1;
      this.privateParam = (AsymmetricKeyParameter)var2;
   }

   public AsymmetricKeyParameter getPublic() {
      return this.publicParam;
   }

   public AsymmetricKeyParameter getPrivate() {
      return this.privateParam;
   }
}
