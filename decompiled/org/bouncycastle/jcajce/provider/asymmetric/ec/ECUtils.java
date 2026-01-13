package org.bouncycastle.jcajce.provider.asymmetric.ec;

import java.math.BigInteger;
import java.security.InvalidKeyException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.ECGenParameterSpec;
import java.security.spec.ECParameterSpec;
import java.util.Map;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.DERNull;
import org.bouncycastle.asn1.x9.X962Parameters;
import org.bouncycastle.asn1.x9.X9ECParameters;
import org.bouncycastle.asn1.x9.X9ECPoint;
import org.bouncycastle.crypto.params.AsymmetricKeyParameter;
import org.bouncycastle.jcajce.provider.asymmetric.util.EC5Util;
import org.bouncycastle.jcajce.provider.asymmetric.util.ECUtil;
import org.bouncycastle.jcajce.provider.config.ProviderConfiguration;
import org.bouncycastle.jce.spec.ECNamedCurveSpec;
import org.bouncycastle.math.ec.ECCurve;

class ECUtils {
   static AsymmetricKeyParameter generatePublicKeyParameter(PublicKey var0) throws InvalidKeyException {
      return (AsymmetricKeyParameter)(var0 instanceof BCECPublicKey ? ((BCECPublicKey)var0).engineGetKeyParameters() : ECUtil.generatePublicKeyParameter(var0));
   }

   static AsymmetricKeyParameter generatePrivateKeyParameter(PrivateKey var0) throws InvalidKeyException {
      return (AsymmetricKeyParameter)(var0 instanceof BCECPrivateKey
         ? ((BCECPrivateKey)var0).engineGetKeyParameters()
         : ECUtil.generatePrivateKeyParameter(var0));
   }

   static X9ECParameters getDomainParametersFromGenSpec(ECGenParameterSpec var0, ProviderConfiguration var1) {
      return getDomainParametersFromName(var0.getName(), var1);
   }

   static X9ECParameters getDomainParametersFromName(String var0, ProviderConfiguration var1) {
      if (null != var0 && var0.length() >= 1) {
         int var2 = var0.indexOf(32);
         if (var2 > 0) {
            var0 = var0.substring(var2 + 1);
         }

         ASN1ObjectIdentifier var3 = getOID(var0);
         if (null == var3) {
            return ECUtil.getNamedCurveByName(var0);
         } else {
            X9ECParameters var4 = ECUtil.getNamedCurveByOid(var3);
            if (null == var4 && null != var1) {
               Map var5 = var1.getAdditionalECParameters();
               var4 = (X9ECParameters)var5.get(var3);
            }

            return var4;
         }
      } else {
         return null;
      }
   }

   static X962Parameters getDomainParametersFromName(ECParameterSpec var0, boolean var1) {
      X962Parameters var2;
      if (var0 instanceof ECNamedCurveSpec) {
         ASN1ObjectIdentifier var3 = ECUtil.getNamedCurveOid(((ECNamedCurveSpec)var0).getName());
         if (var3 == null) {
            var3 = new ASN1ObjectIdentifier(((ECNamedCurveSpec)var0).getName());
         }

         var2 = new X962Parameters(var3);
      } else if (var0 == null) {
         var2 = new X962Parameters(DERNull.INSTANCE);
      } else {
         ECCurve var5 = EC5Util.convertCurve(var0.getCurve());
         X9ECParameters var4 = new X9ECParameters(
            var5,
            new X9ECPoint(EC5Util.convertPoint(var5, var0.getGenerator()), var1),
            var0.getOrder(),
            BigInteger.valueOf(var0.getCofactor()),
            var0.getCurve().getSeed()
         );
         var2 = new X962Parameters(var4);
      }

      return var2;
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
