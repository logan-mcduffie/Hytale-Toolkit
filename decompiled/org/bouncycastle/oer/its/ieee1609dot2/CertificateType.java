package org.bouncycastle.oer.its.ieee1609dot2;

import java.math.BigInteger;
import org.bouncycastle.asn1.ASN1Enumerated;
import org.bouncycastle.util.BigIntegers;

public class CertificateType extends ASN1Enumerated {
   public static final CertificateType explicit = new CertificateType(BigInteger.ZERO);
   public static final CertificateType implicit = new CertificateType(BigInteger.ONE);

   public CertificateType(BigInteger var1) {
      super(var1);
      this.assertValues();
   }

   private CertificateType(ASN1Enumerated var1) {
      this(var1.getValue());
   }

   public static CertificateType getInstance(Object var0) {
      if (var0 instanceof CertificateType) {
         return (CertificateType)var0;
      } else {
         return var0 != null ? new CertificateType(ASN1Enumerated.getInstance(var0)) : null;
      }
   }

   protected void assertValues() {
      if (this.getValue().compareTo(BigInteger.ZERO) < 0 || this.getValue().compareTo(BigIntegers.ONE) > 0) {
         throw new IllegalArgumentException("invalid enumeration value " + this.getValue());
      }
   }
}
