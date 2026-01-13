package org.bouncycastle.crypto.params;

import org.bouncycastle.crypto.CipherParameters;

public class AsymmetricKeyParameter implements CipherParameters {
   boolean privateKey;

   public AsymmetricKeyParameter(boolean var1) {
      this.privateKey = var1;
   }

   public boolean isPrivate() {
      return this.privateKey;
   }
}
