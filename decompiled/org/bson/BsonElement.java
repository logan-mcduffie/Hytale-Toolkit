package org.bson;

public class BsonElement {
   private final String name;
   private final BsonValue value;

   public BsonElement(String name, BsonValue value) {
      this.name = name;
      this.value = value;
   }

   public String getName() {
      return this.name;
   }

   public BsonValue getValue() {
      return this.value;
   }

   @Override
   public boolean equals(Object o) {
      if (this == o) {
         return true;
      } else if (o != null && this.getClass() == o.getClass()) {
         BsonElement that = (BsonElement)o;
         if (this.getName() != null ? this.getName().equals(that.getName()) : that.getName() == null) {
            return this.getValue() != null ? this.getValue().equals(that.getValue()) : that.getValue() == null;
         } else {
            return false;
         }
      } else {
         return false;
      }
   }

   @Override
   public int hashCode() {
      int result = this.getName() != null ? this.getName().hashCode() : 0;
      return 31 * result + (this.getValue() != null ? this.getValue().hashCode() : 0);
   }
}
