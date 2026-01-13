package com.nimbusds.jose.shaded.gson.internal.bind;

import com.nimbusds.jose.shaded.gson.Gson;
import com.nimbusds.jose.shaded.gson.JsonElement;
import com.nimbusds.jose.shaded.gson.JsonIOException;
import com.nimbusds.jose.shaded.gson.JsonSyntaxException;
import com.nimbusds.jose.shaded.gson.TypeAdapter;
import com.nimbusds.jose.shaded.gson.TypeAdapterFactory;
import com.nimbusds.jose.shaded.gson.internal.LazilyParsedNumber;
import com.nimbusds.jose.shaded.gson.internal.NumberLimits;
import com.nimbusds.jose.shaded.gson.internal.TroubleshootingGuide;
import com.nimbusds.jose.shaded.gson.reflect.TypeToken;
import com.nimbusds.jose.shaded.gson.stream.JsonReader;
import com.nimbusds.jose.shaded.gson.stream.JsonToken;
import com.nimbusds.jose.shaded.gson.stream.JsonWriter;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.InetAddress;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.Calendar;
import java.util.Currency;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;
import java.util.StringTokenizer;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicIntegerArray;

public final class TypeAdapters {
   public static final TypeAdapter<Class> CLASS = (new TypeAdapter<Class>() {
         public void write(JsonWriter out, Class value) throws IOException {
            throw new UnsupportedOperationException(
               "Attempted to serialize java.lang.Class: "
                  + value.getName()
                  + ". Forgot to register a type adapter?\nSee "
                  + TroubleshootingGuide.createUrl("java-lang-class-unsupported")
            );
         }

         public Class read(JsonReader in) throws IOException {
            throw new UnsupportedOperationException(
               "Attempted to deserialize a java.lang.Class. Forgot to register a type adapter?\nSee "
                  + TroubleshootingGuide.createUrl("java-lang-class-unsupported")
            );
         }
      })
      .nullSafe();
   public static final TypeAdapterFactory CLASS_FACTORY = newFactory(Class.class, CLASS);
   public static final TypeAdapter<BitSet> BIT_SET = (new TypeAdapter<BitSet>() {
      public BitSet read(JsonReader in) throws IOException {
         BitSet bitset = new BitSet();
         in.beginArray();
         int i = 0;

         for (JsonToken tokenType = in.peek(); tokenType != JsonToken.END_ARRAY; tokenType = in.peek()) {
            boolean set;
            switch (tokenType) {
               case NUMBER:
               case STRING:
                  int intValue = in.nextInt();
                  if (intValue == 0) {
                     set = false;
                  } else {
                     if (intValue != 1) {
                        throw new JsonSyntaxException("Invalid bitset value " + intValue + ", expected 0 or 1; at path " + in.getPreviousPath());
                     }

                     set = true;
                  }
                  break;
               case BOOLEAN:
                  set = in.nextBoolean();
                  break;
               default:
                  throw new JsonSyntaxException("Invalid bitset value type: " + tokenType + "; at path " + in.getPath());
            }

            if (set) {
               bitset.set(i);
            }

            i++;
         }

         in.endArray();
         return bitset;
      }

      public void write(JsonWriter out, BitSet src) throws IOException {
         out.beginArray();
         int i = 0;

         for (int length = src.length(); i < length; i++) {
            int value = src.get(i) ? 1 : 0;
            out.value((long)value);
         }

         out.endArray();
      }
   }).nullSafe();
   public static final TypeAdapterFactory BIT_SET_FACTORY = newFactory(BitSet.class, BIT_SET);
   public static final TypeAdapter<Boolean> BOOLEAN = new TypeAdapter<Boolean>() {
      public Boolean read(JsonReader in) throws IOException {
         JsonToken peek = in.peek();
         if (peek == JsonToken.NULL) {
            in.nextNull();
            return null;
         } else {
            return peek == JsonToken.STRING ? Boolean.parseBoolean(in.nextString()) : in.nextBoolean();
         }
      }

      public void write(JsonWriter out, Boolean value) throws IOException {
         out.value(value);
      }
   };
   public static final TypeAdapter<Boolean> BOOLEAN_AS_STRING = new TypeAdapter<Boolean>() {
      public Boolean read(JsonReader in) throws IOException {
         if (in.peek() == JsonToken.NULL) {
            in.nextNull();
            return null;
         } else {
            return Boolean.valueOf(in.nextString());
         }
      }

      public void write(JsonWriter out, Boolean value) throws IOException {
         out.value(value == null ? "null" : value.toString());
      }
   };
   public static final TypeAdapterFactory BOOLEAN_FACTORY = newFactory(boolean.class, Boolean.class, BOOLEAN);
   public static final TypeAdapter<Number> BYTE = new TypeAdapter<Number>() {
      public Number read(JsonReader in) throws IOException {
         if (in.peek() == JsonToken.NULL) {
            in.nextNull();
            return null;
         } else {
            int intValue;
            try {
               intValue = in.nextInt();
            } catch (NumberFormatException var4) {
               throw new JsonSyntaxException(var4);
            }

            if (intValue <= 255 && intValue >= -128) {
               return (byte)intValue;
            } else {
               throw new JsonSyntaxException("Lossy conversion from " + intValue + " to byte; at path " + in.getPreviousPath());
            }
         }
      }

      public void write(JsonWriter out, Number value) throws IOException {
         if (value == null) {
            out.nullValue();
         } else {
            out.value((long)value.byteValue());
         }
      }
   };
   public static final TypeAdapterFactory BYTE_FACTORY = newFactory(byte.class, Byte.class, BYTE);
   public static final TypeAdapter<Number> SHORT = new TypeAdapter<Number>() {
      public Number read(JsonReader in) throws IOException {
         if (in.peek() == JsonToken.NULL) {
            in.nextNull();
            return null;
         } else {
            int intValue;
            try {
               intValue = in.nextInt();
            } catch (NumberFormatException var4) {
               throw new JsonSyntaxException(var4);
            }

            if (intValue <= 65535 && intValue >= -32768) {
               return (short)intValue;
            } else {
               throw new JsonSyntaxException("Lossy conversion from " + intValue + " to short; at path " + in.getPreviousPath());
            }
         }
      }

      public void write(JsonWriter out, Number value) throws IOException {
         if (value == null) {
            out.nullValue();
         } else {
            out.value((long)value.shortValue());
         }
      }
   };
   public static final TypeAdapterFactory SHORT_FACTORY = newFactory(short.class, Short.class, SHORT);
   public static final TypeAdapter<Number> INTEGER = new TypeAdapter<Number>() {
      public Number read(JsonReader in) throws IOException {
         if (in.peek() == JsonToken.NULL) {
            in.nextNull();
            return null;
         } else {
            try {
               return in.nextInt();
            } catch (NumberFormatException var3) {
               throw new JsonSyntaxException(var3);
            }
         }
      }

      public void write(JsonWriter out, Number value) throws IOException {
         if (value == null) {
            out.nullValue();
         } else {
            out.value((long)value.intValue());
         }
      }
   };
   public static final TypeAdapterFactory INTEGER_FACTORY = newFactory(int.class, Integer.class, INTEGER);
   public static final TypeAdapter<AtomicInteger> ATOMIC_INTEGER = (new TypeAdapter<AtomicInteger>() {
      public AtomicInteger read(JsonReader in) throws IOException {
         try {
            return new AtomicInteger(in.nextInt());
         } catch (NumberFormatException var3) {
            throw new JsonSyntaxException(var3);
         }
      }

      public void write(JsonWriter out, AtomicInteger value) throws IOException {
         out.value((long)value.get());
      }
   }).nullSafe();
   public static final TypeAdapterFactory ATOMIC_INTEGER_FACTORY = newFactory(AtomicInteger.class, ATOMIC_INTEGER);
   public static final TypeAdapter<AtomicBoolean> ATOMIC_BOOLEAN = (new TypeAdapter<AtomicBoolean>() {
      public AtomicBoolean read(JsonReader in) throws IOException {
         return new AtomicBoolean(in.nextBoolean());
      }

      public void write(JsonWriter out, AtomicBoolean value) throws IOException {
         out.value(value.get());
      }
   }).nullSafe();
   public static final TypeAdapterFactory ATOMIC_BOOLEAN_FACTORY = newFactory(AtomicBoolean.class, ATOMIC_BOOLEAN);
   public static final TypeAdapter<AtomicIntegerArray> ATOMIC_INTEGER_ARRAY = (new TypeAdapter<AtomicIntegerArray>() {
      public AtomicIntegerArray read(JsonReader in) throws IOException {
         List<Integer> list = new ArrayList<>();
         in.beginArray();

         while (in.hasNext()) {
            try {
               int integer = in.nextInt();
               list.add(integer);
            } catch (NumberFormatException var6) {
               throw new JsonSyntaxException(var6);
            }
         }

         in.endArray();
         int length = list.size();
         AtomicIntegerArray array = new AtomicIntegerArray(length);

         for (int i = 0; i < length; i++) {
            array.set(i, list.get(i));
         }

         return array;
      }

      public void write(JsonWriter out, AtomicIntegerArray value) throws IOException {
         out.beginArray();
         int i = 0;

         for (int length = value.length(); i < length; i++) {
            out.value((long)value.get(i));
         }

         out.endArray();
      }
   }).nullSafe();
   public static final TypeAdapterFactory ATOMIC_INTEGER_ARRAY_FACTORY = newFactory(AtomicIntegerArray.class, ATOMIC_INTEGER_ARRAY);
   public static final TypeAdapter<Number> LONG = new TypeAdapter<Number>() {
      public Number read(JsonReader in) throws IOException {
         if (in.peek() == JsonToken.NULL) {
            in.nextNull();
            return null;
         } else {
            try {
               return in.nextLong();
            } catch (NumberFormatException var3) {
               throw new JsonSyntaxException(var3);
            }
         }
      }

      public void write(JsonWriter out, Number value) throws IOException {
         if (value == null) {
            out.nullValue();
         } else {
            out.value(value.longValue());
         }
      }
   };
   public static final TypeAdapter<Number> FLOAT = new TypeAdapter<Number>() {
      public Number read(JsonReader in) throws IOException {
         if (in.peek() == JsonToken.NULL) {
            in.nextNull();
            return null;
         } else {
            return (float)in.nextDouble();
         }
      }

      public void write(JsonWriter out, Number value) throws IOException {
         if (value == null) {
            out.nullValue();
         } else {
            Number floatNumber = (Number)(value instanceof Float ? value : value.floatValue());
            out.value(floatNumber);
         }
      }
   };
   public static final TypeAdapter<Number> DOUBLE = new TypeAdapter<Number>() {
      public Number read(JsonReader in) throws IOException {
         if (in.peek() == JsonToken.NULL) {
            in.nextNull();
            return null;
         } else {
            return in.nextDouble();
         }
      }

      public void write(JsonWriter out, Number value) throws IOException {
         if (value == null) {
            out.nullValue();
         } else {
            out.value(value.doubleValue());
         }
      }
   };
   public static final TypeAdapter<Character> CHARACTER = new TypeAdapter<Character>() {
      public Character read(JsonReader in) throws IOException {
         if (in.peek() == JsonToken.NULL) {
            in.nextNull();
            return null;
         } else {
            String str = in.nextString();
            if (str.length() != 1) {
               throw new JsonSyntaxException("Expecting character, got: " + str + "; at " + in.getPreviousPath());
            } else {
               return str.charAt(0);
            }
         }
      }

      public void write(JsonWriter out, Character value) throws IOException {
         out.value(value == null ? null : String.valueOf(value));
      }
   };
   public static final TypeAdapterFactory CHARACTER_FACTORY = newFactory(char.class, Character.class, CHARACTER);
   public static final TypeAdapter<String> STRING = new TypeAdapter<String>() {
      public String read(JsonReader in) throws IOException {
         JsonToken peek = in.peek();
         if (peek == JsonToken.NULL) {
            in.nextNull();
            return null;
         } else {
            return peek == JsonToken.BOOLEAN ? Boolean.toString(in.nextBoolean()) : in.nextString();
         }
      }

      public void write(JsonWriter out, String value) throws IOException {
         out.value(value);
      }
   };
   public static final TypeAdapter<BigDecimal> BIG_DECIMAL = new TypeAdapter<BigDecimal>() {
      public BigDecimal read(JsonReader in) throws IOException {
         if (in.peek() == JsonToken.NULL) {
            in.nextNull();
            return null;
         } else {
            String s = in.nextString();

            try {
               return NumberLimits.parseBigDecimal(s);
            } catch (NumberFormatException var4) {
               throw new JsonSyntaxException("Failed parsing '" + s + "' as BigDecimal; at path " + in.getPreviousPath(), var4);
            }
         }
      }

      public void write(JsonWriter out, BigDecimal value) throws IOException {
         out.value(value);
      }
   };
   public static final TypeAdapter<BigInteger> BIG_INTEGER = new TypeAdapter<BigInteger>() {
      public BigInteger read(JsonReader in) throws IOException {
         if (in.peek() == JsonToken.NULL) {
            in.nextNull();
            return null;
         } else {
            String s = in.nextString();

            try {
               return NumberLimits.parseBigInteger(s);
            } catch (NumberFormatException var4) {
               throw new JsonSyntaxException("Failed parsing '" + s + "' as BigInteger; at path " + in.getPreviousPath(), var4);
            }
         }
      }

      public void write(JsonWriter out, BigInteger value) throws IOException {
         out.value(value);
      }
   };
   public static final TypeAdapter<LazilyParsedNumber> LAZILY_PARSED_NUMBER = new TypeAdapter<LazilyParsedNumber>() {
      public LazilyParsedNumber read(JsonReader in) throws IOException {
         if (in.peek() == JsonToken.NULL) {
            in.nextNull();
            return null;
         } else {
            return new LazilyParsedNumber(in.nextString());
         }
      }

      public void write(JsonWriter out, LazilyParsedNumber value) throws IOException {
         out.value(value);
      }
   };
   public static final TypeAdapterFactory STRING_FACTORY = newFactory(String.class, STRING);
   public static final TypeAdapter<StringBuilder> STRING_BUILDER = new TypeAdapter<StringBuilder>() {
      public StringBuilder read(JsonReader in) throws IOException {
         if (in.peek() == JsonToken.NULL) {
            in.nextNull();
            return null;
         } else {
            return new StringBuilder(in.nextString());
         }
      }

      public void write(JsonWriter out, StringBuilder value) throws IOException {
         out.value(value == null ? null : value.toString());
      }
   };
   public static final TypeAdapterFactory STRING_BUILDER_FACTORY = newFactory(StringBuilder.class, STRING_BUILDER);
   public static final TypeAdapter<StringBuffer> STRING_BUFFER = new TypeAdapter<StringBuffer>() {
      public StringBuffer read(JsonReader in) throws IOException {
         if (in.peek() == JsonToken.NULL) {
            in.nextNull();
            return null;
         } else {
            return new StringBuffer(in.nextString());
         }
      }

      public void write(JsonWriter out, StringBuffer value) throws IOException {
         out.value(value == null ? null : value.toString());
      }
   };
   public static final TypeAdapterFactory STRING_BUFFER_FACTORY = newFactory(StringBuffer.class, STRING_BUFFER);
   public static final TypeAdapter<URL> URL = new TypeAdapter<URL>() {
      public URL read(JsonReader in) throws IOException {
         if (in.peek() == JsonToken.NULL) {
            in.nextNull();
            return null;
         } else {
            String nextString = in.nextString();
            return nextString.equals("null") ? null : new URL(nextString);
         }
      }

      public void write(JsonWriter out, URL value) throws IOException {
         out.value(value == null ? null : value.toExternalForm());
      }
   };
   public static final TypeAdapterFactory URL_FACTORY = newFactory(URL.class, URL);
   public static final TypeAdapter<URI> URI = new TypeAdapter<URI>() {
      public URI read(JsonReader in) throws IOException {
         if (in.peek() == JsonToken.NULL) {
            in.nextNull();
            return null;
         } else {
            try {
               String nextString = in.nextString();
               return nextString.equals("null") ? null : new URI(nextString);
            } catch (URISyntaxException var3) {
               throw new JsonIOException(var3);
            }
         }
      }

      public void write(JsonWriter out, URI value) throws IOException {
         out.value(value == null ? null : value.toASCIIString());
      }
   };
   public static final TypeAdapterFactory URI_FACTORY = newFactory(URI.class, URI);
   public static final TypeAdapter<InetAddress> INET_ADDRESS = new TypeAdapter<InetAddress>() {
      public InetAddress read(JsonReader in) throws IOException {
         if (in.peek() == JsonToken.NULL) {
            in.nextNull();
            return null;
         } else {
            return InetAddress.getByName(in.nextString());
         }
      }

      public void write(JsonWriter out, InetAddress value) throws IOException {
         out.value(value == null ? null : value.getHostAddress());
      }
   };
   public static final TypeAdapterFactory INET_ADDRESS_FACTORY = newTypeHierarchyFactory(InetAddress.class, INET_ADDRESS);
   public static final TypeAdapter<UUID> UUID = new TypeAdapter<UUID>() {
      public UUID read(JsonReader in) throws IOException {
         if (in.peek() == JsonToken.NULL) {
            in.nextNull();
            return null;
         } else {
            String s = in.nextString();

            try {
               return java.util.UUID.fromString(s);
            } catch (IllegalArgumentException var4) {
               throw new JsonSyntaxException("Failed parsing '" + s + "' as UUID; at path " + in.getPreviousPath(), var4);
            }
         }
      }

      public void write(JsonWriter out, UUID value) throws IOException {
         out.value(value == null ? null : value.toString());
      }
   };
   public static final TypeAdapterFactory UUID_FACTORY = newFactory(UUID.class, UUID);
   public static final TypeAdapter<Currency> CURRENCY = (new TypeAdapter<Currency>() {
      public Currency read(JsonReader in) throws IOException {
         String s = in.nextString();

         try {
            return Currency.getInstance(s);
         } catch (IllegalArgumentException var4) {
            throw new JsonSyntaxException("Failed parsing '" + s + "' as Currency; at path " + in.getPreviousPath(), var4);
         }
      }

      public void write(JsonWriter out, Currency value) throws IOException {
         out.value(value.getCurrencyCode());
      }
   }).nullSafe();
   public static final TypeAdapterFactory CURRENCY_FACTORY = newFactory(Currency.class, CURRENCY);
   public static final TypeAdapter<Calendar> CALENDAR = new TypeAdapter<Calendar>() {
      private static final String YEAR = "year";
      private static final String MONTH = "month";
      private static final String DAY_OF_MONTH = "dayOfMonth";
      private static final String HOUR_OF_DAY = "hourOfDay";
      private static final String MINUTE = "minute";
      private static final String SECOND = "second";

      public Calendar read(JsonReader in) throws IOException {
         if (in.peek() == JsonToken.NULL) {
            in.nextNull();
            return null;
         } else {
            in.beginObject();
            int year = 0;
            int month = 0;
            int dayOfMonth = 0;
            int hourOfDay = 0;
            int minute = 0;
            int second = 0;

            while (in.peek() != JsonToken.END_OBJECT) {
               String name = in.nextName();
               int value = in.nextInt();
               switch (name) {
                  case "year":
                     year = value;
                     break;
                  case "month":
                     month = value;
                     break;
                  case "dayOfMonth":
                     dayOfMonth = value;
                     break;
                  case "hourOfDay":
                     hourOfDay = value;
                     break;
                  case "minute":
                     minute = value;
                     break;
                  case "second":
                     second = value;
               }
            }

            in.endObject();
            return new GregorianCalendar(year, month, dayOfMonth, hourOfDay, minute, second);
         }
      }

      public void write(JsonWriter out, Calendar value) throws IOException {
         if (value == null) {
            out.nullValue();
         } else {
            out.beginObject();
            out.name("year");
            out.value((long)value.get(1));
            out.name("month");
            out.value((long)value.get(2));
            out.name("dayOfMonth");
            out.value((long)value.get(5));
            out.name("hourOfDay");
            out.value((long)value.get(11));
            out.name("minute");
            out.value((long)value.get(12));
            out.name("second");
            out.value((long)value.get(13));
            out.endObject();
         }
      }
   };
   public static final TypeAdapterFactory CALENDAR_FACTORY = newFactoryForMultipleTypes(Calendar.class, GregorianCalendar.class, CALENDAR);
   public static final TypeAdapter<Locale> LOCALE = new TypeAdapter<Locale>() {
      public Locale read(JsonReader in) throws IOException {
         if (in.peek() == JsonToken.NULL) {
            in.nextNull();
            return null;
         } else {
            String locale = in.nextString();
            StringTokenizer tokenizer = new StringTokenizer(locale, "_");
            String language = null;
            String country = null;
            String variant = null;
            if (tokenizer.hasMoreElements()) {
               language = tokenizer.nextToken();
            }

            if (tokenizer.hasMoreElements()) {
               country = tokenizer.nextToken();
            }

            if (tokenizer.hasMoreElements()) {
               variant = tokenizer.nextToken();
            }

            if (country == null && variant == null) {
               return new Locale(language);
            } else {
               return variant == null ? new Locale(language, country) : new Locale(language, country, variant);
            }
         }
      }

      public void write(JsonWriter out, Locale value) throws IOException {
         out.value(value == null ? null : value.toString());
      }
   };
   public static final TypeAdapterFactory LOCALE_FACTORY = newFactory(Locale.class, LOCALE);
   public static final TypeAdapter<JsonElement> JSON_ELEMENT = JsonElementTypeAdapter.ADAPTER;
   public static final TypeAdapterFactory JSON_ELEMENT_FACTORY = newTypeHierarchyFactory(JsonElement.class, JSON_ELEMENT);
   public static final TypeAdapterFactory ENUM_FACTORY = EnumTypeAdapter.FACTORY;

