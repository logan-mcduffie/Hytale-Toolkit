package org.bouncycastle.jcajce.provider.asymmetric.rsa;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.interfaces.RSAPrivateCrtKey;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.RSAPrivateCrtKeySpec;
import java.security.spec.RSAPrivateKeySpec;
import java.security.spec.RSAPublicKeySpec;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.pkcs.PrivateKeyInfo;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import org.bouncycastle.crypto.params.AsymmetricKeyParameter;
import org.bouncycastle.crypto.params.RSAKeyParameters;
import org.bouncycastle.crypto.params.RSAPrivateCrtKeyParameters;
import org.bouncycastle.crypto.util.OpenSSHPrivateKeyUtil;
import org.bouncycastle.crypto.util.OpenSSHPublicKeyUtil;
import org.bouncycastle.jcajce.provider.asymmetric.util.BaseKeyFactorySpi;
import org.bouncycastle.jcajce.provider.asymmetric.util.ExtendedInvalidKeySpecException;
import org.bouncycastle.jcajce.spec.OpenSSHPrivateKeySpec;
import org.bouncycastle.jcajce.spec.OpenSSHPublicKeySpec;

public class KeyFactorySpi extends BaseKeyFactorySpi {
   @Override
   protected KeySpec engineGetKeySpec(Key var1, Class var2) throws InvalidKeySpecException {
      if ((var2.isAssignableFrom(KeySpec.class) || var2.isAssignableFrom(RSAPublicKeySpec.class)) && var1 instanceof RSAPublicKey) {
         RSAPublicKey var7 = (RSAPublicKey)var1;
         return new RSAPublicKeySpec(var7.getModulus(), var7.getPublicExponent());
      } else if ((var2.isAssignableFrom(KeySpec.class) || var2.isAssignableFrom(RSAPrivateCrtKeySpec.class)) && var1 instanceof RSAPrivateCrtKey) {
         RSAPrivateCrtKey var6 = (RSAPrivateCrtKey)var1;
         return new RSAPrivateCrtKeySpec(
            var6.getModulus(),
            var6.getPublicExponent(),
            var6.getPrivateExponent(),
            var6.getPrimeP(),
            var6.getPrimeQ(),
            var6.getPrimeExponentP(),
            var6.getPrimeExponentQ(),
            var6.getCrtCoefficient()
         );
      } else if ((var2.isAssignableFrom(KeySpec.class) || var2.isAssignableFrom(RSAPrivateKeySpec.class)) && var1 instanceof RSAPrivateKey) {
         RSAPrivateKey var3 = (RSAPrivateKey)var1;
         return new RSAPrivateKeySpec(var3.getModulus(), var3.getPrivateExponent());
      } else if (var2.isAssignableFrom(OpenSSHPublicKeySpec.class) && var1 instanceof RSAPublicKey) {
         try {
            return new OpenSSHPublicKeySpec(
               OpenSSHPublicKeyUtil.encodePublicKey(new RSAKeyParameters(false, ((RSAPublicKey)var1).getModulus(), ((RSAPublicKey)var1).getPublicExponent()))
            );
         } catch (IOException var4) {
            throw new IllegalArgumentException("unable to produce encoding: " + var4.getMessage());
         }
      } else if (var2.isAssignableFrom(OpenSSHPrivateKeySpec.class) && var1 instanceof RSAPrivateCrtKey) {
         try {
            return new OpenSSHPrivateKeySpec(
               OpenSSHPrivateKeyUtil.encodePrivateKey(
                  new RSAPrivateCrtKeyParameters(
                     ((RSAPrivateCrtKey)var1).getModulus(),
                     ((RSAPrivateCrtKey)var1).getPublicExponent(),
                     ((RSAPrivateCrtKey)var1).getPrivateExponent(),
                     ((RSAPrivateCrtKey)var1).getPrimeP(),
                     ((RSAPrivateCrtKey)var1).getPrimeQ(),
                     ((RSAPrivateCrtKey)var1).getPrimeExponentP(),
                     ((RSAPrivateCrtKey)var1).getPrimeExponentQ(),
                     ((RSAPrivateCrtKey)var1).getCrtCoefficient()
                  )
               )
            );
         } catch (IOException var5) {
            throw new IllegalArgumentException("unable to produce encoding: " + var5.getMessage());
         }
      } else {
         return super.engineGetKeySpec(var1, var2);
      }
   }

