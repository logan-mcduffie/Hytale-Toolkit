package org.bouncycastle.crypto.params;

import org.bouncycastle.asn1.ASN1ObjectIdentifier;

public class ECGOST3410Parameters extends ECNamedDomainParameters {
   private final ASN1ObjectIdentifier publicKeyParamSet;
   private final ASN1ObjectIdentifier digestParamSet;
   private final ASN1ObjectIdentifier encryptionParamSet;

   public ECGOST3410Parameters(ECDomainParameters var1, ASN1ObjectIdentifier var2, ASN1ObjectIdentifier var3) {
      this(var1, var2, var3, null);
   }

   public ECGOST3410Parameters(ECDomainParameters var1, ASN1ObjectIdentifier var2, ASN1ObjectIdentifier var3, ASN1ObjectIdentifier var4) {
      super(var2, var1);
      if (var1 instanceof ECNamedDomainParameters && !var2.equals(((ECNamedDomainParameters)var1).getName())) {
         throw new IllegalArgumentException("named parameters do not match publicKeyParamSet value");
      } else {
         this.publicKeyParamSet = var2;
         this.digestParamSet = var3;
         this.encryptionParamSet = var4;
      }
   }

   public ASN1ObjectIdentifier getPublicKeyParamSet() {
      return this.publicKeyParamSet;
   }

   public ASN1ObjectIdentifier getDigestParamSet() {
      return this.digestParamSet;
   }

   public ASN1ObjectIdentifier getEncryptionParamSet() {
      return this.encryptionParamSet;
   }
}
