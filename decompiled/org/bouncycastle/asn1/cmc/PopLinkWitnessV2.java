package org.bouncycastle.asn1.cmc;

import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.DEROctetString;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.util.Arrays;

public class PopLinkWitnessV2 extends ASN1Object {
   private final AlgorithmIdentifier keyGenAlgorithm;
   private final AlgorithmIdentifier macAlgorithm;
   private final byte[] witness;

   public PopLinkWitnessV2(AlgorithmIdentifier var1, AlgorithmIdentifier var2, byte[] var3) {
      this.keyGenAlgorithm = var1;
      this.macAlgorithm = var2;
      this.witness = Arrays.clone(var3);
   }

   private PopLinkWitnessV2(ASN1Sequence var1) {
      if (var1.size() != 3) {
         throw new IllegalArgumentException("incorrect sequence size");
      } else {
         this.keyGenAlgorithm = AlgorithmIdentifier.getInstance(var1.getObjectAt(0));
         this.macAlgorithm = AlgorithmIdentifier.getInstance(var1.getObjectAt(1));
         this.witness = Arrays.clone(ASN1OctetString.getInstance(var1.getObjectAt(2)).getOctets());
      }
   }

   public static PopLinkWitnessV2 getInstance(Object var0) {
      if (var0 instanceof PopLinkWitnessV2) {
         return (PopLinkWitnessV2)var0;
      } else {
         return var0 != null ? new PopLinkWitnessV2(ASN1Sequence.getInstance(var0)) : null;
      }
   }

   public AlgorithmIdentifier getKeyGenAlgorithm() {
      return this.keyGenAlgorithm;
   }

   public AlgorithmIdentifier getMacAlgorithm() {
      return this.macAlgorithm;
   }

   public byte[] getWitness() {
      return Arrays.clone(this.witness);
   }

   @Override
   public ASN1Primitive toASN1Primitive() {
      ASN1EncodableVector var1 = new ASN1EncodableVector(3);
      var1.add(this.keyGenAlgorithm);
      var1.add(this.macAlgorithm);
      var1.add(new DEROctetString(this.getWitness()));
      return new DERSequence(var1);
   }
}
