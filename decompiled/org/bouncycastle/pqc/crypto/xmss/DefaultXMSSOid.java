package org.bouncycastle.pqc.crypto.xmss;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public final class DefaultXMSSOid implements XMSSOid {
   private static final Map<String, DefaultXMSSOid> oidLookupTable;
   private final int oid;
   private final String stringRepresentation;

   private DefaultXMSSOid(int var1, String var2) {
      this.oid = var1;
      this.stringRepresentation = var2;
   }

   public static DefaultXMSSOid lookup(String var0, int var1, int var2, int var3, int var4) {
      if (var0 == null) {
         throw new NullPointerException("algorithmName == null");
      } else {
         return oidLookupTable.get(createKey(var0, var1, var2, var3, var4));
      }
   }

   private static String createKey(String var0, int var1, int var2, int var3, int var4) {
      if (var0 == null) {
         throw new NullPointerException("algorithmName == null");
      } else {
         return var0 + "-" + var1 + "-" + var2 + "-" + var3 + "-" + var4;
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
      var0.put(createKey("SHA-256", 32, 16, 67, 10), new DefaultXMSSOid(1, "XMSS_SHA2_10_256"));
      var0.put(createKey("SHA-256", 32, 16, 67, 16), new DefaultXMSSOid(2, "XMSS_SHA2_16_256"));
      var0.put(createKey("SHA-256", 32, 16, 67, 20), new DefaultXMSSOid(3, "XMSS_SHA2_20_256"));
      var0.put(createKey("SHA-512", 64, 16, 131, 10), new DefaultXMSSOid(4, "XMSS_SHA2_10_512"));
      var0.put(createKey("SHA-512", 64, 16, 131, 16), new DefaultXMSSOid(5, "XMSS_SHA2_16_512"));
      var0.put(createKey("SHA-512", 64, 16, 131, 20), new DefaultXMSSOid(6, "XMSS_SHA2_20_512"));
      var0.put(createKey("SHAKE128", 32, 16, 67, 10), new DefaultXMSSOid(7, "XMSS_SHAKE_10_256"));
      var0.put(createKey("SHAKE128", 32, 16, 67, 16), new DefaultXMSSOid(8, "XMSS_SHAKE_16_256"));
      var0.put(createKey("SHAKE128", 32, 16, 67, 20), new DefaultXMSSOid(9, "XMSS_SHAKE_20_256"));
      var0.put(createKey("SHAKE256", 64, 16, 131, 10), new DefaultXMSSOid(10, "XMSS_SHAKE_10_512"));
      var0.put(createKey("SHAKE256", 64, 16, 131, 16), new DefaultXMSSOid(11, "XMSS_SHAKE_16_512"));
      var0.put(createKey("SHAKE256", 64, 16, 131, 20), new DefaultXMSSOid(12, "XMSS_SHAKE_20_512"));
      oidLookupTable = Collections.unmodifiableMap(var0);
   }
}
