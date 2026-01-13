package io.netty.handler.codec.quic;

final class GroupsConverter {
   static String toBoringSSL(String key) {
      switch (key) {
         case "secp224r1":
            return "P-224";
         case "prime256v1":
         case "secp256r1":
            return "P-256";
         case "secp384r1":
            return "P-384";
         case "secp521r1":
            return "P-521";
         case "x25519":
            return "X25519";
         default:
            return key;
      }
   }

   private GroupsConverter() {
   }
}
