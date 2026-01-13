package org.bson.types;

import java.io.Serializable;

public class Code implements Serializable {
   private static final long serialVersionUID = 475535263314046697L;
   private final String code;

   public Code(String code) {
      this.code = code;
   }

   public String getCode() {
      return this.code;
   }

   @Override
   public boolean equals(Object o) {
      if (this == o) {
         return true;
      } else if (o != null && this.getClass() == o.getClass()) {
         Code code1 = (Code)o;
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
      return "Code{code='" + this.code + '\'' + '}';
   }
}
