package org.bouncycastle.asn1.eac;

import java.math.BigInteger;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1TaggedObject;
import org.bouncycastle.asn1.DEROctetString;
import org.bouncycastle.asn1.DERTaggedObject;

public class UnsignedInteger extends ASN1Object {
   private int tagNo;
   private BigInteger value;

   public UnsignedInteger(int var1, BigInteger var2) {
      this.tagNo = var1;
      this.value = var2;
   }

   private UnsignedInteger(ASN1TaggedObject var1) {
      this.tagNo = var1.getTagNo();
      this.value = new BigInteger(1, ASN1OctetString.getInstance(var1, false).getOctets());
   }

   public static UnsignedInteger getInstance(Object var0) {
      if (var0 instanceof UnsignedInteger) {
         return (UnsignedInteger)var0;
      } else {
         return var0 != null ? new UnsignedInteger(ASN1TaggedObject.getInstance(var0)) : null;
      }
   }

   private byte[] convertValue() {
      byte[] var1 = this.value.toByteArray();
      if (var1[0] == 0) {
         byte[] var2 = new byte[var1.length - 1];
         System.arraycopy(var1, 1, var2, 0, var2.length);
         return var2;
      } else {
         return var1;
      }
   }

   public int getTagNo() {
      return this.tagNo;
   }

   public BigInteger getValue() {
      return this.value;
   }

   @Override
   public ASN1Primitive toASN1Primitive() {
      return new DERTaggedObject(false, this.tagNo, new DEROctetString(this.convertValue()));
   }
}
