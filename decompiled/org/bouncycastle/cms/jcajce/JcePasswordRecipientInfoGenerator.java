package org.bouncycastle.cms.jcajce;

import java.security.GeneralSecurityException;
import java.security.Key;
import java.security.Provider;
import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.cms.CMSException;
import org.bouncycastle.cms.PasswordRecipientInfoGenerator;
import org.bouncycastle.operator.GenericKey;

public class JcePasswordRecipientInfoGenerator extends PasswordRecipientInfoGenerator {
   private EnvelopedDataHelper helper = new EnvelopedDataHelper(new DefaultJcaJceExtHelper());

   public JcePasswordRecipientInfoGenerator(ASN1ObjectIdentifier var1, char[] var2) {
      super(var1, var2);
   }

   public JcePasswordRecipientInfoGenerator setProvider(Provider var1) {
      this.helper = new EnvelopedDataHelper(new ProviderJcaJceExtHelper(var1));
      return this;
   }

   public JcePasswordRecipientInfoGenerator setProvider(String var1) {
      this.helper = new EnvelopedDataHelper(new NamedJcaJceExtHelper(var1));
      return this;
   }

   @Override
   protected byte[] calculateDerivedKey(int var1, AlgorithmIdentifier var2, int var3) throws CMSException {
      return this.helper.calculateDerivedKey(var1, this.password, var2, var3);
   }

   @Override
   public byte[] generateEncryptedBytes(AlgorithmIdentifier var1, byte[] var2, GenericKey var3) throws CMSException {
      Key var4 = this.helper.getJceKey(var3);
      Cipher var5 = this.helper.createRFC3211Wrapper(var1.getAlgorithm());

      try {
         IvParameterSpec var6 = new IvParameterSpec(ASN1OctetString.getInstance(var1.getParameters()).getOctets());
         var5.init(3, new SecretKeySpec(var2, var5.getAlgorithm()), var6);
         return var5.wrap(var4);
      } catch (GeneralSecurityException var7) {
         throw new CMSException("cannot process content encryption key: " + var7.getMessage(), var7);
      }
   }
}
