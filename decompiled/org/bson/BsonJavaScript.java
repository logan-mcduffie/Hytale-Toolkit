package org.bson;

public class BsonJavaScript extends BsonValue {
   private final String code;

   public BsonJavaScript(String code) {
      this.code = code;
   }

   @Override
   public BsonType getBsonType() {
      return BsonType.JAVASCRIPT;
   }

   public String getCode() {
      return this.code;
   }

   @Override
   public boolean equals(Object o) {
      if (this == o) {
         return true;
      } else if (o != null && this.getClass() == o.getClass()) {
         BsonJavaScript code1 = (BsonJavaScript)o;
         return this.code.equals(code1.code);
      } else {
         return false;
      }
   }

   @Override
   public int hashCode() {
      return this.code.hashCode();
   }

   @Override
   public String toString() {
      return "BsonJavaScript{code='" + this.code + '\'' + '}';
   }
}
