package org.bouncycastle.oer.its.etsi103097.extension;

import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1Sequence;

public class EtsiOriginatingHeaderInfoExtension extends Extension {
   public EtsiOriginatingHeaderInfoExtension(ExtId var1, ASN1Encodable var2) {
      super(var1, var2);
   }

   private EtsiOriginatingHeaderInfoExtension(ASN1Sequence var1) {
      super(var1);
   }

   public static EtsiOriginatingHeaderInfoExtension getInstance(Object var0) {
      if (var0 instanceof EtsiOriginatingHeaderInfoExtension) {
         return (EtsiOriginatingHeaderInfoExtension)var0;
      } else {
         return var0 != null ? new EtsiOriginatingHeaderInfoExtension(ASN1Sequence.getInstance(var0)) : null;
      }
   }

   public EtsiTs102941CrlRequest getEtsiTs102941CrlRequest() {
      return EtsiTs102941CrlRequest.getInstance(this.getContent());
   }

   public EtsiTs102941DeltaCtlRequest getEtsiTs102941DeltaCtlRequest() {
      return EtsiTs102941DeltaCtlRequest.getInstance(this.getContent());
   }

   public static EtsiOriginatingHeaderInfoExtension.Builder builder() {
      return new EtsiOriginatingHeaderInfoExtension.Builder();
   }

   public static class Builder {
      private ExtId id;
      private ASN1Encodable encodable;

      public EtsiOriginatingHeaderInfoExtension.Builder setId(ExtId var1) {
         this.id = var1;
         return this;
      }

      public EtsiOriginatingHeaderInfoExtension.Builder setEncodable(ASN1Encodable var1) {
         this.encodable = var1;
         return this;
      }

      public EtsiOriginatingHeaderInfoExtension.Builder setEtsiTs102941CrlRequest(EtsiTs102941CrlRequest var1) {
         this.id = Extension.etsiTs102941CrlRequestId;
         this.encodable = var1;
         return this;
      }

      public EtsiOriginatingHeaderInfoExtension.Builder setEtsiTs102941DeltaCtlRequest(EtsiTs102941DeltaCtlRequest var1) {
         this.id = Extension.etsiTs102941DeltaCtlRequestId;
         this.encodable = var1;
         return this;
      }

      public EtsiOriginatingHeaderInfoExtension createEtsiOriginatingHeaderInfoExtension() {
         return new EtsiOriginatingHeaderInfoExtension(this.id, this.encodable);
      }
   }
}
