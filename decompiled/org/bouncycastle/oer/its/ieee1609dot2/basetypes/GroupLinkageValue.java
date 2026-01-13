package org.bouncycastle.oer.its.ieee1609dot2.basetypes;

import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.DEROctetString;
import org.bouncycastle.oer.its.ItsUtils;
import org.bouncycastle.util.Arrays;

public class GroupLinkageValue extends ASN1Object {
   private final ASN1OctetString jValue;
   private final ASN1OctetString value;

   private GroupLinkageValue(ASN1Sequence var1) {
      if (var1.size() != 2) {
         throw new IllegalArgumentException("expected sequence size of 2");
      } else {
         this.jValue = ASN1OctetString.getInstance(var1.getObjectAt(0));
         this.value = ASN1OctetString.getInstance(var1.getObjectAt(1));
         this.assertValues();
      }
   }

   public GroupLinkageValue(ASN1OctetString var1, ASN1OctetString var2) {
      this.jValue = var1;
      this.value = var2;
      this.assertValues();
   }

   private void assertValues() {
      if (this.jValue == null || this.jValue.getOctets().length != 4) {
         throw new IllegalArgumentException("jValue is null or not four bytes long");
      } else if (this.value == null || this.value.getOctets().length != 9) {
         throw new IllegalArgumentException("value is null or not nine bytes long");
      }
   }

   public static GroupLinkageValue getInstance(Object var0) {
      if (var0 instanceof GroupLinkageValue) {
         return (GroupLinkageValue)var0;
      } else {
         return var0 != null ? new GroupLinkageValue(ASN1Sequence.getInstance(var0)) : null;
      }
   }

   public ASN1OctetString getJValue() {
      return this.jValue;
   }

   public ASN1OctetString getValue() {
      return this.value;
   }

   @Override
   public ASN1Primitive toASN1Primitive() {
      return ItsUtils.toSequence(this.jValue, this.value);
   }

   public static GroupLinkageValue.Builder builder() {
      return new GroupLinkageValue.Builder();
   }

   public static class Builder {
      private ASN1OctetString jValue;
      private ASN1OctetString value;

      public GroupLinkageValue.Builder setJValue(ASN1OctetString var1) {
         this.jValue = var1;
         return this;
      }

      public GroupLinkageValue.Builder setJValue(byte[] var1) {
         return this.setJValue(new DEROctetString(Arrays.clone(var1)));
      }

      public GroupLinkageValue.Builder setValue(ASN1OctetString var1) {
         this.value = var1;
         return this;
      }

      public GroupLinkageValue.Builder setValue(byte[] var1) {
         return this.setValue(new DEROctetString(Arrays.clone(var1)));
      }

      public GroupLinkageValue createGroupLinkageValue() {
         return new GroupLinkageValue(this.jValue, this.value);
      }
   }
}
