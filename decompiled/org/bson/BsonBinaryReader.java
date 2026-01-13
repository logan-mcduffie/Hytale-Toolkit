package org.bson;

import java.nio.ByteBuffer;
import org.bson.assertions.Assertions;
import org.bson.io.BsonInput;
import org.bson.io.BsonInputMark;
import org.bson.io.ByteBufferBsonInput;
import org.bson.types.Decimal128;
import org.bson.types.ObjectId;

public class BsonBinaryReader extends AbstractBsonReader {
   private final BsonInput bsonInput;

   public BsonBinaryReader(ByteBuffer byteBuffer) {
      this(new ByteBufferBsonInput(new ByteBufNIO(Assertions.notNull("byteBuffer", byteBuffer))));
   }

   public BsonBinaryReader(BsonInput bsonInput) {
      if (bsonInput == null) {
         throw new IllegalArgumentException("bsonInput is null");
      } else {
         this.bsonInput = bsonInput;
         this.setContext(new BsonBinaryReader.Context(null, BsonContextType.TOP_LEVEL, 0, 0));
      }
   }

   @Override
   public void close() {
      super.close();
   }

   public BsonInput getBsonInput() {
      return this.bsonInput;
   }

   @Override
   public BsonType readBsonType() {
      if (this.isClosed()) {
         throw new IllegalStateException("BSONBinaryWriter");
      } else if (this.getState() != AbstractBsonReader.State.INITIAL
         && this.getState() != AbstractBsonReader.State.DONE
         && this.getState() != AbstractBsonReader.State.SCOPE_DOCUMENT) {
         if (this.getState() != AbstractBsonReader.State.TYPE) {
            this.throwInvalidState("ReadBSONType", AbstractBsonReader.State.TYPE);
         }

         byte bsonTypeByte = this.bsonInput.readByte();
         BsonType bsonType = BsonType.findByValue(bsonTypeByte);
         if (bsonType == null) {
            String name = this.bsonInput.readCString();
            throw new BsonSerializationException(
               String.format("Detected unknown BSON type \"\\x%x\" for fieldname \"%s\". Are you using the latest driver version?", bsonTypeByte, name)
            );
         } else {
            this.setCurrentBsonType(bsonType);
            if (this.getCurrentBsonType() == BsonType.END_OF_DOCUMENT) {
               switch (this.getContext().getContextType()) {
                  case ARRAY:
                     this.setState(AbstractBsonReader.State.END_OF_ARRAY);
                     return BsonType.END_OF_DOCUMENT;
                  case DOCUMENT:
                  case SCOPE_DOCUMENT:
                     this.setState(AbstractBsonReader.State.END_OF_DOCUMENT);
                     return BsonType.END_OF_DOCUMENT;
                  default:
                     throw new BsonSerializationException(
                        String.format("BSONType EndOfDocument is not valid when ContextType is %s.", this.getContext().getContextType())
                     );
               }
            } else {
               switch (this.getContext().getContextType()) {
                  case ARRAY:
                     this.bsonInput.skipCString();
                     this.setState(AbstractBsonReader.State.VALUE);
                     break;
                  case DOCUMENT:
                  case SCOPE_DOCUMENT:
                     this.setCurrentName(this.bsonInput.readCString());
                     this.setState(AbstractBsonReader.State.NAME);
                     break;
                  default:
                     throw new BSONException("Unexpected ContextType.");
               }

               return this.getCurrentBsonType();
            }
         }
      } else {
         this.setCurrentBsonType(BsonType.DOCUMENT);
         this.setState(AbstractBsonReader.State.VALUE);
         return this.getCurrentBsonType();
      }
   }

   @Override
   protected BsonBinary doReadBinaryData() {
      int numBytes = this.readSize();
      byte type = this.bsonInput.readByte();
      if (type == BsonBinarySubType.OLD_BINARY.getValue()) {
         int repeatedNumBytes = this.bsonInput.readInt32();
         if (repeatedNumBytes != numBytes - 4) {
            throw new BsonSerializationException("Binary sub type OldBinary has inconsistent sizes");
         }

         numBytes -= 4;
      }

      byte[] bytes = new byte[numBytes];
      this.bsonInput.readBytes(bytes);
      return new BsonBinary(type, bytes);
   }

