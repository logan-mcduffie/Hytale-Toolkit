package org.bouncycastle.jcajce.provider.asymmetric.elgamal;

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
import org.bouncycastle.internal.asn1.oiw.OIWObjectIdentifiers;
import org.bouncycastle.jcajce.provider.asymmetric.util.BaseKeyFactorySpi;
import org.bouncycastle.jce.interfaces.ElGamalPrivateKey;
import org.bouncycastle.jce.interfaces.ElGamalPublicKey;
import org.bouncycastle.jce.spec.ElGamalPrivateKeySpec;
import org.bouncycastle.jce.spec.ElGamalPublicKeySpec;

public class KeyFactorySpi extends BaseKeyFactorySpi {
   @Override
   protected PrivateKey engineGeneratePrivate(KeySpec var1) throws InvalidKeySpecException {
      if (var1 instanceof ElGamalPrivateKeySpec) {
         return new BCElGamalPrivateKey((ElGamalPrivateKeySpec)var1);
      } else {
         return (PrivateKey)(var1 instanceof DHPrivateKeySpec ? new BCElGamalPrivateKey((DHPrivateKeySpec)var1) : super.engineGeneratePrivate(var1));
      }
   }

   @Override
   protected PublicKey engineGeneratePublic(KeySpec var1) throws InvalidKeySpecException {
      if (var1 instanceof ElGamalPublicKeySpec) {
         return new BCElGamalPublicKey((ElGamalPublicKeySpec)var1);
      } else {
         return (PublicKey)(var1 instanceof DHPublicKeySpec ? new BCElGamalPublicKey((DHPublicKeySpec)var1) : super.engineGeneratePublic(var1));
      }
   }

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
         return new BCElGamalPublicKey((DHPublicKey)var1);
      } else if (var1 instanceof DHPrivateKey) {
         return new BCElGamalPrivateKey((DHPrivateKey)var1);
      } else if (var1 instanceof ElGamalPublicKey) {
         return new BCElGamalPublicKey((ElGamalPublicKey)var1);
      } else if (var1 instanceof ElGamalPrivateKey) {
         return new BCElGamalPrivateKey((ElGamalPrivateKey)var1);
      } else {
         throw new InvalidKeyException("key type unknown");
      }
   }

   @Override
   public PrivateKey generatePrivate(PrivateKeyInfo var1) throws IOException {
      ASN1ObjectIdentifier var2 = var1.getPrivateKeyAlgorithm().getAlgorithm();
      if (var2.equals(PKCSObjectIdentifiers.dhKeyAgreement)) {
         return new BCElGamalPrivateKey(var1);
      } else if (var2.equals(X9ObjectIdentifiers.dhpublicnumber)) {
         return new BCElGamalPrivateKey(var1);
      } else if (var2.equals(OIWObjectIdentifiers.elGamalAlgorithm)) {
         return new BCElGamalPrivateKey(var1);
      } else {
         throw new IOException("algorithm identifier " + var2 + " in key not recognised");
      }
   }

   @Override
   public PublicKey generatePublic(SubjectPublicKeyInfo var1) throws IOException {
      ASN1ObjectIdentifier var2 = var1.getAlgorithm().getAlgorithm();
      if (var2.equals(PKCSObjectIdentifiers.dhKeyAgreement)) {
         return new BCElGamalPublicKey(var1);
      } else if (var2.equals(X9ObjectIdentifiers.dhpublicnumber)) {
         return new BCElGamalPublicKey(var1);
      } else if (var2.equals(OIWObjectIdentifiers.elGamalAlgorithm)) {
         return new BCElGamalPublicKey(var1);
      } else {
         throw new IOException("algorithm identifier " + var2 + " in key not recognised");
      }
   }
}
