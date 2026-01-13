package org.bson.json;

import java.io.Reader;
import java.text.DateFormat;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.time.format.DateTimeParseException;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;
import java.util.UUID;
import org.bson.AbstractBsonReader;
import org.bson.BsonBinary;
import org.bson.BsonBinarySubType;
import org.bson.BsonContextType;
import org.bson.BsonDbPointer;
import org.bson.BsonInvalidOperationException;
import org.bson.BsonReaderMark;
import org.bson.BsonRegularExpression;
import org.bson.BsonTimestamp;
import org.bson.BsonType;
import org.bson.BsonUndefined;
import org.bson.internal.Base64;
import org.bson.types.Decimal128;
import org.bson.types.MaxKey;
import org.bson.types.MinKey;
import org.bson.types.ObjectId;

public class JsonReader extends AbstractBsonReader {
   private final JsonScanner scanner;
   private JsonToken pushedToken;
   private Object currentValue;

   public JsonReader(String json) {
      this(new JsonScanner(json));
   }

   public JsonReader(Reader reader) {
      this(new JsonScanner(reader));
   }

   private JsonReader(JsonScanner scanner) {
      this.scanner = scanner;
      this.setContext(new JsonReader.Context(null, BsonContextType.TOP_LEVEL));
   }

   @Override
   protected BsonBinary doReadBinaryData() {
      return (BsonBinary)this.currentValue;
   }

   @Override
   protected byte doPeekBinarySubType() {
      return this.doReadBinaryData().getType();
   }

   @Override
   protected int doPeekBinarySize() {
      return this.doReadBinaryData().getData().length;
   }

   @Override
   protected boolean doReadBoolean() {
      return (Boolean)this.currentValue;
   }

   @Override
   public BsonType readBsonType() {
      if (this.isClosed()) {
         throw new IllegalStateException("This instance has been closed");
      } else {
         if (this.getState() == AbstractBsonReader.State.INITIAL
            || this.getState() == AbstractBsonReader.State.DONE
            || this.getState() == AbstractBsonReader.State.SCOPE_DOCUMENT) {
            this.setState(AbstractBsonReader.State.TYPE);
         }

         if (this.getState() != AbstractBsonReader.State.TYPE) {
            this.throwInvalidState("readBSONType", AbstractBsonReader.State.TYPE);
         }

         if (this.getContext().getContextType() == BsonContextType.DOCUMENT) {
            JsonToken nameToken = this.popToken();
            switch (nameToken.getType()) {
               case STRING:
               case UNQUOTED_STRING:
                  this.setCurrentName(nameToken.getValue(String.class));
                  JsonToken colonToken = this.popToken();
                  if (colonToken.getType() != JsonTokenType.COLON) {
                     throw new JsonParseException("JSON reader was expecting ':' but found '%s'.", colonToken.getValue());
                  }
                  break;
               case END_OBJECT:
                  this.setState(AbstractBsonReader.State.END_OF_DOCUMENT);
                  return BsonType.END_OF_DOCUMENT;
               default:
                  throw new JsonParseException("JSON reader was expecting a name but found '%s'.", nameToken.getValue());
            }
         }

         JsonToken token = this.popToken();
         if (this.getContext().getContextType() == BsonContextType.ARRAY && token.getType() == JsonTokenType.END_ARRAY) {
            this.setState(AbstractBsonReader.State.END_OF_ARRAY);
            return BsonType.END_OF_DOCUMENT;
         } else {
            boolean noValueFound = false;
            switch (token.getType()) {
               case STRING:
                  this.setCurrentBsonType(BsonType.STRING);
                  this.currentValue = token.getValue();
                  break;
               case UNQUOTED_STRING:
                  String value = token.getValue(String.class);
                  if ("false".equals(value) || "true".equals(value)) {
                     this.setCurrentBsonType(BsonType.BOOLEAN);
                     this.currentValue = Boolean.parseBoolean(value);
                  } else if ("Infinity".equals(value)) {
                     this.setCurrentBsonType(BsonType.DOUBLE);
                     this.currentValue = Double.POSITIVE_INFINITY;
                  } else if ("NaN".equals(value)) {
                     this.setCurrentBsonType(BsonType.DOUBLE);
                     this.currentValue = Double.NaN;
                  } else if ("null".equals(value)) {
                     this.setCurrentBsonType(BsonType.NULL);
                  } else if ("undefined".equals(value)) {
                     this.setCurrentBsonType(BsonType.UNDEFINED);
                  } else if ("MinKey".equals(value)) {
                     this.visitEmptyConstructor();
                     this.setCurrentBsonType(BsonType.MIN_KEY);
                     this.currentValue = new MinKey();
                  } else if ("MaxKey".equals(value)) {
                     this.visitEmptyConstructor();
                     this.setCurrentBsonType(BsonType.MAX_KEY);
                     this.currentValue = new MaxKey();
                  } else if ("BinData".equals(value)) {
                     this.setCurrentBsonType(BsonType.BINARY);
                     this.currentValue = this.visitBinDataConstructor();
                  } else if ("Date".equals(value)) {
                     this.currentValue = this.visitDateTimeConstructorWithOutNew();
                     this.setCurrentBsonType(BsonType.STRING);
                  } else if ("HexData".equals(value)) {
                     this.setCurrentBsonType(BsonType.BINARY);
                     this.currentValue = this.visitHexDataConstructor();
                  } else if ("ISODate".equals(value)) {
                     this.setCurrentBsonType(BsonType.DATE_TIME);
                     this.currentValue = this.visitISODateTimeConstructor();
                  } else if ("NumberInt".equals(value)) {
                     this.setCurrentBsonType(BsonType.INT32);
                     this.currentValue = this.visitNumberIntConstructor();
                  } else if ("NumberLong".equals(value)) {
                     this.setCurrentBsonType(BsonType.INT64);
                     this.currentValue = this.visitNumberLongConstructor();
                  } else if ("NumberDecimal".equals(value)) {
                     this.setCurrentBsonType(BsonType.DECIMAL128);
                     this.currentValue = this.visitNumberDecimalConstructor();
                  } else if ("ObjectId".equals(value)) {
                     this.setCurrentBsonType(BsonType.OBJECT_ID);
                     this.currentValue = this.visitObjectIdConstructor();
                  } else if ("Timestamp".equals(value)) {
                     this.setCurrentBsonType(BsonType.TIMESTAMP);
                     this.currentValue = this.visitTimestampConstructor();
                  } else if ("RegExp".equals(value)) {
                     this.setCurrentBsonType(BsonType.REGULAR_EXPRESSION);
                     this.currentValue = this.visitRegularExpressionConstructor();
                  } else if ("DBPointer".equals(value)) {
                     this.setCurrentBsonType(BsonType.DB_POINTER);
                     this.currentValue = this.visitDBPointerConstructor();
                  } else if ("UUID".equals(value)) {
                     this.setCurrentBsonType(BsonType.BINARY);
                     this.currentValue = this.visitUUIDConstructor();
                  } else if ("new".equals(value)) {
                     this.visitNew();
                  } else {
                     noValueFound = true;
                  }
                  break;
               case END_OBJECT:
               default:
                  noValueFound = true;
                  break;
               case BEGIN_ARRAY:
                  this.setCurrentBsonType(BsonType.ARRAY);
                  break;
               case BEGIN_OBJECT:
                  this.visitExtendedJSON();
                  break;
               case DOUBLE:
                  this.setCurrentBsonType(BsonType.DOUBLE);
                  this.currentValue = token.getValue();
                  break;
               case END_OF_FILE:
                  this.setCurrentBsonType(BsonType.END_OF_DOCUMENT);
                  break;
               case INT32:
                  this.setCurrentBsonType(BsonType.INT32);
                  this.currentValue = token.getValue();
                  break;
               case INT64:
                  this.setCurrentBsonType(BsonType.INT64);
                  this.currentValue = token.getValue();
                  break;
               case REGULAR_EXPRESSION:
                  this.setCurrentBsonType(BsonType.REGULAR_EXPRESSION);
                  this.currentValue = token.getValue();
            }

            if (noValueFound) {
               throw new JsonParseException("JSON reader was expecting a value but found '%s'.", token.getValue());
            } else {
               if (this.getContext().getContextType() == BsonContextType.ARRAY || this.getContext().getContextType() == BsonContextType.DOCUMENT) {
                  JsonToken commaToken = this.popToken();
                  if (commaToken.getType() != JsonTokenType.COMMA) {
                     this.pushToken(commaToken);
                  }
               }

               switch (this.getContext().getContextType()) {
                  case DOCUMENT:
                  case SCOPE_DOCUMENT:
                  default:
                     this.setState(AbstractBsonReader.State.NAME);
                     break;
                  case ARRAY:
                  case JAVASCRIPT_WITH_SCOPE:
                  case TOP_LEVEL:
                     this.setState(AbstractBsonReader.State.VALUE);
               }

               return this.getCurrentBsonType();
            }
         }
      }
   }

