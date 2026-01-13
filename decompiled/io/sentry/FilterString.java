package io.sentry;

import java.util.Objects;
import java.util.regex.Pattern;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class FilterString {
   @NotNull
   private final String filterString;
   @Nullable
   private final Pattern pattern;

   public FilterString(@NotNull String filterString) {
      this.filterString = filterString;
      Pattern pattern = null;

      try {
         pattern = Pattern.compile(filterString);
      } catch (Throwable var4) {
         Sentry.getCurrentScopes()
            .getOptions()
            .getLogger()
            .log(SentryLevel.DEBUG, "Only using filter string for String comparison as it could not be parsed as regex: %s", filterString);
      }

      this.pattern = pattern;
   }

   @NotNull
   public String getFilterString() {
      return this.filterString;
   }

   public boolean matches(String input) {
      return this.pattern == null ? false : this.pattern.matcher(input).matches();
   }

   @Override
   public boolean equals(Object o) {
      if (o != null && this.getClass() == o.getClass()) {
         FilterString that = (FilterString)o;
         return Objects.equals(this.filterString, that.filterString);
      } else {
         return false;
      }
   }

   @Override
   public int hashCode() {
      return Objects.hash(this.filterString);
   }
}
