package org.bson.types;

import java.io.Serializable;

public final class MaxKey implements Serializable {
   private static final long serialVersionUID = 5123414776151687185L;

   @Override
   public boolean equals(Object o) {
      return o instanceof MaxKey;
   }

   @Override
   public int hashCode() {
      return 0;
   }

   @Override
   public String toString() {
      return "MaxKey";
   }
}
