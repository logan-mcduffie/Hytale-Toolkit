package org.bouncycastle.math.ec;

public interface ECLookupTable {
   int getSize();

   ECPoint lookup(int var1);

   ECPoint lookupVar(int var1);
}
