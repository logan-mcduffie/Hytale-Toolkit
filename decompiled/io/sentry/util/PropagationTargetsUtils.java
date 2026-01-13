package io.sentry.util;

import java.net.URI;
import java.util.List;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.ApiStatus.Internal;

@Internal
public final class PropagationTargetsUtils {
   public static boolean contain(@NotNull List<String> origins, @NotNull String url) {
      if (origins.isEmpty()) {
         return false;
      } else {
         for (String origin : origins) {
            if (url.contains(origin)) {
               return true;
            }

            try {
               if (url.matches(origin)) {
                  return true;
               }
            } catch (Exception var5) {
            }
         }

         return false;
      }
   }

   public static boolean contain(@NotNull List<String> origins, URI uri) {
      return contain(origins, uri.toString());
   }
}
