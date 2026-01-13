package org.bouncycastle.jcajce.provider.symmetric;

import java.security.InvalidAlgorithmParameterException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import javax.crypto.SecretKey;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.pkcs.PKCSObjectIdentifiers;
import org.bouncycastle.crypto.Digest;
import org.bouncycastle.crypto.digests.SHA256Digest;
import org.bouncycastle.crypto.digests.SHA384Digest;
import org.bouncycastle.crypto.digests.SHA512Digest;
import org.bouncycastle.crypto.generators.HKDFBytesGenerator;
import org.bouncycastle.crypto.params.HKDFParameters;
import org.bouncycastle.crypto.params.KeyParameter;
import org.bouncycastle.jcajce.provider.config.ConfigurableProvider;
import org.bouncycastle.jcajce.provider.symmetric.util.BCPBEKey;
import org.bouncycastle.jcajce.provider.symmetric.util.BaseSecretKeyFactory;
import org.bouncycastle.jcajce.provider.util.AlgorithmProvider;
import org.bouncycastle.jcajce.spec.HKDFParameterSpec;

public class HKDF {
   private HKDF() {
   }

   public static class HKDFBase extends BaseSecretKeyFactory {
      protected String algName;
      protected HKDFBytesGenerator hkdf;

      public HKDFBase(String var1, Digest var2, ASN1ObjectIdentifier var3) {
         super(var1, var3);
         this.algName = var1;
         this.hkdf = new HKDFBytesGenerator(var2);
      }

      @Override
      protected SecretKey engineGenerateSecret(KeySpec var1) throws InvalidKeySpecException {
         if (!(var1 instanceof HKDFParameterSpec)) {
            throw new InvalidKeySpecException("invalid KeySpec: expected HKDFParameterSpec, but got " + var1.getClass().getName());
         } else {
            HKDFParameterSpec var2 = (HKDFParameterSpec)var1;
            int var3 = var2.getOutputLength();
            this.hkdf.init(new HKDFParameters(var2.getIKM(), var2.getSalt(), var2.getInfo()));
            byte[] var4 = new byte[var3];
            this.hkdf.generateBytes(var4, 0, var3);
            KeyParameter var5 = new KeyParameter(var4);
            return new BCPBEKey(this.algName, var5);
         }
      }
   }

   public static class HKDFwithSHA256 extends HKDF.HKDFBase {
      public HKDFwithSHA256() throws InvalidAlgorithmParameterException {
         super("HKDF-SHA256", new SHA256Digest(), PKCSObjectIdentifiers.id_alg_hkdf_with_sha256);
      }
   }

   public static class HKDFwithSHA384 extends HKDF.HKDFBase {
      public HKDFwithSHA384() throws InvalidAlgorithmParameterException {
         super("HKDF-SHA384", new SHA384Digest(), PKCSObjectIdentifiers.id_alg_hkdf_with_sha384);
      }
   }

   public static class HKDFwithSHA512 extends HKDF.HKDFBase {
      public HKDFwithSHA512() throws InvalidAlgorithmParameterException {
         super("HKDF-SHA512", new SHA512Digest(), PKCSObjectIdentifiers.id_alg_hkdf_with_sha512);
      }
   }

   public static class Mappings extends AlgorithmProvider {
      private static final String PREFIX = HKDF.class.getName();

      @Override
      public void configure(ConfigurableProvider var1) {
         var1.addAlgorithm("SecretKeyFactory.HKDF-SHA256", PREFIX + "$HKDFwithSHA256");
         var1.addAlgorithm("SecretKeyFactory.HKDF-SHA384", PREFIX + "$HKDFwithSHA384");
         var1.addAlgorithm("SecretKeyFactory.HKDF-SHA512", PREFIX + "$HKDFwithSHA512");
      }
   }
}
