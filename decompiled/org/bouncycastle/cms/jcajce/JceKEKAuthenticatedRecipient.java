package org.bouncycastle.cms.jcajce;

import java.io.OutputStream;
import java.security.Key;
import javax.crypto.Mac;
import javax.crypto.SecretKey;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.cms.CMSException;
import org.bouncycastle.cms.RecipientOperator;
import org.bouncycastle.jcajce.io.MacOutputStream;
import org.bouncycastle.operator.GenericKey;
import org.bouncycastle.operator.MacCalculator;
import org.bouncycastle.operator.jcajce.JceGenericKey;

public class JceKEKAuthenticatedRecipient extends JceKEKRecipient {
   public JceKEKAuthenticatedRecipient(SecretKey var1) {
      super(var1);
   }

   @Override
   public RecipientOperator getRecipientOperator(AlgorithmIdentifier var1, final AlgorithmIdentifier var2, byte[] var3) throws CMSException {
      final Key var4 = this.extractSecretKey(var1, var2, var3);
      final Mac var5 = this.contentHelper.createContentMac(var4, var2);
      return new RecipientOperator(new MacCalculator() {
         @Override
         public AlgorithmIdentifier getAlgorithmIdentifier() {
            return var2;
         }

         @Override
         public GenericKey getKey() {
            return new JceGenericKey(var2, var4);
         }

         @Override
         public OutputStream getOutputStream() {
            return new MacOutputStream(var5);
         }

         @Override
         public byte[] getMac() {
            return var5.doFinal();
         }
      });
   }
}
