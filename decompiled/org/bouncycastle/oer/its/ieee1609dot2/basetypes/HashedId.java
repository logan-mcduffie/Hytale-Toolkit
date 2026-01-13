package org.bouncycastle.oer.its.ieee1609dot2.basetypes;

import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.DEROctetString;
import org.bouncycastle.util.Arrays;

public class HashedId extends ASN1Object {
   private final byte[] id;

   protected HashedId(byte[] var1) {
      this.id = Arrays.clone(var1);
   }

   public byte[] getHashBytes() {
      return Arrays.clone(this.id);
   }

   @Override
   public ASN1Primitive toASN1Primitive() {
      return new DEROctetString(this.id);
   }

   @Override
   public boolean equals(Object var1) {
      if (this == var1) {
         return true;
      } else if (var1 == null || this.getClass() != var1.getClass()) {
         return false;
      } else if (!super.equals(var1)) {
         return false;
      } else {
         HashedId var2 = (HashedId)var1;
         return java.util.Arrays.equals(this.id, var2.id);
      }
   }

   @Override
   public int hashCode() {
      int var1 = super.hashCode();
      return 31 * var1 + java.util.Arrays.hashCode(this.id);
   }
}
