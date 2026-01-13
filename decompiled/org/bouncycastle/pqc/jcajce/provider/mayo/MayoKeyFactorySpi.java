package org.bouncycastle.pqc.jcajce.provider.mayo;

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

public class MayoKeyFactorySpi extends BaseKeyFactorySpi {
   private static final Set<ASN1ObjectIdentifier> keyOids = new HashSet<>();

   public MayoKeyFactorySpi() {
      super(keyOids);
   }

   public MayoKeyFactorySpi(ASN1ObjectIdentifier var1) {
      super(var1);
   }

   @Override
   public final KeySpec engineGetKeySpec(Key var1, Class var2) throws InvalidKeySpecException {
      if (var1 instanceof BCMayoPrivateKey) {
         if (PKCS8EncodedKeySpec.class.isAssignableFrom(var2)) {
            return new PKCS8EncodedKeySpec(var1.getEncoded());
         }
      } else {
         if (!(var1 instanceof BCMayoPublicKey)) {
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
      if (!(var1 instanceof BCMayoPrivateKey) && !(var1 instanceof BCMayoPublicKey)) {
         throw new InvalidKeyException("Unsupported key type");
      } else {
         return var1;
      }
   }

   @Override
   public PrivateKey generatePrivate(PrivateKeyInfo var1) throws IOException {
      return new BCMayoPrivateKey(var1);
   }

   @Override
   public PublicKey generatePublic(SubjectPublicKeyInfo var1) throws IOException {
      return new BCMayoPublicKey(var1);
   }

   static {
      keyOids.add(BCObjectIdentifiers.mayo1);
      keyOids.add(BCObjectIdentifiers.mayo2);
      keyOids.add(BCObjectIdentifiers.mayo3);
      keyOids.add(BCObjectIdentifiers.mayo5);
   }

   public static class Mayo1 extends MayoKeyFactorySpi {
      public Mayo1() {
         super(BCObjectIdentifiers.mayo1);
      }
   }

   public static class Mayo2 extends MayoKeyFactorySpi {
      public Mayo2() {
         super(BCObjectIdentifiers.mayo2);
      }
   }

   public static class Mayo3 extends MayoKeyFactorySpi {
      public Mayo3() {
         super(BCObjectIdentifiers.mayo3);
      }
   }

   public static class Mayo5 extends MayoKeyFactorySpi {
      public Mayo5() {
         super(BCObjectIdentifiers.mayo5);
      }
   }
}
