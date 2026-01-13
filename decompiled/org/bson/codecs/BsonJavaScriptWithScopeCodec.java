package org.bson.codecs;

import org.bson.BsonDocument;
import org.bson.BsonJavaScriptWithScope;
import org.bson.BsonReader;
import org.bson.BsonWriter;

public class BsonJavaScriptWithScopeCodec implements Codec<BsonJavaScriptWithScope> {
   private final Codec<BsonDocument> documentCodec;

   public BsonJavaScriptWithScopeCodec(Codec<BsonDocument> documentCodec) {
      this.documentCodec = documentCodec;
   }

   public BsonJavaScriptWithScope decode(BsonReader bsonReader, DecoderContext decoderContext) {
      String code = bsonReader.readJavaScriptWithScope();
      BsonDocument scope = this.documentCodec.decode(bsonReader, decoderContext);
      return new BsonJavaScriptWithScope(code, scope);
   }

   public void encode(BsonWriter writer, BsonJavaScriptWithScope codeWithScope, EncoderContext encoderContext) {
      writer.writeJavaScriptWithScope(codeWithScope.getCode());
      this.documentCodec.encode(writer, codeWithScope.getScope(), encoderContext);
   }

   @Override
   public Class<BsonJavaScriptWithScope> getEncoderClass() {
      return BsonJavaScriptWithScope.class;
   }
}
