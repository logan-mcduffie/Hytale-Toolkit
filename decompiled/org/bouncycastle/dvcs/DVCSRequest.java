package org.bouncycastle.dvcs;

import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.cms.ContentInfo;
import org.bouncycastle.asn1.cms.SignedData;
import org.bouncycastle.asn1.dvcs.DVCSObjectIdentifiers;
import org.bouncycastle.asn1.dvcs.ServiceType;
import org.bouncycastle.asn1.x509.GeneralName;
import org.bouncycastle.cms.CMSSignedData;

public class DVCSRequest extends DVCSMessage {
   private org.bouncycastle.asn1.dvcs.DVCSRequest asn1;
   private DVCSRequestInfo reqInfo;
   private DVCSRequestData data;

   public DVCSRequest(CMSSignedData var1) throws DVCSConstructionException {
      this(SignedData.getInstance(var1.toASN1Structure().getContent()).getEncapContentInfo());
   }

   public DVCSRequest(ContentInfo var1) throws DVCSConstructionException {
      super(var1);
      if (!DVCSObjectIdentifiers.id_ct_DVCSRequestData.equals(var1.getContentType())) {
         throw new DVCSConstructionException("ContentInfo not a DVCS Request");
      } else {
         try {
            if (var1.getContent().toASN1Primitive() instanceof ASN1Sequence) {
               this.asn1 = org.bouncycastle.asn1.dvcs.DVCSRequest.getInstance(var1.getContent());
            } else {
               this.asn1 = org.bouncycastle.asn1.dvcs.DVCSRequest.getInstance(ASN1OctetString.getInstance(var1.getContent()).getOctets());
            }
         } catch (Exception var3) {
            throw new DVCSConstructionException("Unable to parse content: " + var3.getMessage(), var3);
         }

         this.reqInfo = new DVCSRequestInfo(this.asn1.getRequestInformation());
         int var2 = this.reqInfo.getServiceType();
         if (var2 == ServiceType.CPD.getValue().intValue()) {
            this.data = new CPDRequestData(this.asn1.getData());
         } else if (var2 == ServiceType.VSD.getValue().intValue()) {
            this.data = new VSDRequestData(this.asn1.getData());
         } else if (var2 == ServiceType.VPKC.getValue().intValue()) {
            this.data = new VPKCRequestData(this.asn1.getData());
         } else {
            if (var2 != ServiceType.CCPD.getValue().intValue()) {
               throw new DVCSConstructionException("Unknown service type: " + var2);
            }

            this.data = new CCPDRequestData(this.asn1.getData());
         }
      }
   }

   @Override
   public ASN1Encodable getContent() {
      return this.asn1;
   }

   public DVCSRequestInfo getRequestInfo() {
      return this.reqInfo;
   }

   public DVCSRequestData getData() {
      return this.data;
   }

   public GeneralName getTransactionIdentifier() {
      return this.asn1.getTransactionIdentifier();
   }
}
