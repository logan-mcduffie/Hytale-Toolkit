package org.bouncycastle.pqc.jcajce.provider.dilithium;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.HashSet;
import java.util.Set;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.bc.BCObjectIdentifiers;
import org.bouncycastle.asn1.pkcs.PrivateKeyInfo;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import org.bouncycastle.pqc.jcajce.provider.util.BaseKeyFactorySpi;

public class DilithiumKeyFactorySpi extends BaseKeyFactorySpi {
   private static final Set<ASN1ObjectIdentifier> keyOids = new HashSet<>();

   public DilithiumKeyFactorySpi() {
      super(keyOids);
   }

   public DilithiumKeyFactorySpi(ASN1ObjectIdentifier var1) {
      super(var1);
   }

   @Override
   public final KeySpec engineGetKeySpec(Key var1, Class var2) throws InvalidKeySpecException {
      if (var1 instanceof BCDilithiumPrivateKey) {
         if (PKCS8EncodedKeySpec.class.isAssignableFrom(var2)) {
            return new PKCS8EncodedKeySpec(var1.getEncoded());
         }
      } else {
         if (!(var1 instanceof BCDilithiumPublicKey)) {
            throw new InvalidKeySpecException("Unsupported key type: " + var1.getClass() + ".");
         }

         if (X509EncodedKeySpec.class.isAssignableFrom(var2)) {
            return new X509EncodedKeySpec(var1.getEncoded());
         }
      }

      throw new InvalidKeySpecException("Unknown key specification: " + var2 + ".");
   }

   @Override
   public final Key engineTranslateKey(Key var1) throws InvalidKeyException {
      if (!(var1 instanceof BCDilithiumPrivateKey) && !(var1 instanceof BCDilithiumPublicKey)) {
         throw new InvalidKeyException("Unsupported key type");
      } else {
         return var1;
      }
   }

   @Override
   public PrivateKey generatePrivate(PrivateKeyInfo var1) throws IOException {
      return new BCDilithiumPrivateKey(var1);
   }

   @Override
   public PublicKey generatePublic(SubjectPublicKeyInfo var1) throws IOException {
      return new BCDilithiumPublicKey(var1);
   }

   static {
      keyOids.add(BCObjectIdentifiers.dilithium2);
      keyOids.add(BCObjectIdentifiers.dilithium3);
      keyOids.add(BCObjectIdentifiers.dilithium5);
      keyOids.add(BCObjectIdentifiers.dilithium2_aes);
      keyOids.add(BCObjectIdentifiers.dilithium3_aes);
      keyOids.add(BCObjectIdentifiers.dilithium5_aes);
   }

   public static class Base2 extends DilithiumKeyFactorySpi {
      public Base2() {
         super(BCObjectIdentifiers.dilithium2);
      }
   }

   public static class Base2_AES extends DilithiumKeyFactorySpi {
      public Base2_AES() {
         super(BCObjectIdentifiers.dilithium2_aes);
      }
   }

   public static class Base3 extends DilithiumKeyFactorySpi {
      public Base3() {
         super(BCObjectIdentifiers.dilithium3);
      }
   }

   public static class Base3_AES extends DilithiumKeyFactorySpi {
      public Base3_AES() {
         super(BCObjectIdentifiers.dilithium3_aes);
      }
   }

   public static class Base5 extends DilithiumKeyFactorySpi {
      public Base5() {
         super(BCObjectIdentifiers.dilithium5);
      }
   }

   public static class Base5_AES extends DilithiumKeyFactorySpi {
      public Base5_AES() {
         super(BCObjectIdentifiers.dilithium5_aes);
      }
   }
}
