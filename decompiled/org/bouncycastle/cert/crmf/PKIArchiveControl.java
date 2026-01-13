package org.bouncycastle.cert.crmf;

import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.cms.CMSObjectIdentifiers;
import org.bouncycastle.asn1.cms.ContentInfo;
import org.bouncycastle.asn1.cms.EnvelopedData;
import org.bouncycastle.asn1.crmf.CRMFObjectIdentifiers;
import org.bouncycastle.asn1.crmf.EncryptedKey;
import org.bouncycastle.asn1.crmf.PKIArchiveOptions;
import org.bouncycastle.cms.CMSEnvelopedData;
import org.bouncycastle.cms.CMSException;

public class PKIArchiveControl implements Control {
   public static final int encryptedPrivKey = 0;
   public static final int keyGenParameters = 1;
   public static final int archiveRemGenPrivKey = 2;
   private static final ASN1ObjectIdentifier type = CRMFObjectIdentifiers.id_regCtrl_pkiArchiveOptions;
   private final PKIArchiveOptions pkiArchiveOptions;

   public PKIArchiveControl(PKIArchiveOptions var1) {
      this.pkiArchiveOptions = var1;
   }

   @Override
   public ASN1ObjectIdentifier getType() {
      return type;
   }

   @Override
   public ASN1Encodable getValue() {
      return this.pkiArchiveOptions;
   }

   public int getArchiveType() {
      return this.pkiArchiveOptions.getType();
   }

   public boolean isEnvelopedData() {
      EncryptedKey var1 = EncryptedKey.getInstance(this.pkiArchiveOptions.getValue());
      return !var1.isEncryptedValue();
   }

   public CMSEnvelopedData getEnvelopedData() throws CRMFException {
      try {
         EncryptedKey var1 = EncryptedKey.getInstance(this.pkiArchiveOptions.getValue());
         EnvelopedData var2 = EnvelopedData.getInstance(var1.getValue());
         return new CMSEnvelopedData(new ContentInfo(CMSObjectIdentifiers.envelopedData, var2));
      } catch (CMSException var3) {
         throw new CRMFException("CMS parsing error: " + var3.getMessage(), var3.getCause());
      } catch (Exception var4) {
         throw new CRMFException("CRMF parsing error: " + var4.getMessage(), var4);
      }
   }
}
