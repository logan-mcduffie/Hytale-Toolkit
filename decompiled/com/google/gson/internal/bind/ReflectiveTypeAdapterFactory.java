package com.google.gson.internal.bind;

import com.google.gson.FieldNamingStrategy;
import com.google.gson.Gson;
import com.google.gson.JsonIOException;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSyntaxException;
import com.google.gson.ReflectionAccessFilter;
import com.google.gson.TypeAdapter;
import com.google.gson.TypeAdapterFactory;
import com.google.gson.annotations.JsonAdapter;
import com.google.gson.annotations.SerializedName;
import com.google.gson.internal.ConstructorConstructor;
import com.google.gson.internal.Excluder;
import com.google.gson.internal.GsonTypes;
import com.google.gson.internal.ObjectConstructor;
import com.google.gson.internal.Primitives;
import com.google.gson.internal.ReflectionAccessFilterHelper;
import com.google.gson.internal.TroubleshootingGuide;
import com.google.gson.internal.reflect.ReflectionHelper;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;
import java.io.IOException;
import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public final class ReflectiveTypeAdapterFactory implements TypeAdapterFactory {
   private final ConstructorConstructor constructorConstructor;
   private final FieldNamingStrategy fieldNamingPolicy;
   private final Excluder excluder;
   private final JsonAdapterAnnotationTypeAdapterFactory jsonAdapterFactory;
   private final List<ReflectionAccessFilter> reflectionFilters;

   public ReflectiveTypeAdapterFactory(
      ConstructorConstructor constructorConstructor,
      FieldNamingStrategy fieldNamingPolicy,
      Excluder excluder,
      JsonAdapterAnnotationTypeAdapterFactory jsonAdapterFactory,
      List<ReflectionAccessFilter> reflectionFilters
   ) {
      this.constructorConstructor = constructorConstructor;
      this.fieldNamingPolicy = fieldNamingPolicy;
      this.excluder = excluder;
      this.jsonAdapterFactory = jsonAdapterFactory;
      this.reflectionFilters = reflectionFilters;
   }

   private boolean includeField(Field f, boolean serialize) {
      return !this.excluder.excludeField(f, serialize);
   }

   private List<String> getFieldNames(Field f) {
      SerializedName annotation = f.getAnnotation(SerializedName.class);
      String fieldName;
      List<String> alternates;
      if (annotation == null) {
         fieldName = this.fieldNamingPolicy.translateName(f);
         alternates = this.fieldNamingPolicy.alternateNames(f);
      } else {
         fieldName = annotation.value();
         alternates = Arrays.asList(annotation.alternate());
      }

      if (alternates.isEmpty()) {
         return Collections.singletonList(fieldName);
      } else {
         List<String> fieldNames = new ArrayList<>(alternates.size() + 1);
         fieldNames.add(fieldName);
         fieldNames.addAll(alternates);
         return fieldNames;
      }
   }

   @Override
   public <T> TypeAdapter<T> create(Gson gson, TypeToken<T> type) {
      Class<? super T> raw = type.getRawType();
      if (!Object.class.isAssignableFrom(raw)) {
         return null;
      } else if (ReflectionHelper.isAnonymousOrNonStaticLocal(raw)) {
         return new TypeAdapter<T>() {
            @Override
            public T read(JsonReader in) throws IOException {
               in.skipValue();
               return null;
            }

            @Override
            public void write(JsonWriter out, T value) throws IOException {
               out.nullValue();
            }

            @Override
            public String toString() {
               return "AnonymousOrNonStaticLocalClassAdapter";
            }
         };
      } else {
         ReflectionAccessFilter.FilterResult filterResult = ReflectionAccessFilterHelper.getFilterResult(this.reflectionFilters, raw);
         if (filterResult == ReflectionAccessFilter.FilterResult.BLOCK_ALL) {
            throw new JsonIOException(
               "ReflectionAccessFilter does not permit using reflection for " + raw + ". Register a TypeAdapter for this type or adjust the access filter."
            );
         } else {
            boolean blockInaccessible = filterResult == ReflectionAccessFilter.FilterResult.BLOCK_INACCESSIBLE;
            if (ReflectionHelper.isRecord(raw)) {
               TypeAdapter<T> adapter = new ReflectiveTypeAdapterFactory.RecordAdapter<>(
                  (Class<T>)raw, this.getBoundFields(gson, type, raw, blockInaccessible, true), blockInaccessible
               );
               return adapter;
            } else {
               ObjectConstructor<T> constructor = this.constructorConstructor.get(type, true);
               return new ReflectiveTypeAdapterFactory.FieldReflectionAdapter<>(constructor, this.getBoundFields(gson, type, raw, blockInaccessible, false));
            }
         }
      }
   }

   private static <M extends AccessibleObject & Member> void checkAccessible(Object object, M member) {
      if (!ReflectionAccessFilterHelper.canAccess(member, Modifier.isStatic(member.getModifiers()) ? null : object)) {
         String memberDescription = ReflectionHelper.getAccessibleObjectDescription(member, true);
         throw new JsonIOException(
            memberDescription
               + " is not accessible and ReflectionAccessFilter does not permit making it accessible. Register a TypeAdapter for the declaring type, adjust the access filter or increase the visibility of the element and its declaring type."
         );
      }
   }

   private ReflectiveTypeAdapterFactory.BoundField createBoundField(
      Gson context, Field field, final Method accessor, String serializedName, TypeToken<?> fieldType, boolean serialize, final boolean blockInaccessible
   ) {
      final boolean isPrimitive = Primitives.isPrimitive(fieldType.getRawType());
      int modifiers = field.getModifiers();
      final boolean isStaticFinalField = Modifier.isStatic(modifiers) && Modifier.isFinal(modifiers);
      JsonAdapter annotation = field.getAnnotation(JsonAdapter.class);
      final TypeAdapter<?> mapped = null;
      if (annotation != null) {
         mapped = this.jsonAdapterFactory.getTypeAdapter(this.constructorConstructor, context, fieldType, annotation, false);
      }

      boolean jsonAdapterPresent = mapped != null;
      if (mapped == null) {
         mapped = context.getAdapter(fieldType);
      }

      final TypeAdapter<Object> writeTypeAdapter;
      if (serialize) {
         writeTypeAdapter = (TypeAdapter<Object>)(jsonAdapterPresent
            ? mapped
            : new TypeAdapterRuntimeTypeWrapper<>(context, (TypeAdapter<Object>)mapped, fieldType.getType()));
      } else {
         writeTypeAdapter = (TypeAdapter<Object>)mapped;
      }

      return new ReflectiveTypeAdapterFactory.BoundField(serializedName, field) {
         @Override
         void write(JsonWriter writer, Object source) throws IOException, IllegalAccessException {
            if (blockInaccessible) {
               if (accessor == null) {
                  ReflectiveTypeAdapterFactory.checkAccessible(source, this.field);
               } else {
                  ReflectiveTypeAdapterFactory.checkAccessible(source, accessor);
               }
            }

            Object fieldValue;
            if (accessor != null) {
               try {
                  fieldValue = accessor.invoke(source);
               } catch (InvocationTargetException var6) {
                  String accessorDescription = ReflectionHelper.getAccessibleObjectDescription(accessor, false);
                  throw new JsonIOException("Accessor " + accessorDescription + " threw exception", var6.getCause());
               }
            } else {
               fieldValue = this.field.get(source);
            }

            if (fieldValue != source) {
               writer.name(this.serializedName);
               writeTypeAdapter.write(writer, fieldValue);
            }
         }

         @Override
         void readIntoArray(JsonReader reader, int index, Object[] target) throws IOException, JsonParseException {
            Object fieldValue = mapped.read(reader);
            if (fieldValue == null && isPrimitive) {
               throw new JsonParseException(
                  "null is not allowed as value for record component '" + this.fieldName + "' of primitive type; at path " + reader.getPath()
               );
            } else {
               target[index] = fieldValue;
            }
         }

         @Override
         void readIntoField(JsonReader reader, Object target) throws IOException, IllegalAccessException {
            Object fieldValue = mapped.read(reader);
            if (fieldValue != null || !isPrimitive) {
               if (blockInaccessible) {
                  ReflectiveTypeAdapterFactory.checkAccessible(target, this.field);
               } else if (isStaticFinalField) {
                  String fieldDescription = ReflectionHelper.getAccessibleObjectDescription(this.field, false);
                  throw new JsonIOException("Cannot set value of 'static final' " + fieldDescription);
               }

               this.field.set(target, fieldValue);
            }
         }
      };
   }

   private static IllegalArgumentException createDuplicateFieldException(Class<?> declaringType, String duplicateName, Field field1, Field field2) {
      throw new IllegalArgumentException(
         "Class "
            + declaringType.getName()
            + " declares multiple JSON fields named '"
            + duplicateName
            + "'; conflict is caused by fields "
            + ReflectionHelper.fieldToString(field1)
            + " and "
            + ReflectionHelper.fieldToString(field2)
            + "\nSee "
            + TroubleshootingGuide.createUrl("duplicate-fields")
      );
   }

   private ReflectiveTypeAdapterFactory.FieldsData getBoundFields(Gson context, TypeToken<?> type, Class<?> raw, boolean blockInaccessible, boolean isRecord) {
      if (raw.isInterface()) {
         return ReflectiveTypeAdapterFactory.FieldsData.EMPTY;
      } else {
         Map<String, ReflectiveTypeAdapterFactory.BoundField> deserializedFields = new LinkedHashMap<>();
         Map<String, ReflectiveTypeAdapterFactory.BoundField> serializedFields = new LinkedHashMap<>();
         Class<?> originalRaw = raw;

         while (raw != Object.class) {
            Field[] fields = raw.getDeclaredFields();
            if (raw != originalRaw && fields.length > 0) {
               ReflectionAccessFilter.FilterResult filterResult = ReflectionAccessFilterHelper.getFilterResult(this.reflectionFilters, raw);
               if (filterResult == ReflectionAccessFilter.FilterResult.BLOCK_ALL) {
                  throw new JsonIOException(
                     "ReflectionAccessFilter does not permit using reflection for "
                        + raw
                        + " (supertype of "
                        + originalRaw
                        + "). Register a TypeAdapter for this type or adjust the access filter."
                  );
               }

               blockInaccessible = filterResult == ReflectionAccessFilter.FilterResult.BLOCK_INACCESSIBLE;
            }

            for (Field field : fields) {
               boolean serialize = this.includeField(field, true);
               boolean deserialize = this.includeField(field, false);
               if (serialize || deserialize) {
                  Method accessor = null;
                  if (isRecord) {
                     if (Modifier.isStatic(field.getModifiers())) {
                        deserialize = false;
                     } else {
                        accessor = ReflectionHelper.getAccessor(raw, field);
                        if (!blockInaccessible) {
                           ReflectionHelper.makeAccessible(accessor);
                        }

                        if (accessor.getAnnotation(SerializedName.class) != null && field.getAnnotation(SerializedName.class) == null) {
                           String methodDescription = ReflectionHelper.getAccessibleObjectDescription(accessor, false);
                           throw new JsonIOException("@SerializedName on " + methodDescription + " is not supported");
                        }
                     }
                  }

                  if (!blockInaccessible && accessor == null) {
                     ReflectionHelper.makeAccessible(field);
                  }

                  Type fieldType = GsonTypes.resolve(type.getType(), raw, field.getGenericType());
                  List<String> fieldNames = this.getFieldNames(field);
                  String serializedName = fieldNames.get(0);
                  ReflectiveTypeAdapterFactory.BoundField boundField = this.createBoundField(
                     context, field, accessor, serializedName, TypeToken.get(fieldType), serialize, blockInaccessible
                  );
                  if (deserialize) {
                     for (String name : fieldNames) {
                        ReflectiveTypeAdapterFactory.BoundField replaced = deserializedFields.put(name, boundField);
                        if (replaced != null) {
                           throw createDuplicateFieldException(originalRaw, name, replaced.field, field);
                        }
                     }
                  }

                  if (serialize) {
                     ReflectiveTypeAdapterFactory.BoundField replaced = serializedFields.put(serializedName, boundField);
                     if (replaced != null) {
                        throw createDuplicateFieldException(originalRaw, serializedName, replaced.field, field);
                     }
                  }
               }
            }

            type = TypeToken.get(GsonTypes.resolve(type.getType(), raw, raw.getGenericSuperclass()));
            raw = type.getRawType();
         }

         return new ReflectiveTypeAdapterFactory.FieldsData(deserializedFields, new ArrayList<>(serializedFields.values()));
      }
   }

   public abstract static class Adapter<T, A> extends TypeAdapter<T> {
      private final ReflectiveTypeAdapterFactory.FieldsData fieldsData;

      Adapter(ReflectiveTypeAdapterFactory.FieldsData fieldsData) {
         this.fieldsData = fieldsData;
      }

      @Override
      public void write(JsonWriter out, T value) throws IOException {
         if (value == null) {
            out.nullValue();
         } else {
            out.beginObject();

            try {
               for (ReflectiveTypeAdapterFactory.BoundField boundField : this.fieldsData.serializedFields) {
                  boundField.write(out, value);
               }
            } catch (IllegalAccessException var5) {
               throw ReflectionHelper.createExceptionForUnexpectedIllegalAccess(var5);
            }

            out.endObject();
         }
      }

      @Override
      public T read(JsonReader in) throws IOException {
         if (in.peek() == JsonToken.NULL) {
            in.nextNull();
            return null;
         } else {
            A accumulator = this.createAccumulator();
            Map<String, ReflectiveTypeAdapterFactory.BoundField> deserializedFields = this.fieldsData.deserializedFields;

            try {
               in.beginObject();

               while (in.hasNext()) {
                  String name = in.nextName();
                  ReflectiveTypeAdapterFactory.BoundField field = deserializedFields.get(name);
                  if (field == null) {
                     in.skipValue();
                  } else {
                     this.readField(accumulator, in, field);
                  }
               }
            } catch (IllegalStateException var6) {
               throw new JsonSyntaxException(var6);
            } catch (IllegalAccessException var7) {
               throw ReflectionHelper.createExceptionForUnexpectedIllegalAccess(var7);
            }

            in.endObject();
            return this.finalize(accumulator);
         }
      }

      abstract A createAccumulator();

      abstract void readField(A var1, JsonReader var2, ReflectiveTypeAdapterFactory.BoundField var3) throws IllegalAccessException, IOException;

      abstract T finalize(A var1);
   }

   abstract static class BoundField {
      final String serializedName;
      final Field field;
      final String fieldName;

      protected BoundField(String serializedName, Field field) {
         this.serializedName = serializedName;
         this.field = field;
         this.fieldName = field.getName();
      }

      abstract void write(JsonWriter var1, Object var2) throws IOException, IllegalAccessException;

      abstract void readIntoArray(JsonReader var1, int var2, Object[] var3) throws IOException, JsonParseException;

      abstract void readIntoField(JsonReader var1, Object var2) throws IOException, IllegalAccessException;
   }

   private static final class FieldReflectionAdapter<T> extends ReflectiveTypeAdapterFactory.Adapter<T, T> {
      private final ObjectConstructor<T> constructor;

      FieldReflectionAdapter(ObjectConstructor<T> constructor, ReflectiveTypeAdapterFactory.FieldsData fieldsData) {
         super(fieldsData);
         this.constructor = constructor;
      }

      @Override
      T createAccumulator() {
         return this.constructor.construct();
      }

      @Override
      void readField(T accumulator, JsonReader in, ReflectiveTypeAdapterFactory.BoundField field) throws IllegalAccessException, IOException {
         field.readIntoField(in, accumulator);
      }

      @Override
      T finalize(T accumulator) {
         return accumulator;
      }
   }

   private static class FieldsData {
      static final ReflectiveTypeAdapterFactory.FieldsData EMPTY = new ReflectiveTypeAdapterFactory.FieldsData(Collections.emptyMap(), Collections.emptyList());
      final Map<String, ReflectiveTypeAdapterFactory.BoundField> deserializedFields;
      final List<ReflectiveTypeAdapterFactory.BoundField> serializedFields;

      FieldsData(Map<String, ReflectiveTypeAdapterFactory.BoundField> deserializedFields, List<ReflectiveTypeAdapterFactory.BoundField> serializedFields) {
         this.deserializedFields = deserializedFields;
         this.serializedFields = serializedFields;
      }
   }

   private static final class RecordAdapter<T> extends ReflectiveTypeAdapterFactory.Adapter<T, Object[]> {
      static final Map<Class<?>, Object> PRIMITIVE_DEFAULTS = primitiveDefaults();
      private final Constructor<T> constructor;
      private final Object[] constructorArgsDefaults;
      private final Map<String, Integer> componentIndices = new HashMap<>();

      RecordAdapter(Class<T> raw, ReflectiveTypeAdapterFactory.FieldsData fieldsData, boolean blockInaccessible) {
         super(fieldsData);
         this.constructor = ReflectionHelper.getCanonicalRecordConstructor(raw);
         if (blockInaccessible) {
            ReflectiveTypeAdapterFactory.checkAccessible(null, this.constructor);
         } else {
            ReflectionHelper.makeAccessible(this.constructor);
         }

         String[] componentNames = ReflectionHelper.getRecordComponentNames(raw);

         for (int i = 0; i < componentNames.length; i++) {
            this.componentIndices.put(componentNames[i], i);
         }

         Class<?>[] parameterTypes = this.constructor.getParameterTypes();
         this.constructorArgsDefaults = new Object[parameterTypes.length];

         for (int i = 0; i < parameterTypes.length; i++) {
            this.constructorArgsDefaults[i] = PRIMITIVE_DEFAULTS.get(parameterTypes[i]);
         }
      }

      private static Map<Class<?>, Object> primitiveDefaults() {
         Map<Class<?>, Object> zeroes = new HashMap<>();
         zeroes.put(byte.class, (byte)0);
         zeroes.put(short.class, (short)0);
         zeroes.put(int.class, 0);
         zeroes.put(long.class, 0L);
         zeroes.put(float.class, 0.0F);
         zeroes.put(double.class, 0.0);
         zeroes.put(char.class, '\u0000');
         zeroes.put(boolean.class, false);
         return zeroes;
      }

      Object[] createAccumulator() {
         return (Object[])this.constructorArgsDefaults.clone();
      }

      void readField(Object[] accumulator, JsonReader in, ReflectiveTypeAdapterFactory.BoundField field) throws IOException {
         Integer componentIndex = this.componentIndices.get(field.fieldName);
         if (componentIndex == null) {
            throw new IllegalStateException(
               "Could not find the index in the constructor '"
                  + ReflectionHelper.constructorToString(this.constructor)
                  + "' for field with name '"
                  + field.fieldName
                  + "', unable to determine which argument in the constructor the field corresponds to. This is unexpected behavior, as we expect the RecordComponents to have the same names as the fields in the Java class, and that the order of the RecordComponents is the same as the order of the canonical constructor parameters."
            );
         } else {
            field.readIntoArray(in, componentIndex, accumulator);
         }
      }

      T finalize(Object[] accumulator) {
         try {
            return this.constructor.newInstance(accumulator);
         } catch (IllegalAccessException var3) {
            throw ReflectionHelper.createExceptionForUnexpectedIllegalAccess(var3);
         } catch (IllegalArgumentException | InstantiationException var4) {
            throw new RuntimeException(
               "Failed to invoke constructor '" + ReflectionHelper.constructorToString(this.constructor) + "' with args " + Arrays.toString(accumulator), var4
            );
         } catch (InvocationTargetException var5) {
            throw new RuntimeException(
               "Failed to invoke constructor '" + ReflectionHelper.constructorToString(this.constructor) + "' with args " + Arrays.toString(accumulator),
               var5.getCause()
            );
         }
      }
   }
}
