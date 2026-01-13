package org.bouncycastle.asn1.tsp;

import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.ASN1TaggedObject;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.DERTaggedObject;

public class Accuracy extends ASN1Object {
   ASN1Integer seconds;
   ASN1Integer millis;
   ASN1Integer micros;
   protected static final int MIN_MILLIS = 1;
   protected static final int MAX_MILLIS = 999;
   protected static final int MIN_MICROS = 1;
   protected static final int MAX_MICROS = 999;

   protected Accuracy() {
   }

   public Accuracy(ASN1Integer var1, ASN1Integer var2, ASN1Integer var3) {
      if (null != var2) {
         int var4 = var2.intValueExact();
         if (var4 < 1 || var4 > 999) {
            throw new IllegalArgumentException("Invalid millis field : not in (1..999)");
         }
      }

      if (null != var3) {
         int var5 = var3.intValueExact();
         if (var5 < 1 || var5 > 999) {
            throw new IllegalArgumentException("Invalid micros field : not in (1..999)");
         }
      }

      this.seconds = var1;
      this.millis = var2;
      this.micros = var3;
   }

   private Accuracy(ASN1Sequence var1) {
      this.seconds = null;
      this.millis = null;
      this.micros = null;

      for (int var2 = 0; var2 < var1.size(); var2++) {
         if (var1.getObjectAt(var2) instanceof ASN1Integer) {
            this.seconds = (ASN1Integer)var1.getObjectAt(var2);
         } else if (var1.getObjectAt(var2) instanceof ASN1TaggedObject) {
            ASN1TaggedObject var3 = (ASN1TaggedObject)var1.getObjectAt(var2);
            switch (var3.getTagNo()) {
               case 0:
                  this.millis = ASN1Integer.getInstance(var3, false);
                  int var4 = this.millis.intValueExact();
                  if (var4 < 1 || var4 > 999) {
                     throw new IllegalArgumentException("Invalid millis field : not in (1..999)");
                  }
                  break;
               case 1:
                  this.micros = ASN1Integer.getInstance(var3, false);
                  int var5 = this.micros.intValueExact();
                  if (var5 < 1 || var5 > 999) {
                     throw new IllegalArgumentException("Invalid micros field : not in (1..999)");
                  }
                  break;
               default:
                  throw new IllegalArgumentException("Invalid tag number");
            }
         }
      }
   }

   public static Accuracy getInstance(Object var0) {
      if (var0 instanceof Accuracy) {
         return (Accuracy)var0;
      } else {
         return var0 != null ? new Accuracy(ASN1Sequence.getInstance(var0)) : null;
      }
   }

   public ASN1Integer getSeconds() {
      return this.seconds;
   }

   public ASN1Integer getMillis() {
      return this.millis;
   }

   public ASN1Integer getMicros() {
      return this.micros;
   }

   @Override
   public ASN1Primitive toASN1Primitive() {
      ASN1EncodableVector var1 = new ASN1EncodableVector(3);
      if (this.seconds != null) {
         var1.add(this.seconds);
      }

      if (this.millis != null) {
         var1.add(new DERTaggedObject(false, 0, this.millis));
      }

      if (this.micros != null) {
         var1.add(new DERTaggedObject(false, 1, this.micros));
      }

      return new DERSequence(var1);
   }
}
