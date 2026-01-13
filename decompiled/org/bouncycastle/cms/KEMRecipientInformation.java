package org.bouncycastle.cms;

import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.cms.IssuerAndSerialNumber;
import org.bouncycastle.asn1.cms.KEMRecipientInfo;
import org.bouncycastle.asn1.cms.RecipientIdentifier;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.util.Arrays;

public class KEMRecipientInformation extends RecipientInformation {
   private KEMRecipientInfo info;

   KEMRecipientInformation(KEMRecipientInfo var1, AlgorithmIdentifier var2, CMSSecureReadable var3) {
      super(var1.getKem(), var2, var3);
      this.info = var1;
      RecipientIdentifier var4 = var1.getRecipientIdentifier();
      if (var4.isTagged()) {
         ASN1OctetString var5 = ASN1OctetString.getInstance(var4.getId());
         this.rid = new KEMRecipientId(var5.getOctets());
      } else {
         IssuerAndSerialNumber var6 = IssuerAndSerialNumber.getInstance(var4.getId());
         this.rid = new KEMRecipientId(var6.getName(), var6.getSerialNumber().getValue());
      }
   }

   public AlgorithmIdentifier getKdfAlgorithm() {
      return this.info.getKdf();
   }

   public byte[] getUkm() {
      return Arrays.clone(this.info.getUkm());
   }

   public byte[] getEncapsulation() {
      return Arrays.clone(this.info.getKemct().getOctets());
   }

   @Override
   protected RecipientOperator getRecipientOperator(Recipient var1) throws CMSException {
      return ((KEMRecipient)var1)
         .getRecipientOperator(
            new AlgorithmIdentifier(this.keyEncAlg.getAlgorithm(), this.info), this.messageAlgorithm, this.info.getEncryptedKey().getOctets()
         );
   }
}
