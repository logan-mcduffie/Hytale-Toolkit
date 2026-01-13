package org.bouncycastle.jcajce.provider.asymmetric.dh;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import javax.crypto.interfaces.DHPrivateKey;
import javax.crypto.interfaces.DHPublicKey;
import javax.crypto.spec.DHPrivateKeySpec;
import javax.crypto.spec.DHPublicKeySpec;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.pkcs.PKCSObjectIdentifiers;
import org.bouncycastle.asn1.pkcs.PrivateKeyInfo;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import org.bouncycastle.asn1.x9.X9ObjectIdentifiers;
import org.bouncycastle.jcajce.provider.asymmetric.util.BaseKeyFactorySpi;
import org.bouncycastle.jcajce.provider.asymmetric.util.ExtendedInvalidKeySpecException;

public class KeyFactorySpi extends BaseKeyFactorySpi {
   @Override
   protected KeySpec engineGetKeySpec(Key var1, Class var2) throws InvalidKeySpecException {
      if (var2.isAssignableFrom(DHPrivateKeySpec.class) && var1 instanceof DHPrivateKey) {
         DHPrivateKey var4 = (DHPrivateKey)var1;
         return new DHPrivateKeySpec(var4.getX(), var4.getParams().getP(), var4.getParams().getG());
      } else if (var2.isAssignableFrom(DHPublicKeySpec.class) && var1 instanceof DHPublicKey) {
         DHPublicKey var3 = (DHPublicKey)var1;
         return new DHPublicKeySpec(var3.getY(), var3.getParams().getP(), var3.getParams().getG());
      } else {
         return super.engineGetKeySpec(var1, var2);
      }
   }

   @Override
   protected Key engineTranslateKey(Key var1) throws InvalidKeyException {
      if (var1 instanceof DHPublicKey) {
         return new BCDHPublicKey((DHPublicKey)var1);
      } else if (var1 instanceof DHPrivateKey) {
         return new BCDHPrivateKey((DHPrivateKey)var1);
      } else {
         throw new InvalidKeyException("key type unknown");
      }
   }

   @Override
   protected PrivateKey engineGeneratePrivate(KeySpec var1) throws InvalidKeySpecException {
      return (PrivateKey)(var1 instanceof DHPrivateKeySpec ? new BCDHPrivateKey((DHPrivateKeySpec)var1) : super.engineGeneratePrivate(var1));
   }

   @Override
   protected PublicKey engineGeneratePublic(KeySpec var1) throws InvalidKeySpecException {
      if (var1 instanceof DHPublicKeySpec) {
         try {
            return new BCDHPublicKey((DHPublicKeySpec)var1);
         } catch (IllegalArgumentException var3) {
            throw new ExtendedInvalidKeySpecException(var3.getMessage(), var3);
         }
      } else {
         return super.engineGeneratePublic(var1);
      }
   }

   @Override
   public PrivateKey generatePrivate(PrivateKeyInfo var1) throws IOException {
      ASN1ObjectIdentifier var2 = var1.getPrivateKeyAlgorithm().getAlgorithm();
      if (var2.equals(PKCSObjectIdentifiers.dhKeyAgreement)) {
         return new BCDHPrivateKey(var1);
      } else if (var2.equals(X9ObjectIdentifiers.dhpublicnumber)) {
         return new BCDHPrivateKey(var1);
      } else {
         throw new IOException("algorithm identifier " + var2 + " in key not recognised");
      }
   }

   @Override
   public PublicKey generatePublic(SubjectPublicKeyInfo var1) throws IOException {
      ASN1ObjectIdentifier var2 = var1.getAlgorithm().getAlgorithm();
      if (var2.equals(PKCSObjectIdentifiers.dhKeyAgreement)) {
         return new BCDHPublicKey(var1);
      } else if (var2.equals(X9ObjectIdentifiers.dhpublicnumber)) {
         return new BCDHPublicKey(var1);
      } else {
         throw new IOException("algorithm identifier " + var2 + " in key not recognised");
      }
   }
}
