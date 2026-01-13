package com.google.gson.internal;

import com.google.gson.InstanceCreator;
import com.google.gson.JsonIOException;
import com.google.gson.ReflectionAccessFilter;
import com.google.gson.internal.reflect.ReflectionHelper;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collection;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentSkipListMap;

public final class ConstructorConstructor {
   private final Map<Type, InstanceCreator<?>> instanceCreators;
   private final boolean useJdkUnsafe;
   private final List<ReflectionAccessFilter> reflectionFilters;

   public ConstructorConstructor(Map<Type, InstanceCreator<?>> instanceCreators, boolean useJdkUnsafe, List<ReflectionAccessFilter> reflectionFilters) {
      this.instanceCreators = instanceCreators;
      this.useJdkUnsafe = useJdkUnsafe;
      this.reflectionFilters = reflectionFilters;
   }

   static String checkInstantiable(Class<?> c) {
      int modifiers = c.getModifiers();
      if (Modifier.isInterface(modifiers)) {
         return "Interfaces can't be instantiated! Register an InstanceCreator or a TypeAdapter for this type. Interface name: " + c.getName();
      } else {
         return Modifier.isAbstract(modifiers)
            ? "Abstract classes can't be instantiated! Adjust the R8 configuration or register an InstanceCreator or a TypeAdapter for this type. Class name: "
               + c.getName()
               + "\nSee "
               + TroubleshootingGuide.createUrl("r8-abstract-class")
            : null;
      }
   }

   public <T> ObjectConstructor<T> get(TypeToken<T> typeToken) {
      return this.get(typeToken, true);
   }

   public <T> ObjectConstructor<T> get(TypeToken<T> typeToken, boolean allowUnsafe) {
      Type type = typeToken.getType();
      Class<? super T> rawType = typeToken.getRawType();
      InstanceCreator<T> typeCreator = (InstanceCreator<T>)this.instanceCreators.get(type);
      if (typeCreator != null) {
         return () -> typeCreator.createInstance(type);
      } else {
         InstanceCreator<T> rawTypeCreator = (InstanceCreator<T>)this.instanceCreators.get(rawType);
         if (rawTypeCreator != null) {
            return () -> rawTypeCreator.createInstance(type);
         } else {
            ObjectConstructor<T> specialConstructor = newSpecialCollectionConstructor(type, rawType);
            if (specialConstructor != null) {
               return specialConstructor;
            } else {
               ReflectionAccessFilter.FilterResult filterResult = ReflectionAccessFilterHelper.getFilterResult(this.reflectionFilters, rawType);
               ObjectConstructor<T> defaultConstructor = newDefaultConstructor(rawType, filterResult);
               if (defaultConstructor != null) {
                  return defaultConstructor;
               } else {
                  ObjectConstructor<T> defaultImplementation = newDefaultImplementationConstructor(type, rawType);
                  if (defaultImplementation != null) {
                     return defaultImplementation;
                  } else {
                     String exceptionMessage = checkInstantiable(rawType);
                     if (exceptionMessage != null) {
                        return () -> {
                           throw new JsonIOException(exceptionMessage);
                        };
                     } else if (!allowUnsafe) {
                        String message = "Unable to create instance of " + rawType + "; Register an InstanceCreator or a TypeAdapter for this type.";
                        return () -> {
                           throw new JsonIOException(message);
                        };
                     } else if (filterResult != ReflectionAccessFilter.FilterResult.ALLOW) {
                        String message = "Unable to create instance of "
                           + rawType
                           + "; ReflectionAccessFilter does not permit using reflection or Unsafe. Register an InstanceCreator or a TypeAdapter for this type or adjust the access filter to allow using reflection.";
                        return () -> {
                           throw new JsonIOException(message);
                        };
                     } else {
                        return this.newUnsafeAllocator(rawType);
                     }
                  }
               }
            }
         }
      }
   }

   private static <T> ObjectConstructor<T> newSpecialCollectionConstructor(Type type, Class<? super T> rawType) {
      if (EnumSet.class.isAssignableFrom(rawType)) {
         return () -> {
            if (type instanceof ParameterizedType) {
               Type elementType = ((ParameterizedType)type).getActualTypeArguments()[0];
               if (elementType instanceof Class) {
                  return (T)EnumSet.noneOf((Class)elementType);
               } else {
                  throw new JsonIOException("Invalid EnumSet type: " + type.toString());
               }
            } else {
               throw new JsonIOException("Invalid EnumSet type: " + type.toString());
            }
         };
      } else {
         return rawType == EnumMap.class ? () -> {
            if (type instanceof ParameterizedType) {
               Type elementType = ((ParameterizedType)type).getActualTypeArguments()[0];
               if (elementType instanceof Class) {
                  return (T)(new EnumMap((Class)elementType));
               } else {
                  throw new JsonIOException("Invalid EnumMap type: " + type.toString());
               }
            } else {
               throw new JsonIOException("Invalid EnumMap type: " + type.toString());
            }
         } : null;
      }
   }

