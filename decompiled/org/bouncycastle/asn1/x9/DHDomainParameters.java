package org.bouncycastle.asn1.x9;

import java.math.BigInteger;
import java.util.Enumeration;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.ASN1TaggedObject;
import org.bouncycastle.asn1.DERSequence;

/** @deprecated */
public class DHDomainParameters extends ASN1Object {
   private ASN1Integer p;
   private ASN1Integer g;
   private ASN1Integer q;
   private ASN1Integer j;
   private DHValidationParms validationParms;

   public static DHDomainParameters getInstance(ASN1TaggedObject var0, boolean var1) {
      return getInstance(ASN1Sequence.getInstance(var0, var1));
   }

   public static DHDomainParameters getInstance(Object var0) {
      if (var0 == null || var0 instanceof DHDomainParameters) {
         return (DHDomainParameters)var0;
      } else if (var0 instanceof ASN1Sequence) {
         return new DHDomainParameters((ASN1Sequence)var0);
      } else {
         throw new IllegalArgumentException("Invalid DHDomainParameters: " + var0.getClass().getName());
      }
   }

   public DHDomainParameters(BigInteger var1, BigInteger var2, BigInteger var3, BigInteger var4, DHValidationParms var5) {
      if (var1 == null) {
         throw new IllegalArgumentException("'p' cannot be null");
      } else if (var2 == null) {
         throw new IllegalArgumentException("'g' cannot be null");
      } else if (var3 == null) {
         throw new IllegalArgumentException("'q' cannot be null");
      } else {
         this.p = new ASN1Integer(var1);
         this.g = new ASN1Integer(var2);
         this.q = new ASN1Integer(var3);
         this.j = new ASN1Integer(var4);
         this.validationParms = var5;
      }
   }

   public DHDomainParameters(ASN1Integer var1, ASN1Integer var2, ASN1Integer var3, ASN1Integer var4, DHValidationParms var5) {
      if (var1 == null) {
         throw new IllegalArgumentException("'p' cannot be null");
      } else if (var2 == null) {
         throw new IllegalArgumentException("'g' cannot be null");
      } else if (var3 == null) {
         throw new IllegalArgumentException("'q' cannot be null");
      } else {
         this.p = var1;
         this.g = var2;
         this.q = var3;
         this.j = var4;
         this.validationParms = var5;
      }
   }

   private DHDomainParameters(ASN1Sequence var1) {
      if (var1.size() >= 3 && var1.size() <= 5) {
         Enumeration var2 = var1.getObjects();
         this.p = ASN1Integer.getInstance(var2.nextElement());
         this.g = ASN1Integer.getInstance(var2.nextElement());
         this.q = ASN1Integer.getInstance(var2.nextElement());
         ASN1Encodable var3 = getNext(var2);
         if (var3 != null && var3 instanceof ASN1Integer) {
            this.j = ASN1Integer.getInstance(var3);
            var3 = getNext(var2);
         }

         if (var3 != null) {
            this.validationParms = DHValidationParms.getInstance(var3.toASN1Primitive());
         }
      } else {
         throw new IllegalArgumentException("Bad sequence size: " + var1.size());
      }
   }

   private static ASN1Encodable getNext(Enumeration var0) {
      return var0.hasMoreElements() ? (ASN1Encodable)var0.nextElement() : null;
   }

   public ASN1Integer getP() {
      return this.p;
   }

   public ASN1Integer getG() {
      return this.g;
   }

   public ASN1Integer getQ() {
      return this.q;
   }

   public ASN1Integer getJ() {
      return this.j;
   }

   public DHValidationParms getValidationParms() {
      return this.validationParms;
   }

   @Override
   public ASN1Primitive toASN1Primitive() {
      ASN1EncodableVector var1 = new ASN1EncodableVector(5);
      var1.add(this.p);
      var1.add(this.g);
      var1.add(this.q);
      if (this.j != null) {
         var1.add(this.j);
      }

      if (this.validationParms != null) {
         var1.add(this.validationParms);
      }

      return new DERSequence(var1);
   }
}
