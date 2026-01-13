package org.bson;

public abstract class BsonValue {
   BsonValue() {
   }

   public abstract BsonType getBsonType();

   public BsonDocument asDocument() {
      this.throwIfInvalidType(BsonType.DOCUMENT);
      return (BsonDocument)this;
   }

   public BsonArray asArray() {
      this.throwIfInvalidType(BsonType.ARRAY);
      return (BsonArray)this;
   }

   public BsonString asString() {
      this.throwIfInvalidType(BsonType.STRING);
      return (BsonString)this;
   }

   public BsonNumber asNumber() {
      if (this.getBsonType() != BsonType.INT32 && this.getBsonType() != BsonType.INT64 && this.getBsonType() != BsonType.DOUBLE) {
         throw new BsonInvalidOperationException(String.format("Value expected to be of a numerical BSON type is of unexpected type %s", this.getBsonType()));
      } else {
         return (BsonNumber)this;
      }
   }

   public BsonInt32 asInt32() {
      this.throwIfInvalidType(BsonType.INT32);
      return (BsonInt32)this;
   }

   public BsonInt64 asInt64() {
      this.throwIfInvalidType(BsonType.INT64);
      return (BsonInt64)this;
   }

   public BsonDecimal128 asDecimal128() {
      this.throwIfInvalidType(BsonType.DECIMAL128);
      return (BsonDecimal128)this;
   }

   public BsonDouble asDouble() {
      this.throwIfInvalidType(BsonType.DOUBLE);
      return (BsonDouble)this;
   }

   public BsonBoolean asBoolean() {
      this.throwIfInvalidType(BsonType.BOOLEAN);
      return (BsonBoolean)this;
   }

   public BsonObjectId asObjectId() {
      this.throwIfInvalidType(BsonType.OBJECT_ID);
      return (BsonObjectId)this;
   }

   public BsonDbPointer asDBPointer() {
      this.throwIfInvalidType(BsonType.DB_POINTER);
      return (BsonDbPointer)this;
   }

   public BsonTimestamp asTimestamp() {
      this.throwIfInvalidType(BsonType.TIMESTAMP);
      return (BsonTimestamp)this;
   }

   public BsonBinary asBinary() {
      this.throwIfInvalidType(BsonType.BINARY);
      return (BsonBinary)this;
   }

   public BsonDateTime asDateTime() {
      this.throwIfInvalidType(BsonType.DATE_TIME);
      return (BsonDateTime)this;
   }

   public BsonSymbol asSymbol() {
      this.throwIfInvalidType(BsonType.SYMBOL);
      return (BsonSymbol)this;
   }

   public BsonRegularExpression asRegularExpression() {
      this.throwIfInvalidType(BsonType.REGULAR_EXPRESSION);
      return (BsonRegularExpression)this;
   }

   public BsonJavaScript asJavaScript() {
      this.throwIfInvalidType(BsonType.JAVASCRIPT);
      return (BsonJavaScript)this;
   }

   public BsonJavaScriptWithScope asJavaScriptWithScope() {
      this.throwIfInvalidType(BsonType.JAVASCRIPT_WITH_SCOPE);
      return (BsonJavaScriptWithScope)this;
   }

   public boolean isNull() {
      return this instanceof BsonNull;
   }

   public boolean isDocument() {
      return this instanceof BsonDocument;
   }

   public boolean isArray() {
      return this instanceof BsonArray;
   }

   public boolean isString() {
      return this instanceof BsonString;
   }

   public boolean isNumber() {
      return this.isInt32() || this.isInt64() || this.isDouble();
   }

   public boolean isInt32() {
      return this instanceof BsonInt32;
   }

   public boolean isInt64() {
      return this instanceof BsonInt64;
   }

   public boolean isDecimal128() {
      return this instanceof BsonDecimal128;
   }

   public boolean isDouble() {
      return this instanceof BsonDouble;
   }

   public boolean isBoolean() {
      return this instanceof BsonBoolean;
   }

   public boolean isObjectId() {
      return this instanceof BsonObjectId;
   }

   public boolean isDBPointer() {
      return this instanceof BsonDbPointer;
   }

   public boolean isTimestamp() {
      return this instanceof BsonTimestamp;
   }

   public boolean isBinary() {
      return this instanceof BsonBinary;
   }

   public boolean isDateTime() {
      return this instanceof BsonDateTime;
   }

   public boolean isSymbol() {
      return this instanceof BsonSymbol;
   }

   public boolean isRegularExpression() {
      return this instanceof BsonRegularExpression;
   }

   public boolean isJavaScript() {
      return this instanceof BsonJavaScript;
   }

   public boolean isJavaScriptWithScope() {
      return this instanceof BsonJavaScriptWithScope;
   }

   private void throwIfInvalidType(BsonType expectedType) {
      if (this.getBsonType() != expectedType) {
         throw new BsonInvalidOperationException(String.format("Value expected to be of type %s is of unexpected type %s", expectedType, this.getBsonType()));
      }
   }
}
