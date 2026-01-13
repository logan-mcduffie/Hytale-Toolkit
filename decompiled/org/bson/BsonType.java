package org.bson;

public enum BsonType {
   END_OF_DOCUMENT(0),
   DOUBLE(1),
   STRING(2),
   DOCUMENT(3),
   ARRAY(4),
   BINARY(5),
   UNDEFINED(6),
   OBJECT_ID(7),
   BOOLEAN(8),
   DATE_TIME(9),
   NULL(10),
   REGULAR_EXPRESSION(11),
   DB_POINTER(12),
   JAVASCRIPT(13),
   SYMBOL(14),
   JAVASCRIPT_WITH_SCOPE(15),
   INT32(16),
   TIMESTAMP(17),
   INT64(18),
   DECIMAL128(19),
   MIN_KEY(255),
   MAX_KEY(127);

   private static final BsonType[] LOOKUP_TABLE = new BsonType[MIN_KEY.getValue() + 1];
   private final int value;

   private BsonType(int value) {
      this.value = value;
   }

   public int getValue() {
      return this.value;
   }

   public static BsonType findByValue(int value) {
      return LOOKUP_TABLE[value & 0xFF];
   }

   public boolean isContainer() {
      return this == DOCUMENT || this == ARRAY;
   }

   static {
      for (BsonType cur : values()) {
         LOOKUP_TABLE[cur.getValue()] = cur;
      }
   }
}
