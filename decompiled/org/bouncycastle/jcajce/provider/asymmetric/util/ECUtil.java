package org.bouncycastle.jcajce.provider.asymmetric.util;

import java.math.BigInteger;
import java.security.InvalidKeyException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.AlgorithmParameterSpec;
import java.util.Enumeration;
import java.util.Map;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.pkcs.PrivateKeyInfo;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import org.bouncycastle.asn1.x9.ECNamedCurveTable;
import org.bouncycastle.asn1.x9.X962Parameters;
import org.bouncycastle.asn1.x9.X9ECParameters;
import org.bouncycastle.crypto.ec.CustomNamedCurves;
import org.bouncycastle.crypto.params.AsymmetricKeyParameter;
import org.bouncycastle.crypto.params.ECDomainParameters;
import org.bouncycastle.crypto.params.ECNamedDomainParameters;
import org.bouncycastle.crypto.params.ECPrivateKeyParameters;
import org.bouncycastle.crypto.params.ECPublicKeyParameters;
import org.bouncycastle.jcajce.provider.config.ProviderConfiguration;
import org.bouncycastle.jcajce.util.SpecUtil;
import org.bouncycastle.jce.interfaces.ECPrivateKey;
import org.bouncycastle.jce.interfaces.ECPublicKey;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.jce.spec.ECNamedCurveParameterSpec;
import org.bouncycastle.jce.spec.ECParameterSpec;
import org.bouncycastle.math.ec.ECCurve;
import org.bouncycastle.math.ec.ECPoint;
import org.bouncycastle.math.ec.FixedPointCombMultiplier;
import org.bouncycastle.util.Arrays;
import org.bouncycastle.util.Fingerprint;
import org.bouncycastle.util.Strings;

public class ECUtil {
   static int[] convertMidTerms(int[] var0) {
      int[] var1 = new int[3];
      if (var0.length == 1) {
         var1[0] = var0[0];
      } else {
         if (var0.length != 3) {
            throw new IllegalArgumentException("Only Trinomials and pentanomials supported");
         }

         if (var0[0] < var0[1] && var0[0] < var0[2]) {
            var1[0] = var0[0];
            if (var0[1] < var0[2]) {
               var1[1] = var0[1];
               var1[2] = var0[2];
            } else {
               var1[1] = var0[2];
               var1[2] = var0[1];
            }
         } else if (var0[1] < var0[2]) {
            var1[0] = var0[1];
            if (var0[0] < var0[2]) {
               var1[1] = var0[0];
               var1[2] = var0[2];
            } else {
               var1[1] = var0[2];
               var1[2] = var0[0];
            }
         } else {
            var1[0] = var0[2];
            if (var0[0] < var0[1]) {
               var1[1] = var0[0];
               var1[2] = var0[1];
            } else {
               var1[1] = var0[1];
               var1[2] = var0[0];
            }
         }
      }

      return var1;
   }

   public static ECDomainParameters getDomainParameters(ProviderConfiguration var0, ECParameterSpec var1) {
      Object var2;
      if (var1 instanceof ECNamedCurveParameterSpec) {
         ECNamedCurveParameterSpec var3 = (ECNamedCurveParameterSpec)var1;
         ASN1ObjectIdentifier var4 = getNamedCurveOid(var3.getName());
         var2 = new ECNamedDomainParameters(var4, var3.getCurve(), var3.getG(), var3.getN(), var3.getH(), var3.getSeed());
      } else if (var1 == null) {
         ECParameterSpec var5 = var0.getEcImplicitlyCa();
         var2 = new ECDomainParameters(var5.getCurve(), var5.getG(), var5.getN(), var5.getH(), var5.getSeed());
      } else {
         var2 = new ECDomainParameters(var1.getCurve(), var1.getG(), var1.getN(), var1.getH(), var1.getSeed());
      }

      return (ECDomainParameters)var2;
   }

