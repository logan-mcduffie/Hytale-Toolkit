package org.bouncycastle.crypto.util;

import org.bouncycastle.asn1.ASN1ObjectIdentifier;

public abstract class PBKDFConfig {
   private final ASN1ObjectIdentifier algorithm;

   protected PBKDFConfig(ASN1ObjectIdentifier var1) {
      this.algorithm = var1;
   }

   public ASN1ObjectIdentifier getAlgorithm() {
      return this.algorithm;
   }
}
