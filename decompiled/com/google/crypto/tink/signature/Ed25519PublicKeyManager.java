package com.google.crypto.tink.signature;

class Ed25519PublicKeyManager {
   static String getKeyType() {
      return "type.googleapis.com/google.crypto.tink.Ed25519PublicKey";
   }

   private Ed25519PublicKeyManager() {
   }
}
