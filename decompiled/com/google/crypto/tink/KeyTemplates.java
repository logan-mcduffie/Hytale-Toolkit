package com.google.crypto.tink;

import com.google.crypto.tink.internal.MutableParametersRegistry;
import java.security.GeneralSecurityException;

public final class KeyTemplates {
   public static KeyTemplate get(String name) throws GeneralSecurityException {
      Parameters result = MutableParametersRegistry.globalInstance().get(name);
      return KeyTemplate.createFrom(result);
   }

   private KeyTemplates() {
   }
}
