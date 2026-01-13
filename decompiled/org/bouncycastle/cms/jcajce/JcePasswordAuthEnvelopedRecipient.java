package org.bouncycastle.cms.jcajce;

import java.security.Key;
import javax.crypto.Cipher;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.cms.CMSException;
import org.bouncycastle.cms.RecipientOperator;

public class JcePasswordAuthEnvelopedRecipient extends JcePasswordRecipient {
   public JcePasswordAuthEnvelopedRecipient(char[] var1) {
      super(var1);
   }

   @Override
   public RecipientOperator getRecipientOperator(AlgorithmIdentifier var1, AlgorithmIdentifier var2, byte[] var3, byte[] var4) throws CMSException {
      Key var5 = this.extractSecretKey(var1, var2, var3, var4);
      Cipher var6 = this.helper.createContentCipher(var5, var2);
      return new RecipientOperator(new CMSInputAEADDecryptor(var2, var6));
   }
}
