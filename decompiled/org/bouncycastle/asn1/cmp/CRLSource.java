package org.bouncycastle.asn1.cmp;

import org.bouncycastle.asn1.ASN1Choice;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1TaggedObject;
import org.bouncycastle.asn1.ASN1Util;
import org.bouncycastle.asn1.DERTaggedObject;
import org.bouncycastle.asn1.x509.DistributionPointName;
import org.bouncycastle.asn1.x509.GeneralNames;

public class CRLSource extends ASN1Object implements ASN1Choice {
   private final DistributionPointName dpn;
   private final GeneralNames issuer;

   private CRLSource(ASN1TaggedObject var1) {
      if (var1.hasContextTag(0)) {
         this.dpn = DistributionPointName.getInstance(var1, true);
         this.issuer = null;
      } else {
         if (!var1.hasContextTag(1)) {
            throw new IllegalArgumentException("unknown tag " + ASN1Util.getTagText(var1));
         }

         this.dpn = null;
         this.issuer = GeneralNames.getInstance(var1, true);
      }
   }

   public CRLSource(DistributionPointName var1, GeneralNames var2) {
      if (var1 == null == (var2 == null)) {
         throw new IllegalArgumentException("either dpn or issuer must be set");
      } else {
         this.dpn = var1;
         this.issuer = var2;
      }
   }

   public static CRLSource getInstance(Object var0) {
      if (var0 instanceof CRLSource) {
         return (CRLSource)var0;
      } else {
         return var0 != null ? new CRLSource(ASN1TaggedObject.getInstance(var0)) : null;
      }
   }

   public DistributionPointName getDpn() {
      return this.dpn;
   }

   public GeneralNames getIssuer() {
      return this.issuer;
   }

   @Override
   public ASN1Primitive toASN1Primitive() {
      return this.dpn != null ? new DERTaggedObject(true, 0, this.dpn) : new DERTaggedObject(true, 1, this.issuer);
   }
}
