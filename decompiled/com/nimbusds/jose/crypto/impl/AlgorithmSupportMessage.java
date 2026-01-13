package com.nimbusds.jose.crypto.impl;

import com.nimbusds.jose.EncryptionMethod;
import com.nimbusds.jose.JWEAlgorithm;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.jwk.Curve;
import java.util.Collection;

public class AlgorithmSupportMessage {
   private static String itemize(Collection collection) {
      StringBuilder sb = new StringBuilder();
      Object[] items = collection.toArray();

      for (int i = 0; i < items.length; i++) {
         if (i != 0) {
            if (i < items.length - 1) {
               sb.append(", ");
            } else if (i == items.length - 1) {
               sb.append(" or ");
            }
         }

         sb.append(items[i].toString());
      }

      return sb.toString();
   }

   public static String unsupportedJWSAlgorithm(JWSAlgorithm unsupported, Collection<JWSAlgorithm> supported) {
      return "Unsupported JWS algorithm " + unsupported + ", must be " + itemize(supported);
   }

   public static String unsupportedJWEAlgorithm(JWEAlgorithm unsupported, Collection<JWEAlgorithm> supported) {
      return "Unsupported JWE algorithm " + unsupported + ", must be " + itemize(supported);
   }

   public static String unsupportedEncryptionMethod(EncryptionMethod unsupported, Collection<EncryptionMethod> supported) {
      return "Unsupported JWE encryption method " + unsupported + ", must be " + itemize(supported);
   }

   public static String unsupportedEllipticCurve(Curve unsupported, Collection<Curve> supported) {
      return "Unsupported elliptic curve " + unsupported + ", must be " + itemize(supported);
   }

   private AlgorithmSupportMessage() {
   }
}
