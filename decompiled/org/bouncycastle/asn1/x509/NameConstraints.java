package org.bouncycastle.asn1.x509;

import java.util.Enumeration;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.ASN1TaggedObject;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.DERTaggedObject;

public class NameConstraints extends ASN1Object {
   private GeneralSubtree[] permitted;
   private GeneralSubtree[] excluded;

   public static NameConstraints getInstance(Object var0) {
      if (var0 instanceof NameConstraints) {
         return (NameConstraints)var0;
      } else {
         return var0 != null ? new NameConstraints(ASN1Sequence.getInstance(var0)) : null;
      }
   }

   private NameConstraints(ASN1Sequence var1) {
      Enumeration var2 = var1.getObjects();

      while (var2.hasMoreElements()) {
         ASN1TaggedObject var3 = ASN1TaggedObject.getInstance(var2.nextElement());
         switch (var3.getTagNo()) {
            case 0:
               this.permitted = this.createArray(ASN1Sequence.getInstance(var3, false));
               break;
            case 1:
               this.excluded = this.createArray(ASN1Sequence.getInstance(var3, false));
               break;
            default:
               throw new IllegalArgumentException("Unknown tag encountered: " + var3.getTagNo());
         }
      }
   }

   public NameConstraints(GeneralSubtree[] var1, GeneralSubtree[] var2) {
      this.permitted = cloneSubtree(var1);
      this.excluded = cloneSubtree(var2);
   }

   private GeneralSubtree[] createArray(ASN1Sequence var1) {
      GeneralSubtree[] var2 = new GeneralSubtree[var1.size()];

      for (int var3 = 0; var3 != var2.length; var3++) {
         var2[var3] = GeneralSubtree.getInstance(var1.getObjectAt(var3));
      }

      return var2;
   }

   public GeneralSubtree[] getPermittedSubtrees() {
      return cloneSubtree(this.permitted);
   }

   public GeneralSubtree[] getExcludedSubtrees() {
      return cloneSubtree(this.excluded);
   }

   @Override
   public ASN1Primitive toASN1Primitive() {
      ASN1EncodableVector var1 = new ASN1EncodableVector(2);
      if (this.permitted != null) {
         var1.add(new DERTaggedObject(false, 0, new DERSequence(this.permitted)));
      }

      if (this.excluded != null) {
         var1.add(new DERTaggedObject(false, 1, new DERSequence(this.excluded)));
      }

      return new DERSequence(var1);
   }

   private static GeneralSubtree[] cloneSubtree(GeneralSubtree[] var0) {
      if (var0 != null) {
         GeneralSubtree[] var1 = new GeneralSubtree[var0.length];
         System.arraycopy(var0, 0, var1, 0, var1.length);
         return var1;
      } else {
         return null;
      }
   }
}
