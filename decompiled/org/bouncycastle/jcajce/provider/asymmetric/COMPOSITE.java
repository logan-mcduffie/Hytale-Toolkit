package org.bouncycastle.jcajce.provider.asymmetric;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.HashMap;
import java.util.Map;
import org.bouncycastle.asn1.pkcs.PrivateKeyInfo;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import org.bouncycastle.internal.asn1.misc.MiscObjectIdentifiers;
import org.bouncycastle.jcajce.provider.asymmetric.compositesignatures.KeyFactorySpi;
import org.bouncycastle.jcajce.provider.asymmetric.util.BaseKeyFactorySpi;
import org.bouncycastle.jcajce.provider.config.ConfigurableProvider;
import org.bouncycastle.jcajce.provider.util.AsymmetricAlgorithmProvider;
import org.bouncycastle.jcajce.provider.util.AsymmetricKeyInfoConverter;
import org.bouncycastle.jcajce.util.ProviderJcaJceHelper;
import org.bouncycastle.jce.provider.BouncyCastleProvider;

public class COMPOSITE {
   private static final String PREFIX = "org.bouncycastle.jcajce.provider.asymmetric.COMPOSITE";
   private static final Map<String, String> compositeAttributes = new HashMap<>();
   private static AsymmetricKeyInfoConverter baseConverter;

   static {
      compositeAttributes.put("SupportedKeyClasses", "org.bouncycastle.jcajce.CompositePublicKey|org.bouncycastle.jcajce.CompositePrivateKey");
      compositeAttributes.put("SupportedKeyFormats", "PKCS#8|X.509");
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
         return COMPOSITE.baseConverter.generatePrivate(var1);
      }

      @Override
      public PublicKey generatePublic(SubjectPublicKeyInfo var1) throws IOException {
         return COMPOSITE.baseConverter.generatePublic(var1);
      }
   }

   public static class Mappings extends AsymmetricAlgorithmProvider {
      @Override
      public void configure(ConfigurableProvider var1) {
         var1.addAlgorithm("KeyFactory.COMPOSITE", "org.bouncycastle.jcajce.provider.asymmetric.COMPOSITE$KeyFactory");
         var1.addAlgorithm("KeyFactory." + MiscObjectIdentifiers.id_alg_composite, "org.bouncycastle.jcajce.provider.asymmetric.COMPOSITE$KeyFactory");
         var1.addAlgorithm("KeyFactory.OID." + MiscObjectIdentifiers.id_alg_composite, "org.bouncycastle.jcajce.provider.asymmetric.COMPOSITE$KeyFactory");
         var1.addAlgorithm("KeyFactory." + MiscObjectIdentifiers.id_composite_key, "org.bouncycastle.jcajce.provider.asymmetric.COMPOSITE$KeyFactory");
         var1.addAlgorithm("KeyFactory.OID." + MiscObjectIdentifiers.id_composite_key, "org.bouncycastle.jcajce.provider.asymmetric.COMPOSITE$KeyFactory");
         COMPOSITE.baseConverter = new KeyFactorySpi(new ProviderJcaJceHelper((BouncyCastleProvider)var1));
         var1.addKeyInfoConverter(MiscObjectIdentifiers.id_alg_composite, COMPOSITE.baseConverter);
         var1.addKeyInfoConverter(MiscObjectIdentifiers.id_composite_key, COMPOSITE.baseConverter);
      }
   }
}