   @Override
   protected byte doPeekBinarySubType() {
      BsonBinaryReader.Mark mark = new BsonBinaryReader.Mark();
      this.readSize();
      byte type = this.bsonInput.readByte();
      mark.reset();
      return type;
   }

   @Override
   protected int doPeekBinarySize() {
      BsonBinaryReader.Mark mark = new BsonBinaryReader.Mark();
      int size = this.readSize();
      mark.reset();
      return size;
   }

   @Override
   protected boolean doReadBoolean() {
      byte booleanByte = this.bsonInput.readByte();
      if (booleanByte != 0 && booleanByte != 1) {
         throw new BsonSerializationException(String.format("Expected a boolean value but found %d", booleanByte));
      } else {
         return booleanByte == 1;
      }
   }

   @Override
   protected long doReadDateTime() {
      return this.bsonInput.readInt64();
   }

   @Override
   protected double doReadDouble() {
      return this.bsonInput.readDouble();
   }

   @Override
   protected int doReadInt32() {
      return this.bsonInput.readInt32();
   }

   @Override
   protected long doReadInt64() {
      return this.bsonInput.readInt64();
   }

   @Override
   public Decimal128 doReadDecimal128() {
      long low = this.bsonInput.readInt64();
      long high = this.bsonInput.readInt64();
      return Decimal128.fromIEEE754BIDEncoding(high, low);
   }

   @Override
   protected String doReadJavaScript() {
      return this.bsonInput.readString();
   }

   @Override
   protected String doReadJavaScriptWithScope() {
      int startPosition = this.bsonInput.getPosition();
      int size = this.readSize();
      this.setContext(new BsonBinaryReader.Context(this.getContext(), BsonContextType.JAVASCRIPT_WITH_SCOPE, startPosition, size));
      return this.bsonInput.readString();
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
      return this.bsonInput.readObjectId();
   }

   @Override
   protected BsonRegularExpression doReadRegularExpression() {
      return new BsonRegularExpression(this.bsonInput.readCString(), this.bsonInput.readCString());
   }

   @Override
   protected BsonDbPointer doReadDBPointer() {
      return new BsonDbPointer(this.bsonInput.readString(), this.bsonInput.readObjectId());
   }

   @Override
   protected String doReadString() {
      return this.bsonInput.readString();
   }

   @Override
   protected String doReadSymbol() {
      return this.bsonInput.readString();
   }

   @Override
   protected BsonTimestamp doReadTimestamp() {
      return new BsonTimestamp(this.bsonInput.readInt64());
   }

   @Override
   protected void doReadUndefined() {
   }

   @Override
   public void doReadStartArray() {
      int startPosition = this.bsonInput.getPosition();
      int size = this.readSize();
      this.setContext(new BsonBinaryReader.Context(this.getContext(), BsonContextType.ARRAY, startPosition, size));
   }

   @Override
   protected void doReadStartDocument() {
      BsonContextType contextType = this.getState() == AbstractBsonReader.State.SCOPE_DOCUMENT ? BsonContextType.SCOPE_DOCUMENT : BsonContextType.DOCUMENT;
      int startPosition = this.bsonInput.getPosition();
      int size = this.readSize();
      this.setContext(new BsonBinaryReader.Context(this.getContext(), contextType, startPosition, size));
   }

   @Override
   protected void doReadEndArray() {
      this.setContext(this.getContext().popContext(this.bsonInput.getPosition()));
   }

   @Override
   protected void doReadEndDocument() {
      this.setContext(this.getContext().popContext(this.bsonInput.getPosition()));
      if (this.getContext().getContextType() == BsonContextType.JAVASCRIPT_WITH_SCOPE) {
         this.setContext(this.getContext().popContext(this.bsonInput.getPosition()));
      }
   }

