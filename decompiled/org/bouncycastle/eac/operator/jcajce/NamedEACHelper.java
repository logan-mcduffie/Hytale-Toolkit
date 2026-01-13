package org.bouncycastle.eac.operator.jcajce;

import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.Signature;

class NamedEACHelper extends EACHelper {
   private final String providerName;

   NamedEACHelper(String var1) {
      this.providerName = var1;
   }

   @Override
   protected Signature createSignature(String var1) throws NoSuchProviderException, NoSuchAlgorithmException {
      return Signature.getInstance(var1, this.providerName);
   }
}
