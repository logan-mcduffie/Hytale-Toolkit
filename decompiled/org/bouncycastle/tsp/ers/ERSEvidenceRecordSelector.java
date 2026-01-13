package org.bouncycastle.tsp.ers;

import java.util.Date;
import org.bouncycastle.util.Selector;

public class ERSEvidenceRecordSelector implements Selector<ERSEvidenceRecord> {
   private final ERSData data;
   private final Date date;

   public ERSEvidenceRecordSelector(ERSData var1) {
      this(var1, new Date());
   }

   public ERSEvidenceRecordSelector(ERSData var1, Date var2) {
      this.data = var1;
      this.date = new Date(var2.getTime());
   }

   public ERSData getData() {
      return this.data;
   }

   public boolean match(ERSEvidenceRecord var1) {
      try {
         if (var1.isContaining(this.data, this.date)) {
            try {
               var1.validatePresent(this.data, this.date);
               return true;
            } catch (Exception var3) {
               return false;
            }
         } else {
            return false;
         }
      } catch (Exception var4) {
         return false;
      }
   }

   @Override
   public Object clone() {
      return this;
   }
}
