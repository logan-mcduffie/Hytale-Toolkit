package org.bouncycastle.asn1.esf;

import java.util.Enumeration;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.DERSequence;

public class CrlListID extends ASN1Object {
   private ASN1Sequence crls;

   public static CrlListID getInstance(Object var0) {
      if (var0 instanceof CrlListID) {
         return (CrlListID)var0;
      } else {
         return var0 != null ? new CrlListID(ASN1Sequence.getInstance(var0)) : null;
      }
   }

   private CrlListID(ASN1Sequence var1) {
      this.crls = (ASN1Sequence)var1.getObjectAt(0);
      Enumeration var2 = this.crls.getObjects();

      while (var2.hasMoreElements()) {
         CrlValidatedID.getInstance(var2.nextElement());
      }
   }

   public CrlListID(CrlValidatedID[] var1) {
      this.crls = new DERSequence(var1);
   }

   public CrlValidatedID[] getCrls() {
      CrlValidatedID[] var1 = new CrlValidatedID[this.crls.size()];

      for (int var2 = 0; var2 < var1.length; var2++) {
         var1[var2] = CrlValidatedID.getInstance(this.crls.getObjectAt(var2));
      }

      return var1;
   }

   @Override
   public ASN1Primitive toASN1Primitive() {
      return new DERSequence(this.crls);
   }
}
