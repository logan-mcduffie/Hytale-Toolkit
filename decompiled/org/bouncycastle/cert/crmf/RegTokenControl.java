package org.bouncycastle.cert.crmf;

import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.ASN1UTF8String;
import org.bouncycastle.asn1.DERUTF8String;
import org.bouncycastle.asn1.crmf.CRMFObjectIdentifiers;

public class RegTokenControl implements Control {
   private static final ASN1ObjectIdentifier type = CRMFObjectIdentifiers.id_regCtrl_regToken;
   private final ASN1UTF8String token;

   public RegTokenControl(ASN1UTF8String var1) {
      this.token = var1;
   }

   public RegTokenControl(String var1) {
      this.token = new DERUTF8String(var1);
   }

   @Override
   public ASN1ObjectIdentifier getType() {
      return type;
   }

   @Override
   public ASN1Encodable getValue() {
      return this.token;
   }
}
