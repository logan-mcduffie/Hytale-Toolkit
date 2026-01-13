package com.google.common.flogger.backend;

import com.google.common.flogger.MetadataKey;
import org.checkerframework.checker.nullness.compatqual.NullableDecl;

public abstract class Metadata {
   public static Metadata empty() {
      return Metadata.Empty.INSTANCE;
   }

   public abstract int size();

   public abstract MetadataKey<?> getKey(int var1);

   public abstract Object getValue(int var1);

   @NullableDecl
   public abstract <T> T findValue(MetadataKey<T> var1);

   private static final class Empty extends Metadata {
      static final Metadata.Empty INSTANCE = new Metadata.Empty();

      @Override
      public int size() {
         return 0;
      }

      @Override
      public MetadataKey<?> getKey(int n) {
         throw new IndexOutOfBoundsException("cannot read from empty metadata");
      }

      @Override
      public Object getValue(int n) {
         throw new IndexOutOfBoundsException("cannot read from empty metadata");
      }

      @NullableDecl
      @Override
      public <T> T findValue(MetadataKey<T> key) {
         return null;
      }
   }
}
