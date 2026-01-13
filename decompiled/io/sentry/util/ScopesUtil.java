package io.sentry.util;

import io.sentry.HubAdapter;
import io.sentry.HubScopesWrapper;
import io.sentry.IScopes;
import io.sentry.Scopes;
import io.sentry.ScopesAdapter;
import io.sentry.Sentry;
import org.jetbrains.annotations.Nullable;

public final class ScopesUtil {
   public static void printScopesChain(@Nullable IScopes scopes) {
      System.out.println("==========================================");
      System.out.println("=============== v Scopes v ===============");
      System.out.println("==========================================");
      printScopesChainInternal(scopes);
      System.out.println("==========================================");
      System.out.println("=============== ^ Scopes ^ ===============");
      System.out.println("==========================================");
   }

   private static void printScopesChainInternal(@Nullable IScopes someScopes) {
      if (someScopes != null) {
         if (someScopes instanceof Scopes) {
            Scopes scopes = (Scopes)someScopes;
            String info = String.format(
               "%-25s {g=%-25s, i=%-25s, c=%-25s} [%s]", scopes, scopes.getGlobalScope(), scopes.getIsolationScope(), scopes.getScope(), scopes.getCreator()
            );
            System.out.println(info);
            printScopesChainInternal(someScopes.getParentScopes());
         } else if (someScopes instanceof ScopesAdapter || someScopes instanceof HubAdapter) {
            printScopesChainInternal(Sentry.getCurrentScopes());
         } else if (someScopes instanceof HubScopesWrapper) {
            HubScopesWrapper wrapper = (HubScopesWrapper)someScopes;
            printScopesChainInternal(wrapper.getScopes());
         } else {
            System.out.println("Hit unhandled Scopes class" + someScopes.getClass());
         }
      } else {
         System.out.println("-");
      }
   }
}
