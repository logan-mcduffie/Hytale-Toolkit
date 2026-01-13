package org.bouncycastle.openssl.jcajce;

import java.security.Provider;
import java.security.SecureRandom;
import org.bouncycastle.jcajce.util.DefaultJcaJceHelper;
import org.bouncycastle.jcajce.util.JcaJceHelper;
import org.bouncycastle.jcajce.util.NamedJcaJceHelper;
import org.bouncycastle.jcajce.util.ProviderJcaJceHelper;
import org.bouncycastle.openssl.PEMEncryptor;
import org.bouncycastle.openssl.PEMException;

public class JcePEMEncryptorBuilder {
   private final String algorithm;
   private JcaJceHelper helper = new DefaultJcaJceHelper();
   private SecureRandom random;

   public JcePEMEncryptorBuilder(String var1) {
      this.algorithm = var1;
   }

   public JcePEMEncryptorBuilder setProvider(Provider var1) {
      this.helper = new ProviderJcaJceHelper(var1);
      return this;
   }

   public JcePEMEncryptorBuilder setProvider(String var1) {
      this.helper = new NamedJcaJceHelper(var1);
      return this;
   }

   public JcePEMEncryptorBuilder setSecureRandom(SecureRandom var1) {
      this.random = var1;
      return this;
   }

   public PEMEncryptor build(final char[] var1) {
      if (this.random == null) {
         this.random = new SecureRandom();
      }

      int var2 = this.algorithm.startsWith("AES-") ? 16 : 8;
      final byte[] var3 = new byte[var2];
      this.random.nextBytes(var3);
      return new PEMEncryptor() {
         @Override
         public String getAlgorithm() {
            return JcePEMEncryptorBuilder.this.algorithm;
         }

         @Override
         public byte[] getIV() {
            return var3;
         }

         @Override
         public byte[] encrypt(byte[] var1x) throws PEMException {
            return PEMUtilities.crypt(true, JcePEMEncryptorBuilder.this.helper, var1x, var1, JcePEMEncryptorBuilder.this.algorithm, var3);
         }
      };
   }
}
