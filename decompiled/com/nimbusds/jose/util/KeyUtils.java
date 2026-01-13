package com.nimbusds.jose.util;

import javax.crypto.SecretKey;

public class KeyUtils {
   public static SecretKey toAESKey(final SecretKey secretKey) {
      return secretKey != null && !secretKey.getAlgorithm().equals("AES") ? new SecretKey() {
         @Override
         public String getAlgorithm() {
            return "AES";
         }

         @Override
         public String getFormat() {
            return secretKey.getFormat();
         }

         @Override
         public byte[] getEncoded() {
            return secretKey.getEncoded();
         }
      } : secretKey;
   }

   private KeyUtils() {
   }
}
