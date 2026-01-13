package org.bouncycastle.asn1.pkcs;

import java.util.Enumeration;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.ASN1Set;
import org.bouncycastle.asn1.ASN1TaggedObject;
import org.bouncycastle.asn1.BERSequence;
import org.bouncycastle.asn1.DERTaggedObject;

public class SignedData extends ASN1Object implements PKCSObjectIdentifiers {
   private ASN1Integer version;
   private ASN1Set digestAlgorithms;
   private ContentInfo contentInfo;
   private ASN1Set certificates;
   private ASN1Set crls;
   private ASN1Set signerInfos;

   public static SignedData getInstance(Object var0) {
      if (var0 instanceof SignedData) {
         return (SignedData)var0;
      } else {
         return var0 != null ? new SignedData(ASN1Sequence.getInstance(var0)) : null;
      }
   }

   public SignedData(ASN1Integer var1, ASN1Set var2, ContentInfo var3, ASN1Set var4, ASN1Set var5, ASN1Set var6) {
      this.version = var1;
      this.digestAlgorithms = var2;
      this.contentInfo = var3;
      this.certificates = var4;
      this.crls = var5;
      this.signerInfos = var6;
   }

   public SignedData(ASN1Sequence var1) {
      Enumeration var2 = var1.getObjects();
      this.version = (ASN1Integer)var2.nextElement();
      this.digestAlgorithms = (ASN1Set)var2.nextElement();
      this.contentInfo = ContentInfo.getInstance(var2.nextElement());

      while (var2.hasMoreElements()) {
         ASN1Primitive var3 = (ASN1Primitive)var2.nextElement();
         if (var3 instanceof ASN1TaggedObject) {
            ASN1TaggedObject var4 = (ASN1TaggedObject)var3;
            switch (var4.getTagNo()) {
               case 0:
                  this.certificates = ASN1Set.getInstance(var4, false);
                  break;
               case 1:
                  this.crls = ASN1Set.getInstance(var4, false);
                  break;
               default:
                  throw new IllegalArgumentException("unknown tag value " + var4.getTagNo());
            }
         } else {
            this.signerInfos = (ASN1Set)var3;
         }
      }
   }

   public ASN1Integer getVersion() {
      return this.version;
   }

   public ASN1Set getDigestAlgorithms() {
      return this.digestAlgorithms;
   }

   public ContentInfo getContentInfo() {
      return this.contentInfo;
   }

   public ASN1Set getCertificates() {
      return this.certificates;
   }

   public ASN1Set getCRLs() {
      return this.crls;
   }

   public ASN1Set getSignerInfos() {
      return this.signerInfos;
   }

   @Override
   public ASN1Primitive toASN1Primitive() {
      ASN1EncodableVector var1 = new ASN1EncodableVector(6);
      var1.add(this.version);
      var1.add(this.digestAlgorithms);
      var1.add(this.contentInfo);
      if (this.certificates != null) {
         var1.add(new DERTaggedObject(false, 0, this.certificates));
      }

      if (this.crls != null) {
         var1.add(new DERTaggedObject(false, 1, this.crls));
      }

      var1.add(this.signerInfos);
      return new BERSequence(var1);
   }
}
