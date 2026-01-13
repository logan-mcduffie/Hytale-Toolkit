package org.bouncycastle.eac.jcajce;

import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;

class NamedEACHelper implements EACHelper {
   private final String providerName;

   NamedEACHelper(String var1) {
      this.providerName = var1;
   }

   @Override
   public KeyFactory createKeyFactory(String var1) throws NoSuchProviderException, NoSuchAlgorithmException {
      return KeyFactory.getInstance(var1, this.providerName);
   }
}
