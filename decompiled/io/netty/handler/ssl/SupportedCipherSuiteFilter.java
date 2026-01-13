package io.netty.handler.ssl;

import io.netty.util.internal.EmptyArrays;
import io.netty.util.internal.ObjectUtil;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public final class SupportedCipherSuiteFilter implements CipherSuiteFilter {
   public static final SupportedCipherSuiteFilter INSTANCE = new SupportedCipherSuiteFilter();

   private SupportedCipherSuiteFilter() {
   }

   @Override
   public String[] filterCipherSuites(Iterable<String> ciphers, List<String> defaultCiphers, Set<String> supportedCiphers) {
      ObjectUtil.checkNotNull(defaultCiphers, "defaultCiphers");
      ObjectUtil.checkNotNull(supportedCiphers, "supportedCiphers");
      List<String> newCiphers;
      if (ciphers == null) {
         newCiphers = new ArrayList<>(defaultCiphers.size());
         ciphers = defaultCiphers;
      } else {
         newCiphers = new ArrayList<>(supportedCiphers.size());
      }

      for (String c : ciphers) {
         if (c == null) {
            break;
         }

         if (supportedCiphers.contains(c)) {
            newCiphers.add(c);
         }
      }

      return newCiphers.toArray(EmptyArrays.EMPTY_STRINGS);
   }
}
