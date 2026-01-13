package org.bouncycastle.cms;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.cms.PasswordRecipientInfo;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.util.Integers;

public class PasswordRecipientInformation extends RecipientInformation {
   static Map KEYSIZES = new HashMap();
   static Map BLOCKSIZES = new HashMap();
   private PasswordRecipientInfo info;

   PasswordRecipientInformation(PasswordRecipientInfo var1, AlgorithmIdentifier var2, CMSSecureReadable var3) {
      super(var1.getKeyEncryptionAlgorithm(), var2, var3);
      this.info = var1;
      this.rid = new PasswordRecipientId();
   }

   public String getKeyDerivationAlgOID() {
      return this.info.getKeyDerivationAlgorithm() != null ? this.info.getKeyDerivationAlgorithm().getAlgorithm().getId() : null;
   }

   public byte[] getKeyDerivationAlgParams() {
      try {
         if (this.info.getKeyDerivationAlgorithm() != null) {
            ASN1Encodable var1 = this.info.getKeyDerivationAlgorithm().getParameters();
            if (var1 != null) {
               return var1.toASN1Primitive().getEncoded();
            }
         }

         return null;
      } catch (Exception var2) {
         throw new RuntimeException("exception getting encryption parameters " + var2);
      }
   }

   public AlgorithmIdentifier getKeyDerivationAlgorithm() {
      return this.info.getKeyDerivationAlgorithm();
   }

   @Override
   protected RecipientOperator getRecipientOperator(Recipient var1) throws CMSException, IOException {
      PasswordRecipient var2 = (PasswordRecipient)var1;
      AlgorithmIdentifier var3 = AlgorithmIdentifier.getInstance(this.info.getKeyEncryptionAlgorithm());
      AlgorithmIdentifier var4 = AlgorithmIdentifier.getInstance(var3.getParameters());
      int var5 = (Integer)KEYSIZES.get(var4.getAlgorithm());
      byte[] var6 = var2.calculateDerivedKey(var2.getPasswordConversionScheme(), this.getKeyDerivationAlgorithm(), var5);
      return var2.getRecipientOperator(var4, this.messageAlgorithm, var6, this.info.getEncryptedKey().getOctets());
   }

   static {
      BLOCKSIZES.put(CMSAlgorithm.DES_EDE3_CBC, Integers.valueOf(8));
      BLOCKSIZES.put(CMSAlgorithm.AES128_CBC, Integers.valueOf(16));
      BLOCKSIZES.put(CMSAlgorithm.AES192_CBC, Integers.valueOf(16));
      BLOCKSIZES.put(CMSAlgorithm.AES256_CBC, Integers.valueOf(16));
      KEYSIZES.put(CMSAlgorithm.DES_EDE3_CBC, Integers.valueOf(192));
      KEYSIZES.put(CMSAlgorithm.AES128_CBC, Integers.valueOf(128));
      KEYSIZES.put(CMSAlgorithm.AES192_CBC, Integers.valueOf(192));
      KEYSIZES.put(CMSAlgorithm.AES256_CBC, Integers.valueOf(256));
   }
}
