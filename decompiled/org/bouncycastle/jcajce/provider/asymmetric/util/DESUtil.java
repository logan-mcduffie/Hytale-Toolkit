package org.bouncycastle.jcajce.provider.asymmetric.util;

import java.util.HashSet;
import java.util.Set;
import org.bouncycastle.asn1.pkcs.PKCSObjectIdentifiers;
import org.bouncycastle.internal.asn1.oiw.OIWObjectIdentifiers;
import org.bouncycastle.util.Strings;

public class DESUtil {
   private static final Set<String> des = new HashSet<>();

   public static boolean isDES(String var0) {
      String var1 = Strings.toUpperCase(var0);
      return des.contains(var1);
   }

   public static void setOddParity(byte[] var0) {
      for (int var1 = 0; var1 < var0.length; var1++) {
         byte var2 = var0[var1];
         var0[var1] = (byte)(var2 & 254 | (var2 >> 1 ^ var2 >> 2 ^ var2 >> 3 ^ var2 >> 4 ^ var2 >> 5 ^ var2 >> 6 ^ var2 >> 7 ^ 1) & 1);
      }
   }

   static {
      des.add("DES");
      des.add("DESEDE");
      des.add(OIWObjectIdentifiers.desCBC.getId());
      des.add(PKCSObjectIdentifiers.des_EDE3_CBC.getId());
      des.add(PKCSObjectIdentifiers.id_alg_CMS3DESwrap.getId());
   }
}
