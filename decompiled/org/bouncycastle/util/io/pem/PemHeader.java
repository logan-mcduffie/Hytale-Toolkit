package org.bouncycastle.util.io.pem;

public class PemHeader {
   private String name;
   private String value;

   public PemHeader(String var1, String var2) {
      this.name = var1;
      this.value = var2;
   }

   public String getName() {
      return this.name;
   }

   public String getValue() {
      return this.value;
   }

   @Override
   public int hashCode() {
      return this.getHashCode(this.name) + 31 * this.getHashCode(this.value);
   }

   @Override
   public boolean equals(Object var1) {
      if (!(var1 instanceof PemHeader)) {
         return false;
      } else {
         PemHeader var2 = (PemHeader)var1;
         return var2 == this || this.isEqual(this.name, var2.name) && this.isEqual(this.value, var2.value);
      }
   }

   private int getHashCode(String var1) {
      return var1 == null ? 1 : var1.hashCode();
   }

   private boolean isEqual(String var1, String var2) {
      if (var1 == var2) {
         return true;
      } else {
         return var1 != null && var2 != null ? var1.equals(var2) : false;
      }
   }
}
