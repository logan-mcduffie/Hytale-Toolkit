package org.bouncycastle.asn1.x509;

import java.math.BigInteger;
import org.bouncycastle.asn1.ASN1BitString;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.ASN1TaggedObject;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.x500.X500Name;

public class IssuerSerial extends ASN1Object {
   GeneralNames issuer;
   ASN1Integer serial;
   ASN1BitString issuerUID;

   public static IssuerSerial getInstance(Object var0) {
      if (var0 instanceof IssuerSerial) {
         return (IssuerSerial)var0;
      } else {
         return var0 != null ? new IssuerSerial(ASN1Sequence.getInstance(var0)) : null;
      }
   }

   public static IssuerSerial getInstance(ASN1TaggedObject var0, boolean var1) {
      return getInstance(ASN1Sequence.getInstance(var0, var1));
   }

   private IssuerSerial(ASN1Sequence var1) {
      if (var1.size() != 2 && var1.size() != 3) {
         throw new IllegalArgumentException("Bad sequence size: " + var1.size());
      } else {
         this.issuer = GeneralNames.getInstance(var1.getObjectAt(0));
         this.serial = ASN1Integer.getInstance(var1.getObjectAt(1));
         if (var1.size() == 3) {
            this.issuerUID = ASN1BitString.getInstance(var1.getObjectAt(2));
         }
      }
   }

   public IssuerSerial(X500Name var1, BigInteger var2) {
      this(new GeneralNames(new GeneralName(var1)), new ASN1Integer(var2));
   }

   public IssuerSerial(GeneralNames var1, BigInteger var2) {
      this(var1, new ASN1Integer(var2));
   }

   public IssuerSerial(GeneralNames var1, ASN1Integer var2) {
      this.issuer = var1;
      this.serial = var2;
   }

   public GeneralNames getIssuer() {
      return this.issuer;
   }

   public ASN1Integer getSerial() {
      return this.serial;
   }

   public ASN1BitString getIssuerUID() {
      return this.issuerUID;
   }

   @Override
   public ASN1Primitive toASN1Primitive() {
      ASN1EncodableVector var1 = new ASN1EncodableVector(3);
      var1.add(this.issuer);
      var1.add(this.serial);
      if (this.issuerUID != null) {
         var1.add(this.issuerUID);
      }

      return new DERSequence(var1);
   }
}
