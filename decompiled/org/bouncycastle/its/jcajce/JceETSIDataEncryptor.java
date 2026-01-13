package org.bouncycastle.its.jcajce;

import java.security.Provider;
import java.security.SecureRandom;
import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import org.bouncycastle.its.operator.ETSIDataEncryptor;
import org.bouncycastle.jcajce.util.DefaultJcaJceHelper;
import org.bouncycastle.jcajce.util.JcaJceHelper;
import org.bouncycastle.jcajce.util.NamedJcaJceHelper;
import org.bouncycastle.jcajce.util.ProviderJcaJceHelper;

public class JceETSIDataEncryptor implements ETSIDataEncryptor {
   private final SecureRandom random;
   private final JcaJceHelper helper;
   private byte[] nonce;
   private byte[] key;

   private JceETSIDataEncryptor(SecureRandom var1, JcaJceHelper var2) {
      this.random = var1;
      this.helper = var2;
   }

   @Override
   public byte[] encrypt(byte[] var1) {
      this.key = new byte[16];
      this.random.nextBytes(this.key);
      this.nonce = new byte[12];
      this.random.nextBytes(this.nonce);

      try {
         SecretKeySpec var2 = new SecretKeySpec(this.key, "AES");
         Cipher var3 = this.helper.createCipher("CCM");
         var3.init(1, var2, ClassUtil.getGCMSpec(this.nonce, 128));
         return var3.doFinal(var1);
      } catch (Exception var4) {
         throw new RuntimeException(var4.getMessage(), var4);
      }
   }

   @Override
   public byte[] getKey() {
      return this.key;
   }

   @Override
   public byte[] getNonce() {
      return this.nonce;
   }

   public static class Builder {
      private SecureRandom random;
      private JcaJceHelper helper = new DefaultJcaJceHelper();

      public JceETSIDataEncryptor.Builder setRandom(SecureRandom var1) {
         this.random = var1;
         return this;
      }

      public JceETSIDataEncryptor.Builder setProvider(Provider var1) {
         this.helper = new ProviderJcaJceHelper(var1);
         return this;
      }

      public JceETSIDataEncryptor.Builder setProvider(String var1) {
         this.helper = new NamedJcaJceHelper(var1);
         return this;
      }

      public JceETSIDataEncryptor build() {
         if (this.random == null) {
            this.random = new SecureRandom();
         }

         return new JceETSIDataEncryptor(this.random, this.helper);
      }
   }
}
