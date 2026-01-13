package org.bouncycastle.asn1.x9;

import org.bouncycastle.asn1.ASN1BitString;
import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.ASN1TaggedObject;
import org.bouncycastle.asn1.DERSequence;

/** @deprecated */
public class DHValidationParms extends ASN1Object {
   private ASN1BitString seed;
   private ASN1Integer pgenCounter;

   public static DHValidationParms getInstance(ASN1TaggedObject var0, boolean var1) {
      return getInstance(ASN1Sequence.getInstance(var0, var1));
   }

   public static DHValidationParms getInstance(Object var0) {
      if (var0 instanceof DHValidationParms) {
         return (DHValidationParms)var0;
      } else {
         return var0 != null ? new DHValidationParms(ASN1Sequence.getInstance(var0)) : null;
      }
   }

   public DHValidationParms(ASN1BitString var1, ASN1Integer var2) {
      if (var1 == null) {
         throw new IllegalArgumentException("'seed' cannot be null");
      } else if (var2 == null) {
         throw new IllegalArgumentException("'pgenCounter' cannot be null");
      } else {
         this.seed = var1;
         this.pgenCounter = var2;
      }
   }

   private DHValidationParms(ASN1Sequence var1) {
      if (var1.size() != 2) {
         throw new IllegalArgumentException("Bad sequence size: " + var1.size());
      } else {
         this.seed = ASN1BitString.getInstance(var1.getObjectAt(0));
         this.pgenCounter = ASN1Integer.getInstance(var1.getObjectAt(1));
      }
   }

   public ASN1BitString getSeed() {
      return this.seed;
   }

   public ASN1Integer getPgenCounter() {
      return this.pgenCounter;
   }

   @Override
   public ASN1Primitive toASN1Primitive() {
      return new DERSequence(this.seed, this.pgenCounter);
   }
}
