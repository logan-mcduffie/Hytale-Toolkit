package com.nimbusds.jose.jwk;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

class KeyUseAndOpsConsistency {
   static final Map<KeyUse, Set<KeyOperation>> MAP;

   static boolean areConsistent(KeyUse use, Set<KeyOperation> ops) {
      return use != null && ops != null ? !MAP.containsKey(use) || MAP.get(use).containsAll(ops) : true;
   }

   static {
      Map<KeyUse, Set<KeyOperation>> map = new HashMap<>();
      map.put(KeyUse.SIGNATURE, new HashSet<>(Arrays.asList(KeyOperation.SIGN, KeyOperation.VERIFY)));
      map.put(KeyUse.ENCRYPTION, new HashSet<>(Arrays.asList(KeyOperation.ENCRYPT, KeyOperation.DECRYPT, KeyOperation.WRAP_KEY, KeyOperation.UNWRAP_KEY)));
      MAP = Collections.unmodifiableMap(map);
   }
}