   @Override
   public Decimal128 doReadDecimal128() {
      return (Decimal128)this.currentValue;
   }

   @Override
   protected long doReadDateTime() {
      return (Long)this.currentValue;
   }

   @Override
   protected double doReadDouble() {
      return (Double)this.currentValue;
   }

   @Override
   protected void doReadEndArray() {
      this.setContext(this.getContext().getParentContext());
      if (this.getContext().getContextType() == BsonContextType.ARRAY || this.getContext().getContextType() == BsonContextType.DOCUMENT) {
         JsonToken commaToken = this.popToken();
         if (commaToken.getType() != JsonTokenType.COMMA) {
            this.pushToken(commaToken);
         }
      }
   }

   @Override
   protected void doReadEndDocument() {
      this.setContext(this.getContext().getParentContext());
      if (this.getContext() != null && this.getContext().getContextType() == BsonContextType.SCOPE_DOCUMENT) {
         this.setContext(this.getContext().getParentContext());
         this.verifyToken(JsonTokenType.END_OBJECT);
      }

      if (this.getContext() == null) {
         throw new JsonParseException("Unexpected end of document.");
      } else {
         if (this.getContext().getContextType() == BsonContextType.ARRAY || this.getContext().getContextType() == BsonContextType.DOCUMENT) {
            JsonToken commaToken = this.popToken();
            if (commaToken.getType() != JsonTokenType.COMMA) {
               this.pushToken(commaToken);
            }
         }
      }
   }

   @Override
   protected int doReadInt32() {
      return (Integer)this.currentValue;
   }

   @Override
   protected long doReadInt64() {
      return (Long)this.currentValue;
   }

   @Override
   protected String doReadJavaScript() {
      return (String)this.currentValue;
   }

   @Override
   protected String doReadJavaScriptWithScope() {
      return (String)this.currentValue;
   }

   @Override
   protected void doReadMaxKey() {
   }

   @Override
   protected void doReadMinKey() {
   }

   @Override
   protected void doReadNull() {
   }

   @Override
   protected ObjectId doReadObjectId() {
      return (ObjectId)this.currentValue;
   }

   @Override
   protected BsonRegularExpression doReadRegularExpression() {
      return (BsonRegularExpression)this.currentValue;
   }

   @Override
   protected BsonDbPointer doReadDBPointer() {
      return (BsonDbPointer)this.currentValue;
   }

   @Override
   protected void doReadStartArray() {
      this.setContext(new JsonReader.Context(this.getContext(), BsonContextType.ARRAY));
   }

   @Override
   protected void doReadStartDocument() {
      this.setContext(new JsonReader.Context(this.getContext(), BsonContextType.DOCUMENT));
   }

   @Override
   protected String doReadString() {
      return (String)this.currentValue;
   }

   @Override
   protected String doReadSymbol() {
      return (String)this.currentValue;
   }

   @Override
   protected BsonTimestamp doReadTimestamp() {
      return (BsonTimestamp)this.currentValue;
   }

   @Override
   protected void doReadUndefined() {
   }

   @Override
   protected void doSkipName() {
   }

