package com.nimbusds.jose.crypto.impl;

import com.nimbusds.jose.EncryptionMethod;
import com.nimbusds.jose.JWEAlgorithm;
import com.nimbusds.jose.KeyLengthException;
import com.nimbusds.jose.util.ByteUtils;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;
import javax.crypto.SecretKey;

public abstract class DirectCryptoProvider extends BaseJWEProvider {
   public static final Set<JWEAlgorithm> SUPPORTED_ALGORITHMS;
   public static final Set<EncryptionMethod> SUPPORTED_ENCRYPTION_METHODS = ContentCryptoProvider.SUPPORTED_ENCRYPTION_METHODS;

   private static Set<EncryptionMethod> getCompatibleEncryptionMethods(int cekBitLength) throws KeyLengthException {
      if (cekBitLength == 0) {
         return EncryptionMethod.Family.AES_GCM;
      } else {
         Set<EncryptionMethod> encs = ContentCryptoProvider.COMPATIBLE_ENCRYPTION_METHODS.get(cekBitLength);
         if (encs == null) {
            throw new KeyLengthException(
               "The Content Encryption Key length must be 128 bits (16 bytes), 192 bits (24 bytes), 256 bits (32 bytes), 384 bits (48 bytes) or 512 bites (64 bytes)"
            );
         } else {
            return encs;
         }
      }
   }

   protected DirectCryptoProvider(SecretKey cek) throws KeyLengthException {
      super(SUPPORTED_ALGORITHMS, getCompatibleEncryptionMethods(ByteUtils.bitLength(cek.getEncoded())), cek);
   }

   public SecretKey getKey() {
      try {
         return this.getCEK(null);
      } catch (Exception var2) {
         return null;
      }
   }

   static {
      Set<JWEAlgorithm> algs = new LinkedHashSet<>();
      algs.add(JWEAlgorithm.DIR);
      SUPPORTED_ALGORITHMS = Collections.unmodifiableSet(algs);
   }
}
