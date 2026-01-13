package org.bouncycastle.asn1.cmc;

import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.ASN1UTF8String;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.DERUTF8String;

public class CMCStatusInfoV2 extends ASN1Object {
   private final CMCStatus cMCStatus;
   private final ASN1Sequence bodyList;
   private final ASN1UTF8String statusString;
   private final OtherStatusInfo otherStatusInfo;

   CMCStatusInfoV2(CMCStatus var1, ASN1Sequence var2, ASN1UTF8String var3, OtherStatusInfo var4) {
      this.cMCStatus = var1;
      this.bodyList = var2;
      this.statusString = var3;
      this.otherStatusInfo = var4;
   }

   private CMCStatusInfoV2(ASN1Sequence var1) {
      if (var1.size() >= 2 && var1.size() <= 4) {
         this.cMCStatus = CMCStatus.getInstance(var1.getObjectAt(0));
         this.bodyList = ASN1Sequence.getInstance(var1.getObjectAt(1));
         if (var1.size() > 2) {
            if (var1.size() == 4) {
               this.statusString = ASN1UTF8String.getInstance(var1.getObjectAt(2));
               this.otherStatusInfo = OtherStatusInfo.getInstance(var1.getObjectAt(3));
            } else if (var1.getObjectAt(2) instanceof ASN1UTF8String) {
               this.statusString = ASN1UTF8String.getInstance(var1.getObjectAt(2));
               this.otherStatusInfo = null;
            } else {
               this.statusString = null;
               this.otherStatusInfo = OtherStatusInfo.getInstance(var1.getObjectAt(2));
            }
         } else {
            this.statusString = null;
            this.otherStatusInfo = null;
         }
      } else {
         throw new IllegalArgumentException("incorrect sequence size");
      }
   }

   public CMCStatus getCMCStatus() {
      return this.cMCStatus;
   }

   /** @deprecated */
   public CMCStatus getcMCStatus() {
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

   public OtherStatusInfo getOtherStatusInfo() {
      return this.otherStatusInfo;
   }

   public boolean hasOtherInfo() {
      return this.otherStatusInfo != null;
   }

   public static CMCStatusInfoV2 getInstance(Object var0) {
      if (var0 instanceof CMCStatusInfoV2) {
         return (CMCStatusInfoV2)var0;
      } else {
         return var0 != null ? new CMCStatusInfoV2(ASN1Sequence.getInstance(var0)) : null;
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

      if (this.otherStatusInfo != null) {
         var1.add(this.otherStatusInfo);
      }

      return new DERSequence(var1);
   }
}
