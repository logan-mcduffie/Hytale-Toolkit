package com.google.crypto.tink.signature;

class RsaSsaPssVerifyKeyManager {
   static String getKeyType() {
      return "type.googleapis.com/google.crypto.tink.RsaSsaPssPublicKey";
   }

   private RsaSsaPssVerifyKeyManager() {
   }
}
