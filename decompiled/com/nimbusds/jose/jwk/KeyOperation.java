package com.nimbusds.jose.jwk;

import java.text.ParseException;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public enum KeyOperation {
   SIGN("sign"),
   VERIFY("verify"),
   ENCRYPT("encrypt"),
   DECRYPT("decrypt"),
   WRAP_KEY("wrapKey"),
   UNWRAP_KEY("unwrapKey"),
   DERIVE_KEY("deriveKey"),
   DERIVE_BITS("deriveBits");

   private final String identifier;

   private KeyOperation(String identifier) {
      if (identifier == null) {
         throw new IllegalArgumentException("The key operation identifier must not be null");
      } else {
         this.identifier = identifier;
      }
   }

   public String identifier() {
      return this.identifier;
   }

   @Override
   public String toString() {
      return this.identifier();
   }

   public static Set<KeyOperation> parse(List<String> sl) throws ParseException {
      if (sl == null) {
         return null;
      } else {
         Set<KeyOperation> keyOps = new LinkedHashSet<>();

         for (String s : sl) {
            if (s != null) {
               KeyOperation parsedOp = null;

               for (KeyOperation op : values()) {
                  if (s.equals(op.identifier())) {
                     parsedOp = op;
                     break;
                  }
               }

               if (parsedOp == null) {
                  throw new ParseException("Invalid JWK operation: " + s, 0);
               }

               keyOps.add(parsedOp);
            }
         }

         return keyOps;
      }
   }
}
