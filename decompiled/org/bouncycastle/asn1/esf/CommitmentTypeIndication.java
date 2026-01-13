package org.bouncycastle.asn1.esf;

import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.DERSequence;

public class CommitmentTypeIndication extends ASN1Object {
   private ASN1ObjectIdentifier commitmentTypeId;
   private ASN1Sequence commitmentTypeQualifier;

   private CommitmentTypeIndication(ASN1Sequence var1) {
      this.commitmentTypeId = (ASN1ObjectIdentifier)var1.getObjectAt(0);
      if (var1.size() > 1) {
         this.commitmentTypeQualifier = (ASN1Sequence)var1.getObjectAt(1);
      }
   }

   public CommitmentTypeIndication(ASN1ObjectIdentifier var1) {
      this.commitmentTypeId = var1;
   }

   public CommitmentTypeIndication(ASN1ObjectIdentifier var1, ASN1Sequence var2) {
      this.commitmentTypeId = var1;
      this.commitmentTypeQualifier = var2;
   }

   public static CommitmentTypeIndication getInstance(Object var0) {
      return var0 != null && !(var0 instanceof CommitmentTypeIndication)
         ? new CommitmentTypeIndication(ASN1Sequence.getInstance(var0))
         : (CommitmentTypeIndication)var0;
   }

   public ASN1ObjectIdentifier getCommitmentTypeId() {
      return this.commitmentTypeId;
   }

   public ASN1Sequence getCommitmentTypeQualifier() {
      return this.commitmentTypeQualifier;
   }

   @Override
   public ASN1Primitive toASN1Primitive() {
      ASN1EncodableVector var1 = new ASN1EncodableVector(2);
      var1.add(this.commitmentTypeId);
      if (this.commitmentTypeQualifier != null) {
         var1.add(this.commitmentTypeQualifier);
      }

      return new DERSequence(var1);
   }
}
