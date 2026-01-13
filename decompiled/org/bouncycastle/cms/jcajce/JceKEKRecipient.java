package org.bouncycastle.cms.jcajce;

import java.security.Key;
import java.security.Provider;
import javax.crypto.SecretKey;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.cms.CMSException;
import org.bouncycastle.cms.KEKRecipient;
import org.bouncycastle.operator.OperatorException;
import org.bouncycastle.operator.SymmetricKeyUnwrapper;

public abstract class JceKEKRecipient implements KEKRecipient {
   private SecretKey recipientKey;
   protected EnvelopedDataHelper helper = new EnvelopedDataHelper(new DefaultJcaJceExtHelper());
   protected EnvelopedDataHelper contentHelper = this.helper;
   protected boolean validateKeySize = false;

   public JceKEKRecipient(SecretKey var1) {
      this.recipientKey = var1;
   }

   public JceKEKRecipient setProvider(Provider var1) {
      this.helper = new EnvelopedDataHelper(new ProviderJcaJceExtHelper(var1));
      this.contentHelper = this.helper;
      return this;
   }

   public JceKEKRecipient setProvider(String var1) {
      this.helper = new EnvelopedDataHelper(new NamedJcaJceExtHelper(var1));
      this.contentHelper = this.helper;
      return this;
   }

   public JceKEKRecipient setContentProvider(Provider var1) {
      this.contentHelper = new EnvelopedDataHelper(new ProviderJcaJceExtHelper(var1));
      return this;
   }

   public JceKEKRecipient setContentProvider(String var1) {
      this.contentHelper = new EnvelopedDataHelper(new NamedJcaJceExtHelper(var1));
      return this;
   }

   public JceKEKRecipient setKeySizeValidation(boolean var1) {
      this.validateKeySize = var1;
      return this;
   }

   protected Key extractSecretKey(AlgorithmIdentifier var1, AlgorithmIdentifier var2, byte[] var3) throws CMSException {
      SymmetricKeyUnwrapper var4 = this.helper.createSymmetricUnwrapper(var1, this.recipientKey);

      try {
         Key var5 = this.helper.getJceKey(var2, var4.generateUnwrappedKey(var2, var3));
         if (this.validateKeySize) {
            this.helper.keySizeCheck(var2, var5);
         }

         return var5;
      } catch (OperatorException var6) {
         throw new CMSException("exception unwrapping key: " + var6.getMessage(), var6);
      }
   }
}
