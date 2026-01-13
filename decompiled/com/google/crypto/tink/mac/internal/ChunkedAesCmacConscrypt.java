package com.google.crypto.tink.mac.internal;

import com.google.crypto.tink.AccessesPartialKey;
import com.google.crypto.tink.InsecureSecretKeyAccess;
import com.google.crypto.tink.config.internal.TinkFipsUtil;
import com.google.crypto.tink.internal.Util;
import com.google.crypto.tink.mac.AesCmacKey;
import com.google.crypto.tink.mac.AesCmacParameters;
import com.google.crypto.tink.mac.ChunkedMac;
import com.google.crypto.tink.mac.ChunkedMacComputation;
import com.google.crypto.tink.mac.ChunkedMacVerification;
import com.google.crypto.tink.subtle.Bytes;
import com.google.errorprone.annotations.Immutable;
import java.nio.ByteBuffer;
import java.security.GeneralSecurityException;
import java.security.NoSuchAlgorithmException;
import java.security.Provider;
import java.util.Arrays;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

@Immutable
public final class ChunkedAesCmacConscrypt implements ChunkedMac {
   private static final TinkFipsUtil.AlgorithmFipsCompatibility FIPS = TinkFipsUtil.AlgorithmFipsCompatibility.ALGORITHM_NOT_FIPS;
   private final byte[] outputPrefix;
   private final AesCmacParameters parameters;
   private final SecretKeySpec secretKeySpec;
   private final Provider conscrypt;

   @AccessesPartialKey
   private static SecretKeySpec toSecretKeySpec(AesCmacKey key) {
      return new SecretKeySpec(key.getAesKey().toByteArray(InsecureSecretKeyAccess.get()), "AES");
   }

   private ChunkedAesCmacConscrypt(AesCmacKey key, Provider conscrypt) throws GeneralSecurityException {
      if (conscrypt == null) {
         throw new IllegalArgumentException("conscrypt is null");
      } else if (!FIPS.isCompatible()) {
         throw new GeneralSecurityException("Cannot use AES-CMAC in FIPS-mode.");
      } else {
         try {
            Mac e = Mac.getInstance("AESCMAC", conscrypt);
         } catch (NoSuchAlgorithmException var4) {
            throw new GeneralSecurityException("AES-CMAC not available.", var4);
         }

         this.conscrypt = conscrypt;
         this.outputPrefix = key.getOutputPrefix().toByteArray();
         this.parameters = key.getParameters();
         this.secretKeySpec = toSecretKeySpec(key);
      }
   }

   @Override
   public ChunkedMacComputation createComputation() throws GeneralSecurityException {
      return new ChunkedAesCmacConscrypt.AesCmacComputation(this.secretKeySpec, this.parameters, this.outputPrefix, this.conscrypt);
   }

   @Override
   public ChunkedMacVerification createVerification(final byte[] tag) throws GeneralSecurityException {
      if (!Util.isPrefix(this.outputPrefix, tag)) {
         throw new GeneralSecurityException("Wrong tag prefix");
      } else {
         return ChunkedMacVerificationFromComputation.create(this.createComputation(), tag);
      }
   }

   public static ChunkedMac create(AesCmacKey key, Provider conscrypt) throws GeneralSecurityException {
      return new ChunkedAesCmacConscrypt(key, conscrypt);
   }

   private static final class AesCmacComputation implements ChunkedMacComputation {
      private static final byte[] legacyFormatVersion = new byte[]{0};
      private final byte[] outputPrefix;
      private final AesCmacParameters parameters;
      private final Mac aesCmac;
      private boolean finalized = false;

      private AesCmacComputation(SecretKeySpec secretKeySpec, AesCmacParameters parameters, byte[] outputPrefix, Provider conscrypt) throws GeneralSecurityException {
         this.parameters = parameters;
         this.outputPrefix = outputPrefix;
         this.aesCmac = Mac.getInstance("AESCMAC", conscrypt);
         this.aesCmac.init(secretKeySpec);
      }

      @Override
      public void update(ByteBuffer data) {
         if (this.finalized) {
            throw new IllegalStateException("Cannot update after computing the MAC tag. Please create a new object.");
         } else {
            this.aesCmac.update(data);
         }
      }

      @Override
      public byte[] computeMac() throws GeneralSecurityException {
         if (this.finalized) {
            throw new IllegalStateException("Cannot compute after computing the MAC tag. Please create a new object.");
         } else {
            this.finalized = true;
            if (this.parameters.getVariant() == AesCmacParameters.Variant.LEGACY) {
               this.aesCmac.update(legacyFormatVersion);
            }

            return Bytes.concat(this.outputPrefix, Arrays.copyOf(this.aesCmac.doFinal(), this.parameters.getCryptographicTagSizeBytes()));
         }
      }
   }
}
