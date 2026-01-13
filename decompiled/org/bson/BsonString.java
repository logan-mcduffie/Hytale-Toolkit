package org.bson;

public class BsonString extends BsonValue implements Comparable<BsonString> {
   private final String value;

   public BsonString(String value) {
      if (value == null) {
         throw new IllegalArgumentException("Value can not be null");
      } else {
         this.value = value;
      }
   }

   public int compareTo(BsonString o) {
      return this.value.compareTo(o.value);
   }

   @Override
   public BsonType getBsonType() {
      return BsonType.STRING;
   }

   public String getValue() {
      return this.value;
   }

   @Override
   public boolean equals(Object o) {
      if (this == o) {
         return true;
      } else if (o != null && this.getClass() == o.getClass()) {
         BsonString that = (BsonString)o;
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
      return "BsonString{value='" + this.value + '\'' + '}';
   }
}
