package org.bouncycastle.asn1.x9;

import java.math.BigInteger;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.DERSequence;

public class X9FieldID extends ASN1Object implements X9ObjectIdentifiers {
   private ASN1ObjectIdentifier id;
   private ASN1Primitive parameters;

   public X9FieldID(BigInteger var1) {
      this.id = prime_field;
      this.parameters = new ASN1Integer(var1);
   }

   public X9FieldID(int var1, int var2) {
      this(var1, var2, 0, 0);
   }

   public X9FieldID(int var1, int var2, int var3, int var4) {
      this.id = characteristic_two_field;
      ASN1EncodableVector var5 = new ASN1EncodableVector(3);
      var5.add(new ASN1Integer(var1));
      if (var3 == 0) {
         if (var4 != 0) {
            throw new IllegalArgumentException("inconsistent k values");
         }

         var5.add(tpBasis);
         var5.add(new ASN1Integer(var2));
      } else {
         if (var3 <= var2 || var4 <= var3) {
            throw new IllegalArgumentException("inconsistent k values");
         }

         var5.add(ppBasis);
         ASN1EncodableVector var6 = new ASN1EncodableVector(3);
         var6.add(new ASN1Integer(var2));
         var6.add(new ASN1Integer(var3));
         var6.add(new ASN1Integer(var4));
         var5.add(new DERSequence(var6));
      }

      this.parameters = new DERSequence(var5);
   }

   private X9FieldID(ASN1Sequence var1) {
      this.id = ASN1ObjectIdentifier.getInstance(var1.getObjectAt(0));
      this.parameters = var1.getObjectAt(1).toASN1Primitive();
   }

   public static X9FieldID getInstance(Object var0) {
      if (var0 instanceof X9FieldID) {
         return (X9FieldID)var0;
      } else {
         return var0 != null ? new X9FieldID(ASN1Sequence.getInstance(var0)) : null;
      }
   }

   public ASN1ObjectIdentifier getIdentifier() {
      return this.id;
   }

   public ASN1Primitive getParameters() {
      return this.parameters;
   }

   @Override
   public ASN1Primitive toASN1Primitive() {
      return new DERSequence(this.id, this.parameters);
   }
}
