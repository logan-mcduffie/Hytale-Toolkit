package org.bson;

public final class BsonNull extends BsonValue {
   public static final BsonNull VALUE = new BsonNull();

   @Override
   public BsonType getBsonType() {
      return BsonType.NULL;
   }

   @Override
   public boolean equals(Object o) {
      return this == o ? true : o != null && this.getClass() == o.getClass();
   }

   @Override
   public int hashCode() {
      return 0;
   }

   @Override
   public String toString() {
      return "BsonNull";
   }
}
