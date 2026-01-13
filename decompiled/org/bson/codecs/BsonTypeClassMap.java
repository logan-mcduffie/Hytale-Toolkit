package org.bson.codecs;

import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.bson.BsonDbPointer;
import org.bson.BsonRegularExpression;
import org.bson.BsonTimestamp;
import org.bson.BsonType;
import org.bson.BsonUndefined;
import org.bson.Document;
import org.bson.types.Binary;
import org.bson.types.Code;
import org.bson.types.CodeWithScope;
import org.bson.types.Decimal128;
import org.bson.types.MaxKey;
import org.bson.types.MinKey;
import org.bson.types.ObjectId;
import org.bson.types.Symbol;

public class BsonTypeClassMap {
   static final BsonTypeClassMap DEFAULT_BSON_TYPE_CLASS_MAP = new BsonTypeClassMap();
   private final Map<BsonType, Class<?>> map = new HashMap<>();

   public BsonTypeClassMap(Map<BsonType, Class<?>> replacementsForDefaults) {
      this.addDefaults();
      this.map.putAll(replacementsForDefaults);
   }

   public BsonTypeClassMap() {
      this(Collections.emptyMap());
   }

   Set<BsonType> keys() {
      return this.map.keySet();
   }

   public Class<?> get(BsonType bsonType) {
      return this.map.get(bsonType);
   }

   private void addDefaults() {
      this.map.put(BsonType.ARRAY, List.class);
      this.map.put(BsonType.BINARY, Binary.class);
      this.map.put(BsonType.BOOLEAN, Boolean.class);
      this.map.put(BsonType.DATE_TIME, Date.class);
      this.map.put(BsonType.DB_POINTER, BsonDbPointer.class);
      this.map.put(BsonType.DOCUMENT, Document.class);
      this.map.put(BsonType.DOUBLE, Double.class);
      this.map.put(BsonType.INT32, Integer.class);
      this.map.put(BsonType.INT64, Long.class);
      this.map.put(BsonType.DECIMAL128, Decimal128.class);
      this.map.put(BsonType.MAX_KEY, MaxKey.class);
      this.map.put(BsonType.MIN_KEY, MinKey.class);
      this.map.put(BsonType.JAVASCRIPT, Code.class);
      this.map.put(BsonType.JAVASCRIPT_WITH_SCOPE, CodeWithScope.class);
      this.map.put(BsonType.OBJECT_ID, ObjectId.class);
      this.map.put(BsonType.REGULAR_EXPRESSION, BsonRegularExpression.class);
      this.map.put(BsonType.STRING, String.class);
      this.map.put(BsonType.SYMBOL, Symbol.class);
      this.map.put(BsonType.TIMESTAMP, BsonTimestamp.class);
      this.map.put(BsonType.UNDEFINED, BsonUndefined.class);
   }

   @Override
   public boolean equals(Object o) {
      if (this == o) {
         return true;
      } else if (o != null && this.getClass() == o.getClass()) {
         BsonTypeClassMap that = (BsonTypeClassMap)o;
         return this.map.equals(that.map);
      } else {
         return false;
      }
   }

   @Override
   public int hashCode() {
      return this.map.hashCode();
   }
}
