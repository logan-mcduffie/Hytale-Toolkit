package com.nimbusds.jose.crypto.opts;

import com.nimbusds.jose.JWEDecrypterOption;
import com.nimbusds.jose.JWEEncrypterOption;
import com.nimbusds.jose.shaded.jcip.Immutable;

@Immutable
public final class CipherMode implements JWEEncrypterOption, JWEDecrypterOption {
   public static final CipherMode WRAP_UNWRAP = new CipherMode(3, 4);
   public static final CipherMode ENCRYPT_DECRYPT = new CipherMode(1, 2);
   private final int modeForEncryption;
   private final int modeForDecryption;

   private CipherMode(int modeForEncryption, int modeForDecryption) {
      this.modeForEncryption = modeForEncryption;
      this.modeForDecryption = modeForDecryption;
   }

   public int getForJWEEncrypter() {
      return this.modeForEncryption;
   }

   public int getForJWEDecrypter() {
      return this.modeForDecryption;
   }

   @Override
   public String toString() {
      return "CipherMode [forEncryption=" + this.modeForEncryption + ", forDecryption=" + this.modeForDecryption + "]";
   }
}
