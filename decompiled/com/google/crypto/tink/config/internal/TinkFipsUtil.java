package com.google.crypto.tink.config.internal;

import com.google.crypto.tink.internal.Random;
import java.lang.reflect.Method;
import java.security.GeneralSecurityException;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Logger;

public final class TinkFipsUtil {
   private static final Logger logger = Logger.getLogger(TinkFipsUtil.class.getName());
   private static final AtomicBoolean isRestrictedToFips = new AtomicBoolean(false);

   public static void setFipsRestricted() throws GeneralSecurityException {
      if (!checkConscryptIsAvailableAndUsesFipsBoringSsl()) {
         throw new GeneralSecurityException("Conscrypt is not available or does not support checking for FIPS build.");
      } else {
         Random.validateUsesConscrypt();
         isRestrictedToFips.set(true);
      }
   }

   public static void unsetFipsRestricted() {
      isRestrictedToFips.set(false);
   }

   public static boolean useOnlyFips() {
      return TinkFipsStatus.useOnlyFips() || isRestrictedToFips.get();
   }

   public static boolean fipsModuleAvailable() {
      return checkConscryptIsAvailableAndUsesFipsBoringSsl();
   }

   static Boolean checkConscryptIsAvailableAndUsesFipsBoringSsl() {
      try {
         Class<?> cls = Class.forName("org.conscrypt.Conscrypt");
         Method isBoringSslFipsBuild = cls.getMethod("isBoringSslFIPSBuild");
         return (Boolean)isBoringSslFipsBuild.invoke(null);
      } catch (Exception var2) {
         logger.info("Conscrypt is not available or does not support checking for FIPS build.");
         return false;
      }
   }

   private TinkFipsUtil() {
   }

   public static enum AlgorithmFipsCompatibility {
      ALGORITHM_NOT_FIPS {
         @Override
         public boolean isCompatible() {
            return !TinkFipsUtil.useOnlyFips();
         }
      },
      ALGORITHM_REQUIRES_BORINGCRYPTO {
         @Override
         public boolean isCompatible() {
            return !TinkFipsUtil.useOnlyFips() || TinkFipsUtil.fipsModuleAvailable();
         }
      };

      private AlgorithmFipsCompatibility() {
      }

      public abstract boolean isCompatible();
   }
}
