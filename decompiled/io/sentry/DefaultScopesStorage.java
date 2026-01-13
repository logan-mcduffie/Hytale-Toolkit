package io.sentry;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.ApiStatus.Internal;

@Internal
public final class DefaultScopesStorage implements IScopesStorage {
   @NotNull
   private static final ThreadLocal<IScopes> currentScopes = new ThreadLocal<>();

   @Override
   public void init() {
   }

   @Override
   public ISentryLifecycleToken set(@Nullable IScopes scopes) {
      IScopes oldScopes = this.get();
      currentScopes.set(scopes);
      return new DefaultScopesStorage.DefaultScopesLifecycleToken(oldScopes);
   }

   @Nullable
   @Override
   public IScopes get() {
      return currentScopes.get();
   }

   @Override
   public void close() {
      currentScopes.remove();
   }

   static final class DefaultScopesLifecycleToken implements ISentryLifecycleToken {
      @Nullable
      private final IScopes oldValue;

      DefaultScopesLifecycleToken(@Nullable IScopes scopes) {
         this.oldValue = scopes;
      }

      @Override
      public void close() {
         DefaultScopesStorage.currentScopes.set(this.oldValue);
      }
   }
}
