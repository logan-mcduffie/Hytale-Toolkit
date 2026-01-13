package org.bouncycastle.asn1.bc;

import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.ASN1TaggedObject;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.DERTaggedObject;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.x509.DigestInfo;
import org.bouncycastle.asn1.x509.GeneralName;
import org.bouncycastle.asn1.x509.GeneralNames;

public class LinkedCertificate extends ASN1Object {
   private final DigestInfo digest;
   private final GeneralName certLocation;
   private X500Name certIssuer;
   private GeneralNames cACerts;

   public LinkedCertificate(DigestInfo var1, GeneralName var2) {
      this(var1, var2, null, null);
   }

   public LinkedCertificate(DigestInfo var1, GeneralName var2, X500Name var3, GeneralNames var4) {
      this.digest = var1;
      this.certLocation = var2;
      this.certIssuer = var3;
      this.cACerts = var4;
   }

   private LinkedCertificate(ASN1Sequence var1) {
      this.digest = DigestInfo.getInstance(var1.getObjectAt(0));
      this.certLocation = GeneralName.getInstance(var1.getObjectAt(1));
      if (var1.size() > 2) {
         for (int var2 = 2; var2 != var1.size(); var2++) {
            ASN1TaggedObject var3 = ASN1TaggedObject.getInstance(var1.getObjectAt(var2));
            switch (var3.getTagNo()) {
               case 0:
                  this.certIssuer = X500Name.getInstance(var3, true);
                  break;
               case 1:
                  this.cACerts = GeneralNames.getInstance(var3, false);
                  break;
               default:
                  throw new IllegalArgumentException("unknown tag in tagged field");
            }
         }
      }
   }

   public static LinkedCertificate getInstance(Object var0) {
      if (var0 instanceof LinkedCertificate) {
         return (LinkedCertificate)var0;
      } else {
         return var0 != null ? new LinkedCertificate(ASN1Sequence.getInstance(var0)) : null;
      }
   }

   public DigestInfo getDigest() {
      return this.digest;
   }

   public GeneralName getCertLocation() {
      return this.certLocation;
   }

   public X500Name getCertIssuer() {
      return this.certIssuer;
   }

   public GeneralNames getCACerts() {
      return this.cACerts;
   }

   @Override
   public ASN1Primitive toASN1Primitive() {
      ASN1EncodableVector var1 = new ASN1EncodableVector(4);
      var1.add(this.digest);
      var1.add(this.certLocation);
      if (this.certIssuer != null) {
         var1.add(new DERTaggedObject(true, 0, this.certIssuer));
      }

      if (this.cACerts != null) {
         var1.add(new DERTaggedObject(false, 1, this.cACerts));
      }

      return new DERSequence(var1);
   }
}
