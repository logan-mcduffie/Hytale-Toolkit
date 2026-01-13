package org.bouncycastle.asn1.bc;

import java.util.Iterator;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.util.Arrays;
import org.bouncycastle.util.Iterable;

public class ObjectDataSequence extends ASN1Object implements Iterable<ASN1Encodable> {
   private final ASN1Encodable[] dataSequence;

   public ObjectDataSequence(ObjectData[] var1) {
      this.dataSequence = new ASN1Encodable[var1.length];
      System.arraycopy(var1, 0, this.dataSequence, 0, var1.length);
   }

   private ObjectDataSequence(ASN1Sequence var1) {
      this.dataSequence = new ASN1Encodable[var1.size()];

      for (int var2 = 0; var2 != this.dataSequence.length; var2++) {
         this.dataSequence[var2] = ObjectData.getInstance(var1.getObjectAt(var2));
      }
   }

   public static ObjectDataSequence getInstance(Object var0) {
      if (var0 instanceof ObjectDataSequence) {
         return (ObjectDataSequence)var0;
      } else {
         return var0 != null ? new ObjectDataSequence(ASN1Sequence.getInstance(var0)) : null;
      }
   }

   @Override
   public ASN1Primitive toASN1Primitive() {
      return new DERSequence(this.dataSequence);
   }

   @Override
   public Iterator<ASN1Encodable> iterator() {
      return new Arrays.Iterator<>(this.dataSequence);
   }
}