   @Override
   protected void doSkipValue() {
      switch (this.getCurrentBsonType()) {
         case ARRAY:
            this.readStartArray();

            while (this.readBsonType() != BsonType.END_OF_DOCUMENT) {
               this.skipValue();
            }

            this.readEndArray();
            break;
         case BINARY:
            this.readBinaryData();
            break;
         case BOOLEAN:
            this.readBoolean();
            break;
         case DATE_TIME:
            this.readDateTime();
            break;
         case DOCUMENT:
            this.readStartDocument();

            while (this.readBsonType() != BsonType.END_OF_DOCUMENT) {
               this.skipName();
               this.skipValue();
            }

            this.readEndDocument();
            break;
         case DOUBLE:
            this.readDouble();
            break;
         case INT32:
            this.readInt32();
            break;
         case INT64:
            this.readInt64();
            break;
         case DECIMAL128:
            this.readDecimal128();
            break;
         case JAVASCRIPT:
            this.readJavaScript();
            break;
         case JAVASCRIPT_WITH_SCOPE:
            this.readJavaScriptWithScope();
            this.readStartDocument();

            while (this.readBsonType() != BsonType.END_OF_DOCUMENT) {
               this.skipName();
               this.skipValue();
            }

            this.readEndDocument();
            break;
         case MAX_KEY:
            this.readMaxKey();
            break;
         case MIN_KEY:
            this.readMinKey();
            break;
         case NULL:
            this.readNull();
            break;
         case OBJECT_ID:
            this.readObjectId();
            break;
         case REGULAR_EXPRESSION:
            this.readRegularExpression();
            break;
         case STRING:
            this.readString();
            break;
         case SYMBOL:
            this.readSymbol();
            break;
         case TIMESTAMP:
            this.readTimestamp();
            break;
         case UNDEFINED:
            this.readUndefined();
      }
   }

   private JsonToken popToken() {
      if (this.pushedToken != null) {
         JsonToken token = this.pushedToken;
         this.pushedToken = null;
         return token;
      } else {
         return this.scanner.nextToken();
      }
   }

   private void pushToken(JsonToken token) {
      if (this.pushedToken == null) {
         this.pushedToken = token;
      } else {
         throw new BsonInvalidOperationException("There is already a pending token.");
      }
   }

   private void verifyToken(JsonTokenType expectedType) {
      JsonToken token = this.popToken();
      if (expectedType != token.getType()) {
         throw new JsonParseException("JSON reader expected token type '%s' but found '%s'.", expectedType, token.getValue());
      }
   }

   private void verifyToken(JsonTokenType expectedType, Object expectedValue) {
      JsonToken token = this.popToken();
      if (expectedType != token.getType()) {
         throw new JsonParseException("JSON reader expected token type '%s' but found '%s'.", expectedType, token.getValue());
      } else if (!expectedValue.equals(token.getValue())) {
         throw new JsonParseException("JSON reader expected '%s' but found '%s'.", expectedValue, token.getValue());
      }
   }

   private void verifyString(String expected) {
      if (expected == null) {
         throw new IllegalArgumentException("Can't be null");
      } else {
         JsonToken token = this.popToken();
         JsonTokenType type = token.getType();
         if (type != JsonTokenType.STRING && type != JsonTokenType.UNQUOTED_STRING || !expected.equals(token.getValue())) {
            throw new JsonParseException("JSON reader expected '%s' but found '%s'.", expected, token.getValue());
         }
      }
   }

   private void visitNew() {
      JsonToken typeToken = this.popToken();
      if (typeToken.getType() != JsonTokenType.UNQUOTED_STRING) {
         throw new JsonParseException("JSON reader expected a type name but found '%s'.", typeToken.getValue());
      } else {
         String value = typeToken.getValue(String.class);
         if ("MinKey".equals(value)) {
            this.visitEmptyConstructor();
            this.setCurrentBsonType(BsonType.MIN_KEY);
            this.currentValue = new MinKey();
         } else if ("MaxKey".equals(value)) {
            this.visitEmptyConstructor();
            this.setCurrentBsonType(BsonType.MAX_KEY);
            this.currentValue = new MaxKey();
         } else if ("BinData".equals(value)) {
            this.currentValue = this.visitBinDataConstructor();
            this.setCurrentBsonType(BsonType.BINARY);
         } else if ("Date".equals(value)) {
            this.currentValue = this.visitDateTimeConstructor();
            this.setCurrentBsonType(BsonType.DATE_TIME);
         } else if ("HexData".equals(value)) {
            this.currentValue = this.visitHexDataConstructor();
            this.setCurrentBsonType(BsonType.BINARY);
         } else if ("ISODate".equals(value)) {
            this.currentValue = this.visitISODateTimeConstructor();
            this.setCurrentBsonType(BsonType.DATE_TIME);
         } else if ("NumberInt".equals(value)) {
            this.currentValue = this.visitNumberIntConstructor();
            this.setCurrentBsonType(BsonType.INT32);
         } else if ("NumberLong".equals(value)) {
            this.currentValue = this.visitNumberLongConstructor();
            this.setCurrentBsonType(BsonType.INT64);
         } else if ("NumberDecimal".equals(value)) {
            this.currentValue = this.visitNumberDecimalConstructor();
            this.setCurrentBsonType(BsonType.DECIMAL128);
         } else if ("ObjectId".equals(value)) {
            this.currentValue = this.visitObjectIdConstructor();
            this.setCurrentBsonType(BsonType.OBJECT_ID);
         } else if ("RegExp".equals(value)) {
            this.currentValue = this.visitRegularExpressionConstructor();
            this.setCurrentBsonType(BsonType.REGULAR_EXPRESSION);
         } else if ("DBPointer".equals(value)) {
            this.currentValue = this.visitDBPointerConstructor();
            this.setCurrentBsonType(BsonType.DB_POINTER);
         } else {
            if (!"UUID".equals(value)) {
               throw new JsonParseException("JSON reader expected a type name but found '%s'.", value);
            }

            this.currentValue = this.visitUUIDConstructor();
            this.setCurrentBsonType(BsonType.BINARY);
         }
      }
   }

