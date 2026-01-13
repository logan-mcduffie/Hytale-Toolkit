package org.bouncycastle.jcajce.provider.asymmetric.dsa;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.interfaces.DSAPrivateKey;
import java.security.interfaces.DSAPublicKey;
import java.security.spec.DSAPrivateKeySpec;
import java.security.spec.DSAPublicKeySpec;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.pkcs.PrivateKeyInfo;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import org.bouncycastle.crypto.params.AsymmetricKeyParameter;
import org.bouncycastle.crypto.params.DSAParameters;
import org.bouncycastle.crypto.params.DSAPrivateKeyParameters;
import org.bouncycastle.crypto.params.DSAPublicKeyParameters;
import org.bouncycastle.crypto.util.OpenSSHPrivateKeyUtil;
import org.bouncycastle.crypto.util.OpenSSHPublicKeyUtil;
import org.bouncycastle.jcajce.provider.asymmetric.util.BaseKeyFactorySpi;
import org.bouncycastle.jcajce.spec.OpenSSHPrivateKeySpec;
import org.bouncycastle.jcajce.spec.OpenSSHPublicKeySpec;

public class KeyFactorySpi extends BaseKeyFactorySpi {
   @Override
   protected KeySpec engineGetKeySpec(Key var1, Class var2) throws InvalidKeySpecException {
      if (var2.isAssignableFrom(DSAPublicKeySpec.class) && var1 instanceof DSAPublicKey) {
         DSAPublicKey var9 = (DSAPublicKey)var1;
         return new DSAPublicKeySpec(var9.getY(), var9.getParams().getP(), var9.getParams().getQ(), var9.getParams().getG());
      } else if (var2.isAssignableFrom(DSAPrivateKeySpec.class) && var1 instanceof DSAPrivateKey) {
         DSAPrivateKey var8 = (DSAPrivateKey)var1;
         return new DSAPrivateKeySpec(var8.getX(), var8.getParams().getP(), var8.getParams().getQ(), var8.getParams().getG());
      } else if (var2.isAssignableFrom(OpenSSHPublicKeySpec.class) && var1 instanceof DSAPublicKey) {
         DSAPublicKey var7 = (DSAPublicKey)var1;

         try {
            return new OpenSSHPublicKeySpec(
               OpenSSHPublicKeyUtil.encodePublicKey(
                  new DSAPublicKeyParameters(var7.getY(), new DSAParameters(var7.getParams().getP(), var7.getParams().getQ(), var7.getParams().getG()))
               )
            );
         } catch (IOException var5) {
            throw new IllegalArgumentException("unable to produce encoding: " + var5.getMessage());
         }
      } else if (var2.isAssignableFrom(OpenSSHPrivateKeySpec.class) && var1 instanceof DSAPrivateKey) {
         DSAPrivateKey var3 = (DSAPrivateKey)var1;

         try {
            return new OpenSSHPrivateKeySpec(
               OpenSSHPrivateKeyUtil.encodePrivateKey(
                  new DSAPrivateKeyParameters(var3.getX(), new DSAParameters(var3.getParams().getP(), var3.getParams().getQ(), var3.getParams().getG()))
               )
            );
         } catch (IOException var6) {
            throw new IllegalArgumentException("unable to produce encoding: " + var6.getMessage());
         }
      } else {
         return super.engineGetKeySpec(var1, var2);
      }
   }

   @Override
   protected Key engineTranslateKey(Key var1) throws InvalidKeyException {
      if (var1 instanceof DSAPublicKey) {
         return new BCDSAPublicKey((DSAPublicKey)var1);
      } else if (var1 instanceof DSAPrivateKey) {
         return new BCDSAPrivateKey((DSAPrivateKey)var1);
      } else {
         throw new InvalidKeyException("key type unknown");
      }
   }

   @Override
   public PrivateKey generatePrivate(PrivateKeyInfo var1) throws IOException {
      ASN1ObjectIdentifier var2 = var1.getPrivateKeyAlgorithm().getAlgorithm();
      if (DSAUtil.isDsaOid(var2)) {
         return new BCDSAPrivateKey(var1);
      } else {
         throw new IOException("algorithm identifier " + var2 + " in key not recognised");
      }
   }

   @Override
   public PublicKey generatePublic(SubjectPublicKeyInfo var1) throws IOException {
      ASN1ObjectIdentifier var2 = var1.getAlgorithm().getAlgorithm();
      if (DSAUtil.isDsaOid(var2)) {
         return new BCDSAPublicKey(var1);
      } else {
         throw new IOException("algorithm identifier " + var2 + " in key not recognised");
      }
   }

   @Override
   protected PrivateKey engineGeneratePrivate(KeySpec var1) throws InvalidKeySpecException {
      if (var1 instanceof DSAPrivateKeySpec) {
         return new BCDSAPrivateKey((DSAPrivateKeySpec)var1);
      } else if (var1 instanceof OpenSSHPrivateKeySpec) {
         AsymmetricKeyParameter var2 = OpenSSHPrivateKeyUtil.parsePrivateKeyBlob(((OpenSSHPrivateKeySpec)var1).getEncoded());
         if (var2 instanceof DSAPrivateKeyParameters) {
            return this.engineGeneratePrivate(
               new DSAPrivateKeySpec(
                  ((DSAPrivateKeyParameters)var2).getX(),
                  ((DSAPrivateKeyParameters)var2).getParameters().getP(),
                  ((DSAPrivateKeyParameters)var2).getParameters().getQ(),
                  ((DSAPrivateKeyParameters)var2).getParameters().getG()
               )
            );
         } else {
            throw new IllegalArgumentException("openssh private key is not dsa privare key");
         }
      } else {
         return super.engineGeneratePrivate(var1);
      }
   }

   @Override
   protected PublicKey engineGeneratePublic(KeySpec var1) throws InvalidKeySpecException {
      if (var1 instanceof DSAPublicKeySpec) {
         try {
            return new BCDSAPublicKey((DSAPublicKeySpec)var1);
         } catch (final Exception var3) {
            throw new InvalidKeySpecException("invalid KeySpec: " + var3.getMessage()) {
               @Override
               public Throwable getCause() {
                  return var3;
               }
            };
         }
      } else if (var1 instanceof OpenSSHPublicKeySpec) {
         AsymmetricKeyParameter var2 = OpenSSHPublicKeyUtil.parsePublicKey(((OpenSSHPublicKeySpec)var1).getEncoded());
         if (var2 instanceof DSAPublicKeyParameters) {
            return this.engineGeneratePublic(
               new DSAPublicKeySpec(
                  ((DSAPublicKeyParameters)var2).getY(),
                  ((DSAPublicKeyParameters)var2).getParameters().getP(),
                  ((DSAPublicKeyParameters)var2).getParameters().getQ(),
                  ((DSAPublicKeyParameters)var2).getParameters().getG()
               )
            );
         } else {
            throw new IllegalArgumentException("openssh public key is not dsa public key");
         }
      } else {
         return super.engineGeneratePublic(var1);
      }
   }
}
