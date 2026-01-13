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

public class GeneralSubtree extends ASN1Object {
   private static final BigInteger ZERO = BigInteger.valueOf(0L);
   private GeneralName base;
   private ASN1Integer minimum;
   private ASN1Integer maximum;

   private GeneralSubtree(ASN1Sequence var1) {
      this.base = GeneralName.getInstance(var1.getObjectAt(0));
      switch (var1.size()) {
         case 1:
            break;
         case 2:
            ASN1TaggedObject var2 = ASN1TaggedObject.getInstance(var1.getObjectAt(1));
            switch (var2.getTagNo()) {
               case 0:
                  this.minimum = ASN1Integer.getInstance(var2, false);
                  return;
               case 1:
                  this.maximum = ASN1Integer.getInstance(var2, false);
                  return;
               default:
                  throw new IllegalArgumentException("Bad tag number: " + var2.getTagNo());
            }
         case 3:
            ASN1TaggedObject var3 = ASN1TaggedObject.getInstance(var1.getObjectAt(1));
            if (var3.getTagNo() != 0) {
               throw new IllegalArgumentException("Bad tag number for 'minimum': " + var3.getTagNo());
            }

            this.minimum = ASN1Integer.getInstance(var3, false);
            var3 = ASN1TaggedObject.getInstance(var1.getObjectAt(2));
            if (var3.getTagNo() != 1) {
               throw new IllegalArgumentException("Bad tag number for 'maximum': " + var3.getTagNo());
            }

            this.maximum = ASN1Integer.getInstance(var3, false);
            break;
         default:
            throw new IllegalArgumentException("Bad sequence size: " + var1.size());
      }
   }

   public GeneralSubtree(GeneralName var1, BigInteger var2, BigInteger var3) {
      this.base = var1;
      if (var3 != null) {
         this.maximum = new ASN1Integer(var3);
      }

      if (var2 == null) {
         this.minimum = null;
      } else {
         this.minimum = new ASN1Integer(var2);
      }
   }

   public GeneralSubtree(GeneralName var1) {
      this(var1, null, null);
   }

   public static GeneralSubtree getInstance(ASN1TaggedObject var0, boolean var1) {
      return new GeneralSubtree(ASN1Sequence.getInstance(var0, var1));
   }

   public static GeneralSubtree getInstance(Object var0) {
      if (var0 == null) {
         return null;
      } else {
         return var0 instanceof GeneralSubtree ? (GeneralSubtree)var0 : new GeneralSubtree(ASN1Sequence.getInstance(var0));
      }
   }

   public GeneralName getBase() {
      return this.base;
   }

   public BigInteger getMinimum() {
      return this.minimum == null ? ZERO : this.minimum.getValue();
   }

   public BigInteger getMaximum() {
      return this.maximum == null ? null : this.maximum.getValue();
   }

   @Override
   public ASN1Primitive toASN1Primitive() {
      ASN1EncodableVector var1 = new ASN1EncodableVector(3);
      var1.add(this.base);
      if (this.minimum != null && !this.minimum.hasValue(0)) {
         var1.add(new DERTaggedObject(false, 0, this.minimum));
      }

      if (this.maximum != null) {
         var1.add(new DERTaggedObject(false, 1, this.maximum));
      }

      return new DERSequence(var1);
   }
}
