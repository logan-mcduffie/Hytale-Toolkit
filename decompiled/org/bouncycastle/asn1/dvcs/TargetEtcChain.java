package org.bouncycastle.asn1.dvcs;

import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.ASN1TaggedObject;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.DERTaggedObject;

public class TargetEtcChain extends ASN1Object {
   private CertEtcToken target;
   private ASN1Sequence chain;
   private PathProcInput pathProcInput;

   public TargetEtcChain(CertEtcToken var1) {
      this(var1, null, null);
   }

   public TargetEtcChain(CertEtcToken var1, CertEtcToken[] var2) {
      this(var1, var2, null);
   }

   public TargetEtcChain(CertEtcToken var1, PathProcInput var2) {
      this(var1, null, var2);
   }

   public TargetEtcChain(CertEtcToken var1, CertEtcToken[] var2, PathProcInput var3) {
      this.target = var1;
      if (var2 != null) {
         this.chain = new DERSequence(var2);
      }

      this.pathProcInput = var3;
   }

   private TargetEtcChain(ASN1Sequence var1) {
      int var2 = 0;
      ASN1Encodable var3 = var1.getObjectAt(var2++);
      this.target = CertEtcToken.getInstance(var3);
      if (var1.size() > 1) {
         var3 = var1.getObjectAt(var2++);
         if (var3 instanceof ASN1TaggedObject) {
            this.extractPathProcInput(var3);
         } else {
            this.chain = ASN1Sequence.getInstance(var3);
            if (var1.size() > 2) {
               var3 = var1.getObjectAt(var2);
               this.extractPathProcInput(var3);
            }
         }
      }
   }

   private void extractPathProcInput(ASN1Encodable var1) {
      ASN1TaggedObject var2 = ASN1TaggedObject.getInstance(var1);
      switch (var2.getTagNo()) {
         case 0:
            this.pathProcInput = PathProcInput.getInstance(var2, false);
            return;
         default:
            throw new IllegalArgumentException("Unknown tag encountered: " + var2.getTagNo());
      }
   }

   public static TargetEtcChain getInstance(Object var0) {
      if (var0 instanceof TargetEtcChain) {
         return (TargetEtcChain)var0;
      } else {
         return var0 != null ? new TargetEtcChain(ASN1Sequence.getInstance(var0)) : null;
      }
   }

   public static TargetEtcChain getInstance(ASN1TaggedObject var0, boolean var1) {
      return getInstance(ASN1Sequence.getInstance(var0, var1));
   }

   @Override
   public ASN1Primitive toASN1Primitive() {
      ASN1EncodableVector var1 = new ASN1EncodableVector(3);
      var1.add(this.target);
      if (this.chain != null) {
         var1.add(this.chain);
      }

      if (this.pathProcInput != null) {
         var1.add(new DERTaggedObject(false, 0, this.pathProcInput));
      }

      return new DERSequence(var1);
   }

   @Override
   public String toString() {
      StringBuilder var1 = new StringBuilder();
      var1.append("TargetEtcChain {\n");
      var1.append("target: " + this.target + "\n");
      if (this.chain != null) {
         var1.append("chain: " + this.chain + "\n");
      }

      if (this.pathProcInput != null) {
         var1.append("pathProcInput: " + this.pathProcInput + "\n");
      }

      var1.append("}\n");
      return var1.toString();
   }

   public CertEtcToken getTarget() {
      return this.target;
   }

   public CertEtcToken[] getChain() {
      return this.chain != null ? CertEtcToken.arrayFromSequence(this.chain) : null;
   }

   public PathProcInput getPathProcInput() {
      return this.pathProcInput;
   }

   public static TargetEtcChain[] arrayFromSequence(ASN1Sequence var0) {
      TargetEtcChain[] var1 = new TargetEtcChain[var0.size()];

      for (int var2 = 0; var2 != var1.length; var2++) {
         var1[var2] = getInstance(var0.getObjectAt(var2));
      }

      return var1;
   }
}
