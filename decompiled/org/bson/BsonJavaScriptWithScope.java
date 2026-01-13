package org.bson;

public class BsonJavaScriptWithScope extends BsonValue {
   private final String code;
   private final BsonDocument scope;

   public BsonJavaScriptWithScope(String code, BsonDocument scope) {
      if (code == null) {
         throw new IllegalArgumentException("code can not be null");
      } else if (scope == null) {
         throw new IllegalArgumentException("scope can not be null");
      } else {
         this.code = code;
         this.scope = scope;
      }
   }

   @Override
   public BsonType getBsonType() {
      return BsonType.JAVASCRIPT_WITH_SCOPE;
   }

   public String getCode() {
      return this.code;
   }

   public BsonDocument getScope() {
      return this.scope;
   }

   @Override
   public boolean equals(Object o) {
      if (this == o) {
         return true;
      } else if (o != null && this.getClass() == o.getClass()) {
         BsonJavaScriptWithScope that = (BsonJavaScriptWithScope)o;
         return !this.code.equals(that.code) ? false : this.scope.equals(that.scope);
      } else {
         return false;
      }
   }

   @Override
   public int hashCode() {
      int result = this.code.hashCode();
      return 31 * result + this.scope.hashCode();
   }

   @Override
   public String toString() {
      return "BsonJavaScriptWithScope{code=" + this.getCode() + "scope=" + this.scope + '}';
   }

   static BsonJavaScriptWithScope clone(BsonJavaScriptWithScope from) {
      return new BsonJavaScriptWithScope(from.code, from.scope.clone());
   }
}
