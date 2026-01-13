package org.bouncycastle.its;

import org.bouncycastle.its.operator.ETSIDataDecryptor;
import org.bouncycastle.oer.its.ieee1609dot2.AesCcmCiphertext;
import org.bouncycastle.oer.its.ieee1609dot2.EncryptedData;
import org.bouncycastle.oer.its.ieee1609dot2.EncryptedDataEncryptionKey;
import org.bouncycastle.oer.its.ieee1609dot2.PKRecipientInfo;
import org.bouncycastle.oer.its.ieee1609dot2.RecipientInfo;
import org.bouncycastle.oer.its.ieee1609dot2.basetypes.EccP256CurvePoint;
import org.bouncycastle.oer.its.ieee1609dot2.basetypes.EciesP256EncryptedKey;
import org.bouncycastle.util.Arrays;

public class ETSIRecipientInfo {
   private final RecipientInfo recipientInfo;
   private final EncryptedData encryptedData;

   public ETSIRecipientInfo(EncryptedData var1, RecipientInfo var2) {
      this.recipientInfo = var2;
      this.encryptedData = var1;
   }

   public ETSIRecipientInfo(RecipientInfo var1) {
      this.recipientInfo = var1;
      this.encryptedData = null;
   }

   public RecipientInfo getRecipientInfo() {
      return this.recipientInfo;
   }

   public EncryptedData getEncryptedData() {
      return this.encryptedData;
   }

   public byte[] getContent(ETSIDataDecryptor var1) {
      if (0 != this.encryptedData.getCiphertext().getChoice()) {
         throw new IllegalArgumentException("Encrypted data is no AES 128 CCM");
      } else {
         AesCcmCiphertext var2 = AesCcmCiphertext.getInstance(this.encryptedData.getCiphertext().getSymmetricCiphertext());
         PKRecipientInfo var3 = PKRecipientInfo.getInstance(this.recipientInfo.getRecipientInfo());
         EncryptedDataEncryptionKey var4 = var3.getEncKey();
         EciesP256EncryptedKey var5 = EciesP256EncryptedKey.getInstance(var4.getEncryptedDataEncryptionKey());
         EccP256CurvePoint var6 = EccP256CurvePoint.getInstance(var5.getV());
         byte[] var7 = Arrays.concatenate(var6.getEncodedPoint(), var5.getC().getOctets(), var5.getT().getOctets());
         return var1.decrypt(var7, var2.getCcmCiphertext().getContent(), var2.getNonce().getOctets());
      }
   }
}
