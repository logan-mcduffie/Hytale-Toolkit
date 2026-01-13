package io.sentry.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.ApiStatus.Internal;

@Internal
public final class CollectionUtils {
   private CollectionUtils() {
   }

   public static int size(@NotNull Iterable<?> data) {
      if (data instanceof Collection) {
         return ((Collection)data).size();
      } else {
         int counter = 0;

         for (Object ignored : data) {
            counter++;
         }

         return counter;
      }
   }

   @Nullable
   public static <K, V> Map<K, V> newConcurrentHashMap(@Nullable Map<K, V> map) {
      if (map != null) {
         Map<K, V> concurrentMap = new ConcurrentHashMap<>();

         for (Entry<K, V> entry : map.entrySet()) {
            if (entry.getKey() != null && entry.getValue() != null) {
               concurrentMap.put(entry.getKey(), entry.getValue());
            }
         }

         return concurrentMap;
      } else {
         return null;
      }
   }

   @Nullable
   public static <K, V> Map<K, @NotNull V> newHashMap(@Nullable Map<K, @NotNull V> map) {
      return map != null ? new HashMap<>(map) : null;
   }

   @Nullable
   public static <T> List<T> newArrayList(@Nullable List<T> list) {
      return list != null ? new ArrayList<>(list) : null;
   }

   @NotNull
   public static <K, V> Map<K, V> filterMapEntries(@NotNull Map<K, V> map, @NotNull CollectionUtils.Predicate<Entry<K, V>> predicate) {
      Map<K, V> filteredMap = new HashMap<>();

      for (Entry<K, V> entry : map.entrySet()) {
         if (predicate.test(entry)) {
            filteredMap.put(entry.getKey(), entry.getValue());
         }
      }

      return filteredMap;
   }

   @NotNull
   public static <T, R> List<R> map(@NotNull List<T> list, @NotNull CollectionUtils.Mapper<T, R> f) {
      List<R> mappedList = new ArrayList<>(list.size());

      for (T t : list) {
         mappedList.add(f.map(t));
      }

      return mappedList;
   }

   @NotNull
   public static <T> List<T> filterListEntries(@NotNull List<T> list, @NotNull CollectionUtils.Predicate<T> predicate) {
      List<T> filteredList = new ArrayList<>(list.size());

      for (T entry : list) {
         if (predicate.test(entry)) {
            filteredList.add(entry);
         }
      }

      return filteredList;
   }

   public static <T> boolean contains(@NotNull T[] array, @NotNull T element) {
      for (T t : array) {
         if (element.equals(t)) {
            return true;
         }
      }

      return false;
   }

   @NotNull
   public static <T> ListIterator<T> reverseListIterator(@NotNull CopyOnWriteArrayList<T> list) {
      CopyOnWriteArrayList<T> copy = new CopyOnWriteArrayList<>(list);
      return copy.listIterator(copy.size());
   }

   public interface Mapper<T, R> {
      R map(T var1);
   }

   public interface Predicate<T> {
      boolean test(T var1);
   }
}
