package org.bson.types;

import org.bson.Document;

public class CodeWithScope extends Code {
   private final Document scope;
   private static final long serialVersionUID = -6284832275113680002L;

   public CodeWithScope(String code, Document scope) {
      super(code);
      this.scope = scope;
   }

   public Document getScope() {
      return this.scope;
   }

   @Override
   public boolean equals(Object o) {
      if (this == o) {
         return true;
      } else if (o == null || this.getClass() != o.getClass()) {
         return false;
      } else if (!super.equals(o)) {
         return false;
      } else {
         CodeWithScope that = (CodeWithScope)o;
         return this.scope != null ? this.scope.equals(that.scope) : that.scope == null;
      }
   }

   @Override
   public int hashCode() {
      return this.getCode().hashCode() ^ this.scope.hashCode();
   }
}
