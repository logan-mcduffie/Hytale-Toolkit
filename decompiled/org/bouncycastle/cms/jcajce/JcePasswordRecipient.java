package org.bouncycastle.cms.jcajce;

import java.security.GeneralSecurityException;
import java.security.Key;
import java.security.Provider;
import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.cms.CMSException;
import org.bouncycastle.cms.PasswordRecipient;

public abstract class JcePasswordRecipient implements PasswordRecipient {
   private int schemeID = 1;
   protected EnvelopedDataHelper helper = new EnvelopedDataHelper(new DefaultJcaJceExtHelper());
   private char[] password;

   JcePasswordRecipient(char[] var1) {
      this.password = var1;
   }

   public JcePasswordRecipient setPasswordConversionScheme(int var1) {
      this.schemeID = var1;
      return this;
   }

   public JcePasswordRecipient setProvider(Provider var1) {
      this.helper = new EnvelopedDataHelper(new ProviderJcaJceExtHelper(var1));
      return this;
   }

   public JcePasswordRecipient setProvider(String var1) {
      this.helper = new EnvelopedDataHelper(new NamedJcaJceExtHelper(var1));
      return this;
   }

   protected Key extractSecretKey(AlgorithmIdentifier var1, AlgorithmIdentifier var2, byte[] var3, byte[] var4) throws CMSException {
      Cipher var5 = this.helper.createRFC3211Wrapper(var1.getAlgorithm());

      try {
         IvParameterSpec var6 = new IvParameterSpec(ASN1OctetString.getInstance(var1.getParameters()).getOctets());
         var5.init(4, new SecretKeySpec(var3, var5.getAlgorithm()), var6);
         return var5.unwrap(var4, var2.getAlgorithm().getId(), 3);
      } catch (GeneralSecurityException var7) {
         throw new CMSException("cannot process content encryption key: " + var7.getMessage(), var7);
      }
   }

   @Override
   public byte[] calculateDerivedKey(int var1, AlgorithmIdentifier var2, int var3) throws CMSException {
      return this.helper.calculateDerivedKey(var1, this.password, var2, var3);
   }

   @Override
   public int getPasswordConversionScheme() {
      return this.schemeID;
   }

   @Override
   public char[] getPassword() {
      return this.password;
   }
}
