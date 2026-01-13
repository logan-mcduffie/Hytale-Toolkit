package org.bouncycastle.asn1.cms;

import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.ASN1Set;
import org.bouncycastle.asn1.ASN1TaggedObject;
import org.bouncycastle.asn1.BERSequence;
import org.bouncycastle.asn1.DERTaggedObject;

public class AuthEnvelopedData extends ASN1Object {
   private ASN1Integer version;
   private OriginatorInfo originatorInfo;
   private ASN1Set recipientInfos;
   private EncryptedContentInfo authEncryptedContentInfo;
   private ASN1Set authAttrs;
   private ASN1OctetString mac;
   private ASN1Set unauthAttrs;

   public AuthEnvelopedData(OriginatorInfo var1, ASN1Set var2, EncryptedContentInfo var3, ASN1Set var4, ASN1OctetString var5, ASN1Set var6) {
      this.version = new ASN1Integer(0L);
      this.originatorInfo = var1;
      this.recipientInfos = var2;
      if (this.recipientInfos.size() == 0) {
         throw new IllegalArgumentException("AuthEnvelopedData requires at least 1 RecipientInfo");
      } else {
         this.authEncryptedContentInfo = var3;
         this.authAttrs = var4;
         if (var3.getContentType().equals(CMSObjectIdentifiers.data) || var4 != null && var4.size() != 0) {
            this.mac = var5;
            this.unauthAttrs = var6;
         } else {
            throw new IllegalArgumentException("authAttrs must be present with non-data content");
         }
      }
   }

   private AuthEnvelopedData(ASN1Sequence var1) {
      int var2 = 0;
      ASN1Primitive var3 = var1.getObjectAt(var2++).toASN1Primitive();
      this.version = ASN1Integer.getInstance(var3);
      if (!this.version.hasValue(0)) {
         throw new IllegalArgumentException("AuthEnvelopedData version number must be 0");
      } else {
         var3 = var1.getObjectAt(var2++).toASN1Primitive();
         if (var3 instanceof ASN1TaggedObject) {
            this.originatorInfo = OriginatorInfo.getInstance((ASN1TaggedObject)var3, false);
            var3 = var1.getObjectAt(var2++).toASN1Primitive();
         }

         this.recipientInfos = ASN1Set.getInstance(var3);
         if (this.recipientInfos.size() == 0) {
            throw new IllegalArgumentException("AuthEnvelopedData requires at least 1 RecipientInfo");
         } else {
            var3 = var1.getObjectAt(var2++).toASN1Primitive();
            this.authEncryptedContentInfo = EncryptedContentInfo.getInstance(var3);
            var3 = var1.getObjectAt(var2++).toASN1Primitive();
            if (var3 instanceof ASN1TaggedObject) {
               this.authAttrs = ASN1Set.getInstance((ASN1TaggedObject)var3, false);
               var3 = var1.getObjectAt(var2++).toASN1Primitive();
            } else if (!this.authEncryptedContentInfo.getContentType().equals(CMSObjectIdentifiers.data)
               && (this.authAttrs == null || this.authAttrs.size() == 0)) {
               throw new IllegalArgumentException("authAttrs must be present with non-data content");
            }

            this.mac = ASN1OctetString.getInstance(var3);
            if (var1.size() > var2) {
               var3 = var1.getObjectAt(var2).toASN1Primitive();
               this.unauthAttrs = ASN1Set.getInstance((ASN1TaggedObject)var3, false);
            }
         }
      }
   }

   public static AuthEnvelopedData getInstance(ASN1TaggedObject var0, boolean var1) {
      return getInstance(ASN1Sequence.getInstance(var0, var1));
   }

   public static AuthEnvelopedData getInstance(Object var0) {
      if (var0 instanceof AuthEnvelopedData) {
         return (AuthEnvelopedData)var0;
      } else {
         return var0 != null ? new AuthEnvelopedData(ASN1Sequence.getInstance(var0)) : null;
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

   public EncryptedContentInfo getAuthEncryptedContentInfo() {
      return this.authEncryptedContentInfo;
   }

   public ASN1Set getAuthAttrs() {
      return this.authAttrs;
   }

   public ASN1OctetString getMac() {
      return this.mac;
   }

   public ASN1Set getUnauthAttrs() {
      return this.unauthAttrs;
   }

   @Override
   public ASN1Primitive toASN1Primitive() {
      ASN1EncodableVector var1 = new ASN1EncodableVector(7);
      var1.add(this.version);
      if (this.originatorInfo != null) {
         var1.add(new DERTaggedObject(false, 0, this.originatorInfo));
      }

      var1.add(this.recipientInfos);
      var1.add(this.authEncryptedContentInfo);
      if (this.authAttrs != null) {
         var1.add(new DERTaggedObject(false, 1, this.authAttrs));
      }

      var1.add(this.mac);
      if (this.unauthAttrs != null) {
         var1.add(new DERTaggedObject(false, 2, this.unauthAttrs));
      }

      return new BERSequence(var1);
   }
}
