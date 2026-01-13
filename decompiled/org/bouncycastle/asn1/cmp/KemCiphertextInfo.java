package org.bouncycastle.asn1.cmp;

import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;

public class KemCiphertextInfo extends ASN1Object {
   private final AlgorithmIdentifier kem;
   private final ASN1OctetString ct;

   private KemCiphertextInfo(ASN1Sequence var1) {
      if (var1.size() != 2) {
         throw new IllegalArgumentException("sequence size should 2");
      } else {
         this.kem = AlgorithmIdentifier.getInstance(var1.getObjectAt(0));
         this.ct = ASN1OctetString.getInstance(var1.getObjectAt(1));
      }
   }

   public KemCiphertextInfo(AlgorithmIdentifier var1, ASN1OctetString var2) {
      this.kem = var1;
      this.ct = var2;
   }

   public static KemCiphertextInfo getInstance(Object var0) {
      if (var0 instanceof KemCiphertextInfo) {
         return (KemCiphertextInfo)var0;
      } else {
         return var0 != null ? new KemCiphertextInfo(ASN1Sequence.getInstance(var0)) : null;
      }
   }

   public AlgorithmIdentifier getKem() {
      return this.kem;
   }

   public ASN1OctetString getCt() {
      return this.ct;
   }

   @Override
   public ASN1Primitive toASN1Primitive() {
      return new DERSequence(this.kem, this.ct);
   }
}
