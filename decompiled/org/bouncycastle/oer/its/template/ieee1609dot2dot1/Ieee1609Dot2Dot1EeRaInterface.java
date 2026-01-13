package org.bouncycastle.oer.its.template.ieee1609dot2dot1;

import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.oer.OERDefinition;
import org.bouncycastle.oer.its.template.ieee1609dot2.IEEE1609dot2;
import org.bouncycastle.oer.its.template.ieee1609dot2.basetypes.Ieee1609Dot2BaseTypes;

public class Ieee1609Dot2Dot1EeRaInterface {
   public static final OERDefinition.Builder ButterflyExpansion = OERDefinition.choice(OERDefinition.octets(16).label("aes128"), OERDefinition.extension())
      .typeName("ButterflyExpansion");
   public static final OERDefinition.Builder ButterflyParamsOriginal = OERDefinition.seq(
         ButterflyExpansion.label("signingExpansion"),
         Ieee1609Dot2BaseTypes.PublicEncryptionKey.label("encryptionKey"),
         ButterflyExpansion.label("encryptionExpansion")
      )
      .typeName("ButterflyParamsOriginal");
   public static final OERDefinition.Builder AdditionalParams = OERDefinition.choice(
         ButterflyParamsOriginal.label("original"),
         ButterflyExpansion.label("unified"),
         ButterflyExpansion.label("compactUnified"),
         Ieee1609Dot2BaseTypes.PublicEncryptionKey.label("encryptionKey"),
         OERDefinition.extension()
      )
      .typeName("AdditionalParams");
   public static final OERDefinition.Builder EeRaCertRequest = OERDefinition.seq(
         Ieee1609Dot2BaseTypes.UINT8.label("version").validSwitchValue(new ASN1Integer(2L)),
         Ieee1609Dot2BaseTypes.Time32.label("generationTime"),
         IEEE1609dot2.CertificateType.label("type"),
         IEEE1609dot2.ToBeSignedCertificate.label("tbsCert"),
         OERDefinition.optional(AdditionalParams.label("additionalParams")),
         OERDefinition.extension()
      )
      .typeName("EeRaCertRequest");
}
