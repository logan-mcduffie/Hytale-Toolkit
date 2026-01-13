package org.bouncycastle.asn1.est;

class Utils {
   static AttrOrOID[] clone(AttrOrOID[] var0) {
      AttrOrOID[] var1 = new AttrOrOID[var0.length];
      System.arraycopy(var0, 0, var1, 0, var0.length);
      return var1;
   }
}
