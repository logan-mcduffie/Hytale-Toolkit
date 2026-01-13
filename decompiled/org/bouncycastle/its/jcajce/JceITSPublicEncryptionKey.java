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
import org.bouncycastle.its.ITSPublicEncryptionKey;
import org.bouncycastle.jcajce.util.DefaultJcaJceHelper;
import org.bouncycastle.jcajce.util.JcaJceHelper;
import org.bouncycastle.jcajce.util.NamedJcaJceHelper;
import org.bouncycastle.jcajce.util.ProviderJcaJceHelper;
import org.bouncycastle.math.ec.ECCurve;
import org.bouncycastle.math.ec.ECPoint;
import org.bouncycastle.oer.its.ieee1609dot2.basetypes.BasePublicEncryptionKey;
import org.bouncycastle.oer.its.ieee1609dot2.basetypes.EccCurvePoint;
import org.bouncycastle.oer.its.ieee1609dot2.basetypes.EccP256CurvePoint;
import org.bouncycastle.oer.its.ieee1609dot2.basetypes.EccP384CurvePoint;
import org.bouncycastle.oer.its.ieee1609dot2.basetypes.PublicEncryptionKey;
import org.bouncycastle.oer.its.ieee1609dot2.basetypes.SymmAlgorithm;

public class JceITSPublicEncryptionKey extends ITSPublicEncryptionKey {
   private final JcaJceHelper helper;

   JceITSPublicEncryptionKey(PublicEncryptionKey var1, JcaJceHelper var2) {
      super(var1);
      this.helper = var2;
   }

   JceITSPublicEncryptionKey(PublicKey var1, JcaJceHelper var2) {
      super(fromPublicKey(var1));
      this.helper = var2;
   }

   static PublicEncryptionKey fromPublicKey(PublicKey var0) {
      if (!(var0 instanceof ECPublicKey)) {
         throw new IllegalArgumentException("must be ECPublicKey instance");
      } else {
         ECPublicKey var1 = (ECPublicKey)var0;
         ASN1ObjectIdentifier var2 = ASN1ObjectIdentifier.getInstance(SubjectPublicKeyInfo.getInstance(var0.getEncoded()).getAlgorithm().getParameters());
         if (var2.equals(SECObjectIdentifiers.secp256r1)) {
            return new PublicEncryptionKey(
               SymmAlgorithm.aes128Ccm,
               new BasePublicEncryptionKey.Builder()
                  .setChoice(0)
                  .setValue(EccP256CurvePoint.uncompressedP256(var1.getW().getAffineX(), var1.getW().getAffineY()))
                  .createBasePublicEncryptionKey()
            );
         } else if (var2.equals(TeleTrusTObjectIdentifiers.brainpoolP256r1)) {
            return new PublicEncryptionKey(
               SymmAlgorithm.aes128Ccm,
               new BasePublicEncryptionKey.Builder()
                  .setChoice(1)
                  .setValue(EccP256CurvePoint.uncompressedP256(var1.getW().getAffineX(), var1.getW().getAffineY()))
                  .createBasePublicEncryptionKey()
            );
         } else {
            throw new IllegalArgumentException("unknown curve in public encryption key");
         }
      }
   }

   public PublicKey getKey() {
      BasePublicEncryptionKey var1 = this.encryptionKey.getPublicKey();
      X9ECParameters var2;
      switch (var1.getChoice()) {
         case 0:
            var2 = NISTNamedCurves.getByOID(SECObjectIdentifiers.secp256r1);
            break;
         case 1:
            var2 = TeleTrusTNamedCurves.getByOID(TeleTrusTObjectIdentifiers.brainpoolP256r1);
            break;
         default:
            throw new IllegalStateException("unknown key type");
      }

      ASN1Encodable var3 = this.encryptionKey.getPublicKey().getBasePublicEncryptionKey();
      if (var3 instanceof EccCurvePoint) {
         EccCurvePoint var4 = (EccCurvePoint)var1.getBasePublicEncryptionKey();
         ECCurve var5 = var2.getCurve();
         byte[] var6;
         if (var4 instanceof EccP256CurvePoint) {
            var6 = var4.getEncodedPoint();
         } else {
            if (!(var4 instanceof EccP384CurvePoint)) {
               throw new IllegalStateException("unknown key type");
            }

            var6 = var4.getEncodedPoint();
         }

         ECPoint var7 = var5.decodePoint(var6).normalize();

         try {
            KeyFactory var8 = this.helper.createKeyFactory("EC");
            ECParameterSpec var9 = ECUtil.convertToSpec(var2);
            java.security.spec.ECPoint var10 = ECUtil.convertPoint(var7);
            return var8.generatePublic(new ECPublicKeySpec(var10, var9));
         } catch (Exception var11) {
            throw new IllegalStateException(var11.getMessage(), var11);
         }
      } else {
         throw new IllegalStateException("extension to public verification key not supported");
      }
   }

   public static class Builder {
      private JcaJceHelper helper = new DefaultJcaJceHelper();

      public JceITSPublicEncryptionKey.Builder setProvider(Provider var1) {
         this.helper = new ProviderJcaJceHelper(var1);
         return this;
      }

      public JceITSPublicEncryptionKey.Builder setProvider(String var1) {
         this.helper = new NamedJcaJceHelper(var1);
         return this;
      }

      public JceITSPublicEncryptionKey build(PublicEncryptionKey var1) {
         return new JceITSPublicEncryptionKey(var1, this.helper);
      }

      public JceITSPublicEncryptionKey build(PublicKey var1) {
         return new JceITSPublicEncryptionKey(var1, this.helper);
      }
   }
}
