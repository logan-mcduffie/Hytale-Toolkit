package org.bouncycastle.asn1.esf;

import java.math.BigInteger;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.ASN1UTCTime;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.x500.X500Name;

public class CrlIdentifier extends ASN1Object {
   private X500Name crlIssuer;
   private ASN1UTCTime crlIssuedTime;
   private ASN1Integer crlNumber;

   public static CrlIdentifier getInstance(Object var0) {
      if (var0 instanceof CrlIdentifier) {
         return (CrlIdentifier)var0;
      } else {
         return var0 != null ? new CrlIdentifier(ASN1Sequence.getInstance(var0)) : null;
      }
   }

   private CrlIdentifier(ASN1Sequence var1) {
      if (var1.size() >= 2 && var1.size() <= 3) {
         this.crlIssuer = X500Name.getInstance(var1.getObjectAt(0));
         this.crlIssuedTime = ASN1UTCTime.getInstance(var1.getObjectAt(1));
         if (var1.size() > 2) {
            this.crlNumber = ASN1Integer.getInstance(var1.getObjectAt(2));
         }
      } else {
         throw new IllegalArgumentException();
      }
   }

   public CrlIdentifier(X500Name var1, ASN1UTCTime var2) {
      this(var1, var2, null);
   }

   public CrlIdentifier(X500Name var1, ASN1UTCTime var2, BigInteger var3) {
      this.crlIssuer = var1;
      this.crlIssuedTime = var2;
      if (null != var3) {
         this.crlNumber = new ASN1Integer(var3);
      }
   }

   public X500Name getCrlIssuer() {
      return this.crlIssuer;
   }

   public ASN1UTCTime getCrlIssuedTime() {
      return this.crlIssuedTime;
   }

   public BigInteger getCrlNumber() {
      return null == this.crlNumber ? null : this.crlNumber.getValue();
   }

   @Override
   public ASN1Primitive toASN1Primitive() {
      ASN1EncodableVector var1 = new ASN1EncodableVector(3);
      var1.add(this.crlIssuer.toASN1Primitive());
      var1.add(this.crlIssuedTime);
      if (null != this.crlNumber) {
         var1.add(this.crlNumber);
      }

      return new DERSequence(var1);
   }
}
