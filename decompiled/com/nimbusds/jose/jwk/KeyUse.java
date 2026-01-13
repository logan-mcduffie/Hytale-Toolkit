package com.nimbusds.jose.jwk;

import java.io.Serializable;
import java.security.cert.X509Certificate;
import java.text.ParseException;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

public final class KeyUse implements Serializable {
   private static final long serialVersionUID = 1L;
   public static final KeyUse SIGNATURE = new KeyUse("sig");
   public static final KeyUse ENCRYPTION = new KeyUse("enc");
   private final String identifier;

   public KeyUse(String identifier) {
      if (identifier == null) {
         throw new IllegalArgumentException("The key use identifier must not be null");
      } else {
         this.identifier = identifier;
      }
   }

   public String identifier() {
      return this.identifier;
   }

   public String getValue() {
      return this.identifier();
   }

   @Override
   public String toString() {
      return this.identifier();
   }

   @Override
   public boolean equals(Object o) {
      if (this == o) {
         return true;
      } else if (!(o instanceof KeyUse)) {
         return false;
      } else {
         KeyUse keyUse = (KeyUse)o;
         return Objects.equals(this.identifier, keyUse.identifier);
      }
   }

   @Override
   public int hashCode() {
      return Objects.hash(this.identifier);
   }

   public static KeyUse parse(String s) throws ParseException {
      if (s == null) {
         return null;
      } else if (s.equals(SIGNATURE.identifier())) {
         return SIGNATURE;
      } else if (s.equals(ENCRYPTION.identifier())) {
         return ENCRYPTION;
      } else if (s.trim().isEmpty()) {
         throw new ParseException("JWK use value must not be empty or blank", 0);
      } else {
         return new KeyUse(s);
      }
   }

   public static KeyUse from(X509Certificate cert) {
      if (cert.getKeyUsage() == null) {
         return null;
      } else {
         Set<KeyUse> foundUses = new HashSet<>();
         if (cert.getKeyUsage()[0] || cert.getKeyUsage()[1]) {
            foundUses.add(SIGNATURE);
         }

         if (cert.getKeyUsage()[0] && cert.getKeyUsage()[2]) {
            foundUses.add(ENCRYPTION);
         }

         if (cert.getKeyUsage()[0] && cert.getKeyUsage()[4]) {
            foundUses.add(ENCRYPTION);
         }

         if (cert.getKeyUsage()[2] || cert.getKeyUsage()[3] || cert.getKeyUsage()[4]) {
            foundUses.add(ENCRYPTION);
         }

         if (cert.getKeyUsage()[5] || cert.getKeyUsage()[6]) {
            foundUses.add(SIGNATURE);
         }

         return foundUses.size() == 1 ? foundUses.iterator().next() : null;
      }
   }
}
