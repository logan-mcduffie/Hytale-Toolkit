package org.bson.codecs;

import java.util.HashMap;
import java.util.Map;
import org.bson.BsonArray;
import org.bson.BsonBinary;
import org.bson.BsonBoolean;
import org.bson.BsonDateTime;
import org.bson.BsonDbPointer;
import org.bson.BsonDecimal128;
import org.bson.BsonDocument;
import org.bson.BsonDocumentWrapper;
import org.bson.BsonDouble;
import org.bson.BsonInt32;
import org.bson.BsonInt64;
import org.bson.BsonJavaScript;
import org.bson.BsonJavaScriptWithScope;
import org.bson.BsonMaxKey;
import org.bson.BsonMinKey;
import org.bson.BsonNull;
import org.bson.BsonObjectId;
import org.bson.BsonRegularExpression;
import org.bson.BsonString;
import org.bson.BsonSymbol;
import org.bson.BsonTimestamp;
import org.bson.BsonType;
import org.bson.BsonUndefined;
import org.bson.BsonValue;
import org.bson.RawBsonDocument;
import org.bson.codecs.configuration.CodecProvider;
import org.bson.codecs.configuration.CodecRegistry;

public class BsonValueCodecProvider implements CodecProvider {
   private static final BsonTypeClassMap DEFAULT_BSON_TYPE_CLASS_MAP;
   private final Map<Class<?>, Codec<?>> codecs = new HashMap<>();

   public BsonValueCodecProvider() {
      this.addCodecs();
   }

   public static Class<? extends BsonValue> getClassForBsonType(BsonType bsonType) {
      return (Class<? extends BsonValue>)DEFAULT_BSON_TYPE_CLASS_MAP.get(bsonType);
   }

   public static BsonTypeClassMap getBsonTypeClassMap() {
      return DEFAULT_BSON_TYPE_CLASS_MAP;
   }

   @Override
   public <T> Codec<T> get(Class<T> clazz, CodecRegistry registry) {
      if (this.codecs.containsKey(clazz)) {
         return (Codec<T>)this.codecs.get(clazz);
      } else if (clazz == BsonJavaScriptWithScope.class) {
         return new BsonJavaScriptWithScopeCodec(registry.get(BsonDocument.class));
      } else if (clazz == BsonValue.class) {
         return new BsonValueCodec(registry);
      } else if (clazz == BsonDocumentWrapper.class) {
         return new BsonDocumentWrapperCodec(registry.get(BsonDocument.class));
      } else if (clazz == RawBsonDocument.class) {
         return new RawBsonDocumentCodec();
      } else if (BsonDocument.class.isAssignableFrom(clazz)) {
         return new BsonDocumentCodec(registry);
      } else {
         return BsonArray.class.isAssignableFrom(clazz) ? new BsonArrayCodec(registry) : null;
      }
   }

   private void addCodecs() {
      this.addCodec(new BsonNullCodec());
      this.addCodec(new BsonBinaryCodec());
      this.addCodec(new BsonBooleanCodec());
      this.addCodec(new BsonDateTimeCodec());
      this.addCodec(new BsonDBPointerCodec());
      this.addCodec(new BsonDoubleCodec());
      this.addCodec(new BsonInt32Codec());
      this.addCodec(new BsonInt64Codec());
      this.addCodec(new BsonDecimal128Codec());
      this.addCodec(new BsonMinKeyCodec());
      this.addCodec(new BsonMaxKeyCodec());
      this.addCodec(new BsonJavaScriptCodec());
      this.addCodec(new BsonObjectIdCodec());
      this.addCodec(new BsonRegularExpressionCodec());
      this.addCodec(new BsonStringCodec());
      this.addCodec(new BsonSymbolCodec());
      this.addCodec(new BsonTimestampCodec());
      this.addCodec(new BsonUndefinedCodec());
   }

   private <T extends BsonValue> void addCodec(Codec<T> codec) {
      this.codecs.put(codec.getEncoderClass(), codec);
   }

   static {
      Map<BsonType, Class<?>> map = new HashMap<>();
      map.put(BsonType.NULL, BsonNull.class);
      map.put(BsonType.ARRAY, BsonArray.class);
      map.put(BsonType.BINARY, BsonBinary.class);
      map.put(BsonType.BOOLEAN, BsonBoolean.class);
      map.put(BsonType.DATE_TIME, BsonDateTime.class);
      map.put(BsonType.DB_POINTER, BsonDbPointer.class);
      map.put(BsonType.DOCUMENT, BsonDocument.class);
      map.put(BsonType.DOUBLE, BsonDouble.class);
      map.put(BsonType.INT32, BsonInt32.class);
      map.put(BsonType.INT64, BsonInt64.class);
      map.put(BsonType.DECIMAL128, BsonDecimal128.class);
      map.put(BsonType.MAX_KEY, BsonMaxKey.class);
      map.put(BsonType.MIN_KEY, BsonMinKey.class);
      map.put(BsonType.JAVASCRIPT, BsonJavaScript.class);
      map.put(BsonType.JAVASCRIPT_WITH_SCOPE, BsonJavaScriptWithScope.class);
      map.put(BsonType.OBJECT_ID, BsonObjectId.class);
      map.put(BsonType.REGULAR_EXPRESSION, BsonRegularExpression.class);
      map.put(BsonType.STRING, BsonString.class);
      map.put(BsonType.SYMBOL, BsonSymbol.class);
      map.put(BsonType.TIMESTAMP, BsonTimestamp.class);
      map.put(BsonType.UNDEFINED, BsonUndefined.class);
      DEFAULT_BSON_TYPE_CLASS_MAP = new BsonTypeClassMap(map);
   }
}
