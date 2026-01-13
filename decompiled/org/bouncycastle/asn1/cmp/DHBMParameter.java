package org.bouncycastle.asn1.cmp;

import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;

public class DHBMParameter extends ASN1Object {
   private final AlgorithmIdentifier owf;
   private final AlgorithmIdentifier mac;

   private DHBMParameter(ASN1Sequence var1) {
      if (var1.size() != 2) {
         throw new IllegalArgumentException("expecting sequence size of 2");
      } else {
         this.owf = AlgorithmIdentifier.getInstance(var1.getObjectAt(0));
         this.mac = AlgorithmIdentifier.getInstance(var1.getObjectAt(1));
      }
   }

   public DHBMParameter(AlgorithmIdentifier var1, AlgorithmIdentifier var2) {
      this.owf = var1;
      this.mac = var2;
   }

   public static DHBMParameter getInstance(Object var0) {
      if (var0 instanceof DHBMParameter) {
         return (DHBMParameter)var0;
      } else {
         return var0 != null ? new DHBMParameter(ASN1Sequence.getInstance(var0)) : null;
      }
   }

   public AlgorithmIdentifier getOwf() {
      return this.owf;
   }

   public AlgorithmIdentifier getMac() {
      return this.mac;
   }

   @Override
   public ASN1Primitive toASN1Primitive() {
      return new DERSequence(new ASN1Encodable[]{this.owf, this.mac});
   }
}
