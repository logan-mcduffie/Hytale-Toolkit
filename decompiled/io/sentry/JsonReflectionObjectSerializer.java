package io.sentry;

import io.sentry.util.JsonSerializationUtils;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.net.InetAddress;
import java.net.URI;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Currency;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicIntegerArray;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.ApiStatus.Internal;

@Internal
public final class JsonReflectionObjectSerializer {
   private final Set<Object> visiting = new HashSet<>();
   private final int maxDepth;

   JsonReflectionObjectSerializer(int maxDepth) {
      this.maxDepth = maxDepth;
   }

   @Nullable
   public Object serialize(@Nullable Object object, @NotNull ILogger logger) throws Exception {
      if (object == null) {
         return null;
      } else if (object instanceof Character) {
         return object.toString();
      } else if (object instanceof Number) {
         return object;
      } else if (object instanceof Boolean) {
         return object;
      } else if (object instanceof String) {
         return object;
      } else if (object instanceof Locale) {
         return object.toString();
      } else if (object instanceof AtomicIntegerArray) {
         return JsonSerializationUtils.atomicIntegerArrayToList((AtomicIntegerArray)object);
      } else if (object instanceof AtomicBoolean) {
         return ((AtomicBoolean)object).get();
      } else if (object instanceof URI) {
         return object.toString();
      } else if (object instanceof InetAddress) {
         return object.toString();
      } else if (object instanceof UUID) {
         return object.toString();
      } else if (object instanceof Currency) {
         return object.toString();
      } else if (object instanceof Calendar) {
         return JsonSerializationUtils.calendarToMap((Calendar)object);
      } else if (object.getClass().isEnum()) {
         return object.toString();
      } else if (this.visiting.contains(object)) {
         logger.log(SentryLevel.INFO, "Cyclic reference detected. Calling toString() on object.");
         return object.toString();
      } else {
         this.visiting.add(object);
         if (this.visiting.size() > this.maxDepth) {
            this.visiting.remove(object);
            logger.log(SentryLevel.INFO, "Max depth exceeded. Calling toString() on object.");
            return object.toString();
         } else {
            Object serializedObject = null;

            try {
               if (object.getClass().isArray()) {
                  serializedObject = this.list((Object[])object, logger);
               } else if (object instanceof Collection) {
                  serializedObject = this.list((Collection<?>)object, logger);
               } else if (object instanceof Map) {
                  serializedObject = this.map((Map<?, ?>)object, logger);
               } else {
                  Map<String, Object> objectAsMap = this.serializeObject(object, logger);
                  if (objectAsMap.isEmpty()) {
                     serializedObject = object.toString();
                  } else {
                     serializedObject = objectAsMap;
                  }
               }
            } catch (Exception var8) {
               logger.log(SentryLevel.INFO, "Not serializing object due to throwing sub-path.", var8);
            } finally {
               this.visiting.remove(object);
            }

            return serializedObject;
         }
      }
   }

   @NotNull
   public Map<String, Object> serializeObject(@NotNull Object object, @NotNull ILogger logger) throws Exception {
      Field[] fields = object.getClass().getDeclaredFields();
      Map<String, Object> map = new HashMap<>();

      for (Field field : fields) {
         if (!Modifier.isTransient(field.getModifiers()) && !Modifier.isStatic(field.getModifiers())) {
            String fieldName = field.getName();

            try {
               field.setAccessible(true);
               Object fieldObject = field.get(object);
               map.put(fieldName, this.serialize(fieldObject, logger));
               field.setAccessible(false);
            } catch (Exception var11) {
               logger.log(SentryLevel.INFO, "Cannot access field " + fieldName + ".");
            }
         }
      }

      return map;
   }

   @NotNull
   private List<Object> list(@NotNull Object[] objectArray, @NotNull ILogger logger) throws Exception {
      List<Object> list = new ArrayList<>();

      for (Object object : objectArray) {
         list.add(this.serialize(object, logger));
      }

      return list;
   }

   @NotNull
   private List<Object> list(@NotNull Collection<?> collection, @NotNull ILogger logger) throws Exception {
      List<Object> list = new ArrayList<>();

      for (Object object : collection) {
         list.add(this.serialize(object, logger));
      }

      return list;
   }

   @NotNull
   private Map<String, Object> map(@NotNull Map<?, ?> map, @NotNull ILogger logger) throws Exception {
      Map<String, Object> hashMap = new HashMap<>();

      for (Object key : map.keySet()) {
         Object object = map.get(key);
         if (object != null) {
            hashMap.put(key.toString(), this.serialize(object, logger));
         } else {
            hashMap.put(key.toString(), null);
         }
      }

      return hashMap;
   }
}
