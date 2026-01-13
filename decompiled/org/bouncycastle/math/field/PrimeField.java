package org.bouncycastle.math.field;

import java.math.BigInteger;

class PrimeField implements FiniteField {
   protected final BigInteger characteristic;

   PrimeField(BigInteger var1) {
      this.characteristic = var1;
   }

   @Override
   public BigInteger getCharacteristic() {
      return this.characteristic;
   }

   @Override
   public int getDimension() {
      return 1;
   }

   @Override
   public boolean equals(Object var1) {
      if (this == var1) {
         return true;
      } else if (!(var1 instanceof PrimeField)) {
         return false;
      } else {
         PrimeField var2 = (PrimeField)var1;
         return this.characteristic.equals(var2.characteristic);
      }
   }

   @Override
   public int hashCode() {
      return this.characteristic.hashCode();
   }
}
