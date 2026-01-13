package org.bouncycastle.asn1.cmc;

import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.x509.Extension;

class Utils {
   static BodyPartID[] toBodyPartIDArray(ASN1Sequence var0) {
      BodyPartID[] var1 = new BodyPartID[var0.size()];

      for (int var2 = 0; var2 != var0.size(); var2++) {
         var1[var2] = BodyPartID.getInstance(var0.getObjectAt(var2));
      }

      return var1;
   }

   static BodyPartID[] clone(BodyPartID[] var0) {
      BodyPartID[] var1 = new BodyPartID[var0.length];
      System.arraycopy(var0, 0, var1, 0, var0.length);
      return var1;
   }

   static Extension[] clone(Extension[] var0) {
      Extension[] var1 = new Extension[var0.length];
      System.arraycopy(var0, 0, var1, 0, var0.length);
      return var1;
   }
}
