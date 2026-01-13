package org.bson;

public class BsonSymbol extends BsonValue {
   private final String symbol;

   public BsonSymbol(String value) {
      if (value == null) {
         throw new IllegalArgumentException("Value can not be null");
      } else {
         this.symbol = value;
      }
   }

   @Override
   public BsonType getBsonType() {
      return BsonType.SYMBOL;
   }

   public String getSymbol() {
      return this.symbol;
   }

   @Override
   public boolean equals(Object o) {
      if (this == o) {
         return true;
      } else if (o != null && this.getClass() == o.getClass()) {
         BsonSymbol symbol1 = (BsonSymbol)o;
         return this.symbol.equals(symbol1.symbol);
      } else {
         return false;
      }
   }

   @Override
   public int hashCode() {
      return this.symbol.hashCode();
   }

   @Override
   public String toString() {
      return this.symbol;
   }
}
