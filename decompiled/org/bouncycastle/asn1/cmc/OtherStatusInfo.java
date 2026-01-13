package org.bouncycastle.asn1.cmc;

import java.io.IOException;
import org.bouncycastle.asn1.ASN1Choice;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;

public class OtherStatusInfo extends ASN1Object implements ASN1Choice {
   private final CMCFailInfo failInfo;
   private final PendInfo pendInfo;
   private final ExtendedFailInfo extendedFailInfo;

   public static OtherStatusInfo getInstance(Object var0) {
      if (var0 instanceof OtherStatusInfo) {
         return (OtherStatusInfo)var0;
      } else {
         if (var0 instanceof ASN1Encodable) {
            ASN1Primitive var1 = ((ASN1Encodable)var0).toASN1Primitive();
            if (var1 instanceof ASN1Integer) {
               return new OtherStatusInfo(CMCFailInfo.getInstance(var1));
            }

            if (var1 instanceof ASN1Sequence) {
               if (((ASN1Sequence)var1).getObjectAt(0) instanceof ASN1ObjectIdentifier) {
                  return new OtherStatusInfo(ExtendedFailInfo.getInstance(var1));
               }

               return new OtherStatusInfo(PendInfo.getInstance(var1));
            }
         } else if (var0 instanceof byte[]) {
            try {
               return getInstance(ASN1Primitive.fromByteArray((byte[])var0));
            } catch (IOException var2) {
               throw new IllegalArgumentException("parsing error: " + var2.getMessage());
            }
         }

         throw new IllegalArgumentException("unknown object in getInstance(): " + var0.getClass().getName());
      }
   }

   OtherStatusInfo(CMCFailInfo var1) {
      this(var1, null, null);
   }

   OtherStatusInfo(PendInfo var1) {
      this(null, var1, null);
   }

   OtherStatusInfo(ExtendedFailInfo var1) {
      this(null, null, var1);
   }

   private OtherStatusInfo(CMCFailInfo var1, PendInfo var2, ExtendedFailInfo var3) {
      this.failInfo = var1;
      this.pendInfo = var2;
      this.extendedFailInfo = var3;
   }

   public boolean isPendingInfo() {
      return this.pendInfo != null;
   }

   public boolean isFailInfo() {
      return this.failInfo != null;
   }

   public boolean isExtendedFailInfo() {
      return this.extendedFailInfo != null;
   }

   @Override
   public ASN1Primitive toASN1Primitive() {
      if (this.pendInfo != null) {
         return this.pendInfo.toASN1Primitive();
      } else {
         return this.failInfo != null ? this.failInfo.toASN1Primitive() : this.extendedFailInfo.toASN1Primitive();
      }
   }
}