   private void visitExtendedJSON() {
      JsonToken nameToken = this.popToken();
      String value = nameToken.getValue(String.class);
      JsonTokenType type = nameToken.getType();
      if (type == JsonTokenType.STRING || type == JsonTokenType.UNQUOTED_STRING) {
         if ("$binary".equals(value) || "$type".equals(value)) {
            this.currentValue = this.visitBinDataExtendedJson(value);
            if (this.currentValue != null) {
               this.setCurrentBsonType(BsonType.BINARY);
               return;
            }
         }

         if ("$uuid".equals(value)) {
            this.currentValue = this.visitUuidExtendedJson();
            this.setCurrentBsonType(BsonType.BINARY);
            return;
         }

         if (!"$regex".equals(value) && !"$options".equals(value)) {
            if ("$code".equals(value)) {
               this.visitJavaScriptExtendedJson();
               return;
            }

            if ("$date".equals(value)) {
               this.currentValue = this.visitDateTimeExtendedJson();
               this.setCurrentBsonType(BsonType.DATE_TIME);
               return;
            }

            if ("$maxKey".equals(value)) {
               this.currentValue = this.visitMaxKeyExtendedJson();
               this.setCurrentBsonType(BsonType.MAX_KEY);
               return;
            }

            if ("$minKey".equals(value)) {
               this.currentValue = this.visitMinKeyExtendedJson();
               this.setCurrentBsonType(BsonType.MIN_KEY);
               return;
            }

            if ("$oid".equals(value)) {
               this.currentValue = this.visitObjectIdExtendedJson();
               this.setCurrentBsonType(BsonType.OBJECT_ID);
               return;
            }

            if ("$regularExpression".equals(value)) {
               this.currentValue = this.visitNewRegularExpressionExtendedJson();
               this.setCurrentBsonType(BsonType.REGULAR_EXPRESSION);
               return;
            }

            if ("$symbol".equals(value)) {
               this.currentValue = this.visitSymbolExtendedJson();
               this.setCurrentBsonType(BsonType.SYMBOL);
               return;
            }

            if ("$timestamp".equals(value)) {
               this.currentValue = this.visitTimestampExtendedJson();
               this.setCurrentBsonType(BsonType.TIMESTAMP);
               return;
            }

            if ("$undefined".equals(value)) {
               this.currentValue = this.visitUndefinedExtendedJson();
               this.setCurrentBsonType(BsonType.UNDEFINED);
               return;
            }

            if ("$numberLong".equals(value)) {
               this.currentValue = this.visitNumberLongExtendedJson();
               this.setCurrentBsonType(BsonType.INT64);
               return;
            }

            if ("$numberInt".equals(value)) {
               this.currentValue = this.visitNumberIntExtendedJson();
               this.setCurrentBsonType(BsonType.INT32);
               return;
            }

            if ("$numberDouble".equals(value)) {
               this.currentValue = this.visitNumberDoubleExtendedJson();
               this.setCurrentBsonType(BsonType.DOUBLE);
               return;
            }

            if ("$numberDecimal".equals(value)) {
               this.currentValue = this.visitNumberDecimalExtendedJson();
               this.setCurrentBsonType(BsonType.DECIMAL128);
               return;
            }

            if ("$dbPointer".equals(value)) {
               this.currentValue = this.visitDbPointerExtendedJson();
               this.setCurrentBsonType(BsonType.DB_POINTER);
               return;
            }
         } else {
            this.currentValue = this.visitRegularExpressionExtendedJson(value);
            if (this.currentValue != null) {
               this.setCurrentBsonType(BsonType.REGULAR_EXPRESSION);
               return;
            }
         }
      }

      this.pushToken(nameToken);
      this.setCurrentBsonType(BsonType.DOCUMENT);
   }

   private void visitEmptyConstructor() {
      JsonToken nextToken = this.popToken();
      if (nextToken.getType() == JsonTokenType.LEFT_PAREN) {
         this.verifyToken(JsonTokenType.RIGHT_PAREN);
      } else {
         this.pushToken(nextToken);
      }
   }

   private BsonBinary visitBinDataConstructor() {
      this.verifyToken(JsonTokenType.LEFT_PAREN);
      JsonToken subTypeToken = this.popToken();
      if (subTypeToken.getType() != JsonTokenType.INT32) {
         throw new JsonParseException("JSON reader expected a binary subtype but found '%s'.", subTypeToken.getValue());
      } else {
         this.verifyToken(JsonTokenType.COMMA);
         JsonToken bytesToken = this.popToken();
         if (bytesToken.getType() != JsonTokenType.UNQUOTED_STRING && bytesToken.getType() != JsonTokenType.STRING) {
            throw new JsonParseException("JSON reader expected a string but found '%s'.", bytesToken.getValue());
         } else {
            this.verifyToken(JsonTokenType.RIGHT_PAREN);
            byte[] bytes = Base64.decode(bytesToken.getValue(String.class));
            return new BsonBinary(subTypeToken.getValue(Integer.class).byteValue(), bytes);
         }
      }
   }

   private BsonBinary visitUUIDConstructor() {
      this.verifyToken(JsonTokenType.LEFT_PAREN);
      String hexString = this.readStringFromExtendedJson().replace("-", "");
      this.verifyToken(JsonTokenType.RIGHT_PAREN);
      return new BsonBinary(BsonBinarySubType.UUID_STANDARD, decodeHex(hexString));
   }

   private BsonRegularExpression visitRegularExpressionConstructor() {
      this.verifyToken(JsonTokenType.LEFT_PAREN);
      String pattern = this.readStringFromExtendedJson();
      String options = "";
      JsonToken commaToken = this.popToken();
      if (commaToken.getType() == JsonTokenType.COMMA) {
         options = this.readStringFromExtendedJson();
      } else {
         this.pushToken(commaToken);
      }

      this.verifyToken(JsonTokenType.RIGHT_PAREN);
      return new BsonRegularExpression(pattern, options);
   }

   private ObjectId visitObjectIdConstructor() {
      this.verifyToken(JsonTokenType.LEFT_PAREN);
      ObjectId objectId = new ObjectId(this.readStringFromExtendedJson());
      this.verifyToken(JsonTokenType.RIGHT_PAREN);
      return objectId;
   }

   private BsonTimestamp visitTimestampConstructor() {
      this.verifyToken(JsonTokenType.LEFT_PAREN);
      JsonToken timeToken = this.popToken();
      if (timeToken.getType() != JsonTokenType.INT32) {
         throw new JsonParseException("JSON reader expected an integer but found '%s'.", timeToken.getValue());
      } else {
         int time = timeToken.getValue(Integer.class);
         this.verifyToken(JsonTokenType.COMMA);
         JsonToken incrementToken = this.popToken();
         if (incrementToken.getType() != JsonTokenType.INT32) {
            throw new JsonParseException("JSON reader expected an integer but found '%s'.", timeToken.getValue());
         } else {
            int increment = incrementToken.getValue(Integer.class);
            this.verifyToken(JsonTokenType.RIGHT_PAREN);
            return new BsonTimestamp(time, increment);
         }
      }
   }

