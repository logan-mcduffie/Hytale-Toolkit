package org.bouncycastle.asn1.cms;

import org.bouncycastle.asn1.ASN1Encodable;
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
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;

public class AuthenticatedData extends ASN1Object {
   private ASN1Integer version;
   private OriginatorInfo originatorInfo;
   private ASN1Set recipientInfos;
   private AlgorithmIdentifier macAlgorithm;
   private AlgorithmIdentifier digestAlgorithm;
   private ContentInfo encapsulatedContentInfo;
   private ASN1Set authAttrs;
   private ASN1OctetString mac;
   private ASN1Set unauthAttrs;

   public AuthenticatedData(
      OriginatorInfo var1, ASN1Set var2, AlgorithmIdentifier var3, AlgorithmIdentifier var4, ContentInfo var5, ASN1Set var6, ASN1OctetString var7, ASN1Set var8
   ) {
      if (var4 == null && var6 == null || var4 != null && var6 != null) {
         this.version = new ASN1Integer(calculateVersion(var1));
         this.originatorInfo = var1;
         this.macAlgorithm = var3;
         this.digestAlgorithm = var4;
         this.recipientInfos = var2;
         this.encapsulatedContentInfo = var5;
         this.authAttrs = var6;
         this.mac = var7;
         this.unauthAttrs = var8;
      } else {
         throw new IllegalArgumentException("digestAlgorithm and authAttrs must be set together");
      }
   }

   private AuthenticatedData(ASN1Sequence var1) {
      int var2 = 0;
      this.version = (ASN1Integer)var1.getObjectAt(var2++);
      ASN1Encodable var3 = var1.getObjectAt(var2++);
      if (var3 instanceof ASN1TaggedObject) {
         this.originatorInfo = OriginatorInfo.getInstance((ASN1TaggedObject)var3, false);
         var3 = var1.getObjectAt(var2++);
      }

      this.recipientInfos = ASN1Set.getInstance(var3);
      this.macAlgorithm = AlgorithmIdentifier.getInstance(var1.getObjectAt(var2++));
      var3 = var1.getObjectAt(var2++);
      if (var3 instanceof ASN1TaggedObject) {
         this.digestAlgorithm = AlgorithmIdentifier.getInstance((ASN1TaggedObject)var3, false);
         var3 = var1.getObjectAt(var2++);
      }

      this.encapsulatedContentInfo = ContentInfo.getInstance(var3);
      var3 = var1.getObjectAt(var2++);
      if (var3 instanceof ASN1TaggedObject) {
         this.authAttrs = ASN1Set.getInstance((ASN1TaggedObject)var3, false);
         var3 = var1.getObjectAt(var2++);
      }

      this.mac = ASN1OctetString.getInstance(var3);
      if (var1.size() > var2) {
         this.unauthAttrs = ASN1Set.getInstance((ASN1TaggedObject)var1.getObjectAt(var2), false);
      }
   }

   public static AuthenticatedData getInstance(ASN1TaggedObject var0, boolean var1) {
      return getInstance(ASN1Sequence.getInstance(var0, var1));
   }

   public static AuthenticatedData getInstance(Object var0) {
      if (var0 instanceof AuthenticatedData) {
         return (AuthenticatedData)var0;
      } else {
         return var0 != null ? new AuthenticatedData(ASN1Sequence.getInstance(var0)) : null;
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

   public AlgorithmIdentifier getMacAlgorithm() {
      return this.macAlgorithm;
   }

   public AlgorithmIdentifier getDigestAlgorithm() {
      return this.digestAlgorithm;
   }

   public ContentInfo getEncapsulatedContentInfo() {
      return this.encapsulatedContentInfo;
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
      ASN1EncodableVector var1 = new ASN1EncodableVector(9);
      var1.add(this.version);
      if (this.originatorInfo != null) {
         var1.add(new DERTaggedObject(false, 0, this.originatorInfo));
      }

      var1.add(this.recipientInfos);
      var1.add(this.macAlgorithm);
      if (this.digestAlgorithm != null) {
         var1.add(new DERTaggedObject(false, 1, this.digestAlgorithm));
      }

      var1.add(this.encapsulatedContentInfo);
      if (this.authAttrs != null) {
         var1.add(new DERTaggedObject(false, 2, this.authAttrs));
      }

      var1.add(this.mac);
      if (this.unauthAttrs != null) {
         var1.add(new DERTaggedObject(false, 3, this.unauthAttrs));
      }

      return new BERSequence(var1);
   }

   public static int calculateVersion(OriginatorInfo var0) {
      if (var0 != null) {
         ASN1Set var1 = var0.getCRLs();
         if (var1 != null) {
            int var2 = 0;

            for (int var3 = var1.size(); var2 < var3; var2++) {
               ASN1Encodable var4 = var1.getObjectAt(var2);
               if (var4 instanceof ASN1TaggedObject) {
                  ASN1TaggedObject var5 = (ASN1TaggedObject)var4;
                  if (var5.hasContextTag(1)) {
                     return 3;
                  }
               }
            }
         }

         ASN1Set var8 = var0.getCertificates();
         if (var8 != null) {
            boolean var9 = false;
            int var10 = 0;

            for (int var11 = var8.size(); var10 < var11; var10++) {
               ASN1Encodable var6 = var8.getObjectAt(var10);
               if (var6 instanceof ASN1TaggedObject) {
                  ASN1TaggedObject var7 = (ASN1TaggedObject)var6;
                  if (var7.hasContextTag(3)) {
                     return 3;
                  }

                  var9 = var9 || var7.hasContextTag(2);
               }
            }

            if (var9) {
               return 1;
            }
         }
      }

      return 0;
   }
}
