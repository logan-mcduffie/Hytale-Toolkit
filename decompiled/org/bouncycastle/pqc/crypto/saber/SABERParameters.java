package org.bouncycastle.pqc.crypto.saber;

import org.bouncycastle.pqc.crypto.KEMParameters;

public class SABERParameters implements KEMParameters {
   public static final SABERParameters lightsaberkem128r3 = new SABERParameters("lightsaberkem128r3", 2, 128, false, false);
   public static final SABERParameters saberkem128r3 = new SABERParameters("saberkem128r3", 3, 128, false, false);
   public static final SABERParameters firesaberkem128r3 = new SABERParameters("firesaberkem128r3", 4, 128, false, false);
   public static final SABERParameters lightsaberkem192r3 = new SABERParameters("lightsaberkem192r3", 2, 192, false, false);
   public static final SABERParameters saberkem192r3 = new SABERParameters("saberkem192r3", 3, 192, false, false);
   public static final SABERParameters firesaberkem192r3 = new SABERParameters("firesaberkem192r3", 4, 192, false, false);
   public static final SABERParameters lightsaberkem256r3 = new SABERParameters("lightsaberkem256r3", 2, 256, false, false);
   public static final SABERParameters saberkem256r3 = new SABERParameters("saberkem256r3", 3, 256, false, false);
   public static final SABERParameters firesaberkem256r3 = new SABERParameters("firesaberkem256r3", 4, 256, false, false);
   public static final SABERParameters lightsaberkem90sr3 = new SABERParameters("lightsaberkem90sr3", 2, 256, true, false);
   public static final SABERParameters saberkem90sr3 = new SABERParameters("saberkem90sr3", 3, 256, true, false);
   public static final SABERParameters firesaberkem90sr3 = new SABERParameters("firesaberkem90sr3", 4, 256, true, false);
   public static final SABERParameters ulightsaberkemr3 = new SABERParameters("ulightsaberkemr3", 2, 256, false, true);
   public static final SABERParameters usaberkemr3 = new SABERParameters("usaberkemr3", 3, 256, false, true);
   public static final SABERParameters ufiresaberkemr3 = new SABERParameters("ufiresaberkemr3", 4, 256, false, true);
   public static final SABERParameters ulightsaberkem90sr3 = new SABERParameters("ulightsaberkem90sr3", 2, 256, true, true);
   public static final SABERParameters usaberkem90sr3 = new SABERParameters("usaberkem90sr3", 3, 256, true, true);
   public static final SABERParameters ufiresaberkem90sr3 = new SABERParameters("ufiresaberkem90sr3", 4, 256, true, true);
   private final String name;
   private final int l;
   private final int defaultKeySize;
   private final SABEREngine engine;

   public SABERParameters(String var1, int var2, int var3, boolean var4, boolean var5) {
      this.name = var1;
      this.l = var2;
      this.defaultKeySize = var3;
      this.engine = new SABEREngine(var2, var3, var4, var5);
   }

   public String getName() {
      return this.name;
   }

   public int getL() {
      return this.l;
   }

   public int getSessionKeySize() {
      return this.defaultKeySize;
   }

   public SABEREngine getEngine() {
      return this.engine;
   }
}
