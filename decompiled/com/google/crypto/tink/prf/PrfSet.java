package com.google.crypto.tink.prf;

import com.google.errorprone.annotations.Immutable;
import java.security.GeneralSecurityException;
import java.util.Map;

@Immutable
public abstract class PrfSet {
   public abstract int getPrimaryId();

   public abstract Map<Integer, Prf> getPrfs() throws GeneralSecurityException;

   public byte[] computePrimary(byte[] input, int outputLength) throws GeneralSecurityException {
      return this.getPrfs().get(this.getPrimaryId()).compute(input, outputLength);
   }
}
