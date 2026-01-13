package org.bouncycastle.oer.its.etsi102941;

import org.bouncycastle.asn1.ASN1Choice;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1TaggedObject;
import org.bouncycastle.asn1.DERTaggedObject;
import org.bouncycastle.oer.its.ieee1609dot2.basetypes.HashedId8;

public class CtlDelete extends ASN1Object implements ASN1Choice {
   public static final int cert = 0;
   public static final int dc = 1;
   private final int choice;
   private final ASN1Encodable ctlDelete;

   public static CtlDelete cert(HashedId8 var0) {
      return new CtlDelete(0, var0);
   }

   public static CtlDelete dc(DcDelete var0) {
      return new CtlDelete(1, var0);
   }

   public CtlDelete(int var1, ASN1Encodable var2) {
      this.choice = var1;
      switch (var1) {
         case 0:
            this.ctlDelete = HashedId8.getInstance(var2);
            return;
         case 1:
            this.ctlDelete = DcDelete.getInstance(var2);
            return;
         default:
            throw new IllegalArgumentException("invalid choice value " + var1);
      }
   }

   private CtlDelete(ASN1TaggedObject var1) {
      this(var1.getTagNo(), var1.getExplicitBaseObject());
   }

   public static CtlDelete getInstance(Object var0) {
      if (var0 instanceof CtlDelete) {
         return (CtlDelete)var0;
      } else {
         return var0 != null ? new CtlDelete(ASN1TaggedObject.getInstance(var0, 128)) : null;
      }
   }

   public int getChoice() {
      return this.choice;
   }

   public ASN1Encodable getCtlDelete() {
      return this.ctlDelete;
   }

   @Override
   public ASN1Primitive toASN1Primitive() {
      return new DERTaggedObject(this.choice, this.ctlDelete);
   }
}
