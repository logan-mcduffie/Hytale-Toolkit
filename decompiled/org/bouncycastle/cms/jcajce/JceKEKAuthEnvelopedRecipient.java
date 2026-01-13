package org.bouncycastle.cms.jcajce;

import java.security.Key;
import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.cms.CMSException;
import org.bouncycastle.cms.RecipientOperator;

public class JceKEKAuthEnvelopedRecipient extends JceKEKRecipient {
   public JceKEKAuthEnvelopedRecipient(SecretKey var1) {
      super(var1);
   }

   @Override
   public RecipientOperator getRecipientOperator(AlgorithmIdentifier var1, AlgorithmIdentifier var2, byte[] var3) throws CMSException {
      Key var4 = this.extractSecretKey(var1, var2, var3);
      Cipher var5 = this.contentHelper.createContentCipher(var4, var2);
      return new RecipientOperator(new CMSInputAEADDecryptor(var2, var5));
   }
}