   @Override
   protected Key engineTranslateKey(Key var1) throws InvalidKeyException {
      if (var1 instanceof RSAPublicKey) {
         return new BCRSAPublicKey((RSAPublicKey)var1);
      } else if (var1 instanceof RSAPrivateCrtKey) {
         return new BCRSAPrivateCrtKey((RSAPrivateCrtKey)var1);
      } else if (var1 instanceof RSAPrivateKey) {
         return new BCRSAPrivateKey((RSAPrivateKey)var1);
      } else {
         throw new InvalidKeyException("key type unknown");
      }
   }

   @Override
   protected PrivateKey engineGeneratePrivate(KeySpec var1) throws InvalidKeySpecException {
      if (var1 instanceof PKCS8EncodedKeySpec) {
         try {
            return this.generatePrivate(PrivateKeyInfo.getInstance(((PKCS8EncodedKeySpec)var1).getEncoded()));
         } catch (Exception var5) {
            try {
               return new BCRSAPrivateCrtKey(org.bouncycastle.asn1.pkcs.RSAPrivateKey.getInstance(((PKCS8EncodedKeySpec)var1).getEncoded()));
            } catch (Exception var4) {
               throw new ExtendedInvalidKeySpecException("unable to process key spec: " + var5.toString(), var5);
            }
         }
      } else if (var1 instanceof RSAPrivateCrtKeySpec) {
         return new BCRSAPrivateCrtKey((RSAPrivateCrtKeySpec)var1);
      } else if (var1 instanceof RSAPrivateKeySpec) {
         return new BCRSAPrivateKey((RSAPrivateKeySpec)var1);
      } else if (var1 instanceof OpenSSHPrivateKeySpec) {
         AsymmetricKeyParameter var2 = OpenSSHPrivateKeyUtil.parsePrivateKeyBlob(((OpenSSHPrivateKeySpec)var1).getEncoded());
         if (var2 instanceof RSAPrivateCrtKeyParameters) {
            return new BCRSAPrivateCrtKey((RSAPrivateCrtKeyParameters)var2);
         } else {
            throw new InvalidKeySpecException("open SSH public key is not RSA private key");
         }
      } else {
         throw new InvalidKeySpecException("unknown KeySpec type: " + var1.getClass().getName());
      }
   }

   @Override
   protected PublicKey engineGeneratePublic(KeySpec var1) throws InvalidKeySpecException {
      if (var1 instanceof RSAPublicKeySpec) {
         return new BCRSAPublicKey((RSAPublicKeySpec)var1);
      } else if (var1 instanceof OpenSSHPublicKeySpec) {
         AsymmetricKeyParameter var2 = OpenSSHPublicKeyUtil.parsePublicKey(((OpenSSHPublicKeySpec)var1).getEncoded());
         if (var2 instanceof RSAKeyParameters) {
            return new BCRSAPublicKey((RSAKeyParameters)var2);
         } else {
            throw new InvalidKeySpecException("Open SSH public key is not RSA public key");
         }
      } else {
         return super.engineGeneratePublic(var1);
      }
   }

   @Override
   public PrivateKey generatePrivate(PrivateKeyInfo var1) throws IOException {
      ASN1ObjectIdentifier var2 = var1.getPrivateKeyAlgorithm().getAlgorithm();
      if (RSAUtil.isRsaOid(var2)) {
         org.bouncycastle.asn1.pkcs.RSAPrivateKey var3 = org.bouncycastle.asn1.pkcs.RSAPrivateKey.getInstance(var1.parsePrivateKey());
         return (PrivateKey)(var3.getCoefficient().intValue() == 0 ? new BCRSAPrivateKey(var1.getPrivateKeyAlgorithm(), var3) : new BCRSAPrivateCrtKey(var1));
      } else {
         throw new IOException("algorithm identifier " + var2 + " in key not recognised");
      }
   }

   @Override
   public PublicKey generatePublic(SubjectPublicKeyInfo var1) throws IOException {
      ASN1ObjectIdentifier var2 = var1.getAlgorithm().getAlgorithm();
      if (RSAUtil.isRsaOid(var2)) {
         return new BCRSAPublicKey(var1);
      } else {
         throw new IOException("algorithm identifier " + var2 + " in key not recognised");
      }
   }
}
