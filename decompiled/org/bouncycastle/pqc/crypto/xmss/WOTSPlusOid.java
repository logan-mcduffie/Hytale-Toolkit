package org.bouncycastle.pqc.crypto.xmss;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

final class WOTSPlusOid implements XMSSOid {
   private static final Map<String, WOTSPlusOid> oidLookupTable;
   private final int oid;
   private final String stringRepresentation;

   private WOTSPlusOid(int var1, String var2) {
      this.oid = var1;
      this.stringRepresentation = var2;
   }

   protected static WOTSPlusOid lookup(String var0, int var1, int var2, int var3) {
      if (var0 == null) {
         throw new NullPointerException("algorithmName == null");
      } else {
         return oidLookupTable.get(createKey(var0, var1, var2, var3));
      }
   }

   private static String createKey(String var0, int var1, int var2, int var3) {
      if (var0 == null) {
         throw new NullPointerException("algorithmName == null");
      } else {
         return var0 + "-" + var1 + "-" + var2 + "-" + var3;
      }
   }

   @Override
   public int getOid() {
      return this.oid;
   }

   @Override
   public String toString() {
      return this.stringRepresentation;
   }

   static {
      HashMap var0 = new HashMap();
      var0.put(createKey("SHA-256", 32, 16, 67), new WOTSPlusOid(16777217, "WOTSP_SHA2-256_W16"));
      var0.put(createKey("SHA-512", 64, 16, 131), new WOTSPlusOid(33554434, "WOTSP_SHA2-512_W16"));
      var0.put(createKey("SHAKE128", 32, 16, 67), new WOTSPlusOid(50331651, "WOTSP_SHAKE128_W16"));
      var0.put(createKey("SHAKE256", 64, 16, 131), new WOTSPlusOid(67108868, "WOTSP_SHAKE256_W16"));
      oidLookupTable = Collections.unmodifiableMap(var0);
   }
}
