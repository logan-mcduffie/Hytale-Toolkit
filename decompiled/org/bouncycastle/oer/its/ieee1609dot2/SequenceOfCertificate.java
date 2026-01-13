package org.bouncycastle.oer.its.ieee1609dot2;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.oer.its.ItsUtils;

public class SequenceOfCertificate extends ASN1Object {
   private final List<Certificate> certificates;

   public SequenceOfCertificate(List<Certificate> var1) {
      this.certificates = Collections.unmodifiableList(var1);
   }

   private SequenceOfCertificate(ASN1Sequence var1) {
      Iterator var2 = var1.iterator();
      ArrayList var3 = new ArrayList();

      while (var2.hasNext()) {
         var3.add(Certificate.getInstance(var2.next()));
      }

      this.certificates = Collections.unmodifiableList(var3);
   }

   public static SequenceOfCertificate getInstance(Object var0) {
      if (var0 instanceof SequenceOfCertificate) {
         return (SequenceOfCertificate)var0;
      } else {
         return var0 != null ? new SequenceOfCertificate(ASN1Sequence.getInstance(var0)) : null;
      }
   }

   public static SequenceOfCertificate.Builder builder() {
      return new SequenceOfCertificate.Builder();
   }

   @Override
   public ASN1Primitive toASN1Primitive() {
      return ItsUtils.toSequence(this.certificates);
   }

   public List<Certificate> getCertificates() {
      return this.certificates;
   }

   public static class Builder {
      List<Certificate> certificates = new ArrayList<>();

      public SequenceOfCertificate.Builder add(Certificate... var1) {
         this.certificates.addAll(Arrays.asList(var1));
         return this;
      }

      public SequenceOfCertificate build() {
         return new SequenceOfCertificate(this.certificates);
      }
   }
}
