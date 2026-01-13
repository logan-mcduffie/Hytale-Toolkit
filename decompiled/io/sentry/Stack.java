package io.sentry;

import io.sentry.util.AutoClosableReentrantLock;
import io.sentry.util.Objects;
import java.util.Deque;
import java.util.Iterator;
import java.util.concurrent.LinkedBlockingDeque;
import org.jetbrains.annotations.NotNull;

final class Stack {
   @NotNull
   private final Deque<Stack.StackItem> items = new LinkedBlockingDeque<>();
   @NotNull
   private final ILogger logger;
   @NotNull
   private final AutoClosableReentrantLock itemsLock = new AutoClosableReentrantLock();

   public Stack(@NotNull ILogger logger, @NotNull Stack.StackItem rootStackItem) {
      this.logger = Objects.requireNonNull(logger, "logger is required");
      this.items.push(Objects.requireNonNull(rootStackItem, "rootStackItem is required"));
   }

   public Stack(@NotNull Stack stack) {
      this(stack.logger, new Stack.StackItem(stack.items.getLast()));
      Iterator<Stack.StackItem> iterator = stack.items.descendingIterator();
      if (iterator.hasNext()) {
         iterator.next();
      }

      while (iterator.hasNext()) {
         this.push(new Stack.StackItem(iterator.next()));
      }
   }

   @NotNull
   Stack.StackItem peek() {
      return this.items.peek();
   }

   void pop() {
      ISentryLifecycleToken ignored = this.itemsLock.acquire();

      try {
         if (this.items.size() != 1) {
            this.items.pop();
         } else {
            this.logger.log(SentryLevel.WARNING, "Attempt to pop the root scope.");
         }
      } catch (Throwable var5) {
         if (ignored != null) {
            try {
               ignored.close();
            } catch (Throwable var4) {
               var5.addSuppressed(var4);
            }
         }

         throw var5;
      }

      if (ignored != null) {
         ignored.close();
      }
   }

   void push(@NotNull Stack.StackItem stackItem) {
      this.items.push(stackItem);
   }

   int size() {
      return this.items.size();
   }

   static final class StackItem {
      private final SentryOptions options;
      @NotNull
      private volatile ISentryClient client;
      @NotNull
      private volatile IScope scope;

      StackItem(@NotNull SentryOptions options, @NotNull ISentryClient client, @NotNull IScope scope) {
         this.client = Objects.requireNonNull(client, "ISentryClient is required.");
         this.scope = Objects.requireNonNull(scope, "Scope is required.");
         this.options = Objects.requireNonNull(options, "Options is required");
      }

      StackItem(@NotNull Stack.StackItem item) {
         this.options = item.options;
         this.client = item.client;
         this.scope = item.scope.clone();
      }

      @NotNull
      public ISentryClient getClient() {
         return this.client;
      }

      public void setClient(@NotNull ISentryClient client) {
         this.client = client;
      }

      @NotNull
      public IScope getScope() {
         return this.scope;
      }

      @NotNull
      public SentryOptions getOptions() {
         return this.options;
      }
   }
}
