package org.bson;

public final class BsonMaxKey extends BsonValue {
   @Override
   public BsonType getBsonType() {
      return BsonType.MAX_KEY;
   }

   @Override
   public boolean equals(Object o) {
      return o instanceof BsonMaxKey;
   }

   @Override
   public int hashCode() {
      return 0;
   }

   @Override
   public String toString() {
      return "BsonMaxKey";
   }
}
