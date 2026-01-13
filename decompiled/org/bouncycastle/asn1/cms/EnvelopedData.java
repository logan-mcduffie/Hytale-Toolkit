package org.bouncycastle.asn1.cms;

import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.ASN1Set;
import org.bouncycastle.asn1.ASN1TaggedObject;
import org.bouncycastle.asn1.BERSequence;
import org.bouncycastle.asn1.DERTaggedObject;

public class EnvelopedData extends ASN1Object {
   private ASN1Integer version;
   private OriginatorInfo originatorInfo;
   private ASN1Set recipientInfos;
   private EncryptedContentInfo encryptedContentInfo;
   private ASN1Set unprotectedAttrs;

   public EnvelopedData(OriginatorInfo var1, ASN1Set var2, EncryptedContentInfo var3, ASN1Set var4) {
      this.version = new ASN1Integer(calculateVersion(var1, var2, var4));
      this.originatorInfo = var1;
      this.recipientInfos = var2;
      this.encryptedContentInfo = var3;
      this.unprotectedAttrs = var4;
   }

   public EnvelopedData(OriginatorInfo var1, ASN1Set var2, EncryptedContentInfo var3, Attributes var4) {
      this.version = new ASN1Integer(calculateVersion(var1, var2, ASN1Set.getInstance(var4)));
      this.originatorInfo = var1;
      this.recipientInfos = var2;
      this.encryptedContentInfo = var3;
      this.unprotectedAttrs = ASN1Set.getInstance(var4);
   }

   private EnvelopedData(ASN1Sequence var1) {
      int var2 = 0;
      this.version = (ASN1Integer)var1.getObjectAt(var2++);
      ASN1Encodable var3 = var1.getObjectAt(var2++);
      if (var3 instanceof ASN1TaggedObject) {
         this.originatorInfo = OriginatorInfo.getInstance((ASN1TaggedObject)var3, false);
         var3 = var1.getObjectAt(var2++);
      }

      this.recipientInfos = ASN1Set.getInstance(var3);
      this.encryptedContentInfo = EncryptedContentInfo.getInstance(var1.getObjectAt(var2++));
      if (var1.size() > var2) {
         this.unprotectedAttrs = ASN1Set.getInstance((ASN1TaggedObject)var1.getObjectAt(var2), false);
      }
   }

   public static EnvelopedData getInstance(ASN1TaggedObject var0, boolean var1) {
      return getInstance(ASN1Sequence.getInstance(var0, var1));
   }

   public static EnvelopedData getInstance(Object var0) {
      if (var0 instanceof EnvelopedData) {
         return (EnvelopedData)var0;
      } else {
         return var0 != null ? new EnvelopedData(ASN1Sequence.getInstance(var0)) : null;
      }
   }

   public ASN1Integer getVersion() {
      return this.version;
   }

   public OriginatorInfo getOriginatorInfo() {
      return this.originatorInfo;
   }

   public ASN1Set getRecipientInfos() {
      return this.recipientInfos;
   }

   public EncryptedContentInfo getEncryptedContentInfo() {
      return this.encryptedContentInfo;
   }

   public ASN1Set getUnprotectedAttrs() {
      return this.unprotectedAttrs;
   }

   @Override
   public ASN1Primitive toASN1Primitive() {
      ASN1EncodableVector var1 = new ASN1EncodableVector(5);
      var1.add(this.version);
      if (this.originatorInfo != null) {
         var1.add(new DERTaggedObject(false, 0, this.originatorInfo));
      }

      var1.add(this.recipientInfos);
      var1.add(this.encryptedContentInfo);
      if (this.unprotectedAttrs != null) {
         var1.add(new DERTaggedObject(false, 1, this.unprotectedAttrs));
      }

      return new BERSequence(var1);
   }

   public static int calculateVersion(OriginatorInfo var0, ASN1Set var1, ASN1Set var2) {
      if (var0 != null) {
         ASN1Set var3 = var0.getCRLs();
         if (var3 != null) {
            int var4 = 0;

            for (int var5 = var3.size(); var4 < var5; var4++) {
               ASN1Encodable var6 = var3.getObjectAt(var4);
               if (var6 instanceof ASN1TaggedObject) {
                  ASN1TaggedObject var7 = (ASN1TaggedObject)var6;
                  if (var7.hasContextTag(1)) {
                     return 4;
                  }
               }
            }
         }

         ASN1Set var11 = var0.getCertificates();
         if (var11 != null) {
            boolean var13 = false;
            int var15 = 0;

            for (int var17 = var11.size(); var15 < var17; var15++) {
               ASN1Encodable var8 = var11.getObjectAt(var15);
               if (var8 instanceof ASN1TaggedObject) {
                  ASN1TaggedObject var9 = (ASN1TaggedObject)var8;
                  if (var9.hasContextTag(3)) {
                     return 4;
                  }

                  var13 = var13 || var9.hasContextTag(2);
               }
            }

            if (var13) {
               return 3;
            }
         }
      }

      boolean var10 = true;
      int var12 = 0;

      for (int var14 = var1.size(); var12 < var14; var12++) {
         RecipientInfo var16 = RecipientInfo.getInstance(var1.getObjectAt(var12));
         if (var16.isPasswordOrOther()) {
            return 3;
         }

         var10 = var10 && var16.isKeyTransV0();
      }

      return var0 == null && var2 == null && var10 ? 0 : 2;
   }
}
