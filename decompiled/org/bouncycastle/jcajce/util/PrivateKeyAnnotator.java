package org.bouncycastle.jcajce.util;

import java.security.PrivateKey;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class PrivateKeyAnnotator {
   public static AnnotatedPrivateKey annotate(PrivateKey var0, String var1) {
      return new AnnotatedPrivateKey(var0, var1);
   }

   public static AnnotatedPrivateKey annotate(PrivateKey var0, Map<String, Object> var1) {
      HashMap var2 = new HashMap(var1);
      return new AnnotatedPrivateKey(var0, Collections.unmodifiableMap(var2));
   }
}
