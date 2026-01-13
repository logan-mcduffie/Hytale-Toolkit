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

public class DomainParameters extends ASN1Object {
   private final ASN1Integer p;
   private final ASN1Integer g;
   private final ASN1Integer q;
   private final ASN1Integer j;
   private final ValidationParams validationParams;

   public static DomainParameters getInstance(ASN1TaggedObject var0, boolean var1) {
      return getInstance(ASN1Sequence.getInstance(var0, var1));
   }

   public static DomainParameters getInstance(Object var0) {
      if (var0 instanceof DomainParameters) {
         return (DomainParameters)var0;
      } else {
         return var0 != null ? new DomainParameters(ASN1Sequence.getInstance(var0)) : null;
      }
   }

   public DomainParameters(BigInteger var1, BigInteger var2, BigInteger var3, BigInteger var4, ValidationParams var5) {
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
         if (var4 != null) {
            this.j = new ASN1Integer(var4);
         } else {
            this.j = null;
         }

         this.validationParams = var5;
      }
   }

   private DomainParameters(ASN1Sequence var1) {
      if (var1.size() >= 3 && var1.size() <= 5) {
         Enumeration var2 = var1.getObjects();
         this.p = ASN1Integer.getInstance(var2.nextElement());
         this.g = ASN1Integer.getInstance(var2.nextElement());
         this.q = ASN1Integer.getInstance(var2.nextElement());
         ASN1Encodable var3 = getNext(var2);
         if (var3 != null && var3 instanceof ASN1Integer) {
            this.j = ASN1Integer.getInstance(var3);
            var3 = getNext(var2);
         } else {
            this.j = null;
         }

         if (var3 != null) {
            this.validationParams = ValidationParams.getInstance(var3.toASN1Primitive());
         } else {
            this.validationParams = null;
         }
      } else {
         throw new IllegalArgumentException("Bad sequence size: " + var1.size());
      }
   }

   private static ASN1Encodable getNext(Enumeration var0) {
      return var0.hasMoreElements() ? (ASN1Encodable)var0.nextElement() : null;
   }

   public BigInteger getP() {
      return this.p.getPositiveValue();
   }

   public BigInteger getG() {
      return this.g.getPositiveValue();
   }

   public BigInteger getQ() {
      return this.q.getPositiveValue();
   }

   public BigInteger getJ() {
      return this.j == null ? null : this.j.getPositiveValue();
   }

   public ValidationParams getValidationParams() {
      return this.validationParams;
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

      if (this.validationParams != null) {
         var1.add(this.validationParams);
      }

      return new DERSequence(var1);
   }
}