   private TypeAdapters() {
      throw new UnsupportedOperationException();
   }

   public static <TT> TypeAdapterFactory newFactory(final TypeToken<TT> type, final TypeAdapter<TT> typeAdapter) {
      return new TypeAdapterFactory() {
         @Override
         public <T> TypeAdapter<T> create(Gson gson, TypeToken<T> typeToken) {
            return (TypeAdapter<T>)(typeToken.equals(type) ? typeAdapter : null);
         }
      };
   }

   public static <TT> TypeAdapterFactory newFactory(final Class<TT> type, final TypeAdapter<TT> typeAdapter) {
      return new TypeAdapterFactory() {
         @Override
         public <T> TypeAdapter<T> create(Gson gson, TypeToken<T> typeToken) {
            return (TypeAdapter<T>)(typeToken.getRawType() == type ? typeAdapter : null);
         }

         @Override
         public String toString() {
            return "Factory[type=" + type.getName() + ",adapter=" + typeAdapter + "]";
         }
      };
   }

   public static <TT> TypeAdapterFactory newFactory(final Class<TT> unboxed, final Class<TT> boxed, final TypeAdapter<? super TT> typeAdapter) {
      return new TypeAdapterFactory() {
         @Override
         public <T> TypeAdapter<T> create(Gson gson, TypeToken<T> typeToken) {
            Class<? super T> rawType = typeToken.getRawType();
            return (TypeAdapter<T>)(rawType != unboxed && rawType != boxed ? null : typeAdapter);
         }

         @Override
         public String toString() {
            return "Factory[type=" + boxed.getName() + "+" + unboxed.getName() + ",adapter=" + typeAdapter + "]";
         }
      };
   }