   private static <T> ObjectConstructor<T> newDefaultConstructor(Class<? super T> rawType, ReflectionAccessFilter.FilterResult filterResult) {
      if (Modifier.isAbstract(rawType.getModifiers())) {
         return null;
      } else {
         Constructor<? super T> constructor;
         try {
            constructor = rawType.getDeclaredConstructor();
         } catch (NoSuchMethodException var5) {
            return null;
         }

         boolean canAccess = filterResult == ReflectionAccessFilter.FilterResult.ALLOW
            || ReflectionAccessFilterHelper.canAccess(constructor, null)
               && (filterResult != ReflectionAccessFilter.FilterResult.BLOCK_ALL || Modifier.isPublic(constructor.getModifiers()));
         if (!canAccess) {
            String message = "Unable to invoke no-args constructor of "
               + rawType
               + "; constructor is not accessible and ReflectionAccessFilter does not permit making it accessible. Register an InstanceCreator or a TypeAdapter for this type, change the visibility of the constructor or adjust the access filter.";
            return () -> {
               throw new JsonIOException(message);
            };
         } else {
            if (filterResult == ReflectionAccessFilter.FilterResult.ALLOW) {
               String exceptionMessage = ReflectionHelper.tryMakeAccessible(constructor);
               if (exceptionMessage != null) {
                  return () -> {
                     throw new JsonIOException(exceptionMessage);
                  };
               }
            }

            return () -> {
               try {
                  return (T)constructor.newInstance();
               } catch (InstantiationException var2x) {
                  throw new RuntimeException("Failed to invoke constructor '" + ReflectionHelper.constructorToString(constructor) + "' with no args", var2x);
               } catch (InvocationTargetException var3x) {
                  throw new RuntimeException(
                     "Failed to invoke constructor '" + ReflectionHelper.constructorToString(constructor) + "' with no args", var3x.getCause()
                  );
               } catch (IllegalAccessException var4x) {
                  throw ReflectionHelper.createExceptionForUnexpectedIllegalAccess(var4x);
               }
            };
         }
      }
   }

   private static <T> ObjectConstructor<T> newDefaultImplementationConstructor(Type type, Class<? super T> rawType) {
      if (Collection.class.isAssignableFrom(rawType)) {
         return (ObjectConstructor<T>)newCollectionConstructor(rawType);
      } else {
         return (ObjectConstructor<T>)(Map.class.isAssignableFrom(rawType) ? newMapConstructor(type, rawType) : null);
      }
   }

   private static ObjectConstructor<? extends Collection<? extends Object>> newCollectionConstructor(Class<?> rawType) {
      if (rawType.isAssignableFrom(ArrayList.class)) {
         return () -> new ArrayList<>();
      } else if (rawType.isAssignableFrom(LinkedHashSet.class)) {
         return () -> new LinkedHashSet<>();
      } else if (rawType.isAssignableFrom(TreeSet.class)) {
         return () -> new TreeSet<>();
      } else {
         return rawType.isAssignableFrom(ArrayDeque.class) ? () -> new ArrayDeque<>() : null;
      }
   }

   private static boolean hasStringKeyType(Type mapType) {
      if (!(mapType instanceof ParameterizedType)) {
         return true;
      } else {
         Type[] typeArguments = ((ParameterizedType)mapType).getActualTypeArguments();
         return typeArguments.length == 0 ? false : GsonTypes.getRawType(typeArguments[0]) == String.class;
      }
   }

   private static ObjectConstructor<? extends Map<? extends Object, Object>> newMapConstructor(Type type, Class<?> rawType) {
      if (rawType.isAssignableFrom(LinkedTreeMap.class) && hasStringKeyType(type)) {
         return () -> new LinkedTreeMap<>();
      } else if (rawType.isAssignableFrom(LinkedHashMap.class)) {
         return () -> new LinkedHashMap<>();
      } else if (rawType.isAssignableFrom(TreeMap.class)) {
         return () -> new TreeMap<>();
      } else if (rawType.isAssignableFrom(ConcurrentHashMap.class)) {
         return () -> new ConcurrentHashMap<>();
      } else {
         return rawType.isAssignableFrom(ConcurrentSkipListMap.class) ? () -> new ConcurrentSkipListMap<>() : null;
      }
   }

   private <T> ObjectConstructor<T> newUnsafeAllocator(Class<? super T> rawType) {
      if (this.useJdkUnsafe) {
         return () -> {
            try {
               return UnsafeAllocator.INSTANCE.newInstance((Class<T>)rawType);
            } catch (Exception var2x) {
               throw new RuntimeException(
                  "Unable to create instance of "
                     + rawType
                     + ". Registering an InstanceCreator or a TypeAdapter for this type, or adding a no-args constructor may fix this problem.",
                  var2x
               );
            }
         };
      } else {
         String exceptionMessage = "Unable to create instance of "
            + rawType
            + "; usage of JDK Unsafe is disabled. Registering an InstanceCreator or a TypeAdapter for this type, adding a no-args constructor, or enabling usage of JDK Unsafe may fix this problem.";
         if (rawType.getDeclaredConstructors().length == 0) {
            exceptionMessage = exceptionMessage + " Or adjust your R8 configuration to keep the no-args constructor of the class.";
         }

         String exceptionMessageF = exceptionMessage;
         return () -> {
            throw new JsonIOException(exceptionMessageF);
         };
      }
   }

   @Override
   public String toString() {
      return this.instanceCreators.toString();
   }
}
