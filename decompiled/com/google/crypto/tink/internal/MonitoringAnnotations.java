package com.google.crypto.tink.internal;

import com.google.crypto.tink.annotations.Alpha;
import com.google.errorprone.annotations.CanIgnoreReturnValue;
import com.google.errorprone.annotations.Immutable;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@Immutable
@Alpha
public final class MonitoringAnnotations {
   public static final MonitoringAnnotations EMPTY = newBuilder().build();
   private final Map<String, String> entries;

   private MonitoringAnnotations(Map<String, String> entries) {
      this.entries = entries;
   }

   public static MonitoringAnnotations.Builder newBuilder() {
      return new MonitoringAnnotations.Builder();
   }

   public Map<String, String> toMap() {
      return this.entries;
   }

   public boolean isEmpty() {
      return this.entries.isEmpty();
   }

   @Override
   public boolean equals(Object obj) {
      if (!(obj instanceof MonitoringAnnotations)) {
         return false;
      } else {
         MonitoringAnnotations that = (MonitoringAnnotations)obj;
         return this.entries.equals(that.entries);
      }
   }

   @Override
   public int hashCode() {
      return this.entries.hashCode();
   }

   @Override
   public String toString() {
      return this.entries.toString();
   }

   public static final class Builder {
      private HashMap<String, String> builderEntries = new HashMap<>();

      @CanIgnoreReturnValue
      public MonitoringAnnotations.Builder addAll(Map<String, String> newEntries) {
         if (this.builderEntries == null) {
            throw new IllegalStateException("addAll cannot be called after build()");
         } else {
            this.builderEntries.putAll(newEntries);
            return this;
         }
      }

      @CanIgnoreReturnValue
      public MonitoringAnnotations.Builder add(String name, String value) {
         if (this.builderEntries == null) {
            throw new IllegalStateException("add cannot be called after build()");
         } else {
            this.builderEntries.put(name, value);
            return this;
         }
      }

      public MonitoringAnnotations build() {
         if (this.builderEntries == null) {
            throw new IllegalStateException("cannot call build() twice");
         } else {
            MonitoringAnnotations output = new MonitoringAnnotations(Collections.unmodifiableMap(this.builderEntries));
            this.builderEntries = null;
            return output;
         }
      }
   }
}
