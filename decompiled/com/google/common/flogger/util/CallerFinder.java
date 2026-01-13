package com.google.common.flogger.util;

import com.google.errorprone.annotations.CheckReturnValue;
import org.checkerframework.checker.nullness.compatqual.NullableDecl;

@CheckReturnValue
public final class CallerFinder {
   private static final FastStackGetter stackGetter = FastStackGetter.createIfSupported();

   @NullableDecl
   public static StackTraceElement findCallerOf(Class<?> target, Throwable throwable, int skip) {
      Checks.checkNotNull(target, "target");
      Checks.checkNotNull(throwable, "throwable");
      if (skip < 0) {
         throw new IllegalArgumentException("skip count cannot be negative: " + skip);
      } else {
         StackTraceElement[] stack = stackGetter != null ? null : throwable.getStackTrace();
         boolean foundCaller = false;

         try {
            int index = skip;

            while (true) {
               StackTraceElement element = stackGetter != null ? stackGetter.getStackTraceElement(throwable, index) : stack[index];
               if (target.getName().equals(element.getClassName())) {
                  foundCaller = true;
               } else if (foundCaller) {
                  return element;
               }

               index++;
            }
         } catch (Exception var7) {
            return null;
         }
      }
   }

   public static StackTraceElement[] getStackForCallerOf(Class<?> target, Throwable throwable, int maxDepth, int skip) {
      Checks.checkNotNull(target, "target");
      Checks.checkNotNull(throwable, "throwable");
      if (maxDepth <= 0 && maxDepth != -1) {
         throw new IllegalArgumentException("invalid maximum depth: " + maxDepth);
      } else if (skip < 0) {
         throw new IllegalArgumentException("skip count cannot be negative: " + skip);
      } else {
         StackTraceElement[] stack;
         int depth;
         if (stackGetter != null) {
            stack = null;
            depth = stackGetter.getStackTraceDepth(throwable);
         } else {
            stack = throwable.getStackTrace();
            depth = stack.length;
         }

         boolean foundCaller = false;

         for (int index = skip; index < depth; index++) {
            StackTraceElement element = stackGetter != null ? stackGetter.getStackTraceElement(throwable, index) : stack[index];
            if (target.getName().equals(element.getClassName())) {
               foundCaller = true;
            } else if (foundCaller) {
               int elementsToAdd = depth - index;
               if (maxDepth > 0 && maxDepth < elementsToAdd) {
                  elementsToAdd = maxDepth;
               }

               StackTraceElement[] syntheticStack = new StackTraceElement[elementsToAdd];
               syntheticStack[0] = element;

               for (int n = 1; n < elementsToAdd; n++) {
                  syntheticStack[n] = stackGetter != null ? stackGetter.getStackTraceElement(throwable, index + n) : stack[index + n];
               }

               return syntheticStack;
            }
         }

         return new StackTraceElement[0];
      }
   }
}
