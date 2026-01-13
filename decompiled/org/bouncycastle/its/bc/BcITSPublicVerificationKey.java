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
import org.bouncycastle.its.ITSPublicVerificationKey;
import org.bouncycastle.math.ec.ECCurve;
import org.bouncycastle.math.ec.ECPoint;
import org.bouncycastle.oer.its.ieee1609dot2.basetypes.EccCurvePoint;
import org.bouncycastle.oer.its.ieee1609dot2.basetypes.EccP256CurvePoint;
import org.bouncycastle.oer.its.ieee1609dot2.basetypes.EccP384CurvePoint;
import org.bouncycastle.oer.its.ieee1609dot2.basetypes.Point256;
import org.bouncycastle.oer.its.ieee1609dot2.basetypes.Point384;
import org.bouncycastle.oer.its.ieee1609dot2.basetypes.PublicVerificationKey;

public class BcITSPublicVerificationKey extends ITSPublicVerificationKey {
   public BcITSPublicVerificationKey(PublicVerificationKey var1) {
      super(var1);
   }

   static PublicVerificationKey fromKeyParameters(ECPublicKeyParameters var0) {
      ASN1ObjectIdentifier var1 = ((ECNamedDomainParameters)var0.getParameters()).getName();
      ECPoint var2 = var0.getQ();
      if (var1.equals(SECObjectIdentifiers.secp256r1)) {
         return new PublicVerificationKey(
            0,
            EccP256CurvePoint.uncompressedP256(
               Point256.builder().setX(var2.getAffineXCoord().toBigInteger()).setY(var2.getAffineYCoord().toBigInteger()).createPoint256()
            )
         );
      } else if (var1.equals(TeleTrusTObjectIdentifiers.brainpoolP256r1)) {
         return new PublicVerificationKey(
            1,
            EccP256CurvePoint.uncompressedP256(
               Point256.builder().setX(var2.getAffineXCoord().toBigInteger()).setY(var2.getAffineYCoord().toBigInteger()).createPoint256()
            )
         );
      } else if (var1.equals(TeleTrusTObjectIdentifiers.brainpoolP384r1)) {
         return new PublicVerificationKey(
            2,
            EccP384CurvePoint.uncompressedP384(
               Point384.builder().setX(var2.getAffineXCoord().toBigInteger()).setY(var2.getAffineYCoord().toBigInteger()).createPoint384()
            )
         );
      } else {
         throw new IllegalArgumentException("unknown curve in public encryption key");
      }
   }

   public BcITSPublicVerificationKey(AsymmetricKeyParameter var1) {
      super(fromKeyParameters((ECPublicKeyParameters)var1));
   }

   public AsymmetricKeyParameter getKey() {
      X9ECParameters var1;
      ASN1ObjectIdentifier var2;
      switch (this.verificationKey.getChoice()) {
         case 0:
            var2 = SECObjectIdentifiers.secp256r1;
            var1 = NISTNamedCurves.getByOID(SECObjectIdentifiers.secp256r1);
            break;
         case 1:
            var2 = TeleTrusTObjectIdentifiers.brainpoolP256r1;
            var1 = TeleTrusTNamedCurves.getByOID(TeleTrusTObjectIdentifiers.brainpoolP256r1);
            break;
         case 2:
            var2 = TeleTrusTObjectIdentifiers.brainpoolP384r1;
            var1 = TeleTrusTNamedCurves.getByOID(TeleTrusTObjectIdentifiers.brainpoolP384r1);
            break;
         default:
            throw new IllegalStateException("unknown key type");
      }

      ECCurve var3 = var1.getCurve();
      ASN1Encodable var4 = this.verificationKey.getPublicVerificationKey();
      if (var4 instanceof EccCurvePoint) {
         EccCurvePoint var5 = (EccCurvePoint)this.verificationKey.getPublicVerificationKey();
         byte[] var6;
         if (var5 instanceof EccP256CurvePoint) {
            var6 = var5.getEncodedPoint();
         } else {
            if (!(var5 instanceof EccP384CurvePoint)) {
               throw new IllegalStateException("unknown key type");
            }

            var6 = var5.getEncodedPoint();
         }

         ECPoint var7 = var3.decodePoint(var6).normalize();
         return new ECPublicKeyParameters(var7, new ECNamedDomainParameters(var2, var1));
      } else {
         throw new IllegalStateException("extension to public verification key not supported");
      }
   }
}
