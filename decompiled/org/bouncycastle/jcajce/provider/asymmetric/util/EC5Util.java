package org.bouncycastle.jcajce.provider.asymmetric.util;

import java.math.BigInteger;
import java.security.spec.ECField;
import java.security.spec.ECFieldF2m;
import java.security.spec.ECFieldFp;
import java.security.spec.ECParameterSpec;
import java.security.spec.ECPoint;
import java.security.spec.EllipticCurve;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.cryptopro.ECGOST3410NamedCurves;
import org.bouncycastle.asn1.cryptopro.GOST3410PublicKeyAlgParameters;
import org.bouncycastle.asn1.x9.ECNamedCurveTable;
import org.bouncycastle.asn1.x9.X962Parameters;
import org.bouncycastle.asn1.x9.X9ECParameters;
import org.bouncycastle.asn1.x9.X9ECParametersHolder;
import org.bouncycastle.crypto.ec.CustomNamedCurves;
import org.bouncycastle.crypto.params.ECDomainParameters;
import org.bouncycastle.jcajce.provider.config.ProviderConfiguration;
import org.bouncycastle.jce.ECGOST3410NamedCurveTable;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.jce.spec.ECNamedCurveParameterSpec;
import org.bouncycastle.jce.spec.ECNamedCurveSpec;
import org.bouncycastle.math.ec.ECAlgorithms;
import org.bouncycastle.math.ec.ECCurve;
import org.bouncycastle.math.field.FiniteField;
import org.bouncycastle.math.field.Polynomial;
import org.bouncycastle.math.field.PolynomialExtensionField;
import org.bouncycastle.util.Arrays;

public class EC5Util {
   public static ECCurve getCurve(ProviderConfiguration var0, X962Parameters var1) {
      Set var3 = var0.getAcceptableNamedCurves();
      ECCurve var2;
      if (var1.isNamedCurve()) {
         ASN1ObjectIdentifier var4 = ASN1ObjectIdentifier.getInstance(var1.getParameters());
         if (!var3.isEmpty() && !var3.contains(var4)) {
            throw new IllegalStateException("named curve not acceptable");
         }

         X9ECParameters var5 = ECUtil.getNamedCurveByOid(var4);
         if (var5 == null) {
            var5 = (X9ECParameters)var0.getAdditionalECParameters().get(var4);
         }

         var2 = var5.getCurve();
      } else if (var1.isImplicitlyCA()) {
         var2 = var0.getEcImplicitlyCa().getCurve();
      } else {
         ASN1Sequence var6 = ASN1Sequence.getInstance(var1.getParameters());
         if (!var3.isEmpty()) {
            throw new IllegalStateException("encoded parameters not acceptable");
         }

         if (var6.size() > 3) {
            X9ECParameters var7 = X9ECParameters.getInstance(var6);
            var2 = var7.getCurve();
         } else {
            ASN1ObjectIdentifier var8 = ASN1ObjectIdentifier.getInstance(var6.getObjectAt(0));
            var2 = ECGOST3410NamedCurves.getByOIDX9(var8).getCurve();
         }
      }

      return var2;
   }

   public static ECDomainParameters getDomainParameters(ProviderConfiguration var0, ECParameterSpec var1) {
      ECDomainParameters var2;
      if (var1 == null) {
         org.bouncycastle.jce.spec.ECParameterSpec var3 = var0.getEcImplicitlyCa();
         var2 = new ECDomainParameters(var3.getCurve(), var3.getG(), var3.getN(), var3.getH(), var3.getSeed());
      } else {
         var2 = ECUtil.getDomainParameters(var0, convertSpec(var1));
      }

      return var2;
   }

   public static ECParameterSpec convertToSpec(X962Parameters var0, ECCurve var1) {
      Object var2;
      if (var0.isNamedCurve()) {
         ASN1ObjectIdentifier var4 = (ASN1ObjectIdentifier)var0.getParameters();
         X9ECParameters var5 = ECUtil.getNamedCurveByOid(var4);
         if (var5 == null) {
            Map var6 = BouncyCastleProvider.CONFIGURATION.getAdditionalECParameters();
            if (!var6.isEmpty()) {
               var5 = (X9ECParameters)var6.get(var4);
            }
         }

         EllipticCurve var3 = convertCurve(var1, var5.getSeed());
         var2 = new ECNamedCurveSpec(ECUtil.getCurveName(var4), var3, convertPoint(var5.getG()), var5.getN(), var5.getH());
      } else if (var0.isImplicitlyCA()) {
         var2 = null;
      } else {
         ASN1Sequence var10 = ASN1Sequence.getInstance(var0.getParameters());
         if (var10.size() > 3) {
            X9ECParameters var11 = X9ECParameters.getInstance(var10);
            EllipticCurve var8 = convertCurve(var1, var11.getSeed());
            if (var11.getH() != null) {
               var2 = new ECParameterSpec(var8, convertPoint(var11.getG()), var11.getN(), var11.getH().intValue());
            } else {
               var2 = new ECParameterSpec(var8, convertPoint(var11.getG()), var11.getN(), 1);
            }
         } else {
            GOST3410PublicKeyAlgParameters var12 = GOST3410PublicKeyAlgParameters.getInstance(var10);
            ECNamedCurveParameterSpec var13 = ECGOST3410NamedCurveTable.getParameterSpec(ECGOST3410NamedCurves.getName(var12.getPublicKeyParamSet()));
            var1 = var13.getCurve();
            EllipticCurve var9 = convertCurve(var1, var13.getSeed());
            var2 = new ECNamedCurveSpec(
               ECGOST3410NamedCurves.getName(var12.getPublicKeyParamSet()), var9, convertPoint(var13.getG()), var13.getN(), var13.getH()
            );
         }
      }

      return (ECParameterSpec)var2;
   }

