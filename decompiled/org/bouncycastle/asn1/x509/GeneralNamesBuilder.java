package org.bouncycastle.asn1.x509;

import java.util.Vector;

public class GeneralNamesBuilder {
   private Vector names = new Vector();

   public GeneralNamesBuilder addNames(GeneralNames var1) {
      GeneralName[] var2 = var1.getNames();

      for (int var3 = 0; var3 != var2.length; var3++) {
         this.names.addElement(var2[var3]);
      }

      return this;
   }

   public GeneralNamesBuilder addName(GeneralName var1) {
      this.names.addElement(var1);
      return this;
   }

   public GeneralNames build() {
      GeneralName[] var1 = new GeneralName[this.names.size()];

      for (int var2 = 0; var2 != var1.length; var2++) {
         var1[var2] = (GeneralName)this.names.elementAt(var2);
      }

      return new GeneralNames(var1);
   }
}
