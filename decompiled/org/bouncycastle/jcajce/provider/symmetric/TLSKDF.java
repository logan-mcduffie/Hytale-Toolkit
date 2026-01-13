package org.bouncycastle.jcajce.provider.symmetric;

import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import org.bouncycastle.crypto.Mac;
import org.bouncycastle.crypto.digests.SHA256Digest;
import org.bouncycastle.crypto.digests.SHA384Digest;
import org.bouncycastle.crypto.digests.SHA512Digest;
import org.bouncycastle.crypto.macs.HMac;
import org.bouncycastle.crypto.params.KeyParameter;
import org.bouncycastle.crypto.util.DigestFactory;
import org.bouncycastle.jcajce.provider.config.ConfigurableProvider;
import org.bouncycastle.jcajce.provider.symmetric.util.BaseSecretKeyFactory;
import org.bouncycastle.jcajce.provider.util.AlgorithmProvider;
import org.bouncycastle.jcajce.spec.TLSKeyMaterialSpec;
import org.bouncycastle.util.Arrays;
import org.bouncycastle.util.Strings;

public class TLSKDF {
   private static byte[] PRF_legacy(TLSKeyMaterialSpec var0) {
      HMac var1 = new HMac(DigestFactory.createMD5());
      HMac var2 = new HMac(DigestFactory.createSHA1());
      byte[] var3 = Strings.toByteArray(var0.getLabel());
      byte[] var4 = Arrays.concatenate(var3, var0.getSeed());
      byte[] var5 = var0.getSecret();
      int var6 = (var5.length + 1) / 2;
      byte[] var7 = new byte[var6];
      byte[] var8 = new byte[var6];
      System.arraycopy(var5, 0, var7, 0, var6);
      System.arraycopy(var5, var5.length - var6, var8, 0, var6);
      int var9 = var0.getLength();
      byte[] var10 = new byte[var9];
      byte[] var11 = new byte[var9];
      hmac_hash(var1, var7, var4, var10);
      hmac_hash(var2, var8, var4, var11);

      for (int var12 = 0; var12 < var9; var12++) {
         var10[var12] ^= var11[var12];
      }

      return var10;
   }

   private static void hmac_hash(Mac var0, byte[] var1, byte[] var2, byte[] var3) {
      var0.init(new KeyParameter(var1));
      byte[] var4 = var2;
      int var5 = var0.getMacSize();
      int var6 = (var3.length + var5 - 1) / var5;
      byte[] var7 = new byte[var0.getMacSize()];
      byte[] var8 = new byte[var0.getMacSize()];

      for (int var9 = 0; var9 < var6; var9++) {
         var0.update(var4, 0, var4.length);
         var0.doFinal(var7, 0);
         var4 = var7;
         var0.update(var7, 0, var7.length);
         var0.update(var2, 0, var2.length);
         var0.doFinal(var8, 0);
         System.arraycopy(var8, 0, var3, var5 * var9, Math.min(var5, var3.length - var5 * var9));
      }
   }

   public static class Mappings extends AlgorithmProvider {
      private static final String PREFIX = TLSKDF.class.getName();

      @Override
      public void configure(ConfigurableProvider var1) {
         var1.addAlgorithm("SecretKeyFactory.TLS10KDF", PREFIX + "$TLS10");
         var1.addAlgorithm("SecretKeyFactory.TLS11KDF", PREFIX + "$TLS11");
         var1.addAlgorithm("SecretKeyFactory.TLS12WITHSHA256KDF", PREFIX + "$TLS12withSHA256");
         var1.addAlgorithm("SecretKeyFactory.TLS12WITHSHA384KDF", PREFIX + "$TLS12withSHA384");
         var1.addAlgorithm("SecretKeyFactory.TLS12WITHSHA512KDF", PREFIX + "$TLS12withSHA512");
      }
   }

   public static final class TLS10 extends TLSKDF.TLSKeyMaterialFactory {
      public TLS10() {
         super("TLS10KDF");
      }

      @Override
      protected SecretKey engineGenerateSecret(KeySpec var1) throws InvalidKeySpecException {
         if (var1 instanceof TLSKeyMaterialSpec) {
            return new SecretKeySpec(TLSKDF.PRF_legacy((TLSKeyMaterialSpec)var1), this.algName);
         } else {
            throw new InvalidKeySpecException("Invalid KeySpec");
         }
      }
   }

   public static final class TLS11 extends TLSKDF.TLSKeyMaterialFactory {
      public TLS11() {
         super("TLS11KDF");
      }

      @Override
      protected SecretKey engineGenerateSecret(KeySpec var1) throws InvalidKeySpecException {
         if (var1 instanceof TLSKeyMaterialSpec) {
            return new SecretKeySpec(TLSKDF.PRF_legacy((TLSKeyMaterialSpec)var1), this.algName);
         } else {
            throw new InvalidKeySpecException("Invalid KeySpec");
         }
      }
   }

   public static class TLS12 extends TLSKDF.TLSKeyMaterialFactory {
      private final Mac prf;

      protected TLS12(String var1, Mac var2) {
         super(var1);
         this.prf = var2;
      }

      @Override
      protected SecretKey engineGenerateSecret(KeySpec var1) throws InvalidKeySpecException {
         if (var1 instanceof TLSKeyMaterialSpec) {
            return new SecretKeySpec(this.PRF((TLSKeyMaterialSpec)var1, this.prf), this.algName);
         } else {
            throw new InvalidKeySpecException("Invalid KeySpec");
         }
      }

      private byte[] PRF(TLSKeyMaterialSpec var1, Mac var2) {
         byte[] var3 = Strings.toByteArray(var1.getLabel());
         byte[] var4 = Arrays.concatenate(var3, var1.getSeed());
         byte[] var5 = var1.getSecret();
         byte[] var6 = new byte[var1.getLength()];
         TLSKDF.hmac_hash(var2, var5, var4, var6);
         return var6;
      }
   }

   public static final class TLS12withSHA256 extends TLSKDF.TLS12 {
      public TLS12withSHA256() {
         super("TLS12withSHA256KDF", new HMac(SHA256Digest.newInstance()));
      }
   }

   public static final class TLS12withSHA384 extends TLSKDF.TLS12 {
      public TLS12withSHA384() {
         super("TLS12withSHA384KDF", new HMac(new SHA384Digest()));
      }
   }

   public static final class TLS12withSHA512 extends TLSKDF.TLS12 {
      public TLS12withSHA512() {
         super("TLS12withSHA512KDF", new HMac(new SHA512Digest()));
      }
   }

   public static class TLSKeyMaterialFactory extends BaseSecretKeyFactory {
      protected TLSKeyMaterialFactory(String var1) {
         super(var1, null);
      }
   }
}
