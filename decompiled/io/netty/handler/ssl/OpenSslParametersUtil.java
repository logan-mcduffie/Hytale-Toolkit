package io.netty.handler.ssl;

import io.netty.util.internal.PlatformDependent;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.invoke.MethodHandles.Lookup;
import java.security.AccessController;
import java.security.PrivilegedAction;
import javax.net.ssl.SSLParameters;

final class OpenSslParametersUtil {
   private static final MethodHandle GET_NAMED_GROUPS;
   private static final MethodHandle SET_NAMED_GROUPS;

   private static MethodHandle obtainHandle(Lookup lookup, String methodName, MethodType type) {
      return AccessController.doPrivileged((PrivilegedAction<MethodHandle>)(() -> {
         try {
            return lookup.findVirtual(SSLParameters.class, methodName, type);
         } catch (SecurityException | NoSuchMethodException | IllegalAccessException | UnsupportedOperationException var4) {
            return null;
         }
      }));
   }

   static String[] getNamesGroups(SSLParameters parameters) {
      if (GET_NAMED_GROUPS == null) {
         return null;
      } else {
         try {
            return (String[])GET_NAMED_GROUPS.invoke((SSLParameters)parameters);
         } catch (Throwable var2) {
            return null;
         }
      }
   }

   static void setNamesGroups(SSLParameters parameters, String[] names) {
      if (SET_NAMED_GROUPS != null) {
         try {
            SET_NAMED_GROUPS.invoke((SSLParameters)parameters, (String[])names);
         } catch (Throwable var3) {
         }
      }
   }

   private OpenSslParametersUtil() {
   }

   static {
      MethodHandle getNamedGroups = null;
      MethodHandle setNamedGroups = null;
      if (PlatformDependent.javaVersion() >= 20) {
         Lookup lookup = MethodHandles.lookup();
         getNamedGroups = obtainHandle(lookup, "getNamedGroups", MethodType.methodType(String[].class));
         setNamedGroups = obtainHandle(lookup, "setNamedGroups", MethodType.methodType(void.class, String[].class));
      }

      GET_NAMED_GROUPS = getNamedGroups;
      SET_NAMED_GROUPS = setNamedGroups;
   }
}
