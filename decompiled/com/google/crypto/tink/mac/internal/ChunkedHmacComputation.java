package com.google.crypto.tink.mac.internal;

import com.google.crypto.tink.AccessesPartialKey;
import com.google.crypto.tink.InsecureSecretKeyAccess;
import com.google.crypto.tink.mac.ChunkedMacComputation;
import com.google.crypto.tink.mac.HmacKey;
import com.google.crypto.tink.mac.HmacParameters;
import com.google.crypto.tink.subtle.Bytes;
import com.google.crypto.tink.subtle.EngineFactory;
import java.nio.ByteBuffer;
import java.security.GeneralSecurityException;
import java.util.Arrays;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

@AccessesPartialKey
final class ChunkedHmacComputation implements ChunkedMacComputation {
   private static final byte[] formatVersion = new byte[]{0};
   private final Mac mac;
   private final HmacKey key;
   private boolean finalized = false;

   ChunkedHmacComputation(HmacKey key) throws GeneralSecurityException {
      this.mac = EngineFactory.MAC.getInstance(composeAlgorithmName(key));
      this.mac.init(new SecretKeySpec(key.getKeyBytes().toByteArray(InsecureSecretKeyAccess.get()), "HMAC"));
      this.key = key;
   }

   @Override
   public void update(ByteBuffer data) {
      if (this.finalized) {
         throw new IllegalStateException("Cannot update after computing the MAC tag. Please create a new object.");
      } else {
         this.mac.update(data);
      }
   }

   @Override
   public byte[] computeMac() throws GeneralSecurityException {
      if (this.finalized) {
         throw new IllegalStateException("Cannot compute after already computing the MAC tag. Please create a new object.");
      } else {
         if (this.key.getParameters().getVariant() == HmacParameters.Variant.LEGACY) {
            this.update(ByteBuffer.wrap(formatVersion));
         }

         this.finalized = true;
         return Bytes.concat(
            this.key.getOutputPrefix().toByteArray(), Arrays.copyOf(this.mac.doFinal(), this.key.getParameters().getCryptographicTagSizeBytes())
         );
      }
   }

   private static String composeAlgorithmName(HmacKey key) {
      return "HMAC" + key.getParameters().getHashType();
   }
}
