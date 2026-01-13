package com.google.crypto.tink.hybrid;

import com.google.crypto.tink.proto.HashType;
import java.security.NoSuchAlgorithmException;

final class HybridUtil {
   public static String toHmacAlgo(HashType hash) throws NoSuchAlgorithmException {
      switch (hash) {
         case SHA1:
            return "HmacSha1";
         case SHA224:
            return "HmacSha224";
         case SHA256:
            return "HmacSha256";
         case SHA384:
            return "HmacSha384";
         case SHA512:
            return "HmacSha512";
         default:
            throw new NoSuchAlgorithmException("hash unsupported for HMAC: " + hash);
      }
   }

   private HybridUtil() {
   }
}
