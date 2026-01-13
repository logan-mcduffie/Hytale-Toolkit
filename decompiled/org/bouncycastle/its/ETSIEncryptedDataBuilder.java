package org.bouncycastle.its;

import java.util.ArrayList;
import java.util.List;
import org.bouncycastle.its.operator.ETSIDataEncryptor;
import org.bouncycastle.oer.its.ieee1609dot2.AesCcmCiphertext;
import org.bouncycastle.oer.its.ieee1609dot2.EncryptedData;
import org.bouncycastle.oer.its.ieee1609dot2.SequenceOfRecipientInfo;
import org.bouncycastle.oer.its.ieee1609dot2.SymmetricCiphertext;

public class ETSIEncryptedDataBuilder {
   private final List<ETSIRecipientInfoBuilder> recipientInfoBuilders = new ArrayList<>();

   public void addRecipientInfoBuilder(ETSIRecipientInfoBuilder var1) {
      this.recipientInfoBuilders.add(var1);
   }

   public ETSIEncryptedData build(ETSIDataEncryptor var1, byte[] var2) {
      byte[] var3 = var1.encrypt(var2);
      byte[] var4 = var1.getKey();
      byte[] var5 = var1.getNonce();
      SequenceOfRecipientInfo.Builder var6 = SequenceOfRecipientInfo.builder();

      for (ETSIRecipientInfoBuilder var8 : this.recipientInfoBuilders) {
         var6.addRecipients(var8.build(var4));
      }

      return new ETSIEncryptedData(
         EncryptedData.builder()
            .setRecipients(var6.createSequenceOfRecipientInfo())
            .setCiphertext(SymmetricCiphertext.aes128ccm(AesCcmCiphertext.builder().setCcmCiphertext(var3).setNonce(var5).createAesCcmCiphertext()))
            .createEncryptedData()
      );
   }
}
