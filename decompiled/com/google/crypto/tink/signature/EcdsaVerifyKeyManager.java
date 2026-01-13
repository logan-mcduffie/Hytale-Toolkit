package com.google.crypto.tink.signature;

class EcdsaVerifyKeyManager {
   static String getKeyType() {
      return "type.googleapis.com/google.crypto.tink.EcdsaPublicKey";
   }

   private EcdsaVerifyKeyManager() {
   }
}
