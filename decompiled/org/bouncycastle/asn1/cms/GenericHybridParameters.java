package org.bouncycastle.asn1.cms;

import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;

public class GenericHybridParameters extends ASN1Object {
   private final AlgorithmIdentifier kem;
   private final AlgorithmIdentifier dem;

   private GenericHybridParameters(ASN1Sequence var1) {
      if (var1.size() != 2) {
         throw new IllegalArgumentException("ASN.1 SEQUENCE should be of length 2");
      } else {
         this.kem = AlgorithmIdentifier.getInstance(var1.getObjectAt(0));
         this.dem = AlgorithmIdentifier.getInstance(var1.getObjectAt(1));
      }
   }

   public static GenericHybridParameters getInstance(Object var0) {
      if (var0 instanceof GenericHybridParameters) {
         return (GenericHybridParameters)var0;
      } else {
         return var0 != null ? new GenericHybridParameters(ASN1Sequence.getInstance(var0)) : null;
      }
   }

   public GenericHybridParameters(AlgorithmIdentifier var1, AlgorithmIdentifier var2) {
      this.kem = var1;
      this.dem = var2;
   }

   public AlgorithmIdentifier getDem() {
      return this.dem;
   }

   public AlgorithmIdentifier getKem() {
      return this.kem;
   }

   @Override
   public ASN1Primitive toASN1Primitive() {
      return new DERSequence(this.kem, this.dem);
   }
}
