package org.bouncycastle.eac.operator.jcajce;

import java.security.NoSuchAlgorithmException;
import java.security.Provider;
import java.security.Signature;

class ProviderEACHelper extends EACHelper {
   private final Provider provider;

   ProviderEACHelper(Provider var1) {
      this.provider = var1;
   }

   @Override
   protected Signature createSignature(String var1) throws NoSuchAlgorithmException {
      return Signature.getInstance(var1, this.provider);
   }
}
