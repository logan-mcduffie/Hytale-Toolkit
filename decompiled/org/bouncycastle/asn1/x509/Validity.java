package org.bouncycastle.asn1.x509;

import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.ASN1TaggedObject;
import org.bouncycastle.asn1.DERSequence;

public class Validity extends ASN1Object {
   private final Time notBefore;
   private final Time notAfter;

   public static Validity getInstance(Object var0) {
      if (var0 instanceof Validity) {
         return (Validity)var0;
      } else {
         return var0 != null ? new Validity(ASN1Sequence.getInstance(var0)) : null;
      }
   }

   public static Validity getInstance(ASN1TaggedObject var0, boolean var1) {
      return new Validity(ASN1Sequence.getInstance(var0, var1));
   }

   private Validity(ASN1Sequence var1) {
      int var2 = var1.size();
      if (var2 != 2) {
         throw new IllegalArgumentException("Bad sequence size: " + var2);
      } else {
         this.notBefore = Time.getInstance(var1.getObjectAt(0));
         this.notAfter = Time.getInstance(var1.getObjectAt(1));
      }
   }

   public Validity(Time var1, Time var2) {
      if (var1 == null) {
         throw new NullPointerException("'notBefore' cannot be null");
      } else if (var2 == null) {
         throw new NullPointerException("'notAfter' cannot be null");
      } else {
         this.notBefore = var1;
         this.notAfter = var2;
      }
   }

   public Time getNotBefore() {
      return this.notBefore;
   }

   public Time getNotAfter() {
      return this.notAfter;
   }

   @Override
   public ASN1Primitive toASN1Primitive() {
      return new DERSequence(this.notBefore, this.notAfter);
   }
}
