package org.bouncycastle.crypto.constraints;

import java.util.Set;

class Utils {
   static void addAliases(Set<String> var0) {
      if (var0.contains("RC4")) {
         var0.add("ARC4");
      } else if (var0.contains("ARC4")) {
         var0.add("RC4");
      }
   }
}
