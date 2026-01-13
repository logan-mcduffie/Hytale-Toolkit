package org.bouncycastle.util;

public class Objects {
   public static boolean areEqual(Object var0, Object var1) {
      return var0 == var1 || null != var0 && null != var1 && var0.equals(var1);
   }

   public static int hashCode(Object var0) {
      return null == var0 ? 0 : var0.hashCode();
   }
}
