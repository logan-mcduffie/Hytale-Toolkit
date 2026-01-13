package org.bouncycastle.its.operator;

import java.io.IOException;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.DEROctetString;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.sec.SECObjectIdentifiers;
import org.bouncycastle.asn1.teletrust.TeleTrusTObjectIdentifiers;
import org.bouncycastle.oer.its.ieee1609dot2.basetypes.EccP256CurvePoint;
import org.bouncycastle.oer.its.ieee1609dot2.basetypes.EccP384CurvePoint;
import org.bouncycastle.oer.its.ieee1609dot2.basetypes.EcdsaP256Signature;
import org.bouncycastle.oer.its.ieee1609dot2.basetypes.EcdsaP384Signature;
import org.bouncycastle.oer.its.ieee1609dot2.basetypes.Signature;
import org.bouncycastle.util.BigIntegers;

public class ECDSAEncoder {
   public static byte[] toX962(Signature var0) {
      byte[] var1;
      byte[] var2;
      if (var0.getChoice() != 0 && var0.getChoice() != 1) {
         EcdsaP384Signature var5 = EcdsaP384Signature.getInstance(var0.getSignature());
         var1 = ASN1OctetString.getInstance(var5.getRSig().getEccP384CurvePoint()).getOctets();
         var2 = var5.getSSig().getOctets();
      } else {
         EcdsaP256Signature var3 = EcdsaP256Signature.getInstance(var0.getSignature());
         var1 = ASN1OctetString.getInstance(var3.getRSig().getEccp256CurvePoint()).getOctets();
         var2 = var3.getSSig().getOctets();
      }

      try {
         return new DERSequence(
               new ASN1Encodable[]{new ASN1Integer(BigIntegers.fromUnsignedByteArray(var1)), new ASN1Integer(BigIntegers.fromUnsignedByteArray(var2))}
            )
            .getEncoded();
      } catch (IOException var4) {
         throw new RuntimeException("der encoding r & s");
      }
   }

   public static Signature toITS(ASN1ObjectIdentifier var0, byte[] var1) {
      ASN1Sequence var2 = ASN1Sequence.getInstance(var1);
      if (var0.equals(SECObjectIdentifiers.secp256r1)) {
         return new Signature(
            0,
            new EcdsaP256Signature(
               new EccP256CurvePoint(0, new DEROctetString(BigIntegers.asUnsignedByteArray(32, ASN1Integer.getInstance(var2.getObjectAt(0)).getValue()))),
               new DEROctetString(BigIntegers.asUnsignedByteArray(32, ASN1Integer.getInstance(var2.getObjectAt(1)).getValue()))
            )
         );
      } else if (var0.equals(TeleTrusTObjectIdentifiers.brainpoolP256r1)) {
         return new Signature(
            1,
            new EcdsaP256Signature(
               new EccP256CurvePoint(0, new DEROctetString(BigIntegers.asUnsignedByteArray(32, ASN1Integer.getInstance(var2.getObjectAt(0)).getValue()))),
               new DEROctetString(BigIntegers.asUnsignedByteArray(32, ASN1Integer.getInstance(var2.getObjectAt(1)).getValue()))
            )
         );
      } else if (var0.equals(TeleTrusTObjectIdentifiers.brainpoolP384r1)) {
         return new Signature(
            2,
            new EcdsaP384Signature(
               new EccP384CurvePoint(0, new DEROctetString(BigIntegers.asUnsignedByteArray(48, ASN1Integer.getInstance(var2.getObjectAt(0)).getValue()))),
               new DEROctetString(BigIntegers.asUnsignedByteArray(48, ASN1Integer.getInstance(var2.getObjectAt(1)).getValue()))
            )
         );
      } else {
         throw new IllegalArgumentException("unknown curveID");
      }
   }
}
