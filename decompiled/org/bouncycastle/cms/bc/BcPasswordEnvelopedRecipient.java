package org.bouncycastle.cms.bc;

import java.io.InputStream;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.cms.CMSException;
import org.bouncycastle.cms.RecipientOperator;
import org.bouncycastle.crypto.BufferedBlockCipher;
import org.bouncycastle.crypto.StreamCipher;
import org.bouncycastle.crypto.io.CipherInputStream;
import org.bouncycastle.crypto.params.KeyParameter;
import org.bouncycastle.operator.InputDecryptor;

public class BcPasswordEnvelopedRecipient extends BcPasswordRecipient {
   public BcPasswordEnvelopedRecipient(char[] var1) {
      super(var1);
   }

   @Override
   public RecipientOperator getRecipientOperator(AlgorithmIdentifier var1, final AlgorithmIdentifier var2, byte[] var3, byte[] var4) throws CMSException {
      KeyParameter var5 = this.extractSecretKey(var1, var2, var3, var4);
      final Object var6 = EnvelopedDataHelper.createContentCipher(false, var5, var2);
      return new RecipientOperator(
         new InputDecryptor() {
            @Override
            public AlgorithmIdentifier getAlgorithmIdentifier() {
               return var2;
            }

            @Override
            public InputStream getInputStream(InputStream var1) {
               return var6 instanceof BufferedBlockCipher
                  ? new CipherInputStream(var1, (BufferedBlockCipher)var6)
                  : new CipherInputStream(var1, (StreamCipher)var6);
            }
         }
      );
   }
}
