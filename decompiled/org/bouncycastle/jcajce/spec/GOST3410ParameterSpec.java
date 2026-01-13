package org.bouncycastle.jcajce.spec;

import java.security.spec.AlgorithmParameterSpec;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.cryptopro.CryptoProObjectIdentifiers;
import org.bouncycastle.asn1.cryptopro.ECGOST3410NamedCurves;
import org.bouncycastle.internal.asn1.rosstandart.RosstandartObjectIdentifiers;

public class GOST3410ParameterSpec implements AlgorithmParameterSpec {
   private final ASN1ObjectIdentifier publicKeyParamSet;
   private final ASN1ObjectIdentifier digestParamSet;
   private final ASN1ObjectIdentifier encryptionParamSet;

   public GOST3410ParameterSpec(String var1) {
      this(getOid(var1), getDigestOid(var1), null);
   }

   public GOST3410ParameterSpec(ASN1ObjectIdentifier var1, ASN1ObjectIdentifier var2) {
      this(var1, var2, null);
   }

   public GOST3410ParameterSpec(ASN1ObjectIdentifier var1, ASN1ObjectIdentifier var2, ASN1ObjectIdentifier var3) {
      this.publicKeyParamSet = var1;
      this.digestParamSet = var2;
      this.encryptionParamSet = var3;
   }

   public String getPublicKeyParamSetName() {
      return ECGOST3410NamedCurves.getName(this.getPublicKeyParamSet());
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

   private static ASN1ObjectIdentifier getOid(String var0) {
      return ECGOST3410NamedCurves.getOID(var0);
   }

   private static ASN1ObjectIdentifier getDigestOid(String var0) {
      if (var0.indexOf("12-512") > 0) {
         return RosstandartObjectIdentifiers.id_tc26_gost_3411_12_512;
      } else {
         return var0.indexOf("12-256") > 0 ? RosstandartObjectIdentifiers.id_tc26_gost_3411_12_256 : CryptoProObjectIdentifiers.gostR3411_94_CryptoProParamSet;
      }
   }
}
