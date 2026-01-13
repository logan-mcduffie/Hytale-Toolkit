package org.bouncycastle.asn1.esf;

import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.DERSequence;

public class SignaturePolicyId extends ASN1Object {
   private ASN1ObjectIdentifier sigPolicyId;
   private OtherHashAlgAndValue sigPolicyHash;
   private SigPolicyQualifiers sigPolicyQualifiers;

   public static SignaturePolicyId getInstance(Object var0) {
      if (var0 instanceof SignaturePolicyId) {
         return (SignaturePolicyId)var0;
      } else {
         return var0 != null ? new SignaturePolicyId(ASN1Sequence.getInstance(var0)) : null;
      }
   }

   private SignaturePolicyId(ASN1Sequence var1) {
      if (var1.size() != 2 && var1.size() != 3) {
         throw new IllegalArgumentException("Bad sequence size: " + var1.size());
      } else {
         this.sigPolicyId = ASN1ObjectIdentifier.getInstance(var1.getObjectAt(0));
         this.sigPolicyHash = OtherHashAlgAndValue.getInstance(var1.getObjectAt(1));
         if (var1.size() == 3) {
            this.sigPolicyQualifiers = SigPolicyQualifiers.getInstance(var1.getObjectAt(2));
         }
      }
   }

   public SignaturePolicyId(ASN1ObjectIdentifier var1, OtherHashAlgAndValue var2) {
      this(var1, var2, null);
   }

   public SignaturePolicyId(ASN1ObjectIdentifier var1, OtherHashAlgAndValue var2, SigPolicyQualifiers var3) {
      this.sigPolicyId = var1;
      this.sigPolicyHash = var2;
      this.sigPolicyQualifiers = var3;
   }

   public ASN1ObjectIdentifier getSigPolicyId() {
      return new ASN1ObjectIdentifier(this.sigPolicyId.getId());
   }

   public OtherHashAlgAndValue getSigPolicyHash() {
      return this.sigPolicyHash;
   }

   public SigPolicyQualifiers getSigPolicyQualifiers() {
      return this.sigPolicyQualifiers;
   }

   @Override
   public ASN1Primitive toASN1Primitive() {
      ASN1EncodableVector var1 = new ASN1EncodableVector(3);
      var1.add(this.sigPolicyId);
      var1.add(this.sigPolicyHash);
      if (this.sigPolicyQualifiers != null) {
         var1.add(this.sigPolicyQualifiers);
      }

      return new DERSequence(var1);
   }
}
