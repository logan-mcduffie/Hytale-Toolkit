package org.bouncycastle.cert.crmf;

import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.DEROctetString;
import org.bouncycastle.asn1.cmp.CMPCertificate;
import org.bouncycastle.asn1.cmp.CertOrEncCert;
import org.bouncycastle.asn1.cmp.CertResponse;
import org.bouncycastle.asn1.cmp.CertifiedKeyPair;
import org.bouncycastle.asn1.cmp.PKIStatusInfo;
import org.bouncycastle.asn1.cms.EnvelopedData;
import org.bouncycastle.asn1.crmf.EncryptedKey;
import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.cms.CMSEnvelopedData;

public class CertificateResponseBuilder {
   private final ASN1Integer certReqId;
   private final PKIStatusInfo statusInfo;
   private CertifiedKeyPair certKeyPair;
   private ASN1OctetString rspInfo;

   public CertificateResponseBuilder(ASN1Integer var1, PKIStatusInfo var2) {
      this.certReqId = var1;
      this.statusInfo = var2;
   }

   public CertificateResponseBuilder withCertificate(X509CertificateHolder var1) {
      if (this.certKeyPair != null) {
         throw new IllegalStateException("certificate in response already set");
      } else {
         this.certKeyPair = new CertifiedKeyPair(new CertOrEncCert(new CMPCertificate(var1.toASN1Structure())));
         return this;
      }
   }

   public CertificateResponseBuilder withCertificate(CMPCertificate var1) {
      if (this.certKeyPair != null) {
         throw new IllegalStateException("certificate in response already set");
      } else {
         this.certKeyPair = new CertifiedKeyPair(new CertOrEncCert(var1));
         return this;
      }
   }

   public CertificateResponseBuilder withCertificate(CMSEnvelopedData var1) {
      if (this.certKeyPair != null) {
         throw new IllegalStateException("certificate in response already set");
      } else {
         this.certKeyPair = new CertifiedKeyPair(new CertOrEncCert(new EncryptedKey(EnvelopedData.getInstance(var1.toASN1Structure().getContent()))));
         return this;
      }
   }

   public CertificateResponseBuilder withResponseInfo(byte[] var1) {
      if (this.rspInfo != null) {
         throw new IllegalStateException("response info already set");
      } else {
         this.rspInfo = new DEROctetString(var1);
         return this;
      }
   }

   public CertificateResponse build() {
      return new CertificateResponse(new CertResponse(this.certReqId, this.statusInfo, this.certKeyPair, this.rspInfo));
   }
}
