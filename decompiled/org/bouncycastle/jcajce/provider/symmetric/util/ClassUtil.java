package org.bouncycastle.jcajce.provider.symmetric.util;

import java.security.AccessController;
import java.security.PrivilegedAction;

public class ClassUtil {
   public static Class loadClass(Class var0, final String var1) {
      try {
         ClassLoader var2 = var0.getClassLoader();
         return var2 != null ? var2.loadClass(var1) : AccessController.doPrivileged(new PrivilegedAction() {
            @Override
            public Object run() {
               try {
                  ClassLoader var1x = ClassLoader.getSystemClassLoader();
                  return var1x.loadClass(var1);
               } catch (Exception var2x) {
                  return null;
               }
            }
         });
      } catch (ClassNotFoundException var3) {
         return null;
      }
   }
}
