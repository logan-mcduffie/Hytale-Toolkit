package org.bouncycastle.its.jcajce;

import java.security.PrivateKey;
import java.security.Provider;
import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import org.bouncycastle.its.operator.ETSIDataDecryptor;
import org.bouncycastle.jcajce.spec.IESKEMParameterSpec;
import org.bouncycastle.jcajce.util.JcaJceHelper;
import org.bouncycastle.jcajce.util.NamedJcaJceHelper;
import org.bouncycastle.jcajce.util.ProviderJcaJceHelper;
import org.bouncycastle.util.Arrays;

public class JcaETSIDataDecryptor implements ETSIDataDecryptor {
   private final PrivateKey privateKey;
   private final JcaJceHelper helper;
   private final byte[] recipientHash;
   private SecretKey secretKey = null;

   JcaETSIDataDecryptor(PrivateKey var1, byte[] var2, JcaJceHelper var3) {
      this.privateKey = var1;
      this.helper = var3;
      this.recipientHash = var2;
   }

   @Override
   public byte[] decrypt(byte[] var1, byte[] var2, byte[] var3) {
      try {
         Cipher var4 = this.helper.createCipher("ETSIKEMwithSHA256");
         var4.init(4, this.privateKey, new IESKEMParameterSpec(this.recipientHash));
         this.secretKey = (SecretKey)var4.unwrap(var1, "AES", 3);
         Cipher var5 = this.helper.createCipher("CCM");
         var5.init(2, this.secretKey, ClassUtil.getGCMSpec(var3, 128));
         return var5.doFinal(var2);
      } catch (Exception var6) {
         throw new RuntimeException(var6.getMessage(), var6);
      }
   }

   @Override
   public byte[] getKey() {
      if (this.secretKey == null) {
         throw new IllegalStateException("no secret key recovered");
      } else {
         return this.secretKey.getEncoded();
      }
   }

   public static JcaETSIDataDecryptor.Builder builder(PrivateKey var0, byte[] var1) {
      return new JcaETSIDataDecryptor.Builder(var0, var1);
   }

   public static class Builder {
      private JcaJceHelper provider;
      private final byte[] recipientHash;
      private final PrivateKey key;

      public Builder(PrivateKey var1, byte[] var2) {
         this.key = var1;
         this.recipientHash = Arrays.clone(var2);
      }

      public JcaETSIDataDecryptor.Builder provider(Provider var1) {
         this.provider = new ProviderJcaJceHelper(var1);
         return this;
      }

      public JcaETSIDataDecryptor.Builder provider(String var1) {
         this.provider = new NamedJcaJceHelper(var1);
         return this;
      }

      public JcaETSIDataDecryptor build() {
         return new JcaETSIDataDecryptor(this.key, this.recipientHash, this.provider);
      }
   }
}
