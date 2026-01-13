package org.bouncycastle.asn1.smime;

import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.DERSequence;

public class SMIMECapabilityVector {
   private ASN1EncodableVector capabilities = new ASN1EncodableVector();

   public void addCapability(ASN1ObjectIdentifier var1) {
      this.capabilities.add(new DERSequence(var1));
   }

   public void addCapability(ASN1ObjectIdentifier var1, int var2) {
      this.capabilities.add(new DERSequence(var1, new ASN1Integer(var2)));
   }

   public void addCapability(ASN1ObjectIdentifier var1, ASN1Encodable var2) {
      this.capabilities.add(new DERSequence(var1, var2));
   }

   public ASN1EncodableVector toASN1EncodableVector() {
      return this.capabilities;
   }
}
