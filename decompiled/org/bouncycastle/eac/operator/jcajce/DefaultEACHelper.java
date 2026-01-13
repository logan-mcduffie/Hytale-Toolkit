package org.bouncycastle.eac.operator.jcajce;

import java.security.NoSuchAlgorithmException;
import java.security.Signature;

class DefaultEACHelper extends EACHelper {
   @Override
   protected Signature createSignature(String var1) throws NoSuchAlgorithmException {
      return Signature.getInstance(var1);
   }
}
