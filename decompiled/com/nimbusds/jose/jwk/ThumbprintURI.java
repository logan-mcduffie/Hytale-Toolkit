package com.nimbusds.jose.jwk;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.shaded.jcip.Immutable;
import com.nimbusds.jose.util.Base64URL;
import java.net.URI;
import java.net.URISyntaxException;
import java.text.ParseException;
import java.util.Objects;

@Immutable
public class ThumbprintURI {
   public static final String PREFIX = "urn:ietf:params:oauth:jwk-thumbprint:";
   private final String hashAlg;
   private final Base64URL thumbprint;

   public ThumbprintURI(String hashAlg, Base64URL thumbprint) {
      if (hashAlg.isEmpty()) {
         throw new IllegalArgumentException("The hash algorithm must not be empty");
      } else {
         this.hashAlg = hashAlg;
         if (thumbprint.toString().isEmpty()) {
            throw new IllegalArgumentException("The thumbprint must not be empty");
         } else {
            this.thumbprint = thumbprint;
         }
      }
   }

   public String getAlgorithmString() {
      return this.hashAlg;
   }

   public Base64URL getThumbprint() {
      return this.thumbprint;
   }

   public URI toURI() {
      return URI.create(this.toString());
   }

   @Override
   public String toString() {
      return "urn:ietf:params:oauth:jwk-thumbprint:" + this.hashAlg + ":" + this.thumbprint;
   }

   @Override
   public boolean equals(Object o) {
      if (this == o) {
         return true;
      } else if (!(o instanceof ThumbprintURI)) {
         return false;
      } else {
         ThumbprintURI that = (ThumbprintURI)o;
         return this.hashAlg.equals(that.hashAlg) && this.getThumbprint().equals(that.getThumbprint());
      }
   }

   @Override
   public int hashCode() {
      return Objects.hash(this.hashAlg, this.getThumbprint());
   }

   public static ThumbprintURI compute(JWK jwk) throws JOSEException {
      return new ThumbprintURI("sha-256", jwk.computeThumbprint());
   }

   public static ThumbprintURI parse(URI uri) throws ParseException {
      String uriString = uri.toString();
      if (!uriString.startsWith("urn:ietf:params:oauth:jwk-thumbprint:")) {
         throw new ParseException("Illegal JWK thumbprint prefix", 0);
      } else {
         String valuesString = uriString.substring("urn:ietf:params:oauth:jwk-thumbprint:".length());
         if (valuesString.isEmpty()) {
            throw new ParseException("Illegal JWK thumbprint: Missing value", 0);
         } else {
            String[] values = valuesString.split(":");
            if (values.length != 2) {
               throw new ParseException("Illegal JWK thumbprint: Unexpected number of components", 0);
            } else if (values[0].isEmpty()) {
               throw new ParseException("Illegal JWK thumbprint: The hash algorithm must not be empty", 0);
            } else {
               return new ThumbprintURI(values[0], new Base64URL(values[1]));
            }
         }
      }
   }

   public static ThumbprintURI parse(String s) throws ParseException {
      try {
         return parse(new URI(s));
      } catch (URISyntaxException var2) {
         throw new ParseException(var2.getMessage(), 0);
      }
   }
}
