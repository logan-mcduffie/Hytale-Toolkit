package com.google.crypto.tink.signature;

final class RsaSsaPkcs1VerifyKeyManager {
   static String getKeyType() {
      return "type.googleapis.com/google.crypto.tink.RsaSsaPkcs1PublicKey";
   }

   private RsaSsaPkcs1VerifyKeyManager() {
   }
}
