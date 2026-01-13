package org.bouncycastle.jcajce.spec;

import java.security.spec.AlgorithmParameterSpec;
import org.bouncycastle.internal.asn1.edec.EdECObjectIdentifiers;

public class XDHParameterSpec implements AlgorithmParameterSpec {
   public static final String X25519 = "X25519";
   public static final String X448 = "X448";
   private final String curveName;

   public XDHParameterSpec(String var1) {
      if (var1.equalsIgnoreCase("X25519")) {
         this.curveName = "X25519";
      } else if (var1.equalsIgnoreCase("X448")) {
         this.curveName = "X448";
      } else if (var1.equals(EdECObjectIdentifiers.id_X25519.getId())) {
         this.curveName = "X25519";
      } else {
         if (!var1.equals(EdECObjectIdentifiers.id_X448.getId())) {
            throw new IllegalArgumentException("unrecognized curve name: " + var1);
         }

         this.curveName = "X448";
      }
   }

   public String getCurveName() {
      return this.curveName;
   }
}
