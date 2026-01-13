package org.bson;

import org.bson.types.Decimal128;
import org.bson.types.ObjectId;

class BSONCallbackAdapter extends AbstractBsonWriter {
   private BSONCallback bsonCallback;

   protected BSONCallbackAdapter(BsonWriterSettings settings, BSONCallback bsonCallback) {
      super(settings);
      this.bsonCallback = bsonCallback;
   }

   @Override
   public void flush() {
   }

   @Override
   public void doWriteStartDocument() {
      BsonContextType contextType = this.getState() == AbstractBsonWriter.State.SCOPE_DOCUMENT ? BsonContextType.SCOPE_DOCUMENT : BsonContextType.DOCUMENT;
      if (this.getContext() != null && contextType != BsonContextType.SCOPE_DOCUMENT) {
         this.bsonCallback.objectStart(this.getName());
      } else {
         this.bsonCallback.objectStart();
      }

      this.setContext(new BSONCallbackAdapter.Context(this.getContext(), contextType));
   }

   @Override
   protected void doWriteEndDocument() {
      BsonContextType contextType = this.getContext().getContextType();
      this.setContext(this.getContext().getParentContext());
      this.bsonCallback.objectDone();
      if (contextType == BsonContextType.SCOPE_DOCUMENT) {
         Object scope = this.bsonCallback.get();
         this.bsonCallback = this.getContext().callback;
         this.bsonCallback.gotCodeWScope(this.getContext().name, this.getContext().code, scope);
      }
   }

   @Override
   public void doWriteStartArray() {
      this.bsonCallback.arrayStart(this.getName());
      this.setContext(new BSONCallbackAdapter.Context(this.getContext(), BsonContextType.ARRAY));
   }

   @Override
   protected void doWriteEndArray() {
      this.setContext(this.getContext().getParentContext());
      this.bsonCallback.arrayDone();
   }

   @Override
   protected void doWriteBinaryData(BsonBinary value) {
      if (value.getType() == BsonBinarySubType.UUID_LEGACY.getValue()) {
         this.bsonCallback.gotUUID(this.getName(), Bits.readLong(value.getData(), 0), Bits.readLong(value.getData(), 8));
      } else {
         this.bsonCallback.gotBinary(this.getName(), value.getType(), value.getData());
      }
   }

   @Override
   public void doWriteBoolean(boolean value) {
      this.bsonCallback.gotBoolean(this.getName(), value);
      this.setState(this.getNextState());
   }

   @Override
   protected void doWriteDateTime(long value) {
      this.bsonCallback.gotDate(this.getName(), value);
   }

   @Override
   protected void doWriteDBPointer(BsonDbPointer value) {
      this.bsonCallback.gotDBRef(this.getName(), value.getNamespace(), value.getId());
   }

   @Override
   protected void doWriteDouble(double value) {
      this.bsonCallback.gotDouble(this.getName(), value);
   }

   @Override
   protected void doWriteInt32(int value) {
      this.bsonCallback.gotInt(this.getName(), value);
   }

   @Override
   protected void doWriteInt64(long value) {
      this.bsonCallback.gotLong(this.getName(), value);
   }

   @Override
   protected void doWriteDecimal128(Decimal128 value) {
      this.bsonCallback.gotDecimal128(this.getName(), value);
   }

   @Override
   protected void doWriteJavaScript(String value) {
      this.bsonCallback.gotCode(this.getName(), value);
   }

   @Override
   protected void doWriteJavaScriptWithScope(String value) {
      this.getContext().callback = this.bsonCallback;
      this.getContext().code = value;
      this.getContext().name = this.getName();
      this.bsonCallback = this.bsonCallback.createBSONCallback();
   }

   @Override
   protected void doWriteMaxKey() {
      this.bsonCallback.gotMaxKey(this.getName());
   }

   @Override
   protected void doWriteMinKey() {
      this.bsonCallback.gotMinKey(this.getName());
   }

   @Override
   public void doWriteNull() {
      this.bsonCallback.gotNull(this.getName());
   }

   @Override
   public void doWriteObjectId(ObjectId value) {
      this.bsonCallback.gotObjectId(this.getName(), value);
   }

   @Override
   public void doWriteRegularExpression(BsonRegularExpression value) {
      this.bsonCallback.gotRegex(this.getName(), value.getPattern(), value.getOptions());
   }

   @Override
   public void doWriteString(String value) {
      this.bsonCallback.gotString(this.getName(), value);
   }

   @Override
   public void doWriteSymbol(String value) {
      this.bsonCallback.gotSymbol(this.getName(), value);
   }

   @Override
   public void doWriteTimestamp(BsonTimestamp value) {
      this.bsonCallback.gotTimestamp(this.getName(), value.getTime(), value.getInc());
   }

   @Override
   public void doWriteUndefined() {
      this.bsonCallback.gotUndefined(this.getName());
   }

   protected BSONCallbackAdapter.Context getContext() {
      return (BSONCallbackAdapter.Context)super.getContext();
   }

   @Override
   protected String getName() {
      return this.getContext().getContextType() == BsonContextType.ARRAY ? Integer.toString(this.getContext().index++) : super.getName();
   }

   public class Context extends AbstractBsonWriter.Context {
      private int index;
      private BSONCallback callback;
      private String code;
      private String name;

      Context(BSONCallbackAdapter.Context parentContext, BsonContextType contextType) {
         super(parentContext, contextType);
      }

      public BSONCallbackAdapter.Context getParentContext() {
         return (BSONCallbackAdapter.Context)super.getParentContext();
      }
   }
}
