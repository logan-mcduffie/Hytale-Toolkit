package org.bouncycastle.cert.crmf;

import java.util.Collection;
import org.bouncycastle.asn1.cmp.CMPCertificate;
import org.bouncycastle.asn1.cmp.CertResponse;
import org.bouncycastle.asn1.cmp.CertifiedKeyPair;
import org.bouncycastle.asn1.cms.ContentInfo;
import org.bouncycastle.asn1.pkcs.PKCSObjectIdentifiers;
import org.bouncycastle.cms.CMSEnvelopedData;
import org.bouncycastle.cms.CMSException;
import org.bouncycastle.cms.Recipient;
import org.bouncycastle.cms.RecipientInformation;
import org.bouncycastle.cms.RecipientInformationStore;

public class CertificateResponse {
   private final CertResponse certResponse;

   public CertificateResponse(CertResponse var1) {
      this.certResponse = var1;
   }

   public boolean hasEncryptedCertificate() {
      return this.certResponse.getCertifiedKeyPair().getCertOrEncCert().hasEncryptedCertificate();
   }

   public CMSEnvelopedData getEncryptedCertificate() throws CMSException {
      if (!this.hasEncryptedCertificate()) {
         throw new IllegalStateException("encrypted certificate asked for, none found");
      } else {
         CertifiedKeyPair var1 = this.certResponse.getCertifiedKeyPair();
         CMSEnvelopedData var2 = new CMSEnvelopedData(
            new ContentInfo(PKCSObjectIdentifiers.envelopedData, var1.getCertOrEncCert().getEncryptedCert().getValue())
         );
         if (var2.getRecipientInfos().size() != 1) {
            throw new IllegalStateException("data encrypted for more than one recipient");
         } else {
            return var2;
         }
      }
   }

   public CMPCertificate getCertificate(Recipient var1) throws CMSException {
      CMSEnvelopedData var2 = this.getEncryptedCertificate();
      RecipientInformationStore var3 = var2.getRecipientInfos();
      Collection var4 = var3.getRecipients();
      RecipientInformation var5 = (RecipientInformation)var4.iterator().next();
      return CMPCertificate.getInstance(var5.getContent(var1));
   }

   public CMPCertificate getCertificate() throws CMSException {
      if (this.hasEncryptedCertificate()) {
         throw new IllegalStateException("plaintext certificate asked for, none found");
      } else {
         return this.certResponse.getCertifiedKeyPair().getCertOrEncCert().getCertificate();
      }
   }

   public CertResponse toASN1Structure() {
      return this.certResponse;
   }
}
