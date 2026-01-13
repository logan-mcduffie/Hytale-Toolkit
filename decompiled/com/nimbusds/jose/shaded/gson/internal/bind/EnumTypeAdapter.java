package com.nimbusds.jose.shaded.gson.internal.bind;

import com.nimbusds.jose.shaded.gson.Gson;
import com.nimbusds.jose.shaded.gson.TypeAdapter;
import com.nimbusds.jose.shaded.gson.TypeAdapterFactory;
import com.nimbusds.jose.shaded.gson.annotations.SerializedName;
import com.nimbusds.jose.shaded.gson.reflect.TypeToken;
import com.nimbusds.jose.shaded.gson.stream.JsonReader;
import com.nimbusds.jose.shaded.gson.stream.JsonToken;
import com.nimbusds.jose.shaded.gson.stream.JsonWriter;
import java.io.IOException;
import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

class EnumTypeAdapter<T extends Enum<T>> extends TypeAdapter<T> {
   static final TypeAdapterFactory FACTORY = new TypeAdapterFactory() {
      @Override
      public <T> TypeAdapter<T> create(Gson gson, TypeToken<T> typeToken) {
         Class<? super T> rawType = typeToken.getRawType();
         if (Enum.class.isAssignableFrom(rawType) && rawType != Enum.class) {
            if (!rawType.isEnum()) {
               rawType = rawType.getSuperclass();
            }

            TypeAdapter<T> adapter = new EnumTypeAdapter<>(rawType);
            return adapter;
         } else {
            return null;
         }
      }
   };
   private final Map<String, T> nameToConstant = new HashMap<>();
   private final Map<String, T> stringToConstant = new HashMap<>();
   private final Map<T, String> constantToName = new HashMap<>();

   private EnumTypeAdapter(Class<T> classOfT) {
      try {
         Field[] fields = classOfT.getDeclaredFields();
         int constantCount = 0;

         for (Field f : fields) {
            if (f.isEnumConstant()) {
               fields[constantCount++] = f;
            }
         }

         fields = Arrays.copyOf(fields, constantCount);
         AccessibleObject.setAccessible(fields, true);

         for (Field constantField : fields) {
            T constant = (T)constantField.get(null);
            String name = constant.name();
            String toStringVal = constant.toString();
            SerializedName annotation = constantField.getAnnotation(SerializedName.class);
            if (annotation != null) {
               name = annotation.value();

               for (String alternate : annotation.alternate()) {
                  this.nameToConstant.put(alternate, constant);
               }
            }

            this.nameToConstant.put(name, constant);
            this.stringToConstant.put(toStringVal, constant);
            this.constantToName.put(constant, name);
         }
      } catch (IllegalAccessException var16) {
         throw new AssertionError(var16);
      }
   }

   public T read(JsonReader in) throws IOException {
      if (in.peek() == JsonToken.NULL) {
         in.nextNull();
         return null;
      } else {
         String key = in.nextString();
         T constant = this.nameToConstant.get(key);
         return constant == null ? this.stringToConstant.get(key) : constant;
      }
   }

   public void write(JsonWriter out, T value) throws IOException {
      out.value(value == null ? null : this.constantToName.get(value));
   }
}