   private BsonDbPointer visitDBPointerConstructor() {
      this.verifyToken(JsonTokenType.LEFT_PAREN);
      String namespace = this.readStringFromExtendedJson();
      this.verifyToken(JsonTokenType.COMMA);
      ObjectId id = new ObjectId(this.readStringFromExtendedJson());
      this.verifyToken(JsonTokenType.RIGHT_PAREN);
      return new BsonDbPointer(namespace, id);
   }

   private int visitNumberIntConstructor() {
      this.verifyToken(JsonTokenType.LEFT_PAREN);
      JsonToken valueToken = this.popToken();
      int value;
      if (valueToken.getType() == JsonTokenType.INT32) {
         value = valueToken.getValue(Integer.class);
      } else {
         if (valueToken.getType() != JsonTokenType.STRING) {
            throw new JsonParseException("JSON reader expected an integer or a string but found '%s'.", valueToken.getValue());
         }

         value = Integer.parseInt(valueToken.getValue(String.class));
      }

      this.verifyToken(JsonTokenType.RIGHT_PAREN);
      return value;
   }

   private long visitNumberLongConstructor() {
      this.verifyToken(JsonTokenType.LEFT_PAREN);
      JsonToken valueToken = this.popToken();
      long value;
      if (valueToken.getType() != JsonTokenType.INT32 && valueToken.getType() != JsonTokenType.INT64) {
         if (valueToken.getType() != JsonTokenType.STRING) {
            throw new JsonParseException("JSON reader expected an integer or a string but found '%s'.", valueToken.getValue());
         }

         value = Long.parseLong(valueToken.getValue(String.class));
      } else {
         value = valueToken.getValue(Long.class);
      }

      this.verifyToken(JsonTokenType.RIGHT_PAREN);
      return value;
   }

   private Decimal128 visitNumberDecimalConstructor() {
      this.verifyToken(JsonTokenType.LEFT_PAREN);
      JsonToken valueToken = this.popToken();
      Decimal128 value;
      if (valueToken.getType() != JsonTokenType.INT32 && valueToken.getType() != JsonTokenType.INT64 && valueToken.getType() != JsonTokenType.DOUBLE) {
         if (valueToken.getType() != JsonTokenType.STRING) {
            throw new JsonParseException("JSON reader expected a number or a string but found '%s'.", valueToken.getValue());
         }

         value = Decimal128.parse(valueToken.getValue(String.class));
      } else {
         value = valueToken.getValue(Decimal128.class);
      }

      this.verifyToken(JsonTokenType.RIGHT_PAREN);
      return value;
   }

   private long visitISODateTimeConstructor() {
      this.verifyToken(JsonTokenType.LEFT_PAREN);
      JsonToken token = this.popToken();
      if (token.getType() == JsonTokenType.RIGHT_PAREN) {
         return new Date().getTime();
      } else if (token.getType() != JsonTokenType.STRING) {
         throw new JsonParseException("JSON reader expected a string but found '%s'.", token.getValue());
      } else {
         this.verifyToken(JsonTokenType.RIGHT_PAREN);
         String dateTimeString = token.getValue(String.class);

         try {
            return DateTimeFormatter.parse(dateTimeString);
         } catch (DateTimeParseException var4) {
            throw new JsonParseException("Failed to parse string as a date: " + dateTimeString, var4);
         }
      }
   }

   private BsonBinary visitHexDataConstructor() {
      this.verifyToken(JsonTokenType.LEFT_PAREN);
      JsonToken subTypeToken = this.popToken();
      if (subTypeToken.getType() != JsonTokenType.INT32) {
         throw new JsonParseException("JSON reader expected a binary subtype but found '%s'.", subTypeToken.getValue());
      } else {
         this.verifyToken(JsonTokenType.COMMA);
         String hex = this.readStringFromExtendedJson();
         this.verifyToken(JsonTokenType.RIGHT_PAREN);
         if ((hex.length() & 1) != 0) {
            hex = "0" + hex;
         }

         for (BsonBinarySubType subType : BsonBinarySubType.values()) {
            if (subType.getValue() == subTypeToken.getValue(Integer.class)) {
               return new BsonBinary(subType, decodeHex(hex));
            }
         }

         return new BsonBinary(decodeHex(hex));
      }
   }

   private long visitDateTimeConstructor() {
      DateFormat format = new SimpleDateFormat("EEE MMM dd yyyy HH:mm:ss z", Locale.ENGLISH);
      this.verifyToken(JsonTokenType.LEFT_PAREN);
      JsonToken token = this.popToken();
      if (token.getType() == JsonTokenType.RIGHT_PAREN) {
         return new Date().getTime();
      } else if (token.getType() == JsonTokenType.STRING) {
         this.verifyToken(JsonTokenType.RIGHT_PAREN);
         String s = token.getValue(String.class);
         ParsePosition pos = new ParsePosition(0);
         Date dateTime = format.parse(s, pos);
         if (dateTime != null && pos.getIndex() == s.length()) {
            return dateTime.getTime();
         } else {
            throw new JsonParseException("JSON reader expected a date in 'EEE MMM dd yyyy HH:mm:ss z' format but found '%s'.", s);
         }
      } else if (token.getType() != JsonTokenType.INT32 && token.getType() != JsonTokenType.INT64) {
         throw new JsonParseException("JSON reader expected an integer or a string but found '%s'.", token.getValue());
      } else {
         long[] values = new long[7];
         int pos = 0;

         do {
            if (pos < values.length) {
               values[pos++] = token.getValue(Long.class);
            }

            token = this.popToken();
            if (token.getType() == JsonTokenType.RIGHT_PAREN) {
               if (pos == 1) {
                  return values[0];
               }

               if (pos >= 3 && pos <= 7) {
                  Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
                  calendar.set(1, (int)values[0]);
                  calendar.set(2, (int)values[1]);
                  calendar.set(5, (int)values[2]);
                  calendar.set(11, (int)values[3]);
                  calendar.set(12, (int)values[4]);
                  calendar.set(13, (int)values[5]);
                  calendar.set(14, (int)values[6]);
                  return calendar.getTimeInMillis();
               }

               throw new JsonParseException("JSON reader expected 1 or 3-7 integers but found %d.", pos);
            }

            if (token.getType() != JsonTokenType.COMMA) {
               throw new JsonParseException("JSON reader expected a ',' or a ')' but found '%s'.", token.getValue());
            }

            token = this.popToken();
         } while (token.getType() == JsonTokenType.INT32 || token.getType() == JsonTokenType.INT64);

         throw new JsonParseException("JSON reader expected an integer but found '%s'.", token.getValue());
      }
   }

