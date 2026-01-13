package org.bson;

import org.bson.assertions.Assertions;
import org.bson.types.Decimal128;

public final class BsonDecimal128 extends BsonNumber {
   private final Decimal128 value;

   public BsonDecimal128(Decimal128 value) {
      Assertions.notNull("value", value);
      this.value = value;
   }

   @Override
   public BsonType getBsonType() {
      return BsonType.DECIMAL128;
   }

   public Decimal128 getValue() {
      return this.value;
   }

   @Override
   public boolean equals(Object o) {
      if (this == o) {
         return true;
      } else if (o != null && this.getClass() == o.getClass()) {
         BsonDecimal128 that = (BsonDecimal128)o;
         return this.value.equals(that.value);
      } else {
         return false;
      }
   }

   @Override
   public int hashCode() {
      return this.value.hashCode();
   }

   @Override
   public String toString() {
      return "BsonDecimal128{value=" + this.value + '}';
   }

   @Override
   public int intValue() {
      return this.value.bigDecimalValue().intValue();
   }

   @Override
   public long longValue() {
      return this.value.bigDecimalValue().longValue();
   }

   @Override
   public double doubleValue() {
      return this.value.bigDecimalValue().doubleValue();
   }

   @Override
   public Decimal128 decimal128Value() {
      return this.value;
   }
}
