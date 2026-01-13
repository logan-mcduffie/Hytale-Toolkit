package org.bouncycastle.asn1;

import java.io.IOException;
import java.io.InputStream;
import org.bouncycastle.util.io.Streams;

@Deprecated
public class BERBitStringParser implements ASN1BitStringParser {
   private ASN1StreamParser _parser;
   private ConstructedBitStream _bitStream;

   BERBitStringParser(ASN1StreamParser var1) {
      this._parser = var1;
   }

   @Override
   public InputStream getOctetStream() throws IOException {
      return this._bitStream = new ConstructedBitStream(this._parser, true);
   }

   @Override
   public InputStream getBitStream() throws IOException {
      return this._bitStream = new ConstructedBitStream(this._parser, false);
   }

   @Override
   public int getPadBits() {
      return this._bitStream.getPadBits();
   }

   @Override
   public ASN1Primitive getLoadedObject() throws IOException {
      return parse(this._parser);
   }

   @Override
   public ASN1Primitive toASN1Primitive() {
      try {
         return this.getLoadedObject();
      } catch (IOException var2) {
         throw new ASN1ParsingException("IOException converting stream to byte array: " + var2.getMessage(), var2);
      }
   }

   static BERBitString parse(ASN1StreamParser var0) throws IOException {
      ConstructedBitStream var1 = new ConstructedBitStream(var0, false);
      byte[] var2 = Streams.readAll(var1);
      int var3 = var1.getPadBits();
      return new BERBitString(var2, var3);
   }
}
