package org.bouncycastle.eac.jcajce;

import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.Provider;

class ProviderEACHelper implements EACHelper {
   private final Provider provider;

   ProviderEACHelper(Provider var1) {
      this.provider = var1;
   }

   @Override
   public KeyFactory createKeyFactory(String var1) throws NoSuchAlgorithmException {
      return KeyFactory.getInstance(var1, this.provider);
   }
}
