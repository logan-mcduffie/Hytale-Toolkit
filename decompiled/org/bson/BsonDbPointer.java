package org.bson;

import org.bson.types.ObjectId;

public class BsonDbPointer extends BsonValue {
   private final String namespace;
   private final ObjectId id;

   public BsonDbPointer(String namespace, ObjectId id) {
      if (namespace == null) {
         throw new IllegalArgumentException("namespace can not be null");
      } else if (id == null) {
         throw new IllegalArgumentException("id can not be null");
      } else {
         this.namespace = namespace;
         this.id = id;
      }
   }

   @Override
   public BsonType getBsonType() {
      return BsonType.DB_POINTER;
   }

   public String getNamespace() {
      return this.namespace;
   }

   public ObjectId getId() {
      return this.id;
   }

   @Override
   public boolean equals(Object o) {
      if (this == o) {
         return true;
      } else if (o != null && this.getClass() == o.getClass()) {
         BsonDbPointer dbPointer = (BsonDbPointer)o;
         return !this.id.equals(dbPointer.id) ? false : this.namespace.equals(dbPointer.namespace);
      } else {
         return false;
      }
   }

   @Override
   public int hashCode() {
      int result = this.namespace.hashCode();
      return 31 * result + this.id.hashCode();
   }

   @Override
   public String toString() {
      return "BsonDbPointer{namespace='" + this.namespace + '\'' + ", id=" + this.id + '}';
   }
}
