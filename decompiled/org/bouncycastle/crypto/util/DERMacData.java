package org.bouncycastle.crypto.util;

import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.DERTaggedObject;
import org.bouncycastle.util.Arrays;
import org.bouncycastle.util.Strings;

public final class DERMacData {
   private final byte[] macData;

   private DERMacData(byte[] var1) {
      this.macData = var1;
   }

   public byte[] getMacData() {
      return Arrays.clone(this.macData);
   }

   public static final class Builder {
      private final DERMacData.Type type;
      private ASN1OctetString idU;
      private ASN1OctetString idV;
      private ASN1OctetString ephemDataU;
      private ASN1OctetString ephemDataV;
      private byte[] text;

      public Builder(DERMacData.Type var1, byte[] var2, byte[] var3, byte[] var4, byte[] var5) {
         this.type = var1;
         this.idU = DerUtil.getOctetString(var2);
         this.idV = DerUtil.getOctetString(var3);
         this.ephemDataU = DerUtil.getOctetString(var4);
         this.ephemDataV = DerUtil.getOctetString(var5);
      }

      public DERMacData.Builder withText(byte[] var1) {
         this.text = DerUtil.toByteArray(new DERTaggedObject(false, 0, DerUtil.getOctetString(var1)));
         return this;
      }

      public DERMacData build() {
         switch (this.type) {
            case UNILATERALU:
            case BILATERALU:
               return new DERMacData(
                  this.concatenate(
                     this.type.getHeader(),
                     DerUtil.toByteArray(this.idU),
                     DerUtil.toByteArray(this.idV),
                     DerUtil.toByteArray(this.ephemDataU),
                     DerUtil.toByteArray(this.ephemDataV),
                     this.text
                  )
               );
            case UNILATERALV:
            case BILATERALV:
               return new DERMacData(
                  this.concatenate(
                     this.type.getHeader(),
                     DerUtil.toByteArray(this.idV),
                     DerUtil.toByteArray(this.idU),
                     DerUtil.toByteArray(this.ephemDataV),
                     DerUtil.toByteArray(this.ephemDataU),
                     this.text
                  )
               );
            default:
               throw new IllegalStateException("Unknown type encountered in build");
         }
      }

      private byte[] concatenate(byte[] var1, byte[] var2, byte[] var3, byte[] var4, byte[] var5, byte[] var6) {
         return Arrays.concatenate(Arrays.concatenate(var1, var2, var3), Arrays.concatenate(var4, var5, var6));
      }
   }

   public static enum Type {
      UNILATERALU("KC_1_U"),
      UNILATERALV("KC_1_V"),
      BILATERALU("KC_2_U"),
      BILATERALV("KC_2_V");

      private final String enc;

      private Type(String nullxx) {
         this.enc = nullxx;
      }

      public byte[] getHeader() {
         return Strings.toByteArray(this.enc);
      }
   }
}
