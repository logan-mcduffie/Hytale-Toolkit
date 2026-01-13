package org.bson;

public final class BsonBoolean extends BsonValue implements Comparable<BsonBoolean> {
   private final boolean value;
   public static final BsonBoolean TRUE = new BsonBoolean(true);
   public static final BsonBoolean FALSE = new BsonBoolean(false);

   public static BsonBoolean valueOf(boolean value) {
      return value ? TRUE : FALSE;
   }

   public BsonBoolean(boolean value) {
      this.value = value;
   }

   public int compareTo(BsonBoolean o) {
      return Boolean.valueOf(this.value).compareTo(o.value);
   }

   @Override
   public BsonType getBsonType() {
      return BsonType.BOOLEAN;
   }

   public boolean getValue() {
      return this.value;
   }

   @Override
   public boolean equals(Object o) {
      if (this == o) {
         return true;
      } else if (o != null && this.getClass() == o.getClass()) {
         BsonBoolean that = (BsonBoolean)o;
         return this.value == that.value;
      } else {
         return false;
      }
   }

   @Override
   public int hashCode() {
      return this.value ? 1 : 0;
   }

   @Override
   public String toString() {
      return "BsonBoolean{value=" + this.value + '}';
   }
}
