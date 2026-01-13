package org.bouncycastle.asn1.x509;

import java.math.BigInteger;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.ASN1TaggedObject;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.DERTaggedObject;

public class PolicyConstraints extends ASN1Object {
   private BigInteger requireExplicitPolicyMapping;
   private BigInteger inhibitPolicyMapping;

   public PolicyConstraints(BigInteger var1, BigInteger var2) {
      this.requireExplicitPolicyMapping = var1;
      this.inhibitPolicyMapping = var2;
   }

   private PolicyConstraints(ASN1Sequence var1) {
      for (int var2 = 0; var2 != var1.size(); var2++) {
         ASN1TaggedObject var3 = ASN1TaggedObject.getInstance(var1.getObjectAt(var2));
         if (var3.getTagNo() == 0) {
            this.requireExplicitPolicyMapping = ASN1Integer.getInstance(var3, false).getValue();
         } else {
            if (var3.getTagNo() != 1) {
               throw new IllegalArgumentException("Unknown tag encountered.");
            }

            this.inhibitPolicyMapping = ASN1Integer.getInstance(var3, false).getValue();
         }
      }
   }

   public static PolicyConstraints getInstance(Object var0) {
      if (var0 instanceof PolicyConstraints) {
         return (PolicyConstraints)var0;
      } else {
         return var0 != null ? new PolicyConstraints(ASN1Sequence.getInstance(var0)) : null;
      }
   }

   public static PolicyConstraints fromExtensions(Extensions var0) {
      return getInstance(Extensions.getExtensionParsedValue(var0, Extension.policyConstraints));
   }

   public BigInteger getRequireExplicitPolicyMapping() {
      return this.requireExplicitPolicyMapping;
   }

   public BigInteger getInhibitPolicyMapping() {
      return this.inhibitPolicyMapping;
   }

   @Override
   public ASN1Primitive toASN1Primitive() {
      ASN1EncodableVector var1 = new ASN1EncodableVector(2);
      if (this.requireExplicitPolicyMapping != null) {
         var1.add(new DERTaggedObject(false, 0, new ASN1Integer(this.requireExplicitPolicyMapping)));
      }

      if (this.inhibitPolicyMapping != null) {
         var1.add(new DERTaggedObject(false, 1, new ASN1Integer(this.inhibitPolicyMapping)));
      }

      return new DERSequence(var1);
   }
}
