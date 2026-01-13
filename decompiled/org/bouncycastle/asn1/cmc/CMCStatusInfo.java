package org.bouncycastle.asn1.cmc;

import org.bouncycastle.asn1.ASN1Choice;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.ASN1UTF8String;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.DERUTF8String;

public class CMCStatusInfo extends ASN1Object {
   private final CMCStatus cMCStatus;
   private final ASN1Sequence bodyList;
   private final ASN1UTF8String statusString;
   private final CMCStatusInfo.OtherInfo otherInfo;

   CMCStatusInfo(CMCStatus var1, ASN1Sequence var2, ASN1UTF8String var3, CMCStatusInfo.OtherInfo var4) {
      this.cMCStatus = var1;
      this.bodyList = var2;
      this.statusString = var3;
      this.otherInfo = var4;
   }

   private CMCStatusInfo(ASN1Sequence var1) {
      if (var1.size() >= 2 && var1.size() <= 4) {
         this.cMCStatus = CMCStatus.getInstance(var1.getObjectAt(0));
         this.bodyList = ASN1Sequence.getInstance(var1.getObjectAt(1));
         if (var1.size() > 3) {
            this.statusString = ASN1UTF8String.getInstance(var1.getObjectAt(2));
            this.otherInfo = CMCStatusInfo.OtherInfo.getInstance(var1.getObjectAt(3));
         } else if (var1.size() > 2) {
            if (var1.getObjectAt(2) instanceof ASN1UTF8String) {
               this.statusString = ASN1UTF8String.getInstance(var1.getObjectAt(2));
               this.otherInfo = null;
            } else {
               this.statusString = null;
               this.otherInfo = CMCStatusInfo.OtherInfo.getInstance(var1.getObjectAt(2));
            }
         } else {
            this.statusString = null;
            this.otherInfo = null;
         }
      } else {
         throw new IllegalArgumentException("incorrect sequence size");
      }
   }

   public static CMCStatusInfo getInstance(Object var0) {
      if (var0 instanceof CMCStatusInfo) {
         return (CMCStatusInfo)var0;
      } else {
         return var0 != null ? new CMCStatusInfo(ASN1Sequence.getInstance(var0)) : null;
      }
   }

   @Override
   public ASN1Primitive toASN1Primitive() {
      ASN1EncodableVector var1 = new ASN1EncodableVector(4);
      var1.add(this.cMCStatus);
      var1.add(this.bodyList);
      if (this.statusString != null) {
         var1.add(this.statusString);
      }

      if (this.otherInfo != null) {
         var1.add(this.otherInfo);
      }

      return new DERSequence(var1);
   }

   public CMCStatus getCMCStatus() {
      return this.cMCStatus;
   }

   public BodyPartID[] getBodyList() {
      return Utils.toBodyPartIDArray(this.bodyList);
   }

   /** @deprecated */
   public DERUTF8String getStatusString() {
      return null != this.statusString && !(this.statusString instanceof DERUTF8String)
         ? new DERUTF8String(this.statusString.getString())
         : (DERUTF8String)this.statusString;
   }

   public ASN1UTF8String getStatusStringUTF8() {
      return this.statusString;
   }

   public boolean hasOtherInfo() {
      return this.otherInfo != null;
   }

   public CMCStatusInfo.OtherInfo getOtherInfo() {
      return this.otherInfo;
   }

   public static class OtherInfo extends ASN1Object implements ASN1Choice {
      private final CMCFailInfo failInfo;
      private final PendInfo pendInfo;

      private static CMCStatusInfo.OtherInfo getInstance(Object var0) {
         if (var0 instanceof CMCStatusInfo.OtherInfo) {
            return (CMCStatusInfo.OtherInfo)var0;
         } else {
            if (var0 instanceof ASN1Encodable) {
               ASN1Primitive var1 = ((ASN1Encodable)var0).toASN1Primitive();
               if (var1 instanceof ASN1Integer) {
                  return new CMCStatusInfo.OtherInfo(CMCFailInfo.getInstance(var1));
               }

               if (var1 instanceof ASN1Sequence) {
                  return new CMCStatusInfo.OtherInfo(PendInfo.getInstance(var1));
               }
            }

            throw new IllegalArgumentException("unknown object in getInstance(): " + var0.getClass().getName());
         }
      }

      OtherInfo(CMCFailInfo var1) {
         this(var1, null);
      }

      OtherInfo(PendInfo var1) {
         this(null, var1);
      }

      private OtherInfo(CMCFailInfo var1, PendInfo var2) {
         this.failInfo = var1;
         this.pendInfo = var2;
      }

      public boolean isFailInfo() {
         return this.failInfo != null;
      }

      @Override
      public ASN1Primitive toASN1Primitive() {
         return this.pendInfo != null ? this.pendInfo.toASN1Primitive() : this.failInfo.toASN1Primitive();
      }
   }
}
