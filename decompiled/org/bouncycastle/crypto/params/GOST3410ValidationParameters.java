package org.bouncycastle.crypto.params;

public class GOST3410ValidationParameters {
   private int x0;
   private int c;
   private long x0L;
   private long cL;

   public GOST3410ValidationParameters(int var1, int var2) {
      this.x0 = var1;
      this.c = var2;
   }

   public GOST3410ValidationParameters(long var1, long var3) {
      this.x0L = var1;
      this.cL = var3;
   }

   public int getC() {
      return this.c;
   }

   public int getX0() {
      return this.x0;
   }

   public long getCL() {
      return this.cL;
   }

   public long getX0L() {
      return this.x0L;
   }

   @Override
   public boolean equals(Object var1) {
      if (!(var1 instanceof GOST3410ValidationParameters)) {
         return false;
      } else {
         GOST3410ValidationParameters var2 = (GOST3410ValidationParameters)var1;
         if (var2.c != this.c) {
            return false;
         } else if (var2.x0 != this.x0) {
            return false;
         } else {
            return var2.cL != this.cL ? false : var2.x0L == this.x0L;
         }
      }
   }

   @Override
   public int hashCode() {
      return this.x0 ^ this.c ^ (int)this.x0L ^ (int)(this.x0L >> 32) ^ (int)this.cL ^ (int)(this.cL >> 32);
   }
}