   public static <TT> TypeAdapterFactory newFactoryForMultipleTypes(
      final Class<TT> base, final Class<? extends TT> sub, final TypeAdapter<? super TT> typeAdapter
   ) {
      return new TypeAdapterFactory() {
         @Override
         public <T> TypeAdapter<T> create(Gson gson, TypeToken<T> typeToken) {
            Class<? super T> rawType = typeToken.getRawType();
            return (TypeAdapter<T>)(rawType != base && rawType != sub ? null : typeAdapter);
         }

         @Override
         public String toString() {
            return "Factory[type=" + base.getName() + "+" + sub.getName() + ",adapter=" + typeAdapter + "]";
         }
      };
   }

   public static <T1> TypeAdapterFactory newTypeHierarchyFactory(final Class<T1> clazz, final TypeAdapter<T1> typeAdapter) {
      return new TypeAdapterFactory() {
         @Override
         public <T2> TypeAdapter<T2> create(Gson gson, TypeToken<T2> typeToken) {
            final Class<? super T2> requestedType = typeToken.getRawType();
            return (TypeAdapter<T2>)(!clazz.isAssignableFrom(requestedType)
               ? null
               : new TypeAdapter<T1>() {
                  @Override
                  public void write(JsonWriter out, T1 value) throws IOException {
                     typeAdapter.write(out, value);
                  }

                  @Override
                  public T1 read(JsonReader in) throws IOException {
                     T1 result = typeAdapter.read(in);
                     if (result != null && !requestedType.isInstance(result)) {
                        throw new JsonSyntaxException(
                           "Expected a " + requestedType.getName() + " but was " + result.getClass().getName() + "; at path " + in.getPreviousPath()
                        );
                     } else {
                        return result;
                     }
                  }
               });
         }

         @Override
         public String toString() {
            return "Factory[typeHierarchy=" + clazz.getName() + ",adapter=" + typeAdapter + "]";
         }
      };
   }
}
