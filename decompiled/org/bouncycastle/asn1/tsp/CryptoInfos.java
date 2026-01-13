package org.bouncycastle.asn1.tsp;

import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.ASN1TaggedObject;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.cms.Attribute;

public class CryptoInfos extends ASN1Object {
   private ASN1Sequence attributes;

   public static CryptoInfos getInstance(Object var0) {
      if (var0 instanceof CryptoInfos) {
         return (CryptoInfos)var0;
      } else {
         return var0 != null ? new CryptoInfos(ASN1Sequence.getInstance(var0)) : null;
      }
   }

   public static CryptoInfos getInstance(ASN1TaggedObject var0, boolean var1) {
      return getInstance(ASN1Sequence.getInstance(var0, var1));
   }

   private CryptoInfos(ASN1Sequence var1) {
      this.attributes = var1;
   }

   public CryptoInfos(Attribute[] var1) {
      this.attributes = new DERSequence(var1);
   }

   public Attribute[] getAttributes() {
      Attribute[] var1 = new Attribute[this.attributes.size()];

      for (int var2 = 0; var2 != var1.length; var2++) {
         var1[var2] = Attribute.getInstance(this.attributes.getObjectAt(var2));
      }

      return var1;
   }

   @Override
   public ASN1Primitive toASN1Primitive() {
      return this.attributes;
   }
}
