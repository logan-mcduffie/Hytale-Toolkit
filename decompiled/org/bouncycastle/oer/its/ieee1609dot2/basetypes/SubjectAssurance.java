package org.bouncycastle.oer.its.ieee1609dot2.basetypes;

import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.DEROctetString;

public class SubjectAssurance extends DEROctetString {
   public SubjectAssurance(byte[] var1) {
      super(var1);
      if (var1.length != 1) {
         throw new IllegalArgumentException("length is not 1");
      }
   }

   private SubjectAssurance(ASN1OctetString var1) {
      this(var1.getOctets());
   }

   public static SubjectAssurance getInstance(Object var0) {
      if (var0 instanceof SubjectAssurance) {
         return (SubjectAssurance)var0;
      } else {
         return var0 != null ? new SubjectAssurance(DEROctetString.getInstance(var0)) : null;
      }
   }
}
