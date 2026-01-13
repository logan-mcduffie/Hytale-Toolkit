package org.bson;

import java.io.InvalidObjectException;
import java.io.ObjectInputStream;
import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import org.bson.codecs.Encoder;
import org.bson.codecs.EncoderContext;
import org.bson.codecs.configuration.CodecRegistry;

public final class BsonDocumentWrapper<T> extends BsonDocument {
   private static final long serialVersionUID = 1L;
   private final transient T wrappedDocument;
   private final transient Encoder<T> encoder;
   private BsonDocument unwrapped;

   public static BsonDocument asBsonDocument(Object document, CodecRegistry codecRegistry) {
      if (document == null) {
         return null;
      } else {
         return (BsonDocument)(document instanceof BsonDocument
            ? (BsonDocument)document
            : new BsonDocumentWrapper<>(document, codecRegistry.get((Class<Object>)document.getClass())));
      }
   }

   public BsonDocumentWrapper(T wrappedDocument, Encoder<T> encoder) {
      if (wrappedDocument == null) {
         throw new IllegalArgumentException("Document can not be null");
      } else {
         this.wrappedDocument = wrappedDocument;
         this.encoder = encoder;
      }
   }

   public T getWrappedDocument() {
      return this.wrappedDocument;
   }

   public Encoder<T> getEncoder() {
      return this.encoder;
   }

   public boolean isUnwrapped() {
      return this.unwrapped != null;
   }

   @Override
   public int size() {
      return this.getUnwrapped().size();
   }

   @Override
   public boolean isEmpty() {
      return this.getUnwrapped().isEmpty();
   }

   @Override
   public boolean containsKey(Object key) {
      return this.getUnwrapped().containsKey(key);
   }

   @Override
   public boolean containsValue(Object value) {
      return this.getUnwrapped().containsValue(value);
   }

   @Override
   public BsonValue get(Object key) {
      return this.getUnwrapped().get(key);
   }

   @Override
   public BsonValue put(String key, BsonValue value) {
      return this.getUnwrapped().put(key, value);
   }

   @Override
   public BsonValue remove(Object key) {
      return this.getUnwrapped().remove(key);
   }

   @Override
   public void putAll(Map<? extends String, ? extends BsonValue> m) {
      super.putAll(m);
   }

   @Override
   public void clear() {
      super.clear();
   }

   @Override
   public Set<String> keySet() {
      return this.getUnwrapped().keySet();
   }

   @Override
   public Collection<BsonValue> values() {
      return this.getUnwrapped().values();
   }

   @Override
   public Set<Entry<String, BsonValue>> entrySet() {
      return this.getUnwrapped().entrySet();
   }

   @Override
   public boolean equals(Object o) {
      return this.getUnwrapped().equals(o);
   }

   @Override
   public int hashCode() {
      return this.getUnwrapped().hashCode();
   }

   @Override
   public String toString() {
      return this.getUnwrapped().toString();
   }

   @Override
   public BsonDocument clone() {
      return this.getUnwrapped().clone();
   }

   private BsonDocument getUnwrapped() {
      if (this.encoder == null) {
         throw new BsonInvalidOperationException("Can not unwrap a BsonDocumentWrapper with no Encoder");
      } else {
         if (this.unwrapped == null) {
            BsonDocument unwrapped = new BsonDocument();
            BsonWriter writer = new BsonDocumentWriter(unwrapped);
            this.encoder.encode(writer, this.wrappedDocument, EncoderContext.builder().build());
            this.unwrapped = unwrapped;
         }

         return this.unwrapped;
      }
   }

   private Object writeReplace() {
      return this.getUnwrapped();
   }

   private void readObject(ObjectInputStream stream) throws InvalidObjectException {
      throw new InvalidObjectException("Proxy required");
   }
}
