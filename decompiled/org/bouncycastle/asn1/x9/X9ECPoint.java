package org.bouncycastle.asn1.x9;

import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.DEROctetString;
import org.bouncycastle.math.ec.ECCurve;
import org.bouncycastle.math.ec.ECPoint;
import org.bouncycastle.util.Arrays;

public class X9ECPoint extends ASN1Object {
   private final ASN1OctetString encoding;
   private ECCurve c;
   private ECPoint p;

   public X9ECPoint(ECPoint var1, boolean var2) {
      this.p = var1.normalize();
      this.encoding = new DEROctetString(var1.getEncoded(var2));
   }

   public X9ECPoint(ECCurve var1, byte[] var2) {
      this.c = var1;
      this.encoding = new DEROctetString(Arrays.clone(var2));
   }

   public X9ECPoint(ECCurve var1, ASN1OctetString var2) {
      this(var1, var2.getOctets());
   }

   public byte[] getPointEncoding() {
      return Arrays.clone(this.encoding.getOctets());
   }

   public synchronized ECPoint getPoint() {
      if (this.p == null) {
         this.p = this.c.decodePoint(this.encoding.getOctets()).normalize();
      }

      return this.p;
   }

   public boolean isPointCompressed() {
      byte[] var1 = this.encoding.getOctets();
      return var1 != null && var1.length > 0 && (var1[0] == 2 || var1[0] == 3);
   }

   @Override
   public ASN1Primitive toASN1Primitive() {
      return this.encoding;
   }
}
