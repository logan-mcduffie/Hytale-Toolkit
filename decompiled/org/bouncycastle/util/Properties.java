package org.bouncycastle.util;

import java.math.BigInteger;
import java.security.AccessControlException;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.security.Security;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;

public class Properties {
   public static final String EMULATE_ORACLE = "org.bouncycastle.emulate.oracle";
   private static final ThreadLocal threadProperties = new ThreadLocal();

   private Properties() {
   }

   public static boolean isOverrideSet(String var0) {
      try {
         return isSetTrue(getPropertyValue(var0));
      } catch (AccessControlException var2) {
         return false;
      }
   }

   public static boolean isOverrideSet(String var0, boolean var1) {
      try {
         String var2 = getPropertyValue(var0);
         return var2 == null ? var1 : isSetTrue(var2);
      } catch (AccessControlException var3) {
         return false;
      }
   }

   public static boolean isOverrideSetTo(String var0, boolean var1) {
      try {
         String var2 = getPropertyValue(var0);
         return var1 ? isSetTrue(var2) : isSetFalse(var2);
      } catch (AccessControlException var3) {
         return false;
      }
   }

   public static boolean setThreadOverride(String var0, boolean var1) {
      boolean var2 = isOverrideSet(var0);
      Object var3 = (Map)threadProperties.get();
      if (var3 == null) {
         var3 = new HashMap();
         threadProperties.set(var3);
      }

      var3.put(var0, var1 ? "true" : "false");
      return var2;
   }

   public static boolean removeThreadOverride(String var0) {
      Map var1 = (Map)threadProperties.get();
      if (var1 != null) {
         String var2 = (String)var1.remove(var0);
         if (var2 != null) {
            if (var1.isEmpty()) {
               threadProperties.remove();
            }

            return "true".equals(Strings.toLowerCase(var2));
         }
      }

      return false;
   }

   public static int asInteger(String var0, int var1) {
      String var2 = getPropertyValue(var0);
      return var2 != null ? Integer.parseInt(var2) : var1;
   }

   public static BigInteger asBigInteger(String var0) {
      String var1 = getPropertyValue(var0);
      return var1 != null ? new BigInteger(var1) : null;
   }

   public static Set<String> asKeySet(String var0) {
      HashSet var1 = new HashSet();
      String var2 = getPropertyValue(var0);
      if (var2 != null) {
         StringTokenizer var3 = new StringTokenizer(var2, ",");

         while (var3.hasMoreElements()) {
            var1.add(Strings.toLowerCase(var3.nextToken()).trim());
         }
      }

      return Collections.unmodifiableSet(var1);
   }

   public static String getPropertyValue(final String var0) {
      String var1 = AccessController.doPrivileged(new PrivilegedAction() {
         @Override
         public Object run() {
            return Security.getProperty(var0);
         }
      });
      if (var1 != null) {
         return var1;
      } else {
         Map var2 = (Map)threadProperties.get();
         if (var2 != null) {
            String var3 = (String)var2.get(var0);
            if (var3 != null) {
               return var3;
            }
         }

         return AccessController.doPrivileged(new PrivilegedAction() {
            @Override
            public Object run() {
               return System.getProperty(var0);
            }
         });
      }
   }

   public static String getPropertyValue(String var0, String var1) {
      String var2 = getPropertyValue(var0);
      return var2 == null ? var1 : var2;
   }

   private static boolean isSetFalse(String var0) {
      return var0 != null && var0.length() == 5
         ? (var0.charAt(0) == 'f' || var0.charAt(0) == 'F')
            && (var0.charAt(1) == 'a' || var0.charAt(1) == 'A')
            && (var0.charAt(2) == 'l' || var0.charAt(2) == 'L')
            && (var0.charAt(3) == 's' || var0.charAt(3) == 'S')
            && (var0.charAt(4) == 'e' || var0.charAt(4) == 'E')
         : false;
   }

   private static boolean isSetTrue(String var0) {
      return var0 != null && var0.length() == 4
         ? (var0.charAt(0) == 't' || var0.charAt(0) == 'T')
            && (var0.charAt(1) == 'r' || var0.charAt(1) == 'R')
            && (var0.charAt(2) == 'u' || var0.charAt(2) == 'U')
            && (var0.charAt(3) == 'e' || var0.charAt(3) == 'E')
         : false;
   }
}