   private String visitDateTimeConstructorWithOutNew() {
      this.verifyToken(JsonTokenType.LEFT_PAREN);
      JsonToken token = this.popToken();
      if (token.getType() != JsonTokenType.RIGHT_PAREN) {
         while (true) {
            if (token.getType() != JsonTokenType.END_OF_FILE) {
               token = this.popToken();
               if (token.getType() != JsonTokenType.RIGHT_PAREN) {
                  continue;
               }
            }

            if (token.getType() != JsonTokenType.RIGHT_PAREN) {
               throw new JsonParseException("JSON reader expected a ')' but found '%s'.", token.getValue());
            }
            break;
         }
      }

      DateFormat df = new SimpleDateFormat("EEE MMM dd yyyy HH:mm:ss z", Locale.ENGLISH);
      return df.format(new Date());
   }

   private BsonBinary visitBinDataExtendedJson(String firstKey) {
      JsonReader.Mark mark = new JsonReader.Mark();

      BsonBinary var8;
      try {
         this.verifyToken(JsonTokenType.COLON);
         if (!firstKey.equals("$binary")) {
            mark.reset();
            return this.visitLegacyBinaryExtendedJson(firstKey);
         }

         JsonToken nextToken = this.popToken();
         if (nextToken.getType() != JsonTokenType.BEGIN_OBJECT) {
            mark.reset();
            return this.visitLegacyBinaryExtendedJson(firstKey);
         }

         JsonToken nameToken = this.popToken();
         String firstNestedKey = nameToken.getValue(String.class);
         byte[] data;
         byte type;
         if (firstNestedKey.equals("base64")) {
            this.verifyToken(JsonTokenType.COLON);
            data = Base64.decode(this.readStringFromExtendedJson());
            this.verifyToken(JsonTokenType.COMMA);
            this.verifyString("subType");
            this.verifyToken(JsonTokenType.COLON);
            type = this.readBinarySubtypeFromExtendedJson();
         } else {
            if (!firstNestedKey.equals("subType")) {
               throw new JsonParseException("Unexpected key for $binary: " + firstNestedKey);
            }

            this.verifyToken(JsonTokenType.COLON);
            type = this.readBinarySubtypeFromExtendedJson();
            this.verifyToken(JsonTokenType.COMMA);
            this.verifyString("base64");
            this.verifyToken(JsonTokenType.COLON);
            data = Base64.decode(this.readStringFromExtendedJson());
         }

         this.verifyToken(JsonTokenType.END_OBJECT);
         this.verifyToken(JsonTokenType.END_OBJECT);
         var8 = new BsonBinary(type, data);
      } finally {
         mark.discard();
      }

      return var8;
   }

   private BsonBinary visitLegacyBinaryExtendedJson(String firstKey) {
      JsonReader.Mark mark = new JsonReader.Mark();

      Object type;
      try {
         this.verifyToken(JsonTokenType.COLON);
         byte[] data;
         byte typex;
         if (firstKey.equals("$binary")) {
            data = Base64.decode(this.readStringFromExtendedJson());
            this.verifyToken(JsonTokenType.COMMA);
            this.verifyString("$type");
            this.verifyToken(JsonTokenType.COLON);
            typex = this.readBinarySubtypeFromExtendedJson();
         } else {
            typex = this.readBinarySubtypeFromExtendedJson();
            this.verifyToken(JsonTokenType.COMMA);
            this.verifyString("$binary");
            this.verifyToken(JsonTokenType.COLON);
            data = Base64.decode(this.readStringFromExtendedJson());
         }

         this.verifyToken(JsonTokenType.END_OBJECT);
         return new BsonBinary(typex, data);
      } catch (JsonParseException var10) {
         mark.reset();
         return null;
      } catch (NumberFormatException var11) {
         mark.reset();
         type = null;
      } finally {
         mark.discard();
      }

      return (BsonBinary)type;
   }

   private byte readBinarySubtypeFromExtendedJson() {
      JsonToken subTypeToken = this.popToken();
      if (subTypeToken.getType() != JsonTokenType.STRING && subTypeToken.getType() != JsonTokenType.INT32) {
         throw new JsonParseException("JSON reader expected a string or number but found '%s'.", subTypeToken.getValue());
      } else {
         return subTypeToken.getType() == JsonTokenType.STRING
            ? (byte)Integer.parseInt(subTypeToken.getValue(String.class), 16)
            : subTypeToken.getValue(Integer.class).byteValue();
      }
   }

   private long visitDateTimeExtendedJson() {
      this.verifyToken(JsonTokenType.COLON);
      JsonToken valueToken = this.popToken();
      long value;
      if (valueToken.getType() == JsonTokenType.BEGIN_OBJECT) {
         JsonToken nameToken = this.popToken();
         String name = nameToken.getValue(String.class);
         if (!name.equals("$numberLong")) {
            throw new JsonParseException(String.format("JSON reader expected $numberLong within $date, but found %s", name));
         }

         value = this.visitNumberLongExtendedJson();
         this.verifyToken(JsonTokenType.END_OBJECT);
      } else {
         if (valueToken.getType() != JsonTokenType.INT32 && valueToken.getType() != JsonTokenType.INT64) {
            if (valueToken.getType() != JsonTokenType.STRING) {
               throw new JsonParseException("JSON reader expected an integer or string but found '%s'.", valueToken.getValue());
            }

            String dateTimeString = valueToken.getValue(String.class);

            try {
               value = DateTimeFormatter.parse(dateTimeString);
            } catch (DateTimeParseException var6) {
               throw new JsonParseException("Failed to parse string as a date", var6);
            }
         } else {
            value = valueToken.getValue(Long.class);
         }

         this.verifyToken(JsonTokenType.END_OBJECT);
      }

      return value;
   }

   private MaxKey visitMaxKeyExtendedJson() {
      this.verifyToken(JsonTokenType.COLON);
      this.verifyToken(JsonTokenType.INT32, 1);
      this.verifyToken(JsonTokenType.END_OBJECT);
      return new MaxKey();
   }

