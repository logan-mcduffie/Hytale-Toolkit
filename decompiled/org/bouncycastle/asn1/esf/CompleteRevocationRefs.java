package org.bouncycastle.asn1.esf;

import java.util.Enumeration;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.DERSequence;

public class CompleteRevocationRefs extends ASN1Object {
   private ASN1Sequence crlOcspRefs;

   public static CompleteRevocationRefs getInstance(Object var0) {
      if (var0 instanceof CompleteRevocationRefs) {
         return (CompleteRevocationRefs)var0;
      } else {
         return var0 != null ? new CompleteRevocationRefs(ASN1Sequence.getInstance(var0)) : null;
      }
   }

   private CompleteRevocationRefs(ASN1Sequence var1) {
      Enumeration var2 = var1.getObjects();

      while (var2.hasMoreElements()) {
         CrlOcspRef.getInstance(var2.nextElement());
      }

      this.crlOcspRefs = var1;
   }

   public CompleteRevocationRefs(CrlOcspRef[] var1) {
      this.crlOcspRefs = new DERSequence(var1);
   }

   public CrlOcspRef[] getCrlOcspRefs() {
      CrlOcspRef[] var1 = new CrlOcspRef[this.crlOcspRefs.size()];

      for (int var2 = 0; var2 < var1.length; var2++) {
         var1[var2] = CrlOcspRef.getInstance(this.crlOcspRefs.getObjectAt(var2));
      }

      return var1;
   }

   @Override
   public ASN1Primitive toASN1Primitive() {
      return this.crlOcspRefs;
   }
}
