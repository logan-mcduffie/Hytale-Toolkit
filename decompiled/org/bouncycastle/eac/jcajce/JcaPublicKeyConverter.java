package org.bouncycastle.eac.jcajce;

import java.math.BigInteger;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.Provider;
import java.security.PublicKey;
import java.security.interfaces.ECPublicKey;
import java.security.spec.ECField;
import java.security.spec.ECFieldF2m;
import java.security.spec.ECFieldFp;
import java.security.spec.ECParameterSpec;
import java.security.spec.ECPoint;
import java.security.spec.ECPublicKeySpec;
import java.security.spec.EllipticCurve;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.RSAPublicKeySpec;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.eac.EACObjectIdentifiers;
import org.bouncycastle.asn1.eac.ECDSAPublicKey;
import org.bouncycastle.asn1.eac.PublicKeyDataObject;
import org.bouncycastle.asn1.eac.RSAPublicKey;
import org.bouncycastle.eac.EACException;
import org.bouncycastle.math.ec.ECAlgorithms;
import org.bouncycastle.math.ec.ECCurve;
import org.bouncycastle.math.field.FiniteField;
import org.bouncycastle.math.field.Polynomial;
import org.bouncycastle.math.field.PolynomialExtensionField;
import org.bouncycastle.util.Arrays;

public class JcaPublicKeyConverter {
   private EACHelper helper = new DefaultEACHelper();

   public JcaPublicKeyConverter setProvider(String var1) {
      this.helper = new NamedEACHelper(var1);
      return this;
   }

   public JcaPublicKeyConverter setProvider(Provider var1) {
      this.helper = new ProviderEACHelper(var1);
      return this;
   }

   public PublicKey getKey(PublicKeyDataObject var1) throws EACException, InvalidKeySpecException {
      if (var1.getUsage().on(EACObjectIdentifiers.id_TA_ECDSA)) {
         return this.getECPublicKeyPublicKey((ECDSAPublicKey)var1);
      } else {
         RSAPublicKey var2 = (RSAPublicKey)var1;
         RSAPublicKeySpec var3 = new RSAPublicKeySpec(var2.getModulus(), var2.getPublicExponent());

         try {
            KeyFactory var4 = this.helper.createKeyFactory("RSA");
            return var4.generatePublic(var3);
         } catch (NoSuchProviderException var5) {
            throw new EACException("cannot find provider: " + var5.getMessage(), var5);
         } catch (NoSuchAlgorithmException var6) {
            throw new EACException("cannot find algorithm ECDSA: " + var6.getMessage(), var6);
         }
      }
   }

   private PublicKey getECPublicKeyPublicKey(ECDSAPublicKey var1) throws EACException, InvalidKeySpecException {
      ECParameterSpec var2 = this.getParams(var1);
      ECPoint var3 = this.getPublicPoint(var1);
      ECPublicKeySpec var4 = new ECPublicKeySpec(var3, var2);

      KeyFactory var5;
      try {
         var5 = this.helper.createKeyFactory("ECDSA");
      } catch (NoSuchProviderException var7) {
         throw new EACException("cannot find provider: " + var7.getMessage(), var7);
      } catch (NoSuchAlgorithmException var8) {
         throw new EACException("cannot find algorithm ECDSA: " + var8.getMessage(), var8);
      }

      return var5.generatePublic(var4);
   }

   private ECPoint getPublicPoint(ECDSAPublicKey var1) {
      if (!var1.hasParameters()) {
         throw new IllegalArgumentException("Public key does not contains EC Params");
      } else {
         BigInteger var2 = var1.getPrimeModulusP();
         ECCurve.Fp var3 = new ECCurve.Fp(var2, var1.getFirstCoefA(), var1.getSecondCoefB(), var1.getOrderOfBasePointR(), var1.getCofactorF());
         org.bouncycastle.math.ec.ECPoint.Fp var4 = (org.bouncycastle.math.ec.ECPoint.Fp)var3.decodePoint(var1.getPublicPointY());
         return new ECPoint(var4.getAffineXCoord().toBigInteger(), var4.getAffineYCoord().toBigInteger());
      }
   }

