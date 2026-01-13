package org.bouncycastle.asn1.x509;

import java.util.Enumeration;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.DERSequence;

public class Targets extends ASN1Object {
   private ASN1Sequence targets;

   public static Targets getInstance(Object var0) {
      if (var0 instanceof Targets) {
         return (Targets)var0;
      } else {
         return var0 != null ? new Targets(ASN1Sequence.getInstance(var0)) : null;
      }
   }

   private Targets(ASN1Sequence var1) {
      this.targets = var1;
   }

   public Targets(Target[] var1) {
      this.targets = new DERSequence(var1);
   }

   public Target[] getTargets() {
      Target[] var1 = new Target[this.targets.size()];
      int var2 = 0;
      Enumeration var3 = this.targets.getObjects();

      while (var3.hasMoreElements()) {
         var1[var2++] = Target.getInstance(var3.nextElement());
      }

      return var1;
   }

   @Override
   public ASN1Primitive toASN1Primitive() {
      return this.targets;
   }
}
