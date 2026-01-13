package org.bson;

public class BsonDateTime extends BsonValue implements Comparable<BsonDateTime> {
   private final long value;

   public BsonDateTime(long value) {
      this.value = value;
   }

   public int compareTo(BsonDateTime o) {
      return Long.compare(this.value, o.value);
   }

   @Override
   public BsonType getBsonType() {
      return BsonType.DATE_TIME;
   }

   public long getValue() {
      return this.value;
   }

   @Override
   public boolean equals(Object o) {
      if (this == o) {
         return true;
      } else if (o != null && this.getClass() == o.getClass()) {
         BsonDateTime that = (BsonDateTime)o;
         return this.value == that.value;
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
      return "BsonDateTime{value=" + this.value + '}';
   }
}
