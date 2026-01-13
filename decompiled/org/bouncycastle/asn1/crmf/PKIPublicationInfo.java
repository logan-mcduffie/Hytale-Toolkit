package org.bouncycastle.asn1.crmf;

import java.math.BigInteger;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.DERSequence;

public class PKIPublicationInfo extends ASN1Object {
   public static final ASN1Integer dontPublish = new ASN1Integer(0L);
   public static final ASN1Integer pleasePublish = new ASN1Integer(1L);
   private ASN1Integer action;
   private ASN1Sequence pubInfos;

   private PKIPublicationInfo(ASN1Sequence var1) {
      this.action = ASN1Integer.getInstance(var1.getObjectAt(0));
      if (var1.size() > 1) {
         this.pubInfos = ASN1Sequence.getInstance(var1.getObjectAt(1));
      }
   }

   public static PKIPublicationInfo getInstance(Object var0) {
      if (var0 instanceof PKIPublicationInfo) {
         return (PKIPublicationInfo)var0;
      } else {
         return var0 != null ? new PKIPublicationInfo(ASN1Sequence.getInstance(var0)) : null;
      }
   }

   public PKIPublicationInfo(BigInteger var1) {
      this(new ASN1Integer(var1));
   }

   public PKIPublicationInfo(ASN1Integer var1) {
      this.action = var1;
   }

   public PKIPublicationInfo(SinglePubInfo var1) {
      this(var1 != null ? new SinglePubInfo[]{var1} : (SinglePubInfo[])null);
   }

   public PKIPublicationInfo(SinglePubInfo[] var1) {
      this.action = pleasePublish;
      if (var1 != null) {
         this.pubInfos = new DERSequence(var1);
      } else {
         this.pubInfos = null;
      }
   }

   public ASN1Integer getAction() {
      return this.action;
   }

   public SinglePubInfo[] getPubInfos() {
      if (this.pubInfos == null) {
         return null;
      } else {
         SinglePubInfo[] var1 = new SinglePubInfo[this.pubInfos.size()];

         for (int var2 = 0; var2 != var1.length; var2++) {
            var1[var2] = SinglePubInfo.getInstance(this.pubInfos.getObjectAt(var2));
         }

         return var1;
      }
   }

   @Override
   public ASN1Primitive toASN1Primitive() {
      ASN1EncodableVector var1 = new ASN1EncodableVector(2);
      var1.add(this.action);
      if (this.pubInfos != null) {
         var1.add(this.pubInfos);
      }

      return new DERSequence(var1);
   }
}
