package com.google.gson;

import com.google.gson.internal.ReflectionAccessFilterHelper;

public interface ReflectionAccessFilter {
   ReflectionAccessFilter BLOCK_INACCESSIBLE_JAVA = new ReflectionAccessFilter() {
      @Override
      public ReflectionAccessFilter.FilterResult check(Class<?> rawClass) {
         return ReflectionAccessFilterHelper.isJavaType(rawClass)
            ? ReflectionAccessFilter.FilterResult.BLOCK_INACCESSIBLE
            : ReflectionAccessFilter.FilterResult.INDECISIVE;
      }

      @Override
      public String toString() {
         return "ReflectionAccessFilter#BLOCK_INACCESSIBLE_JAVA";
      }
   };
   ReflectionAccessFilter BLOCK_ALL_JAVA = new ReflectionAccessFilter() {
      @Override
      public ReflectionAccessFilter.FilterResult check(Class<?> rawClass) {
         return ReflectionAccessFilterHelper.isJavaType(rawClass)
            ? ReflectionAccessFilter.FilterResult.BLOCK_ALL
            : ReflectionAccessFilter.FilterResult.INDECISIVE;
      }

      @Override
      public String toString() {
         return "ReflectionAccessFilter#BLOCK_ALL_JAVA";
      }
   };
   ReflectionAccessFilter BLOCK_ALL_ANDROID = new ReflectionAccessFilter() {
      @Override
      public ReflectionAccessFilter.FilterResult check(Class<?> rawClass) {
         return ReflectionAccessFilterHelper.isAndroidType(rawClass)
            ? ReflectionAccessFilter.FilterResult.BLOCK_ALL
            : ReflectionAccessFilter.FilterResult.INDECISIVE;
      }

      @Override
      public String toString() {
         return "ReflectionAccessFilter#BLOCK_ALL_ANDROID";
      }
   };
   ReflectionAccessFilter BLOCK_ALL_PLATFORM = new ReflectionAccessFilter() {
      @Override
      public ReflectionAccessFilter.FilterResult check(Class<?> rawClass) {
         return ReflectionAccessFilterHelper.isAnyPlatformType(rawClass)
            ? ReflectionAccessFilter.FilterResult.BLOCK_ALL
            : ReflectionAccessFilter.FilterResult.INDECISIVE;
      }

      @Override
      public String toString() {
         return "ReflectionAccessFilter#BLOCK_ALL_PLATFORM";
      }
   };

   ReflectionAccessFilter.FilterResult check(Class<?> var1);

   public static enum FilterResult {
      ALLOW,
      INDECISIVE,
      BLOCK_INACCESSIBLE,
      BLOCK_ALL;
   }
}
