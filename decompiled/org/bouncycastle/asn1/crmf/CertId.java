package org.bouncycastle.asn1.crmf;

import java.math.BigInteger;
import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.ASN1TaggedObject;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.x509.GeneralName;

public class CertId extends ASN1Object {
   private GeneralName issuer;
   private ASN1Integer serialNumber;

   private CertId(ASN1Sequence var1) {
      this.issuer = GeneralName.getInstance(var1.getObjectAt(0));
      this.serialNumber = ASN1Integer.getInstance(var1.getObjectAt(1));
   }

   public static CertId getInstance(Object var0) {
      if (var0 instanceof CertId) {
         return (CertId)var0;
      } else {
         return var0 != null ? new CertId(ASN1Sequence.getInstance(var0)) : null;
      }
   }

   public static CertId getInstance(ASN1TaggedObject var0, boolean var1) {
      return getInstance(ASN1Sequence.getInstance(var0, var1));
   }

   public CertId(GeneralName var1, BigInteger var2) {
      this(var1, new ASN1Integer(var2));
   }

   public CertId(GeneralName var1, ASN1Integer var2) {
      this.issuer = var1;
      this.serialNumber = var2;
   }

   public GeneralName getIssuer() {
      return this.issuer;
   }

   public ASN1Integer getSerialNumber() {
      return this.serialNumber;
   }

   @Override
   public ASN1Primitive toASN1Primitive() {
      return new DERSequence(this.issuer, this.serialNumber);
   }
}
