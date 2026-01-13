package org.bouncycastle.asn1.dvcs;

import java.util.Arrays;
import org.bouncycastle.asn1.ASN1Boolean;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.ASN1TaggedObject;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.DERTaggedObject;
import org.bouncycastle.asn1.x509.PolicyInformation;

public class PathProcInput extends ASN1Object {
   private PolicyInformation[] acceptablePolicySet;
   private boolean inhibitPolicyMapping = false;
   private boolean explicitPolicyReqd = false;
   private boolean inhibitAnyPolicy = false;

   public PathProcInput(PolicyInformation[] var1) {
      this.acceptablePolicySet = this.copy(var1);
   }

   public PathProcInput(PolicyInformation[] var1, boolean var2, boolean var3, boolean var4) {
      this.acceptablePolicySet = this.copy(var1);
      this.inhibitPolicyMapping = var2;
      this.explicitPolicyReqd = var3;
      this.inhibitAnyPolicy = var4;
   }

   private static PolicyInformation[] fromSequence(ASN1Sequence var0) {
      PolicyInformation[] var1 = new PolicyInformation[var0.size()];

      for (int var2 = 0; var2 != var1.length; var2++) {
         var1[var2] = PolicyInformation.getInstance(var0.getObjectAt(var2));
      }

      return var1;
   }

   public static PathProcInput getInstance(Object var0) {
      if (var0 instanceof PathProcInput) {
         return (PathProcInput)var0;
      } else if (var0 != null) {
         ASN1Sequence var1 = ASN1Sequence.getInstance(var0);
         ASN1Sequence var2 = ASN1Sequence.getInstance(var1.getObjectAt(0));
         PathProcInput var3 = new PathProcInput(fromSequence(var2));

         for (int var4 = 1; var4 < var1.size(); var4++) {
            ASN1Encodable var5 = var1.getObjectAt(var4);
            if (var5 instanceof ASN1Boolean) {
               ASN1Boolean var6 = ASN1Boolean.getInstance(var5);
               var3.setInhibitPolicyMapping(var6.isTrue());
            } else if (var5 instanceof ASN1TaggedObject) {
               ASN1TaggedObject var8 = ASN1TaggedObject.getInstance(var5);
               switch (var8.getTagNo()) {
                  case 0:
                     ASN1Boolean var9 = ASN1Boolean.getInstance(var8, false);
                     var3.setExplicitPolicyReqd(var9.isTrue());
                     break;
                  case 1:
                     ASN1Boolean var7 = ASN1Boolean.getInstance(var8, false);
                     var3.setInhibitAnyPolicy(var7.isTrue());
                     break;
                  default:
                     throw new IllegalArgumentException("Unknown tag encountered: " + var8.getTagNo());
               }
            }
         }

         return var3;
      } else {
         return null;
      }
   }

   public static PathProcInput getInstance(ASN1TaggedObject var0, boolean var1) {
      return getInstance(ASN1Sequence.getInstance(var0, var1));
   }

   @Override
   public ASN1Primitive toASN1Primitive() {
      ASN1EncodableVector var1 = new ASN1EncodableVector(4);
      ASN1EncodableVector var2 = new ASN1EncodableVector(this.acceptablePolicySet.length);

      for (int var3 = 0; var3 != this.acceptablePolicySet.length; var3++) {
         var2.add(this.acceptablePolicySet[var3]);
      }

      var1.add(new DERSequence(var2));
      if (this.inhibitPolicyMapping) {
         var1.add(ASN1Boolean.getInstance(this.inhibitPolicyMapping));
      }

      if (this.explicitPolicyReqd) {
         var1.add(new DERTaggedObject(false, 0, ASN1Boolean.getInstance(this.explicitPolicyReqd)));
      }

      if (this.inhibitAnyPolicy) {
         var1.add(new DERTaggedObject(false, 1, ASN1Boolean.getInstance(this.inhibitAnyPolicy)));
      }

      return new DERSequence(var1);
   }

   @Override
   public String toString() {
      return "PathProcInput: {\nacceptablePolicySet: "
         + Arrays.<PolicyInformation>asList(this.acceptablePolicySet)
         + "\ninhibitPolicyMapping: "
         + this.inhibitPolicyMapping
         + "\nexplicitPolicyReqd: "
         + this.explicitPolicyReqd
         + "\ninhibitAnyPolicy: "
         + this.inhibitAnyPolicy
         + "\n}\n";
   }

   public PolicyInformation[] getAcceptablePolicySet() {
      return this.copy(this.acceptablePolicySet);
   }

   public boolean isInhibitPolicyMapping() {
      return this.inhibitPolicyMapping;
   }

   private void setInhibitPolicyMapping(boolean var1) {
      this.inhibitPolicyMapping = var1;
   }

   public boolean isExplicitPolicyReqd() {
      return this.explicitPolicyReqd;
   }

   private void setExplicitPolicyReqd(boolean var1) {
      this.explicitPolicyReqd = var1;
   }

   public boolean isInhibitAnyPolicy() {
      return this.inhibitAnyPolicy;
   }

   private void setInhibitAnyPolicy(boolean var1) {
      this.inhibitAnyPolicy = var1;
   }

   private PolicyInformation[] copy(PolicyInformation[] var1) {
      PolicyInformation[] var2 = new PolicyInformation[var1.length];
      System.arraycopy(var1, 0, var2, 0, var2.length);
      return var2;
   }
}
