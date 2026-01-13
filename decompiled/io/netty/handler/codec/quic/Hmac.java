package io.netty.handler.codec.quic;

import io.netty.util.concurrent.FastThreadLocal;
import java.nio.ByteBuffer;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Arrays;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

final class Hmac {
   private static final FastThreadLocal<Mac> MACS = new FastThreadLocal<Mac>() {
      protected Mac initialValue() {
         return Hmac.newMac();
      }
   };
   private static final String ALGORITM = "HmacSHA256";
   private static final byte[] randomKey = new byte[16];

   private static Mac newMac() {
      try {
         SecretKeySpec keySpec = new SecretKeySpec(randomKey, "HmacSHA256");
         Mac mac = Mac.getInstance("HmacSHA256");
         mac.init(keySpec);
         return mac;
      } catch (InvalidKeyException | NoSuchAlgorithmException var2) {
         throw new IllegalStateException(var2);
      }
   }

   static ByteBuffer sign(ByteBuffer input, int outLength) {
      Mac mac = MACS.get();
      mac.reset();
      mac.update(input);
      byte[] signBytes = mac.doFinal();
      if (signBytes.length != outLength) {
         signBytes = Arrays.copyOf(signBytes, outLength);
      }

      return ByteBuffer.wrap(signBytes);
   }

   private Hmac() {
   }

   static {
      new SecureRandom().nextBytes(randomKey);
   }
}
