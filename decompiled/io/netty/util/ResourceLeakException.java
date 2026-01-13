package io.netty.util;

import java.util.Arrays;

@Deprecated
public class ResourceLeakException extends RuntimeException {
   private static final long serialVersionUID = 7186453858343358280L;
   private final StackTraceElement[] cachedStackTrace = this.getStackTrace();

   public ResourceLeakException() {
   }

   public ResourceLeakException(String message) {
      super(message);
   }

   public ResourceLeakException(String message, Throwable cause) {
      super(message, cause);
   }

   public ResourceLeakException(Throwable cause) {
      super(cause);
   }

   @Override
   public int hashCode() {
      int hashCode = 0;

      for (StackTraceElement e : this.cachedStackTrace) {
         hashCode = hashCode * 31 + e.hashCode();
      }

      return hashCode;
   }

   @Override
   public boolean equals(Object o) {
      if (!(o instanceof ResourceLeakException)) {
         return false;
      } else {
         return o == this ? true : Arrays.equals((Object[])this.cachedStackTrace, (Object[])((ResourceLeakException)o).cachedStackTrace);
      }
   }
}