   @Override
   protected void doSkipName() {
   }

   @Override
   protected void doSkipValue() {
      if (this.isClosed()) {
         throw new IllegalStateException("BSONBinaryWriter");
      } else {
         if (this.getState() != AbstractBsonReader.State.VALUE) {
            this.throwInvalidState("skipValue", AbstractBsonReader.State.VALUE);
         }

         int skip;
         switch (this.getCurrentBsonType()) {
            case ARRAY:
               skip = this.readSize() - 4;
               break;
            case BINARY:
               skip = this.readSize() + 1;
               break;
            case BOOLEAN:
               skip = 1;
               break;
            case DATE_TIME:
               skip = 8;
               break;
            case DOCUMENT:
               skip = this.readSize() - 4;
               break;
            case DOUBLE:
               skip = 8;
               break;
            case INT32:
               skip = 4;
               break;
            case INT64:
               skip = 8;
               break;
            case DECIMAL128:
               skip = 16;
               break;
            case JAVASCRIPT:
               skip = this.readSize();
               break;
            case JAVASCRIPT_WITH_SCOPE:
               skip = this.readSize() - 4;
               break;
            case MAX_KEY:
               skip = 0;
               break;
            case MIN_KEY:
               skip = 0;
               break;
            case NULL:
               skip = 0;
               break;
            case OBJECT_ID:
               skip = 12;
               break;
            case REGULAR_EXPRESSION:
               this.bsonInput.skipCString();
               this.bsonInput.skipCString();
               skip = 0;
               break;
            case STRING:
               skip = this.readSize();
               break;
            case SYMBOL:
               skip = this.readSize();
               break;
            case TIMESTAMP:
               skip = 8;
               break;
            case UNDEFINED:
               skip = 0;
               break;
            case DB_POINTER:
               skip = this.readSize() + 12;
               break;
            default:
               throw new BSONException("Unexpected BSON type: " + this.getCurrentBsonType());
         }

         this.bsonInput.skip(skip);
         this.setState(AbstractBsonReader.State.TYPE);
      }
   }

   private int readSize() {
      int size = this.bsonInput.readInt32();
      if (size < 0) {
         String message = String.format("Size %s is not valid because it is negative.", size);
         throw new BsonSerializationException(message);
      } else {
         return size;
      }
   }

   protected BsonBinaryReader.Context getContext() {
      return (BsonBinaryReader.Context)super.getContext();
   }

   @Override
   public BsonReaderMark getMark() {
      return new BsonBinaryReader.Mark();
   }

   protected class Context extends AbstractBsonReader.Context {
      private final int startPosition;
      private final int size;

      Context(BsonBinaryReader.Context parentContext, BsonContextType contextType, int startPosition, int size) {
         super(parentContext, contextType);
         this.startPosition = startPosition;
         this.size = size;
      }

      BsonBinaryReader.Context popContext(int position) {
         int actualSize = position - this.startPosition;
         if (actualSize != this.size) {
            throw new BsonSerializationException(String.format("Expected size to be %d, not %d.", this.size, actualSize));
         } else {
            return this.getParentContext();
         }
      }

      protected BsonBinaryReader.Context getParentContext() {
         return (BsonBinaryReader.Context)super.getParentContext();
      }
   }

   protected class Mark extends AbstractBsonReader.Mark {
      private final int startPosition;
      private final int size;
      private final BsonInputMark bsonInputMark;

      protected Mark() {
         this.startPosition = BsonBinaryReader.this.getContext().startPosition;
         this.size = BsonBinaryReader.this.getContext().size;
         this.bsonInputMark = BsonBinaryReader.this.bsonInput.getMark(Integer.MAX_VALUE);
      }

      @Override
      public void reset() {
         super.reset();
         this.bsonInputMark.reset();
         BsonBinaryReader.this.setContext(
            BsonBinaryReader.this.new Context((BsonBinaryReader.Context)this.getParentContext(), this.getContextType(), this.startPosition, this.size)
         );
      }
   }
}