   private ECParameterSpec getParams(ECDSAPublicKey var1) {
      if (!var1.hasParameters()) {
         throw new IllegalArgumentException("Public key does not contains EC Params");
      } else {
         BigInteger var2 = var1.getPrimeModulusP();
         ECCurve.Fp var3 = new ECCurve.Fp(var2, var1.getFirstCoefA(), var1.getSecondCoefB(), var1.getOrderOfBasePointR(), var1.getCofactorF());
         org.bouncycastle.math.ec.ECPoint var4 = var3.decodePoint(var1.getBasePointG());
         BigInteger var5 = var1.getOrderOfBasePointR();
         BigInteger var6 = var1.getCofactorF();
         EllipticCurve var7 = convertCurve(var3);
         return new ECParameterSpec(var7, new ECPoint(var4.getAffineXCoord().toBigInteger(), var4.getAffineYCoord().toBigInteger()), var5, var6.intValue());
      }
   }

   public PublicKeyDataObject getPublicKeyDataObject(ASN1ObjectIdentifier var1, PublicKey var2) {
      if (var2 instanceof java.security.interfaces.RSAPublicKey) {
         java.security.interfaces.RSAPublicKey var9 = (java.security.interfaces.RSAPublicKey)var2;
         return new RSAPublicKey(var1, var9.getModulus(), var9.getPublicExponent());
      } else {
         ECPublicKey var3 = (ECPublicKey)var2;
         ECParameterSpec var4 = var3.getParams();
         EllipticCurve var5 = var4.getCurve();
         ECCurve var6 = convertCurve(var5, var4.getOrder(), var4.getCofactor());
         org.bouncycastle.math.ec.ECPoint var7 = convertPoint(var6, var4.getGenerator());
         org.bouncycastle.math.ec.ECPoint var8 = convertPoint(var6, var3.getW());
         return new ECDSAPublicKey(
            var1,
            ((ECFieldFp)var5.getField()).getP(),
            var5.getA(),
            var5.getB(),
            var7.getEncoded(false),
            var4.getOrder(),
            var8.getEncoded(false),
            var4.getCofactor()
         );
      }
   }

   private static org.bouncycastle.math.ec.ECPoint convertPoint(ECCurve var0, ECPoint var1) {
      return var0.createPoint(var1.getAffineX(), var1.getAffineY());
   }

   private static ECCurve convertCurve(EllipticCurve var0, BigInteger var1, int var2) {
      ECField var3 = var0.getField();
      BigInteger var4 = var0.getA();
      BigInteger var5 = var0.getB();
      if (var3 instanceof ECFieldFp) {
         return new ECCurve.Fp(((ECFieldFp)var3).getP(), var4, var5, var1, BigInteger.valueOf(var2));
      } else {
         throw new IllegalStateException("not implemented yet!!!");
      }
   }

   private static EllipticCurve convertCurve(ECCurve var0) {
      ECField var1 = convertField(var0.getField());
      BigInteger var2 = var0.getA().toBigInteger();
      BigInteger var3 = var0.getB().toBigInteger();
      return new EllipticCurve(var1, var2, var3, null);
   }

   private static ECField convertField(FiniteField var0) {
      if (ECAlgorithms.isFpField(var0)) {
         return new ECFieldFp(var0.getCharacteristic());
      } else {
         Polynomial var1 = ((PolynomialExtensionField)var0).getMinimalPolynomial();
         int[] var2 = var1.getExponentsPresent();
         int[] var3 = Arrays.reverseInPlace(Arrays.copyOfRange(var2, 1, var2.length - 1));
         return new ECFieldF2m(var1.getDegree(), var3);
      }
   }
}
