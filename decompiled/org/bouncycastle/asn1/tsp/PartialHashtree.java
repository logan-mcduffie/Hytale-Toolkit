package org.bouncycastle.asn1.tsp;

import java.util.Enumeration;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.DEROctetString;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.util.Arrays;

public class PartialHashtree extends ASN1Object {
   private final ASN1Sequence values;

   public static PartialHashtree getInstance(Object var0) {
      if (var0 instanceof PartialHashtree) {
         return (PartialHashtree)var0;
      } else {
         return var0 != null ? new PartialHashtree(ASN1Sequence.getInstance(var0)) : null;
      }
   }

   private PartialHashtree(ASN1Sequence var1) {
      for (int var2 = 0; var2 != var1.size(); var2++) {
         if (!(var1.getObjectAt(var2) instanceof ASN1OctetString)) {
            throw new IllegalArgumentException("unknown object in constructor: " + var1.getObjectAt(var2).getClass().getName());
         }
      }

      this.values = var1;
   }

   public PartialHashtree(byte[] var1) {
      this(new byte[][]{var1});
   }

   public PartialHashtree(byte[][] var1) {
      ASN1EncodableVector var2 = new ASN1EncodableVector(var1.length);

      for (int var3 = 0; var3 != var1.length; var3++) {
         var2.add(new DEROctetString(Arrays.clone(var1[var3])));
      }

      this.values = new DERSequence(var2);
   }

   public int getValueCount() {
      return this.values.size();
   }

   public byte[][] getValues() {
      byte[][] var1 = new byte[this.values.size()][];

      for (int var2 = 0; var2 != var1.length; var2++) {
         var1[var2] = Arrays.clone(ASN1OctetString.getInstance(this.values.getObjectAt(var2)).getOctets());
      }

      return var1;
   }

   public boolean containsHash(byte[] var1) {
      Enumeration var2 = this.values.getObjects();

      while (var2.hasMoreElements()) {
         byte[] var3 = ASN1OctetString.getInstance(var2.nextElement()).getOctets();
         if (Arrays.constantTimeAreEqual(var1, var3)) {
            return true;
         }
      }

      return false;
   }

   @Override
   public ASN1Primitive toASN1Primitive() {
      return this.values;
   }
}
