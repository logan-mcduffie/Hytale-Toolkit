package org.bouncycastle.oer.its.etsi102941;

import org.bouncycastle.asn1.ASN1Choice;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1TaggedObject;
import org.bouncycastle.asn1.DERTaggedObject;

public class EtsiTs102941DataContent extends ASN1Object implements ASN1Choice {
   public static final int enrolmentRequest = 0;
   public static final int enrolmentResponse = 1;
   public static final int authorizationRequest = 2;
   public static final int authorizationResponse = 3;
   public static final int certificateRevocationList = 4;
   public static final int certificateTrustListTlm = 5;
   public static final int certificateTrustListRca = 6;
   public static final int authorizationValidationRequest = 7;
   public static final int authorizationValidationResponse = 8;
   public static final int caCertificateRequest = 9;
   public static final int linkCertificateTlm = 10;
   public static final int singleSignedLinkCertificateRca = 11;
   public static final int doubleSignedlinkCertificateRca = 12;
   private final int choice;
   private final ASN1Encodable etsiTs102941DataContent;

   public EtsiTs102941DataContent(int var1, ASN1Encodable var2) {
      this.choice = var1;
      this.etsiTs102941DataContent = var2;
   }

   private EtsiTs102941DataContent(ASN1TaggedObject var1) {
      this.choice = var1.getTagNo();
      switch (this.choice) {
         case 0:
            this.etsiTs102941DataContent = InnerEcRequestSignedForPop.getInstance(var1.getExplicitBaseObject());
            return;
         case 1:
            this.etsiTs102941DataContent = InnerEcResponse.getInstance(var1.getExplicitBaseObject());
            return;
         case 2:
            this.etsiTs102941DataContent = InnerAtRequest.getInstance(var1.getExplicitBaseObject());
            return;
         case 3:
            this.etsiTs102941DataContent = InnerAtResponse.getInstance(var1.getExplicitBaseObject());
            return;
         case 4:
         default:
            throw new IllegalArgumentException("choice not implemented " + this.choice);
         case 5:
            this.etsiTs102941DataContent = ToBeSignedTlmCtl.getInstance(var1.getExplicitBaseObject());
            return;
         case 6:
            this.etsiTs102941DataContent = ToBeSignedRcaCtl.getInstance(var1.getExplicitBaseObject());
            return;
         case 7:
            this.etsiTs102941DataContent = AuthorizationValidationRequest.getInstance(var1.getExplicitBaseObject());
            return;
         case 8:
            this.etsiTs102941DataContent = AuthorizationValidationResponse.getInstance(var1.getExplicitBaseObject());
            return;
         case 9:
            this.etsiTs102941DataContent = CaCertificateRequest.getInstance(var1.getExplicitBaseObject());
      }
   }

   public int getChoice() {
      return this.choice;
   }

   public ASN1Encodable getEtsiTs102941DataContent() {
      return this.etsiTs102941DataContent;
   }

   public static EtsiTs102941DataContent getInstance(Object var0) {
      if (var0 instanceof EtsiTs102941DataContent) {
         return (EtsiTs102941DataContent)var0;
      } else {
         return var0 != null ? new EtsiTs102941DataContent(ASN1TaggedObject.getInstance(var0, 128)) : null;
      }
   }

   @Override
   public ASN1Primitive toASN1Primitive() {
      return new DERTaggedObject(this.choice, this.etsiTs102941DataContent);
   }
}
