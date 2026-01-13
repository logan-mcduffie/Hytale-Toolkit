package com.google.common.flogger.context;

import com.google.common.flogger.MetadataKey;
import com.google.common.flogger.backend.Metadata;
import com.google.common.flogger.util.Checks;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.checkerframework.checker.nullness.compatqual.NullableDecl;

public abstract class ScopeMetadata extends Metadata {
   public static ScopeMetadata.Builder builder() {
      return new ScopeMetadata.Builder();
   }

   public static <T> ScopeMetadata singleton(MetadataKey<T> key, T value) {
      return new ScopeMetadata.SingletonMetadata(key, value);
   }

   public static ScopeMetadata none() {
      return ScopeMetadata.EmptyMetadata.INSTANCE;
   }

   private ScopeMetadata() {
   }

   public abstract ScopeMetadata concatenate(ScopeMetadata var1);

   abstract ScopeMetadata.Entry<?> get(int var1);

   @Override
   public MetadataKey<?> getKey(int n) {
      return this.get(n).key;
   }

   @Override
   public Object getValue(int n) {
      return this.get(n).value;
   }

   public static final class Builder {
      private static final ScopeMetadata.Entry<?>[] EMPTY_ARRAY = new ScopeMetadata.Entry[0];
      private final List<ScopeMetadata.Entry<?>> entries = new ArrayList<>(2);

      private Builder() {
      }

      public <T> ScopeMetadata.Builder add(MetadataKey<T> key, T value) {
         this.entries.add(new ScopeMetadata.Entry<>(key, value));
         return this;
      }

      public ScopeMetadata build() {
         return new ScopeMetadata.ImmutableScopeMetadata(this.entries.toArray(EMPTY_ARRAY));
      }
   }

   private static final class EmptyMetadata extends ScopeMetadata {
      static final ScopeMetadata INSTANCE = new ScopeMetadata.EmptyMetadata();

      @Override
      public int size() {
         return 0;
      }

      @Override
      ScopeMetadata.Entry<?> get(int n) {
         throw new IndexOutOfBoundsException();
      }

      @NullableDecl
      @Override
      public <T> T findValue(MetadataKey<T> key) {
         Checks.checkArgument(!key.canRepeat(), "metadata key must be single valued");
         return null;
      }

      @Override
      public ScopeMetadata concatenate(ScopeMetadata metadata) {
         return metadata;
      }
   }

   private static final class Entry<T> {
      final MetadataKey<T> key;
      final T value;

      Entry(MetadataKey<T> key, T value) {
         this.key = Checks.checkNotNull(key, "key");
         this.value = Checks.checkNotNull(value, "value");
      }
   }

   private static final class ImmutableScopeMetadata extends ScopeMetadata {
      private final ScopeMetadata.Entry<?>[] entries;

      ImmutableScopeMetadata(ScopeMetadata.Entry<?>[] entries) {
         this.entries = entries;
      }

      @Override
      public int size() {
         return this.entries.length;
      }

      @Override
      ScopeMetadata.Entry<?> get(int n) {
         return this.entries[n];
      }

      @NullableDecl
      @Override
      public <T> T findValue(MetadataKey<T> key) {
         Checks.checkArgument(!key.canRepeat(), "metadata key must be single valued");

         for (int n = this.entries.length - 1; n >= 0; n--) {
            ScopeMetadata.Entry<?> e = this.entries[n];
            if (e.key.equals(key)) {
               return (T)e.value;
            }
         }

         return null;
      }

      @Override
      public ScopeMetadata concatenate(ScopeMetadata metadata) {
         int extraSize = metadata.size();
         if (extraSize == 0) {
            return this;
         } else if (this.entries.length == 0) {
            return metadata;
         } else {
            ScopeMetadata.Entry<?>[] merged = Arrays.copyOf(this.entries, this.entries.length + extraSize);

            for (int i = 0; i < extraSize; i++) {
               merged[i + this.entries.length] = metadata.get(i);
            }

            return new ScopeMetadata.ImmutableScopeMetadata(merged);
         }
      }
   }

   private static final class SingletonMetadata extends ScopeMetadata {
      private final ScopeMetadata.Entry<?> entry;

      <T> SingletonMetadata(MetadataKey<T> key, T value) {
         this.entry = new ScopeMetadata.Entry<>(key, value);
      }

      @Override
      public int size() {
         return 1;
      }

      @Override
      ScopeMetadata.Entry<?> get(int n) {
         if (n == 0) {
            return this.entry;
         } else {
            throw new IndexOutOfBoundsException();
         }
      }

      @NullableDecl
      @Override
      public <R> R findValue(MetadataKey<R> key) {
         Checks.checkArgument(!key.canRepeat(), "metadata key must be single valued");
         return (R)(this.entry.key.equals(key) ? this.entry.value : null);
      }

      @Override
      public ScopeMetadata concatenate(ScopeMetadata metadata) {
         int extraSize = metadata.size();
         if (extraSize == 0) {
            return this;
         } else {
            ScopeMetadata.Entry<?>[] merged = new ScopeMetadata.Entry[extraSize + 1];
            merged[0] = this.entry;

            for (int i = 0; i < extraSize; i++) {
               merged[i + 1] = metadata.get(i);
            }

            return new ScopeMetadata.ImmutableScopeMetadata(merged);
         }
      }
   }
}
