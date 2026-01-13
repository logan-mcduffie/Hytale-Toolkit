package org.bouncycastle.oer.its.template.etsi103097.extension;

import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.oer.Element;
import org.bouncycastle.oer.OERDefinition;
import org.bouncycastle.oer.Switch;
import org.bouncycastle.oer.SwitchIndexer;
import org.bouncycastle.oer.its.template.ieee1609dot2.basetypes.Ieee1609Dot2BaseTypes;

public class EtsiTs103097ExtensionModule {
   public static final ASN1Integer etsiTs102941CrlRequestId = new ASN1Integer(1L);
   public static final ASN1Integer etsiTs102941DeltaCtlRequestId = new ASN1Integer(2L);
   private static final ASN1Encodable[] extensionKeys = new ASN1Encodable[]{etsiTs102941CrlRequestId, etsiTs102941DeltaCtlRequestId};
   public static final OERDefinition.Builder ExtId = OERDefinition.integer(0L, 255L)
      .validSwitchValue(etsiTs102941CrlRequestId, etsiTs102941DeltaCtlRequestId)
      .typeName("ExtId");
   public static final OERDefinition.Builder EtsiTs102941CrlRequest = OERDefinition.seq(
         Ieee1609Dot2BaseTypes.HashedId8.label("issuerId"), OERDefinition.optional(Ieee1609Dot2BaseTypes.Time32.label("lastKnownUpdate"))
      )
      .typeName("EtsiTs102941CrlRequest");
   public static final OERDefinition.Builder EtsiTs102941CtlRequest = OERDefinition.seq(
         Ieee1609Dot2BaseTypes.HashedId8.label("issuerId"), OERDefinition.optional(OERDefinition.integer(0L, 255L).label("lastKnownCtlSequence"))
      )
      .typeName("EtsiTs102941CtlRequest");
   public static final OERDefinition.Builder EtsiTs102941DeltaCtlRequest = EtsiTs102941CtlRequest.typeName("EtsiTs102941DeltaCtlRequest");
   public static final OERDefinition.Builder Extension = OERDefinition.seq(ExtId.label("id"), OERDefinition.aSwitch(new Switch() {
      private final Element etsiTs102941CrlRequestIdDef = EtsiTs103097ExtensionModule.EtsiTs102941CrlRequest.label("content").build();
      private final Element etsiTs102941DeltaCtlRequestIdDef = EtsiTs103097ExtensionModule.EtsiTs102941DeltaCtlRequest.label("content").build();

      @Override
      public Element result(SwitchIndexer var1) {
         ASN1Integer var2 = ASN1Integer.getInstance(var1.get(0).toASN1Primitive());
         if (var2.equals(EtsiTs103097ExtensionModule.etsiTs102941CrlRequestId)) {
            return this.etsiTs102941CrlRequestIdDef;
         } else if (var2.equals(EtsiTs103097ExtensionModule.etsiTs102941DeltaCtlRequestId)) {
            return this.etsiTs102941DeltaCtlRequestIdDef;
         } else {
            throw new IllegalStateException("unknown extension type " + var2);
         }
      }

      @Override
      public ASN1Encodable[] keys() {
         return EtsiTs103097ExtensionModule.extensionKeys;
      }
   }).label("content")).typeName("Extension");
   public static final OERDefinition.Builder EtsiOriginatingHeaderInfoExtension = Extension.typeName("EtsiOriginatingHeaderInfoExtension");
}
