package org.bouncycastle.pqc.crypto.crystals.dilithium;

import java.security.SecureRandom;

public class DilithiumParameters {
   public static final DilithiumParameters dilithium2 = new DilithiumParameters("dilithium2", 2, false);
   public static final DilithiumParameters dilithium3 = new DilithiumParameters("dilithium3", 3, false);
   public static final DilithiumParameters dilithium5 = new DilithiumParameters("dilithium5", 5, false);
   private final int k;
   private final String name;
   @Deprecated
   private final boolean usingAES;

   private DilithiumParameters(String var1, int var2, boolean var3) {
      this.name = var1;
      this.k = var2;
      this.usingAES = var3;
   }

   DilithiumEngine getEngine(SecureRandom var1) {
      return new DilithiumEngine(this.k, var1, this.usingAES);
   }

   public String getName() {
      return this.name;
   }
}
