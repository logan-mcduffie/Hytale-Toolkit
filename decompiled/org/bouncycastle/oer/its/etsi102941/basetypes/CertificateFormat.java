package org.bouncycastle.oer.its.etsi102941.basetypes;

import java.math.BigInteger;
import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.util.BigIntegers;

public class CertificateFormat extends ASN1Object {
   private final int format;

   public CertificateFormat(int var1) {
      this.format = var1;
   }

   public CertificateFormat(BigInteger var1) {
      this.format = BigIntegers.intValueExact(var1);
   }

   private CertificateFormat(ASN1Integer var1) {
      this(var1.getValue());
   }

   public int getFormat() {
      return this.format;
   }

   public static CertificateFormat getInstance(Object var0) {
      if (var0 instanceof CertificateFormat) {
         return (CertificateFormat)var0;
      } else {
         return var0 != null ? new CertificateFormat(ASN1Integer.getInstance(var0)) : null;
      }
   }

   @Override
   public ASN1Primitive toASN1Primitive() {
      return new ASN1Integer(this.format);
   }
}
