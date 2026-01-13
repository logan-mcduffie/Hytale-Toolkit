package org.bouncycastle.its.bc;

import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.nist.NISTNamedCurves;
import org.bouncycastle.asn1.sec.SECObjectIdentifiers;
import org.bouncycastle.asn1.teletrust.TeleTrusTNamedCurves;
import org.bouncycastle.asn1.teletrust.TeleTrusTObjectIdentifiers;
import org.bouncycastle.asn1.x9.X9ECParameters;
import org.bouncycastle.crypto.params.AsymmetricKeyParameter;
import org.bouncycastle.crypto.params.ECNamedDomainParameters;
import org.bouncycastle.crypto.params.ECPublicKeyParameters;
import org.bouncycastle.its.ITSPublicEncryptionKey;
import org.bouncycastle.math.ec.ECCurve;
import org.bouncycastle.math.ec.ECPoint;
import org.bouncycastle.oer.its.ieee1609dot2.basetypes.BasePublicEncryptionKey;
import org.bouncycastle.oer.its.ieee1609dot2.basetypes.EccCurvePoint;
import org.bouncycastle.oer.its.ieee1609dot2.basetypes.EccP256CurvePoint;
import org.bouncycastle.oer.its.ieee1609dot2.basetypes.EccP384CurvePoint;
import org.bouncycastle.oer.its.ieee1609dot2.basetypes.PublicEncryptionKey;
import org.bouncycastle.oer.its.ieee1609dot2.basetypes.SymmAlgorithm;

public class BcITSPublicEncryptionKey extends ITSPublicEncryptionKey {
   public BcITSPublicEncryptionKey(PublicEncryptionKey var1) {
      super(var1);
   }

   static PublicEncryptionKey fromKeyParameters(ECPublicKeyParameters var0) {
      ASN1ObjectIdentifier var1 = ((ECNamedDomainParameters)var0.getParameters()).getName();
      ECPoint var2 = var0.getQ();
      if (var1.equals(SECObjectIdentifiers.secp256r1)) {
         return new PublicEncryptionKey(
            SymmAlgorithm.aes128Ccm,
            new BasePublicEncryptionKey.Builder()
               .setChoice(0)
               .setValue(EccP256CurvePoint.uncompressedP256(var2.getAffineXCoord().toBigInteger(), var2.getAffineYCoord().toBigInteger()))
               .createBasePublicEncryptionKey()
         );
      } else if (var1.equals(TeleTrusTObjectIdentifiers.brainpoolP256r1)) {
         return new PublicEncryptionKey(
            SymmAlgorithm.aes128Ccm,
            new BasePublicEncryptionKey.Builder()
               .setChoice(1)
               .setValue(EccP256CurvePoint.uncompressedP256(var2.getAffineXCoord().toBigInteger(), var2.getAffineYCoord().toBigInteger()))
               .createBasePublicEncryptionKey()
         );
      } else {
         throw new IllegalArgumentException("unknown curve in public encryption key");
      }
   }

   public BcITSPublicEncryptionKey(AsymmetricKeyParameter var1) {
      super(fromKeyParameters((ECPublicKeyParameters)var1));
   }

   public AsymmetricKeyParameter getKey() {
      BasePublicEncryptionKey var2 = this.encryptionKey.getPublicKey();
      X9ECParameters var1;
      ASN1ObjectIdentifier var3;
      switch (var2.getChoice()) {
         case 0:
            var3 = SECObjectIdentifiers.secp256r1;
            var1 = NISTNamedCurves.getByOID(SECObjectIdentifiers.secp256r1);
            break;
         case 1:
            var3 = TeleTrusTObjectIdentifiers.brainpoolP256r1;
            var1 = TeleTrusTNamedCurves.getByOID(TeleTrusTObjectIdentifiers.brainpoolP256r1);
            break;
         default:
            throw new IllegalStateException("unknown key type");
      }

      ECCurve var4 = var1.getCurve();
      ASN1Encodable var5 = this.encryptionKey.getPublicKey().getBasePublicEncryptionKey();
      if (var5 instanceof EccCurvePoint) {
         EccCurvePoint var6 = (EccCurvePoint)var2.getBasePublicEncryptionKey();
         byte[] var7;
         if (var6 instanceof EccP256CurvePoint) {
            var7 = var6.getEncodedPoint();
         } else {
            if (!(var6 instanceof EccP384CurvePoint)) {
               throw new IllegalStateException("unknown key type");
            }

            var7 = var6.getEncodedPoint();
         }

         ECPoint var8 = var4.decodePoint(var7).normalize();
         return new ECPublicKeyParameters(var8, new ECNamedDomainParameters(var3, var1));
      } else {
         throw new IllegalStateException("extension to public verification key not supported");
      }
   }
}
