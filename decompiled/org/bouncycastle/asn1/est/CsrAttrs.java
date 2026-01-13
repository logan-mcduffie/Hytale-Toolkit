package org.bouncycastle.asn1.est;

import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.ASN1TaggedObject;
import org.bouncycastle.asn1.DERSequence;

public class CsrAttrs extends ASN1Object {
   private final AttrOrOID[] attrOrOIDs;

   public static CsrAttrs getInstance(Object var0) {
      if (var0 instanceof CsrAttrs) {
         return (CsrAttrs)var0;
      } else {
         return var0 != null ? new CsrAttrs(ASN1Sequence.getInstance(var0)) : null;
      }
   }

   public static CsrAttrs getInstance(ASN1TaggedObject var0, boolean var1) {
      return getInstance(ASN1Sequence.getInstance(var0, var1));
   }

   public CsrAttrs(AttrOrOID var1) {
      this.attrOrOIDs = new AttrOrOID[]{var1};
   }

   public CsrAttrs(AttrOrOID[] var1) {
      this.attrOrOIDs = Utils.clone(var1);
   }

   private CsrAttrs(ASN1Sequence var1) {
      this.attrOrOIDs = new AttrOrOID[var1.size()];

      for (int var2 = 0; var2 != var1.size(); var2++) {
         this.attrOrOIDs[var2] = AttrOrOID.getInstance(var1.getObjectAt(var2));
      }
   }

   public AttrOrOID[] getAttrOrOIDs() {
      return Utils.clone(this.attrOrOIDs);
   }

   public int size() {
      return this.attrOrOIDs.length;
   }

   @Override
   public ASN1Primitive toASN1Primitive() {
      return new DERSequence(this.attrOrOIDs);
   }
}
