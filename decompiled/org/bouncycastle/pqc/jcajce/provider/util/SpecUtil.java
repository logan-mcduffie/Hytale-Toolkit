package org.bouncycastle.pqc.jcajce.provider.util;

import java.lang.reflect.Method;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.security.spec.AlgorithmParameterSpec;

@Deprecated
public class SpecUtil {
   private static Class[] NO_PARAMS = new Class[0];
   private static Object[] NO_ARGS = new Object[0];

   public static String getNameFrom(final AlgorithmParameterSpec var0) {
      return AccessController.doPrivileged(new PrivilegedAction() {
         @Override
         public Object run() {
            try {
               Method var1 = var0.getClass().getMethod("getName", SpecUtil.NO_PARAMS);
               return var1.invoke(var0, SpecUtil.NO_ARGS);
            } catch (Exception var2) {
               return null;
            }
         }
      });
   }
}
