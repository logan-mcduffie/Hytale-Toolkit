package org.bouncycastle.asn1.ua;

import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.DEROctetString;
import org.bouncycastle.math.ec.ECPoint;

public class DSTU4145PublicKey extends ASN1Object {
   private ASN1OctetString pubKey;

   public DSTU4145PublicKey(ECPoint var1) {
      this.pubKey = new DEROctetString(DSTU4145PointEncoder.encodePoint(var1));
   }

   private DSTU4145PublicKey(ASN1OctetString var1) {
      this.pubKey = var1;
   }

   public static DSTU4145PublicKey getInstance(Object var0) {
      if (var0 instanceof DSTU4145PublicKey) {
         return (DSTU4145PublicKey)var0;
      } else {
         return var0 != null ? new DSTU4145PublicKey(ASN1OctetString.getInstance(var0)) : null;
      }
   }

   @Override
   public ASN1Primitive toASN1Primitive() {
      return this.pubKey;
   }
}
