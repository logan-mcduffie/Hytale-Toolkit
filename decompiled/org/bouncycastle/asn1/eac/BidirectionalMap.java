package org.bouncycastle.asn1.eac;

import java.util.Hashtable;

public class BidirectionalMap extends Hashtable {
   private static final long serialVersionUID = -7457289971962812909L;
   Hashtable reverseMap = new Hashtable();

   public Object getReverse(Object var1) {
      return this.reverseMap.get(var1);
   }

   @Override
   public Object put(Object var1, Object var2) {
      this.reverseMap.put(var2, var1);
      return super.put(var1, var2);
   }
}
