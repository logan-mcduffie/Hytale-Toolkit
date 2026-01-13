package org.bouncycastle.crypto;

import java.security.Permission;
import java.util.HashSet;
import java.util.Set;

public class CryptoServicesPermission extends Permission {
   public static final String GLOBAL_CONFIG = "globalConfig";
   public static final String THREAD_LOCAL_CONFIG = "threadLocalConfig";
   public static final String DEFAULT_RANDOM = "defaultRandomConfig";
   public static final String CONSTRAINTS = "constraints";
   private final Set<String> actions = new HashSet<>();

   public CryptoServicesPermission(String var1) {
      super(var1);
      this.actions.add(var1);
   }

   @Override
   public boolean implies(Permission var1) {
      if (var1 instanceof CryptoServicesPermission) {
         CryptoServicesPermission var2 = (CryptoServicesPermission)var1;
         if (this.getName().equals(var2.getName())) {
            return true;
         }

         if (this.actions.containsAll(var2.actions)) {
            return true;
         }
      }

      return false;
   }

   @Override
   public boolean equals(Object var1) {
      if (var1 instanceof CryptoServicesPermission) {
         CryptoServicesPermission var2 = (CryptoServicesPermission)var1;
         if (this.actions.equals(var2.actions)) {
            return true;
         }
      }

      return false;
   }

   @Override
   public int hashCode() {
      return this.actions.hashCode();
   }

   @Override
   public String getActions() {
      return this.actions.toString();
   }
}
