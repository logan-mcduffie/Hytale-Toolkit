package org.bouncycastle.asn1.x9;

import java.util.Enumeration;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.DERSequence;

public class KeySpecificInfo extends ASN1Object {
   private ASN1ObjectIdentifier algorithm;
   private ASN1OctetString counter;

   public KeySpecificInfo(ASN1ObjectIdentifier var1, ASN1OctetString var2) {
      this.algorithm = var1;
      this.counter = var2;
   }

   public static KeySpecificInfo getInstance(Object var0) {
      if (var0 instanceof KeySpecificInfo) {
         return (KeySpecificInfo)var0;
      } else {
         return var0 != null ? new KeySpecificInfo(ASN1Sequence.getInstance(var0)) : null;
      }
   }

   private KeySpecificInfo(ASN1Sequence var1) {
      Enumeration var2 = var1.getObjects();
      this.algorithm = (ASN1ObjectIdentifier)var2.nextElement();
      this.counter = (ASN1OctetString)var2.nextElement();
   }

   public ASN1ObjectIdentifier getAlgorithm() {
      return this.algorithm;
   }

   public ASN1OctetString getCounter() {
      return this.counter;
   }

   @Override
   public ASN1Primitive toASN1Primitive() {
      return new DERSequence(this.algorithm, this.counter);
   }
}
