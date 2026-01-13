package org.bouncycastle.cms;

import java.io.IOException;
import org.bouncycastle.asn1.cms.KEKIdentifier;
import org.bouncycastle.asn1.cms.KEKRecipientInfo;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;

public class KEKRecipientInformation extends RecipientInformation {
   private KEKRecipientInfo info;

   KEKRecipientInformation(KEKRecipientInfo var1, AlgorithmIdentifier var2, CMSSecureReadable var3) {
      super(var1.getKeyEncryptionAlgorithm(), var2, var3);
      this.info = var1;
      KEKIdentifier var4 = var1.getKekid();
      this.rid = new KEKRecipientId(var4.getKeyIdentifier().getOctets());
   }

   @Override
   protected RecipientOperator getRecipientOperator(Recipient var1) throws CMSException, IOException {
      return ((KEKRecipient)var1).getRecipientOperator(this.keyEncAlg, this.messageAlgorithm, this.info.getEncryptedKey().getOctets());
   }
}