   public static ECDomainParameters getDomainParameters(ProviderConfiguration var0, X962Parameters var1) {
      Object var2;
      if (var1.isNamedCurve()) {
         ASN1ObjectIdentifier var3 = ASN1ObjectIdentifier.getInstance(var1.getParameters());
         X9ECParameters var4 = getNamedCurveByOid(var3);
         if (var4 == null) {
            Map var5 = var0.getAdditionalECParameters();
            var4 = (X9ECParameters)var5.get(var3);
         }

         var2 = new ECNamedDomainParameters(var3, var4);
      } else if (var1.isImplicitlyCA()) {
         ECParameterSpec var6 = var0.getEcImplicitlyCa();
         var2 = new ECDomainParameters(var6.getCurve(), var6.getG(), var6.getN(), var6.getH(), var6.getSeed());
      } else {
         X9ECParameters var7 = X9ECParameters.getInstance(var1.getParameters());
         var2 = new ECDomainParameters(var7.getCurve(), var7.getG(), var7.getN(), var7.getH(), var7.getSeed());
      }

      return (ECDomainParameters)var2;
   }

   public static AsymmetricKeyParameter generatePublicKeyParameter(PublicKey var0) throws InvalidKeyException {
      if (var0 instanceof ECPublicKey) {
         ECPublicKey var5 = (ECPublicKey)var0;
         ECParameterSpec var7 = var5.getParameters();
         return new ECPublicKeyParameters(var5.getQ(), new ECDomainParameters(var7.getCurve(), var7.getG(), var7.getN(), var7.getH(), var7.getSeed()));
      } else if (var0 instanceof java.security.interfaces.ECPublicKey) {
         java.security.interfaces.ECPublicKey var4 = (java.security.interfaces.ECPublicKey)var0;
         ECParameterSpec var6 = EC5Util.convertSpec(var4.getParams());
         return new ECPublicKeyParameters(
            EC5Util.convertPoint(var4.getParams(), var4.getW()), new ECDomainParameters(var6.getCurve(), var6.getG(), var6.getN(), var6.getH(), var6.getSeed())
         );
      } else {
         try {
            byte[] var1 = var0.getEncoded();
            if (var1 == null) {
               throw new InvalidKeyException("no encoding for EC public key");
            }

            PublicKey var2 = BouncyCastleProvider.getPublicKey(SubjectPublicKeyInfo.getInstance(var1));
            if (var2 instanceof java.security.interfaces.ECPublicKey) {
               return generatePublicKeyParameter(var2);
            }
         } catch (Exception var3) {
            throw new InvalidKeyException("cannot identify EC public key: " + var3.toString());
         }

         throw new InvalidKeyException("cannot identify EC public key.");
      }
   }

   public static AsymmetricKeyParameter generatePrivateKeyParameter(PrivateKey var0) throws InvalidKeyException {
      if (var0 instanceof ECPrivateKey) {
         ECPrivateKey var6 = (ECPrivateKey)var0;
         ECParameterSpec var8 = var6.getParameters();
         if (var8 == null) {
            var8 = BouncyCastleProvider.CONFIGURATION.getEcImplicitlyCa();
         }

         if (var6.getParameters() instanceof ECNamedCurveParameterSpec) {
            String var3 = ((ECNamedCurveParameterSpec)var6.getParameters()).getName();
            return new ECPrivateKeyParameters(
               var6.getD(), new ECNamedDomainParameters(ECNamedCurveTable.getOID(var3), var8.getCurve(), var8.getG(), var8.getN(), var8.getH(), var8.getSeed())
            );
         } else {
            return new ECPrivateKeyParameters(var6.getD(), new ECDomainParameters(var8.getCurve(), var8.getG(), var8.getN(), var8.getH(), var8.getSeed()));
         }
      } else if (var0 instanceof java.security.interfaces.ECPrivateKey) {
         java.security.interfaces.ECPrivateKey var5 = (java.security.interfaces.ECPrivateKey)var0;
         ECParameterSpec var7 = EC5Util.convertSpec(var5.getParams());
         return new ECPrivateKeyParameters(var5.getS(), new ECDomainParameters(var7.getCurve(), var7.getG(), var7.getN(), var7.getH(), var7.getSeed()));
      } else {
         try {
            byte[] var1 = var0.getEncoded();
            if (var1 == null) {
               throw new InvalidKeyException("no encoding for EC private key");
            }

            PrivateKey var2 = BouncyCastleProvider.getPrivateKey(PrivateKeyInfo.getInstance(var1));
            if (var2 instanceof java.security.interfaces.ECPrivateKey) {
               return generatePrivateKeyParameter(var2);
            }
         } catch (Exception var4) {
            throw new InvalidKeyException("cannot identify EC private key: " + var4.toString());
         }

         throw new InvalidKeyException("can't identify EC private key.");
      }
   }

