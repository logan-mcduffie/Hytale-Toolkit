package org.bouncycastle.oer.its.ieee1609dot2.basetypes;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.DEROctetString;
import org.bouncycastle.asn1.DERSequence;

public class SequenceOfOctetString extends ASN1Object {
   private final List<ASN1OctetString> octetStrings;

   public SequenceOfOctetString(List<ASN1OctetString> var1) {
      this.octetStrings = Collections.unmodifiableList(var1);
   }

   private SequenceOfOctetString(ASN1Sequence var1) {
      ArrayList var2 = new ArrayList();
      Iterator var3 = var1.iterator();

      while (var3.hasNext()) {
         var2.add(DEROctetString.getInstance(var3.next()));
      }

      this.octetStrings = Collections.unmodifiableList(var2);
   }

   public static SequenceOfOctetString getInstance(Object var0) {
      if (var0 instanceof SequenceOfOctetString) {
         return (SequenceOfOctetString)var0;
      } else {
         return var0 != null ? new SequenceOfOctetString(ASN1Sequence.getInstance(var0)) : null;
      }
   }

   public List<ASN1OctetString> getOctetStrings() {
      return this.octetStrings;
   }

   @Override
   public ASN1Primitive toASN1Primitive() {
      ASN1EncodableVector var1 = new ASN1EncodableVector();

      for (int var2 = 0; var2 != this.octetStrings.size(); var2++) {
         var1.add(this.octetStrings.get(var2));
      }

      return new DERSequence(var1);
   }
}
