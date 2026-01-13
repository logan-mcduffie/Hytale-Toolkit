package org.bouncycastle.jcajce.provider.asymmetric.gost;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.cryptopro.CryptoProObjectIdentifiers;
import org.bouncycastle.asn1.pkcs.PrivateKeyInfo;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import org.bouncycastle.jcajce.provider.asymmetric.util.BaseKeyFactorySpi;
import org.bouncycastle.jce.interfaces.GOST3410PrivateKey;
import org.bouncycastle.jce.interfaces.GOST3410PublicKey;
import org.bouncycastle.jce.spec.GOST3410PrivateKeySpec;
import org.bouncycastle.jce.spec.GOST3410PublicKeyParameterSetSpec;
import org.bouncycastle.jce.spec.GOST3410PublicKeySpec;

public class KeyFactorySpi extends BaseKeyFactorySpi {
   @Override
   protected KeySpec engineGetKeySpec(Key var1, Class var2) throws InvalidKeySpecException {
      if (var2.isAssignableFrom(GOST3410PublicKeySpec.class) && var1 instanceof GOST3410PublicKey) {
         GOST3410PublicKey var5 = (GOST3410PublicKey)var1;
         GOST3410PublicKeyParameterSetSpec var6 = var5.getParameters().getPublicKeyParameters();
         return new GOST3410PublicKeySpec(var5.getY(), var6.getP(), var6.getQ(), var6.getA());
      } else if (var2.isAssignableFrom(GOST3410PrivateKeySpec.class) && var1 instanceof GOST3410PrivateKey) {
         GOST3410PrivateKey var3 = (GOST3410PrivateKey)var1;
         GOST3410PublicKeyParameterSetSpec var4 = var3.getParameters().getPublicKeyParameters();
         return new GOST3410PrivateKeySpec(var3.getX(), var4.getP(), var4.getQ(), var4.getA());
      } else {
         return super.engineGetKeySpec(var1, var2);
      }
   }

   @Override
   protected Key engineTranslateKey(Key var1) throws InvalidKeyException {
      if (var1 instanceof GOST3410PublicKey) {
         return new BCGOST3410PublicKey((GOST3410PublicKey)var1);
      } else if (var1 instanceof GOST3410PrivateKey) {
         return new BCGOST3410PrivateKey((GOST3410PrivateKey)var1);
      } else {
         throw new InvalidKeyException("key type unknown");
      }
   }

   @Override
   protected PrivateKey engineGeneratePrivate(KeySpec var1) throws InvalidKeySpecException {
      return (PrivateKey)(var1 instanceof GOST3410PrivateKeySpec ? new BCGOST3410PrivateKey((GOST3410PrivateKeySpec)var1) : super.engineGeneratePrivate(var1));
   }

   @Override
   protected PublicKey engineGeneratePublic(KeySpec var1) throws InvalidKeySpecException {
      return (PublicKey)(var1 instanceof GOST3410PublicKeySpec ? new BCGOST3410PublicKey((GOST3410PublicKeySpec)var1) : super.engineGeneratePublic(var1));
   }

   @Override
   public PrivateKey generatePrivate(PrivateKeyInfo var1) throws IOException {
      ASN1ObjectIdentifier var2 = var1.getPrivateKeyAlgorithm().getAlgorithm();
      if (var2.equals(CryptoProObjectIdentifiers.gostR3410_94)) {
         return new BCGOST3410PrivateKey(var1);
      } else {
         throw new IOException("algorithm identifier " + var2 + " in key not recognised");
      }
   }

   @Override
   public PublicKey generatePublic(SubjectPublicKeyInfo var1) throws IOException {
      ASN1ObjectIdentifier var2 = var1.getAlgorithm().getAlgorithm();
      if (var2.equals(CryptoProObjectIdentifiers.gostR3410_94)) {
         return new BCGOST3410PublicKey(var1);
      } else {
         throw new IOException("algorithm identifier " + var2 + " in key not recognised");
      }
   }
}
