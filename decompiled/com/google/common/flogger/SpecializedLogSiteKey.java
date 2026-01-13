package com.google.common.flogger;

import com.google.common.flogger.util.Checks;
import org.checkerframework.checker.nullness.compatqual.NullableDecl;

final class SpecializedLogSiteKey implements LogSiteKey {
   private final LogSiteKey delegate;
   private final Object qualifier;

   static LogSiteKey of(LogSiteKey key, Object qualifier) {
      return new SpecializedLogSiteKey(key, qualifier);
   }

   private SpecializedLogSiteKey(LogSiteKey key, Object qualifier) {
      this.delegate = Checks.checkNotNull(key, "log site key");
      this.qualifier = Checks.checkNotNull(qualifier, "log site qualifier");
   }

   @Override
   public boolean equals(@NullableDecl Object obj) {
      if (!(obj instanceof SpecializedLogSiteKey)) {
         return false;
      } else {
         SpecializedLogSiteKey other = (SpecializedLogSiteKey)obj;
         return this.delegate.equals(other.delegate) && this.qualifier.equals(other.qualifier);
      }
   }

   @Override
   public int hashCode() {
      return this.delegate.hashCode() ^ this.qualifier.hashCode();
   }

   @Override
   public String toString() {
      return "SpecializedLogSiteKey{ delegate='" + this.delegate + "', qualifier='" + this.qualifier + "' }";
   }
}
