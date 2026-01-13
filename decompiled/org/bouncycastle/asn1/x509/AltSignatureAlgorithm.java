package org.bouncycastle.asn1.x509;

import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1TaggedObject;

public class AltSignatureAlgorithm extends ASN1Object {
   private final AlgorithmIdentifier algorithm;

   public static AltSignatureAlgorithm getInstance(ASN1TaggedObject var0, boolean var1) {
      return getInstance(AlgorithmIdentifier.getInstance(var0, var1));
   }

   public static AltSignatureAlgorithm getInstance(Object var0) {
      if (var0 instanceof AltSignatureAlgorithm) {
         return (AltSignatureAlgorithm)var0;
      } else {
         return var0 != null ? new AltSignatureAlgorithm(AlgorithmIdentifier.getInstance(var0)) : null;
      }
   }

   public static AltSignatureAlgorithm fromExtensions(Extensions var0) {
      return getInstance(Extensions.getExtensionParsedValue(var0, Extension.altSignatureAlgorithm));
   }

   public AltSignatureAlgorithm(AlgorithmIdentifier var1) {
      this.algorithm = var1;
   }

   public AltSignatureAlgorithm(ASN1ObjectIdentifier var1) {
      this(var1, null);
   }

   public AltSignatureAlgorithm(ASN1ObjectIdentifier var1, ASN1Encodable var2) {
      this.algorithm = new AlgorithmIdentifier(var1, var2);
   }

   public AlgorithmIdentifier getAlgorithm() {
      return this.algorithm;
   }

   @Override
   public ASN1Primitive toASN1Primitive() {
      return this.algorithm.toASN1Primitive();
   }
}
