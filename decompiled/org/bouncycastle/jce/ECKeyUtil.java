package org.bouncycastle.jce;

import java.io.UnsupportedEncodingException;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.Provider;
import java.security.PublicKey;
import java.security.Security;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.cryptopro.CryptoProObjectIdentifiers;
import org.bouncycastle.asn1.pkcs.PrivateKeyInfo;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import org.bouncycastle.asn1.x9.X962Parameters;
import org.bouncycastle.asn1.x9.X9ECParameters;
import org.bouncycastle.asn1.x9.X9ECPoint;
import org.bouncycastle.asn1.x9.X9ObjectIdentifiers;
import org.bouncycastle.jcajce.provider.asymmetric.util.ECUtil;
import org.bouncycastle.jce.provider.BouncyCastleProvider;

public class ECKeyUtil {
   public static PublicKey publicToExplicitParameters(PublicKey var0, String var1) throws IllegalArgumentException, NoSuchAlgorithmException, NoSuchProviderException {
      Provider var2 = Security.getProvider(var1);
      if (var2 == null) {
         throw new NoSuchProviderException("cannot find provider: " + var1);
      } else {
         return publicToExplicitParameters(var0, var2);
      }
   }

   public static PublicKey publicToExplicitParameters(PublicKey var0, Provider var1) throws IllegalArgumentException, NoSuchAlgorithmException {
      try {
         SubjectPublicKeyInfo var2 = SubjectPublicKeyInfo.getInstance(ASN1Primitive.fromByteArray(var0.getEncoded()));
         if (var2.getAlgorithm().getAlgorithm().equals(CryptoProObjectIdentifiers.gostR3410_2001)) {
            throw new IllegalArgumentException("cannot convert GOST key to explicit parameters.");
         } else {
            X962Parameters var3 = X962Parameters.getInstance(var2.getAlgorithm().getParameters());
            X9ECParameters var4;
            if (var3.isNamedCurve()) {
               ASN1ObjectIdentifier var5 = ASN1ObjectIdentifier.getInstance(var3.getParameters());
               var4 = ECUtil.getNamedCurveByOid(var5);
               if (var4.hasSeed()) {
                  var4 = new X9ECParameters(var4.getCurve(), var4.getBaseEntry(), var4.getN(), var4.getH());
               }
            } else {
               if (!var3.isImplicitlyCA()) {
                  return var0;
               }

               var4 = new X9ECParameters(
                  BouncyCastleProvider.CONFIGURATION.getEcImplicitlyCa().getCurve(),
                  new X9ECPoint(BouncyCastleProvider.CONFIGURATION.getEcImplicitlyCa().getG(), false),
                  BouncyCastleProvider.CONFIGURATION.getEcImplicitlyCa().getN(),
                  BouncyCastleProvider.CONFIGURATION.getEcImplicitlyCa().getH()
               );
            }

            var3 = new X962Parameters(var4);
            var2 = new SubjectPublicKeyInfo(new AlgorithmIdentifier(X9ObjectIdentifiers.id_ecPublicKey, var3), var2.getPublicKeyData().getBytes());
            KeyFactory var11 = KeyFactory.getInstance(var0.getAlgorithm(), var1);
            return var11.generatePublic(new X509EncodedKeySpec(var2.getEncoded()));
         }
      } catch (IllegalArgumentException var6) {
         throw var6;
      } catch (NoSuchAlgorithmException var7) {
         throw var7;
      } catch (Exception var8) {
         throw new ECKeyUtil.UnexpectedException(var8);
      }
   }

   public static PrivateKey privateToExplicitParameters(PrivateKey var0, String var1) throws IllegalArgumentException, NoSuchAlgorithmException, NoSuchProviderException {
      Provider var2 = Security.getProvider(var1);
      if (var2 == null) {
         throw new NoSuchProviderException("cannot find provider: " + var1);
      } else {
         return privateToExplicitParameters(var0, var2);
      }
   }

   public static PrivateKey privateToExplicitParameters(PrivateKey var0, Provider var1) throws IllegalArgumentException, NoSuchAlgorithmException {
      try {
         PrivateKeyInfo var2 = PrivateKeyInfo.getInstance(ASN1Primitive.fromByteArray(var0.getEncoded()));
         if (var2.getPrivateKeyAlgorithm().getAlgorithm().equals(CryptoProObjectIdentifiers.gostR3410_2001)) {
            throw new UnsupportedEncodingException("cannot convert GOST key to explicit parameters.");
         } else {
            X962Parameters var3 = X962Parameters.getInstance(var2.getPrivateKeyAlgorithm().getParameters());
            X9ECParameters var4;
            if (var3.isNamedCurve()) {
               ASN1ObjectIdentifier var5 = ASN1ObjectIdentifier.getInstance(var3.getParameters());
               var4 = ECUtil.getNamedCurveByOid(var5);
               if (var4.hasSeed()) {
                  var4 = new X9ECParameters(var4.getCurve(), var4.getBaseEntry(), var4.getN(), var4.getH());
               }
            } else {
               if (!var3.isImplicitlyCA()) {
                  return var0;
               }

               var4 = new X9ECParameters(
                  BouncyCastleProvider.CONFIGURATION.getEcImplicitlyCa().getCurve(),
                  new X9ECPoint(BouncyCastleProvider.CONFIGURATION.getEcImplicitlyCa().getG(), false),
                  BouncyCastleProvider.CONFIGURATION.getEcImplicitlyCa().getN(),
                  BouncyCastleProvider.CONFIGURATION.getEcImplicitlyCa().getH()
               );
            }

            var3 = new X962Parameters(var4);
            var2 = new PrivateKeyInfo(new AlgorithmIdentifier(X9ObjectIdentifiers.id_ecPublicKey, var3), var2.parsePrivateKey());
            KeyFactory var11 = KeyFactory.getInstance(var0.getAlgorithm(), var1);
            return var11.generatePrivate(new PKCS8EncodedKeySpec(var2.getEncoded()));
         }
      } catch (IllegalArgumentException var6) {
         throw var6;
      } catch (NoSuchAlgorithmException var7) {
         throw var7;
      } catch (Exception var8) {
         throw new ECKeyUtil.UnexpectedException(var8);
      }
   }

   private static class UnexpectedException extends RuntimeException {
      private Throwable cause;

      UnexpectedException(Throwable var1) {
         super(var1.toString());
         this.cause = var1;
      }

      @Override
      public Throwable getCause() {
         return this.cause;
      }
   }
}
