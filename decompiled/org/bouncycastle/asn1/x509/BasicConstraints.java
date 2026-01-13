package org.bouncycastle.asn1.x509;

import java.math.BigInteger;
import org.bouncycastle.asn1.ASN1Boolean;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.ASN1TaggedObject;
import org.bouncycastle.asn1.DERSequence;

public class BasicConstraints extends ASN1Object {
   ASN1Boolean cA = ASN1Boolean.getInstance(false);
   ASN1Integer pathLenConstraint = null;

   public static BasicConstraints getInstance(ASN1TaggedObject var0, boolean var1) {
      return getInstance(ASN1Sequence.getInstance(var0, var1));
   }

   public static BasicConstraints getInstance(Object var0) {
      if (var0 instanceof BasicConstraints) {
         return (BasicConstraints)var0;
      } else if (var0 instanceof X509Extension) {
         return getInstance(X509Extension.convertValueToObject((X509Extension)var0));
      } else {
         return var0 != null ? new BasicConstraints(ASN1Sequence.getInstance(var0)) : null;
      }
   }

   public static BasicConstraints fromExtensions(Extensions var0) {
      return getInstance(Extensions.getExtensionParsedValue(var0, Extension.basicConstraints));
   }

   private BasicConstraints(ASN1Sequence var1) {
      if (var1.size() == 0) {
         this.cA = null;
         this.pathLenConstraint = null;
      } else {
         if (var1.getObjectAt(0) instanceof ASN1Boolean) {
            this.cA = ASN1Boolean.getInstance(var1.getObjectAt(0));
         } else {
            this.cA = null;
            this.pathLenConstraint = ASN1Integer.getInstance(var1.getObjectAt(0));
         }

         if (var1.size() > 1) {
            if (this.cA == null) {
               throw new IllegalArgumentException("wrong sequence in constructor");
            }

            this.pathLenConstraint = ASN1Integer.getInstance(var1.getObjectAt(1));
         }
      }
   }

   public BasicConstraints(boolean var1) {
      if (var1) {
         this.cA = ASN1Boolean.getInstance(true);
      } else {
         this.cA = null;
      }

      this.pathLenConstraint = null;
   }

   public BasicConstraints(int var1) {
      this.cA = ASN1Boolean.getInstance(true);
      this.pathLenConstraint = new ASN1Integer(var1);
   }

   public boolean isCA() {
      return this.cA != null && this.cA.isTrue();
   }

   public BigInteger getPathLenConstraint() {
      return this.pathLenConstraint != null ? this.pathLenConstraint.getValue() : null;
   }

   public ASN1Integer getPathLenConstraintInteger() {
      return this.pathLenConstraint;
   }

   @Override
   public ASN1Primitive toASN1Primitive() {
      ASN1EncodableVector var1 = new ASN1EncodableVector(2);
      if (this.cA != null) {
         var1.add(this.cA);
      }

      if (this.pathLenConstraint != null) {
         var1.add(this.pathLenConstraint);
      }

      return new DERSequence(var1);
   }

   @Override
   public String toString() {
      return this.pathLenConstraint == null
         ? "BasicConstraints: isCa(" + this.isCA() + ")"
         : "BasicConstraints: isCa(" + this.isCA() + "), pathLenConstraint = " + this.pathLenConstraint.getValue();
   }
}