   public static int getOrderBitLength(ProviderConfiguration var0, BigInteger var1, BigInteger var2) {
      if (var1 == null) {
         if (var0 == null) {
            return var2.bitLength();
         } else {
            ECParameterSpec var3 = var0.getEcImplicitlyCa();
            return var3 == null ? var2.bitLength() : var3.getN().bitLength();
         }
      } else {
         return var1.bitLength();
      }
   }

   public static ASN1ObjectIdentifier getNamedCurveOid(String var0) {
      if (null == var0) {
         return null;
      } else {
         var0 = var0.trim();
         if (var0.length() == 0) {
            return null;
         } else {
            int var1 = var0.indexOf(32);
            if (var1 > 0) {
               var0 = var0.substring(var1 + 1);
            }

            ASN1ObjectIdentifier var2 = getOID(var0);
            return null != var2 ? var2 : ECNamedCurveTable.getOID(var0);
         }
      }
   }

   public static ASN1ObjectIdentifier getNamedCurveOid(ECParameterSpec var0) {
      Enumeration var1 = ECNamedCurveTable.getNames();

      while (var1.hasMoreElements()) {
         String var2 = (String)var1.nextElement();
         X9ECParameters var3 = ECNamedCurveTable.getByName(var2);
         if (var3.getN().equals(var0.getN()) && var3.getH().equals(var0.getH()) && var3.getCurve().equals(var0.getCurve()) && var3.getG().equals(var0.getG())) {
            return ECNamedCurveTable.getOID(var2);
         }
      }

      return null;
   }

   public static X9ECParameters getNamedCurveByOid(ASN1ObjectIdentifier var0) {
      X9ECParameters var1 = CustomNamedCurves.getByOID(var0);
      if (var1 == null) {
         var1 = ECNamedCurveTable.getByOID(var0);
      }

      return var1;
   }

   public static X9ECParameters getNamedCurveByName(String var0) {
      X9ECParameters var1 = CustomNamedCurves.getByName(var0);
      if (var1 == null) {
         var1 = ECNamedCurveTable.getByName(var0);
      }

      return var1;
   }

   public static String getCurveName(ASN1ObjectIdentifier var0) {
      return ECNamedCurveTable.getName(var0);
   }

   public static String privateKeyToString(String var0, BigInteger var1, ECParameterSpec var2) {
      StringBuilder var3 = new StringBuilder();
      String var4 = Strings.lineSeparator();
      ECPoint var5 = new FixedPointCombMultiplier().multiply(var2.getG(), var1).normalize();
      var3.append(var0);
      var3.append(" Private Key [").append(generateKeyFingerprint(var5, var2)).append("]").append(var4);
      var3.append("            X: ").append(var5.getAffineXCoord().toBigInteger().toString(16)).append(var4);
      var3.append("            Y: ").append(var5.getAffineYCoord().toBigInteger().toString(16)).append(var4);
      return var3.toString();
   }

   public static String publicKeyToString(String var0, ECPoint var1, ECParameterSpec var2) {
      StringBuilder var3 = new StringBuilder();
      String var4 = Strings.lineSeparator();
      var3.append(var0);
      var3.append(" Public Key [").append(generateKeyFingerprint(var1, var2)).append("]").append(var4);
      var3.append("            X: ").append(var1.getAffineXCoord().toBigInteger().toString(16)).append(var4);
      var3.append("            Y: ").append(var1.getAffineYCoord().toBigInteger().toString(16)).append(var4);
      return var3.toString();
   }

   public static String generateKeyFingerprint(ECPoint var0, ECParameterSpec var1) {
      ECCurve var2 = var1.getCurve();
      ECPoint var3 = var1.getG();
      return var2 != null
         ? new Fingerprint(Arrays.concatenate(var0.getEncoded(false), var2.getA().getEncoded(), var2.getB().getEncoded(), var3.getEncoded(false))).toString()
         : new Fingerprint(var0.getEncoded(false)).toString();
   }

   public static String getNameFrom(AlgorithmParameterSpec var0) {
      return SpecUtil.getNameFrom(var0);
   }

   private static ASN1ObjectIdentifier getOID(String var0) {
      char var1 = var0.charAt(0);
      if (var1 >= '0' && var1 <= '2') {
         try {
            return new ASN1ObjectIdentifier(var0);
         } catch (Exception var3) {
         }
      }

      return null;
   }
}
