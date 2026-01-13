package org.bouncycastle.cms.jcajce;

import java.io.IOException;
import java.io.InputStream;
import java.security.Key;
import java.security.PrivateKey;
import javax.crypto.Cipher;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.cms.CMSException;
import org.bouncycastle.cms.KeyTransRecipientId;
import org.bouncycastle.cms.RecipientOperator;
import org.bouncycastle.jcajce.io.CipherInputStream;
import org.bouncycastle.operator.InputDecryptor;

public class JceKTSKeyTransEnvelopedRecipient extends JceKTSKeyTransRecipient {
   public JceKTSKeyTransEnvelopedRecipient(PrivateKey var1, KeyTransRecipientId var2) throws IOException {
      super(var1, getPartyVInfoFromRID(var2));
   }

   @Override
   public RecipientOperator getRecipientOperator(AlgorithmIdentifier var1, final AlgorithmIdentifier var2, byte[] var3) throws CMSException {
      Key var4 = this.extractSecretKey(var1, var2, var3);
      final Cipher var5 = this.contentHelper.createContentCipher(var4, var2);
      return new RecipientOperator(new InputDecryptor() {
         @Override
         public AlgorithmIdentifier getAlgorithmIdentifier() {
            return var2;
         }

         @Override
         public InputStream getInputStream(InputStream var1) {
            return new CipherInputStream(var1, var5);
         }
      });
   }
}
