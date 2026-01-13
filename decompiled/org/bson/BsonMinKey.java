package org.bson;

public final class BsonMinKey extends BsonValue {
   @Override
   public BsonType getBsonType() {
      return BsonType.MIN_KEY;
   }

   @Override
   public boolean equals(Object o) {
      return o instanceof BsonMinKey;
   }

   @Override
   public int hashCode() {
      return 0;
   }

   @Override
   public String toString() {
      return "BsonMinKey";
   }
}