   public static ECParameterSpec convertToSpec(X9ECParameters var0) {
      return new ECParameterSpec(convertCurve(var0.getCurve(), null), convertPoint(var0.getG()), var0.getN(), var0.getH().intValue());
   }

   public static ECParameterSpec convertToSpec(ECDomainParameters var0) {
      return new ECParameterSpec(convertCurve(var0.getCurve(), null), convertPoint(var0.getG()), var0.getN(), var0.getH().intValue());
   }

   public static EllipticCurve convertCurve(ECCurve var0, byte[] var1) {
      ECField var2 = convertField(var0.getField());
      BigInteger var3 = var0.getA().toBigInteger();
      BigInteger var4 = var0.getB().toBigInteger();
      return new EllipticCurve(var2, var3, var4, null);
   }

   public static ECCurve convertCurve(EllipticCurve var0) {
      ECField var1 = var0.getField();
      BigInteger var2 = var0.getA();
      BigInteger var3 = var0.getB();
      if (var1 instanceof ECFieldFp) {
         return EC5Util.CustomCurves.substitute(new ECCurve.Fp(((ECFieldFp)var1).getP(), var2, var3, null, null));
      } else {
         ECFieldF2m var4 = (ECFieldF2m)var1;
         int var5 = var4.getM();
         int[] var6 = ECUtil.convertMidTerms(var4.getMidTermsOfReductionPolynomial());
         return new ECCurve.F2m(var5, var6[0], var6[1], var6[2], var2, var3, null, null);
      }
   }

   public static ECField convertField(FiniteField var0) {
      if (ECAlgorithms.isFpField(var0)) {
         return new ECFieldFp(var0.getCharacteristic());
      } else {
         Polynomial var1 = ((PolynomialExtensionField)var0).getMinimalPolynomial();
         int[] var2 = var1.getExponentsPresent();
         int[] var3 = Arrays.reverseInPlace(Arrays.copyOfRange(var2, 1, var2.length - 1));
         return new ECFieldF2m(var1.getDegree(), var3);
      }
   }

   public static ECParameterSpec convertSpec(EllipticCurve var0, org.bouncycastle.jce.spec.ECParameterSpec var1) {
      ECPoint var2 = convertPoint(var1.getG());
      if (var1 instanceof ECNamedCurveParameterSpec) {
         String var3 = ((ECNamedCurveParameterSpec)var1).getName();
         return new ECNamedCurveSpec(var3, var0, var2, var1.getN(), var1.getH());
      } else {
         return new ECParameterSpec(var0, var2, var1.getN(), var1.getH().intValue());
      }
   }

   public static org.bouncycastle.jce.spec.ECParameterSpec convertSpec(ECParameterSpec var0) {
      ECCurve var1 = convertCurve(var0.getCurve());
      org.bouncycastle.math.ec.ECPoint var2 = convertPoint(var1, var0.getGenerator());
      BigInteger var3 = var0.getOrder();
      BigInteger var4 = BigInteger.valueOf(var0.getCofactor());
      byte[] var5 = var0.getCurve().getSeed();
      return (org.bouncycastle.jce.spec.ECParameterSpec)(var0 instanceof ECNamedCurveSpec
         ? new ECNamedCurveParameterSpec(((ECNamedCurveSpec)var0).getName(), var1, var2, var3, var4, var5)
         : new org.bouncycastle.jce.spec.ECParameterSpec(var1, var2, var3, var4, var5));
   }

   public static org.bouncycastle.math.ec.ECPoint convertPoint(ECParameterSpec var0, ECPoint var1) {
      return convertPoint(convertCurve(var0.getCurve()), var1);
   }

   public static org.bouncycastle.math.ec.ECPoint convertPoint(ECCurve var0, ECPoint var1) {
      return var0.createPoint(var1.getAffineX(), var1.getAffineY());
   }

   public static ECPoint convertPoint(org.bouncycastle.math.ec.ECPoint var0) {
      var0 = var0.normalize();
      return new ECPoint(var0.getAffineXCoord().toBigInteger(), var0.getAffineYCoord().toBigInteger());
   }

   private static class CustomCurves {
      private static Map CURVE_MAP = createCurveMap();

      private static Map createCurveMap() {
         HashMap var0 = new HashMap();
         Enumeration var1 = CustomNamedCurves.getNames();

         while (var1.hasMoreElements()) {
            String var2 = (String)var1.nextElement();
            X9ECParametersHolder var3 = ECNamedCurveTable.getByNameLazy(var2);
            if (var3 != null) {
               ECCurve var4 = var3.getCurve();
               if (ECAlgorithms.isFpCurve(var4)) {
                  var0.put(var4, CustomNamedCurves.getByNameLazy(var2).getCurve());
               }
            }
         }

         ECCurve var5 = CustomNamedCurves.getByNameLazy("Curve25519").getCurve();
         var0.put(
            new ECCurve.Fp(
               var5.getField().getCharacteristic(), var5.getA().toBigInteger(), var5.getB().toBigInteger(), var5.getOrder(), var5.getCofactor(), true
            ),
            var5
         );
         return var0;
      }

      static ECCurve substitute(ECCurve var0) {
         ECCurve var1 = (ECCurve)CURVE_MAP.get(var0);
         return null != var1 ? var1 : var0;
      }
   }
}
