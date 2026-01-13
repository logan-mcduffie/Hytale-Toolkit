package org.bouncycastle.math.field;

import java.math.BigInteger;
import org.bouncycastle.util.Integers;

class GenericPolynomialExtensionField implements PolynomialExtensionField {
   protected final FiniteField subfield;
   protected final Polynomial minimalPolynomial;

   GenericPolynomialExtensionField(FiniteField var1, Polynomial var2) {
      this.subfield = var1;
      this.minimalPolynomial = var2;
   }

   @Override
   public BigInteger getCharacteristic() {
      return this.subfield.getCharacteristic();
   }

   @Override
   public int getDimension() {
      return this.subfield.getDimension() * this.minimalPolynomial.getDegree();
   }

   @Override
   public FiniteField getSubfield() {
      return this.subfield;
   }

   @Override
   public int getDegree() {
      return this.minimalPolynomial.getDegree();
   }

   @Override
   public Polynomial getMinimalPolynomial() {
      return this.minimalPolynomial;
   }

   @Override
   public boolean equals(Object var1) {
      if (this == var1) {
         return true;
      } else if (!(var1 instanceof GenericPolynomialExtensionField)) {
         return false;
      } else {
         GenericPolynomialExtensionField var2 = (GenericPolynomialExtensionField)var1;
         return this.subfield.equals(var2.subfield) && this.minimalPolynomial.equals(var2.minimalPolynomial);
      }
   }

   @Override
   public int hashCode() {
      return this.subfield.hashCode() ^ Integers.rotateLeft(this.minimalPolynomial.hashCode(), 16);
   }
}
