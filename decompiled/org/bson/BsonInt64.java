package org.bson;

import org.bson.types.Decimal128;

public final class BsonInt64 extends BsonNumber implements Comparable<BsonInt64> {
   private final long value;

   public BsonInt64(long value) {
      this.value = value;
   }

   public int compareTo(BsonInt64 o) {
      return this.value < o.value ? -1 : (this.value == o.value ? 0 : 1);
   }

   @Override
   public BsonType getBsonType() {
      return BsonType.INT64;
   }

   public long getValue() {
      return this.value;
   }

   @Override
   public int intValue() {
      return (int)this.value;
   }

   @Override
   public long longValue() {
      return this.value;
   }

   @Override
   public double doubleValue() {
      return this.value;
   }

   @Override
   public Decimal128 decimal128Value() {
      return new Decimal128(this.value);
   }

   @Override
   public boolean equals(Object o) {
      if (this == o) {
         return true;
      } else if (o != null && this.getClass() == o.getClass()) {
         BsonInt64 bsonInt64 = (BsonInt64)o;
         return this.value == bsonInt64.value;
      } else {
         return false;
      }
   }

   @Override
   public int hashCode() {
      return (int)(this.value ^ this.value >>> 32);
   }

   @Override
   public String toString() {
      return "BsonInt64{value=" + this.value + '}';
   }
}
