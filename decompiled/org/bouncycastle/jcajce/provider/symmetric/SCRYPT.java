package org.bouncycastle.jcajce.provider.symmetric;

import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import javax.crypto.SecretKey;
import org.bouncycastle.crypto.PasswordConverter;
import org.bouncycastle.crypto.generators.SCrypt;
import org.bouncycastle.crypto.params.KeyParameter;
import org.bouncycastle.internal.asn1.misc.MiscObjectIdentifiers;
import org.bouncycastle.jcajce.provider.config.ConfigurableProvider;
import org.bouncycastle.jcajce.provider.symmetric.util.BCPBEKey;
import org.bouncycastle.jcajce.provider.symmetric.util.BaseSecretKeyFactory;
import org.bouncycastle.jcajce.provider.util.AlgorithmProvider;
import org.bouncycastle.jcajce.spec.ScryptKeySpec;

public class SCRYPT {
   private SCRYPT() {
   }

   public static class BasePBKDF2 extends BaseSecretKeyFactory {
      private int scheme;

      public BasePBKDF2(String var1, int var2) {
         super(var1, MiscObjectIdentifiers.id_scrypt);
         this.scheme = var2;
      }

      @Override
      protected SecretKey engineGenerateSecret(KeySpec var1) throws InvalidKeySpecException {
         if (var1 instanceof ScryptKeySpec) {
            ScryptKeySpec var2 = (ScryptKeySpec)var1;
            if (var2.getSalt() == null) {
               throw new IllegalArgumentException("Salt S must be provided.");
            } else if (var2.getCostParameter() <= 1) {
               throw new IllegalArgumentException("Cost parameter N must be > 1.");
            } else if (var2.getKeyLength() <= 0) {
               throw new InvalidKeySpecException("positive key length required: " + var2.getKeyLength());
            } else if (var2.getPassword().length == 0) {
               throw new IllegalArgumentException("password empty");
            } else {
               KeyParameter var3 = new KeyParameter(
                  SCrypt.generate(
                     PasswordConverter.UTF8.convert(var2.getPassword()),
                     var2.getSalt(),
                     var2.getCostParameter(),
                     var2.getBlockSize(),
                     var2.getParallelizationParameter(),
                     var2.getKeyLength() / 8
                  )
               );
               return new BCPBEKey(this.algName, var3);
            }
         } else {
            throw new InvalidKeySpecException("Invalid KeySpec");
         }
      }
   }

   public static class Mappings extends AlgorithmProvider {
      private static final String PREFIX = SCRYPT.class.getName();

      @Override
      public void configure(ConfigurableProvider var1) {
         var1.addAlgorithm("SecretKeyFactory.SCRYPT", PREFIX + "$ScryptWithUTF8");
         var1.addAlgorithm("SecretKeyFactory", MiscObjectIdentifiers.id_scrypt, PREFIX + "$ScryptWithUTF8");
      }
   }

   public static class ScryptWithUTF8 extends SCRYPT.BasePBKDF2 {
      public ScryptWithUTF8() {
         super("SCRYPT", 5);
      }
   }
}
