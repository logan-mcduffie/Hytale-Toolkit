package org.bson;

import org.bson.types.Decimal128;
import org.bson.types.ObjectId;

public class BsonDocumentWriter extends AbstractBsonWriter {
   private final BsonDocument document;

   public BsonDocumentWriter(BsonDocument document) {
      super(new BsonWriterSettings());
      this.document = document;
      this.setContext(new BsonDocumentWriter.Context());
   }

   public BsonDocument getDocument() {
      return this.document;
   }

   @Override
   protected void doWriteStartDocument() {
      switch (this.getState()) {
         case INITIAL:
            this.setContext(new BsonDocumentWriter.Context(this.document, BsonContextType.DOCUMENT, this.getContext()));
            break;
         case VALUE:
            this.setContext(new BsonDocumentWriter.Context(new BsonDocument(), BsonContextType.DOCUMENT, this.getContext()));
            break;
         case SCOPE_DOCUMENT:
            this.setContext(new BsonDocumentWriter.Context(new BsonDocument(), BsonContextType.SCOPE_DOCUMENT, this.getContext()));
            break;
         default:
            throw new BsonInvalidOperationException("Unexpected state " + this.getState());
      }
   }

   @Override
   protected void doWriteEndDocument() {
      BsonValue value = this.getContext().container;
      this.setContext(this.getContext().getParentContext());
      if (this.getContext().getContextType() == BsonContextType.JAVASCRIPT_WITH_SCOPE) {
         BsonDocument scope = (BsonDocument)value;
         BsonString code = (BsonString)this.getContext().container;
         this.setContext(this.getContext().getParentContext());
         this.write(new BsonJavaScriptWithScope(code.getValue(), scope));
      } else if (this.getContext().getContextType() != BsonContextType.TOP_LEVEL) {
         this.write(value);
      }
   }

   @Override
   protected void doWriteStartArray() {
      this.setContext(new BsonDocumentWriter.Context(new BsonArray(), BsonContextType.ARRAY, this.getContext()));
   }

   @Override
   protected void doWriteEndArray() {
      BsonValue array = this.getContext().container;
      this.setContext(this.getContext().getParentContext());
      this.write(array);
   }

   @Override
   protected void doWriteBinaryData(BsonBinary value) {
      this.write(value);
   }

   @Override
   public void doWriteBoolean(boolean value) {
      this.write(BsonBoolean.valueOf(value));
   }

   @Override
   protected void doWriteDateTime(long value) {
      this.write(new BsonDateTime(value));
   }

   @Override
   protected void doWriteDBPointer(BsonDbPointer value) {
      this.write(value);
   }

   @Override
   protected void doWriteDouble(double value) {
      this.write(new BsonDouble(value));
   }

   @Override
   protected void doWriteInt32(int value) {
      this.write(new BsonInt32(value));
   }

   @Override
   protected void doWriteInt64(long value) {
      this.write(new BsonInt64(value));
   }

   @Override
   protected void doWriteDecimal128(Decimal128 value) {
      this.write(new BsonDecimal128(value));
   }

   @Override
   protected void doWriteJavaScript(String value) {
      this.write(new BsonJavaScript(value));
   }

   @Override
   protected void doWriteJavaScriptWithScope(String value) {
      this.setContext(new BsonDocumentWriter.Context(new BsonString(value), BsonContextType.JAVASCRIPT_WITH_SCOPE, this.getContext()));
   }

   @Override
   protected void doWriteMaxKey() {
      this.write(new BsonMaxKey());
   }

   @Override
   protected void doWriteMinKey() {
      this.write(new BsonMinKey());
   }

   @Override
   public void doWriteNull() {
      this.write(BsonNull.VALUE);
   }

   @Override
   public void doWriteObjectId(ObjectId value) {
      this.write(new BsonObjectId(value));
   }

   @Override
   public void doWriteRegularExpression(BsonRegularExpression value) {
      this.write(value);
   }

   @Override
   public void doWriteString(String value) {
      this.write(new BsonString(value));
   }

   @Override
   public void doWriteSymbol(String value) {
      this.write(new BsonSymbol(value));
   }

   @Override
   public void doWriteTimestamp(BsonTimestamp value) {
      this.write(value);
   }

   @Override
   public void doWriteUndefined() {
      this.write(new BsonUndefined());
   }

   @Override
   public void flush() {
   }

   protected BsonDocumentWriter.Context getContext() {
      return (BsonDocumentWriter.Context)super.getContext();
   }

   private void write(BsonValue value) {
      this.getContext().add(value);
   }

   private class Context extends AbstractBsonWriter.Context {
      private BsonValue container;

      Context(BsonValue container, BsonContextType contextType, BsonDocumentWriter.Context parent) {
         super(parent, contextType);
         this.container = container;
      }

      Context() {
         super(null, BsonContextType.TOP_LEVEL);
      }

      void add(BsonValue value) {
         if (this.container instanceof BsonArray) {
            ((BsonArray)this.container).add(value);
         } else {
            ((BsonDocument)this.container).put(BsonDocumentWriter.this.getName(), value);
         }
      }
   }
}
