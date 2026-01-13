package org.bouncycastle.asn1.esf;

import java.util.Enumeration;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.ASN1TaggedObject;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.DERTaggedObject;
import org.bouncycastle.asn1.x509.Attribute;
import org.bouncycastle.asn1.x509.AttributeCertificate;

public class SignerAttribute extends ASN1Object {
   private Object[] values;

   public static SignerAttribute getInstance(Object var0) {
      if (var0 instanceof SignerAttribute) {
         return (SignerAttribute)var0;
      } else {
         return var0 != null ? new SignerAttribute(ASN1Sequence.getInstance(var0)) : null;
      }
   }

   private SignerAttribute(ASN1Sequence var1) {
      int var2 = 0;
      this.values = new Object[var1.size()];

      for (Enumeration var3 = var1.getObjects(); var3.hasMoreElements(); var2++) {
         ASN1TaggedObject var4 = ASN1TaggedObject.getInstance(var3.nextElement());
         if (var4.getTagNo() != 0) {
            if (var4.getTagNo() != 1) {
               throw new IllegalArgumentException("illegal tag: " + var4.getTagNo());
            }

            this.values[var2] = AttributeCertificate.getInstance(ASN1Sequence.getInstance(var4, true));
         } else {
            ASN1Sequence var5 = ASN1Sequence.getInstance(var4, true);
            Attribute[] var6 = new Attribute[var5.size()];

            for (int var7 = 0; var7 != var6.length; var7++) {
               var6[var7] = Attribute.getInstance(var5.getObjectAt(var7));
            }

            this.values[var2] = var6;
         }
      }
   }

   public SignerAttribute(Attribute[] var1) {
      this.values = new Object[1];
      this.values[0] = var1;
   }

   public SignerAttribute(AttributeCertificate var1) {
      this.values = new Object[1];
      this.values[0] = var1;
   }

   public Object[] getValues() {
      Object[] var1 = new Object[this.values.length];
      System.arraycopy(this.values, 0, var1, 0, var1.length);
      return var1;
   }

   @Override
   public ASN1Primitive toASN1Primitive() {
      ASN1EncodableVector var1 = new ASN1EncodableVector(this.values.length);

      for (int var2 = 0; var2 != this.values.length; var2++) {
         if (this.values[var2] instanceof Attribute[]) {
            var1.add(new DERTaggedObject(0, new DERSequence((Attribute[])this.values[var2])));
         } else {
            var1.add(new DERTaggedObject(1, (AttributeCertificate)this.values[var2]));
         }
      }

      return new DERSequence(var1);
   }
}