   private MinKey visitMinKeyExtendedJson() {
      this.verifyToken(JsonTokenType.COLON);
      this.verifyToken(JsonTokenType.INT32, 1);
      this.verifyToken(JsonTokenType.END_OBJECT);
      return new MinKey();
   }

   private ObjectId visitObjectIdExtendedJson() {
      this.verifyToken(JsonTokenType.COLON);
      ObjectId objectId = new ObjectId(this.readStringFromExtendedJson());
      this.verifyToken(JsonTokenType.END_OBJECT);
      return objectId;
   }

   private BsonRegularExpression visitNewRegularExpressionExtendedJson() {
      this.verifyToken(JsonTokenType.COLON);
      this.verifyToken(JsonTokenType.BEGIN_OBJECT);
      String options = "";
      String firstKey = this.readStringFromExtendedJson();
      String pattern;
      if (firstKey.equals("pattern")) {
         this.verifyToken(JsonTokenType.COLON);
         pattern = this.readStringFromExtendedJson();
         this.verifyToken(JsonTokenType.COMMA);
         this.verifyString("options");
         this.verifyToken(JsonTokenType.COLON);
         options = this.readStringFromExtendedJson();
      } else {
         if (!firstKey.equals("options")) {
            throw new JsonParseException("Expected 't' and 'i' fields in $timestamp document but found " + firstKey);
         }

         this.verifyToken(JsonTokenType.COLON);
         options = this.readStringFromExtendedJson();
         this.verifyToken(JsonTokenType.COMMA);
         this.verifyString("pattern");
         this.verifyToken(JsonTokenType.COLON);
         pattern = this.readStringFromExtendedJson();
      }

      this.verifyToken(JsonTokenType.END_OBJECT);
      this.verifyToken(JsonTokenType.END_OBJECT);
      return new BsonRegularExpression(pattern, options);
   }

   private BsonRegularExpression visitRegularExpressionExtendedJson(String firstKey) {
      JsonReader.Mark extendedJsonMark = new JsonReader.Mark();

      Object options;
      try {
         this.verifyToken(JsonTokenType.COLON);
         String optionsx = "";
         String pattern;
         if (firstKey.equals("$regex")) {
            pattern = this.readStringFromExtendedJson();
            this.verifyToken(JsonTokenType.COMMA);
            this.verifyString("$options");
            this.verifyToken(JsonTokenType.COLON);
            optionsx = this.readStringFromExtendedJson();
         } else {
            optionsx = this.readStringFromExtendedJson();
            this.verifyToken(JsonTokenType.COMMA);
            this.verifyString("$regex");
            this.verifyToken(JsonTokenType.COLON);
            pattern = this.readStringFromExtendedJson();
         }

         this.verifyToken(JsonTokenType.END_OBJECT);
         return new BsonRegularExpression(pattern, optionsx);
      } catch (JsonParseException var9) {
         extendedJsonMark.reset();
         options = null;
      } finally {
         extendedJsonMark.discard();
      }

      return (BsonRegularExpression)options;
   }

   private String readStringFromExtendedJson() {
      JsonToken patternToken = this.popToken();
      if (patternToken.getType() != JsonTokenType.STRING) {
         throw new JsonParseException("JSON reader expected a string but found '%s'.", patternToken.getValue());
      } else {
         return patternToken.getValue(String.class);
      }
   }

   private String visitSymbolExtendedJson() {
      this.verifyToken(JsonTokenType.COLON);
      String symbol = this.readStringFromExtendedJson();
      this.verifyToken(JsonTokenType.END_OBJECT);
      return symbol;
   }

   private BsonTimestamp visitTimestampExtendedJson() {
      this.verifyToken(JsonTokenType.COLON);
      this.verifyToken(JsonTokenType.BEGIN_OBJECT);
      String firstKey = this.readStringFromExtendedJson();
      int time;
      int increment;
      if (firstKey.equals("t")) {
         this.verifyToken(JsonTokenType.COLON);
         time = this.readIntFromExtendedJson();
         this.verifyToken(JsonTokenType.COMMA);
         this.verifyString("i");
         this.verifyToken(JsonTokenType.COLON);
         increment = this.readIntFromExtendedJson();
      } else {
         if (!firstKey.equals("i")) {
            throw new JsonParseException("Expected 't' and 'i' fields in $timestamp document but found " + firstKey);
         }

         this.verifyToken(JsonTokenType.COLON);
         increment = this.readIntFromExtendedJson();
         this.verifyToken(JsonTokenType.COMMA);
         this.verifyString("t");
         this.verifyToken(JsonTokenType.COLON);
         time = this.readIntFromExtendedJson();
      }

      this.verifyToken(JsonTokenType.END_OBJECT);
      this.verifyToken(JsonTokenType.END_OBJECT);
      return new BsonTimestamp(time, increment);
   }

   private int readIntFromExtendedJson() {
      JsonToken nextToken = this.popToken();
      int value;
      if (nextToken.getType() == JsonTokenType.INT32) {
         value = nextToken.getValue(Integer.class);
      } else {
         if (nextToken.getType() != JsonTokenType.INT64) {
            throw new JsonParseException("JSON reader expected an integer but found '%s'.", nextToken.getValue());
         }

         value = nextToken.getValue(Long.class).intValue();
      }

      return value;
   }

   private BsonBinary visitUuidExtendedJson() {
      this.verifyToken(JsonTokenType.COLON);
      String uuidString = this.readStringFromExtendedJson();
      this.verifyToken(JsonTokenType.END_OBJECT);

      try {
         return new BsonBinary(UUID.fromString(uuidString));
      } catch (IllegalArgumentException var3) {
         throw new JsonParseException(var3);
      }
   }

   private void visitJavaScriptExtendedJson() {
      this.verifyToken(JsonTokenType.COLON);
      String code = this.readStringFromExtendedJson();
      JsonToken nextToken = this.popToken();
      switch (nextToken.getType()) {
         case END_OBJECT:
            this.currentValue = code;
            this.setCurrentBsonType(BsonType.JAVASCRIPT);
            break;
         case COMMA:
            this.verifyString("$scope");
            this.verifyToken(JsonTokenType.COLON);
            this.setState(AbstractBsonReader.State.VALUE);
            this.currentValue = code;
            this.setCurrentBsonType(BsonType.JAVASCRIPT_WITH_SCOPE);
            this.setContext(new JsonReader.Context(this.getContext(), BsonContextType.SCOPE_DOCUMENT));
            break;
         default:
            throw new JsonParseException("JSON reader expected ',' or '}' but found '%s'.", nextToken);
      }
   }

