package org.bson.types;

import java.io.Serializable;

public class Symbol implements Serializable {
   private static final long serialVersionUID = 1326269319883146072L;
   private final String symbol;

   public Symbol(String symbol) {
      this.symbol = symbol;
   }

   public String getSymbol() {
      return this.symbol;
   }

   @Override
   public boolean equals(Object o) {
      if (this == o) {
         return true;
      } else if (o != null && this.getClass() == o.getClass()) {
         Symbol symbol1 = (Symbol)o;
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
