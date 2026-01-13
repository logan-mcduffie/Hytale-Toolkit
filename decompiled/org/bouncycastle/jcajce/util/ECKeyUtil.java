package org.bouncycastle.jcajce.util;

import java.io.IOException;
import java.security.interfaces.ECPublicKey;
import java.security.spec.ECParameterSpec;
import java.security.spec.ECPoint;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import org.bouncycastle.asn1.x9.ECNamedCurveTable;
import org.bouncycastle.asn1.x9.X962Parameters;
import org.bouncycastle.asn1.x9.X9ECParameters;
import org.bouncycastle.asn1.x9.X9ECParametersHolder;
import org.bouncycastle.asn1.x9.X9ECPoint;
import org.bouncycastle.crypto.ec.CustomNamedCurves;
import org.bouncycastle.math.ec.ECCurve;

public class ECKeyUtil {
   public static ECPublicKey createKeyWithCompression(ECPublicKey var0) {
      return new ECKeyUtil.ECPublicKeyWithCompression(var0);
   }

   private static class ECPublicKeyWithCompression implements ECPublicKey {
      private final ECPublicKey ecPublicKey;

      public ECPublicKeyWithCompression(ECPublicKey var1) {
         this.ecPublicKey = var1;
      }

      @Override
      public ECPoint getW() {
         return this.ecPublicKey.getW();
      }

      @Override
      public String getAlgorithm() {
         return this.ecPublicKey.getAlgorithm();
      }

      @Override
      public String getFormat() {
         return this.ecPublicKey.getFormat();
      }

      @Override
      public byte[] getEncoded() {
         SubjectPublicKeyInfo var1 = SubjectPublicKeyInfo.getInstance(this.ecPublicKey.getEncoded());
         X962Parameters var2 = X962Parameters.getInstance(var1.getAlgorithm().getParameters());
         ECCurve var3;
         if (var2.isNamedCurve()) {
            ASN1ObjectIdentifier var4 = (ASN1ObjectIdentifier)var2.getParameters();
            X9ECParametersHolder var5 = CustomNamedCurves.getByOIDLazy(var4);
            if (var5 == null) {
               var5 = ECNamedCurveTable.getByOIDLazy(var4);
            }

            var3 = var5.getCurve();
         } else {
            if (var2.isImplicitlyCA()) {
               throw new IllegalStateException("unable to identify implictlyCA");
            }

            X9ECParameters var8 = X9ECParameters.getInstance(var2.getParameters());
            var3 = var8.getCurve();
         }

         org.bouncycastle.math.ec.ECPoint var9 = var3.decodePoint(var1.getPublicKeyData().getOctets());
         ASN1OctetString var10 = ASN1OctetString.getInstance(new X9ECPoint(var9, true).toASN1Primitive());

         try {
            return new SubjectPublicKeyInfo(var1.getAlgorithm(), var10.getOctets()).getEncoded();
         } catch (IOException var7) {
            throw new IllegalStateException("unable to encode EC public key: " + var7.getMessage());
         }
      }

      @Override
      public ECParameterSpec getParams() {
         return this.ecPublicKey.getParams();
      }
   }
}
