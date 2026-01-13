package org.bouncycastle.cms;

import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.cms.IssuerAndSerialNumber;
import org.bouncycastle.asn1.cms.KeyTransRecipientInfo;
import org.bouncycastle.asn1.cms.RecipientIdentifier;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;

public class KeyTransRecipientInformation extends RecipientInformation {
   private KeyTransRecipientInfo info;

   KeyTransRecipientInformation(KeyTransRecipientInfo var1, AlgorithmIdentifier var2, CMSSecureReadable var3) {
      super(var1.getKeyEncryptionAlgorithm(), var2, var3);
      this.info = var1;
      RecipientIdentifier var4 = var1.getRecipientIdentifier();
      if (var4.isTagged()) {
         ASN1OctetString var5 = ASN1OctetString.getInstance(var4.getId());
         this.rid = new KeyTransRecipientId(var5.getOctets());
      } else {
         IssuerAndSerialNumber var6 = IssuerAndSerialNumber.getInstance(var4.getId());
         this.rid = new KeyTransRecipientId(var6.getName(), var6.getSerialNumber().getValue());
      }
   }

   @Override
   protected RecipientOperator getRecipientOperator(Recipient var1) throws CMSException {
      return ((KeyTransRecipient)var1).getRecipientOperator(this.keyEncAlg, this.messageAlgorithm, this.info.getEncryptedKey().getOctets());
   }
}
