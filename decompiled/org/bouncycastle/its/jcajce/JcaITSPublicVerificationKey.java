package org.bouncycastle.its.jcajce;

import java.security.KeyFactory;
import java.security.Provider;
import java.security.PublicKey;
import java.security.interfaces.ECPublicKey;
import java.security.spec.ECParameterSpec;
import java.security.spec.ECPublicKeySpec;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.nist.NISTNamedCurves;
import org.bouncycastle.asn1.sec.SECObjectIdentifiers;
import org.bouncycastle.asn1.teletrust.TeleTrusTNamedCurves;
import org.bouncycastle.asn1.teletrust.TeleTrusTObjectIdentifiers;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import org.bouncycastle.asn1.x9.X9ECParameters;
import org.bouncycastle.its.ITSPublicVerificationKey;
import org.bouncycastle.jcajce.util.DefaultJcaJceHelper;
import org.bouncycastle.jcajce.util.JcaJceHelper;
import org.bouncycastle.jcajce.util.NamedJcaJceHelper;
import org.bouncycastle.jcajce.util.ProviderJcaJceHelper;
import org.bouncycastle.math.ec.ECCurve;
import org.bouncycastle.math.ec.ECPoint;
import org.bouncycastle.oer.its.ieee1609dot2.basetypes.EccCurvePoint;
import org.bouncycastle.oer.its.ieee1609dot2.basetypes.EccP256CurvePoint;
import org.bouncycastle.oer.its.ieee1609dot2.basetypes.EccP384CurvePoint;
import org.bouncycastle.oer.its.ieee1609dot2.basetypes.Point256;
import org.bouncycastle.oer.its.ieee1609dot2.basetypes.Point384;
import org.bouncycastle.oer.its.ieee1609dot2.basetypes.PublicVerificationKey;

public class JcaITSPublicVerificationKey extends ITSPublicVerificationKey {
   private final JcaJceHelper helper;

   JcaITSPublicVerificationKey(PublicVerificationKey var1, JcaJceHelper var2) {
      super(var1);
      this.helper = var2;
   }

   JcaITSPublicVerificationKey(PublicKey var1, JcaJceHelper var2) {
      super(fromKeyParameters((ECPublicKey)var1));
      this.helper = var2;
   }

   static PublicVerificationKey fromKeyParameters(ECPublicKey var0) {
      ASN1ObjectIdentifier var1 = ASN1ObjectIdentifier.getInstance(SubjectPublicKeyInfo.getInstance(var0.getEncoded()).getAlgorithm().getParameters());
      if (var1.equals(SECObjectIdentifiers.secp256r1)) {
         return new PublicVerificationKey(
            0, EccP256CurvePoint.uncompressedP256(Point256.builder().setX(var0.getW().getAffineX()).setY(var0.getW().getAffineY()).createPoint256())
         );
      } else if (var1.equals(TeleTrusTObjectIdentifiers.brainpoolP256r1)) {
         return new PublicVerificationKey(
            1, EccP256CurvePoint.uncompressedP256(Point256.builder().setX(var0.getW().getAffineX()).setY(var0.getW().getAffineY()).createPoint256())
         );
      } else if (var1.equals(TeleTrusTObjectIdentifiers.brainpoolP384r1)) {
         return new PublicVerificationKey(
            2, EccP384CurvePoint.uncompressedP384(Point384.builder().setX(var0.getW().getAffineX()).setY(var0.getW().getAffineY()).createPoint384())
         );
      } else {
         throw new IllegalArgumentException("unknown curve in public encryption key");
      }
   }

   public PublicKey getKey() {
      X9ECParameters var1;
      switch (this.verificationKey.getChoice()) {
         case 0:
            var1 = NISTNamedCurves.getByOID(SECObjectIdentifiers.secp256r1);
            break;
         case 1:
            var1 = TeleTrusTNamedCurves.getByOID(TeleTrusTObjectIdentifiers.brainpoolP256r1);
            break;
         case 2:
            var1 = TeleTrusTNamedCurves.getByOID(TeleTrusTObjectIdentifiers.brainpoolP384r1);
            break;
         default:
            throw new IllegalStateException("unknown key type");
      }

      ECCurve var2 = var1.getCurve();
      ASN1Encodable var3 = this.verificationKey.getPublicVerificationKey();
      if (var3 instanceof EccCurvePoint) {
         EccCurvePoint var4 = (EccCurvePoint)this.verificationKey.getPublicVerificationKey();
         byte[] var5;
         if (var4 instanceof EccP256CurvePoint) {
            var5 = var4.getEncodedPoint();
         } else {
            if (!(var4 instanceof EccP384CurvePoint)) {
               throw new IllegalStateException("unknown key type");
            }

            var5 = var4.getEncodedPoint();
         }

         ECPoint var6 = var2.decodePoint(var5).normalize();

         try {
            KeyFactory var7 = this.helper.createKeyFactory("EC");
            ECParameterSpec var8 = ECUtil.convertToSpec(var1);
            java.security.spec.ECPoint var9 = ECUtil.convertPoint(var6);
            return var7.generatePublic(new ECPublicKeySpec(var9, var8));
         } catch (Exception var10) {
            throw new IllegalStateException(var10.getMessage(), var10);
         }
      } else {
         throw new IllegalStateException("extension to public verification key not supported");
      }
   }

   public static class Builder {
      private JcaJceHelper helper = new DefaultJcaJceHelper();

      public JcaITSPublicVerificationKey.Builder setProvider(Provider var1) {
         this.helper = new ProviderJcaJceHelper(var1);
         return this;
      }

      public JcaITSPublicVerificationKey.Builder setProvider(String var1) {
         this.helper = new NamedJcaJceHelper(var1);
         return this;
      }

      public JcaITSPublicVerificationKey build(PublicVerificationKey var1) {
         return new JcaITSPublicVerificationKey(var1, this.helper);
      }

      public JcaITSPublicVerificationKey build(PublicKey var1) {
         return new JcaITSPublicVerificationKey(var1, this.helper);
      }
   }
}
