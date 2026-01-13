package org.bouncycastle.jcajce.provider.asymmetric;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.HashMap;
import java.util.Map;
import org.bouncycastle.asn1.bc.BCObjectIdentifiers;
import org.bouncycastle.asn1.bc.ExternalValue;
import org.bouncycastle.asn1.pkcs.PrivateKeyInfo;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import org.bouncycastle.jcajce.ExternalPublicKey;
import org.bouncycastle.jcajce.provider.asymmetric.util.BaseKeyFactorySpi;
import org.bouncycastle.jcajce.provider.config.ConfigurableProvider;
import org.bouncycastle.jcajce.provider.util.AsymmetricAlgorithmProvider;
import org.bouncycastle.jcajce.provider.util.AsymmetricKeyInfoConverter;

public class EXTERNAL {
   private static final String PREFIX = "org.bouncycastle.jcajce.provider.asymmetric.EXTERNAL";
   private static final Map<String, String> externalAttributes = new HashMap<>();
   private static AsymmetricKeyInfoConverter baseConverter;

   static {
      externalAttributes.put("SupportedKeyClasses", "org.bouncycastle.jcajce.ExternalPublicKey");
      externalAttributes.put("SupportedKeyFormats", "X.509");
   }

   private static class ExternalKeyInfoConverter implements AsymmetricKeyInfoConverter {
      private final ConfigurableProvider provider;

      public ExternalKeyInfoConverter(ConfigurableProvider var1) {
         this.provider = var1;
      }

      @Override
      public PrivateKey generatePrivate(PrivateKeyInfo var1) throws IOException {
         throw new UnsupportedOperationException("no support for private key");
      }

      @Override
      public PublicKey generatePublic(SubjectPublicKeyInfo var1) throws IOException {
         ExternalValue var2 = ExternalValue.getInstance(var1.parsePublicKey());
         return new ExternalPublicKey(var2);
      }
   }

   public static class KeyFactory extends BaseKeyFactorySpi {
      @Override
      protected Key engineTranslateKey(Key var1) throws InvalidKeyException {
         try {
            if (var1 instanceof PrivateKey) {
               return this.generatePrivate(PrivateKeyInfo.getInstance(var1.getEncoded()));
            }

            if (var1 instanceof PublicKey) {
               return this.generatePublic(SubjectPublicKeyInfo.getInstance(var1.getEncoded()));
            }
         } catch (IOException var3) {
            throw new InvalidKeyException("key could not be parsed: " + var3.getMessage());
         }

         throw new InvalidKeyException("key not recognized");
      }

      @Override
      public PrivateKey generatePrivate(PrivateKeyInfo var1) throws IOException {
         return EXTERNAL.baseConverter.generatePrivate(var1);
      }

      @Override
      public PublicKey generatePublic(SubjectPublicKeyInfo var1) throws IOException {
         return EXTERNAL.baseConverter.generatePublic(var1);
      }
   }

   public static class Mappings extends AsymmetricAlgorithmProvider {
      @Override
      public void configure(ConfigurableProvider var1) {
         var1.addAlgorithm("KeyFactory.EXTERNAL", "org.bouncycastle.jcajce.provider.asymmetric.EXTERNAL$KeyFactory");
         var1.addAlgorithm("KeyFactory." + BCObjectIdentifiers.external_value, "org.bouncycastle.jcajce.provider.asymmetric.EXTERNAL$KeyFactory");
         var1.addAlgorithm("KeyFactory.OID." + BCObjectIdentifiers.external_value, "org.bouncycastle.jcajce.provider.asymmetric.EXTERNAL$KeyFactory");
         EXTERNAL.baseConverter = new EXTERNAL.ExternalKeyInfoConverter(var1);
         var1.addKeyInfoConverter(BCObjectIdentifiers.external_value, EXTERNAL.baseConverter);
      }
   }
}
