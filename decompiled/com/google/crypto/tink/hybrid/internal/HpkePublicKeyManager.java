package com.google.crypto.tink.hybrid.internal;

public final class HpkePublicKeyManager {
   static String getKeyType() {
      return "type.googleapis.com/google.crypto.tink.HpkePublicKey";
   }

   private HpkePublicKeyManager() {
   }
}