   private BsonUndefined visitUndefinedExtendedJson() {
      this.verifyToken(JsonTokenType.COLON);
      JsonToken valueToken = this.popToken();
      if (!valueToken.getValue(String.class).equals("true")) {
         throw new JsonParseException("JSON reader requires $undefined to have the value of true but found '%s'.", valueToken.getValue());
      } else {
         this.verifyToken(JsonTokenType.END_OBJECT);
         return new BsonUndefined();
      }
   }

   private Long visitNumberLongExtendedJson() {
      this.verifyToken(JsonTokenType.COLON);
      String longAsString = this.readStringFromExtendedJson();

      Long value;
      try {
         value = Long.valueOf(longAsString);
      } catch (NumberFormatException var4) {
         throw new JsonParseException(String.format("Exception converting value '%s' to type %s", longAsString, Long.class.getName()), var4);
      }

      this.verifyToken(JsonTokenType.END_OBJECT);
      return value;
   }

   private Integer visitNumberIntExtendedJson() {
      this.verifyToken(JsonTokenType.COLON);
      String intAsString = this.readStringFromExtendedJson();

      Integer value;
      try {
         value = Integer.valueOf(intAsString);
      } catch (NumberFormatException var4) {
         throw new JsonParseException(String.format("Exception converting value '%s' to type %s", intAsString, Integer.class.getName()), var4);
      }

      this.verifyToken(JsonTokenType.END_OBJECT);
      return value;
   }

   private Double visitNumberDoubleExtendedJson() {
      this.verifyToken(JsonTokenType.COLON);
      String doubleAsString = this.readStringFromExtendedJson();

      Double value;
      try {
         value = Double.valueOf(doubleAsString);
      } catch (NumberFormatException var4) {
         throw new JsonParseException(String.format("Exception converting value '%s' to type %s", doubleAsString, Double.class.getName()), var4);
      }

      this.verifyToken(JsonTokenType.END_OBJECT);
      return value;
   }

   private Decimal128 visitNumberDecimalExtendedJson() {
      this.verifyToken(JsonTokenType.COLON);
      String decimal128AsString = this.readStringFromExtendedJson();

      Decimal128 value;
      try {
         value = Decimal128.parse(decimal128AsString);
      } catch (NumberFormatException var4) {
         throw new JsonParseException(String.format("Exception converting value '%s' to type %s", decimal128AsString, Decimal128.class.getName()), var4);
      }

      this.verifyToken(JsonTokenType.END_OBJECT);
      return value;
   }

   private BsonDbPointer visitDbPointerExtendedJson() {
      this.verifyToken(JsonTokenType.COLON);
      this.verifyToken(JsonTokenType.BEGIN_OBJECT);
      String firstKey = this.readStringFromExtendedJson();
      String ref;
      ObjectId oid;
      if (firstKey.equals("$ref")) {
         this.verifyToken(JsonTokenType.COLON);
         ref = this.readStringFromExtendedJson();
         this.verifyToken(JsonTokenType.COMMA);
         this.verifyString("$id");
         oid = this.readDbPointerIdFromExtendedJson();
         this.verifyToken(JsonTokenType.END_OBJECT);
      } else {
         if (!firstKey.equals("$id")) {
            throw new JsonParseException("Expected $ref and $id fields in $dbPointer document but found " + firstKey);
         }

         oid = this.readDbPointerIdFromExtendedJson();
         this.verifyToken(JsonTokenType.COMMA);
         this.verifyString("$ref");
         this.verifyToken(JsonTokenType.COLON);
         ref = this.readStringFromExtendedJson();
      }

      this.verifyToken(JsonTokenType.END_OBJECT);
      return new BsonDbPointer(ref, oid);
   }

   private ObjectId readDbPointerIdFromExtendedJson() {
      this.verifyToken(JsonTokenType.COLON);
      this.verifyToken(JsonTokenType.BEGIN_OBJECT);
      this.verifyToken(JsonTokenType.STRING, "$oid");
      return this.visitObjectIdExtendedJson();
   }

   @Override
   public BsonReaderMark getMark() {
      return new JsonReader.Mark();
   }

   protected JsonReader.Context getContext() {
      return (JsonReader.Context)super.getContext();
   }

   private static byte[] decodeHex(String hex) {
      if (hex.length() % 2 != 0) {
         throw new IllegalArgumentException("A hex string must contain an even number of characters: " + hex);
      } else {
         byte[] out = new byte[hex.length() / 2];

         for (int i = 0; i < hex.length(); i += 2) {
            int high = Character.digit(hex.charAt(i), 16);
            int low = Character.digit(hex.charAt(i + 1), 16);
            if (high == -1 || low == -1) {
               throw new IllegalArgumentException("A hex string can only contain the characters 0-9, A-F, a-f: " + hex);
            }

            out[i / 2] = (byte)(high * 16 + low);
         }

         return out;
      }
   }

   protected class Context extends AbstractBsonReader.Context {
      protected Context(AbstractBsonReader.Context parentContext, BsonContextType contextType) {
         super(parentContext, contextType);
      }

      protected JsonReader.Context getParentContext() {
         return (JsonReader.Context)super.getParentContext();
      }

      @Override
      protected BsonContextType getContextType() {
         return super.getContextType();
      }
   }

   protected class Mark extends AbstractBsonReader.Mark {
      private final JsonToken pushedToken;
      private final Object currentValue;
      private final int markPos;

      protected Mark() {
         this.pushedToken = JsonReader.this.pushedToken;
         this.currentValue = JsonReader.this.currentValue;
         this.markPos = JsonReader.this.scanner.mark();
      }

      @Override
      public void reset() {
         super.reset();
         JsonReader.this.pushedToken = this.pushedToken;
         JsonReader.this.currentValue = this.currentValue;
         JsonReader.this.scanner.reset(this.markPos);
         JsonReader.this.setContext(JsonReader.this.new Context(this.getParentContext(), this.getContextType()));
      }

      public void discard() {
         JsonReader.this.scanner.discard(this.markPos);
      }
   }
}
