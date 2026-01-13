package org.bouncycastle.asn1;

import java.io.IOException;
import java.io.InputStream;

class ConstructedBitStream extends InputStream {
   private final ASN1StreamParser _parser;
   private final boolean _octetAligned;
   private boolean _first = true;
   private int _padBits = 0;
   private ASN1BitStringParser _currentParser;
   private InputStream _currentStream;

   ConstructedBitStream(ASN1StreamParser var1, boolean var2) {
      this._parser = var1;
      this._octetAligned = var2;
   }

   int getPadBits() {
      return this._padBits;
   }

   @Override
   public int read(byte[] var1, int var2, int var3) throws IOException {
      if (this._currentStream == null) {
         if (!this._first) {
            return -1;
         }

         this._currentParser = this.getNextParser();
         if (this._currentParser == null) {
            return -1;
         }

         this._first = false;
         this._currentStream = this._currentParser.getBitStream();
      }

      int var4 = 0;

      while (true) {
         int var5 = this._currentStream.read(var1, var2 + var4, var3 - var4);
         if (var5 >= 0) {
            var4 += var5;
            if (var4 == var3) {
               return var4;
            }
         } else {
            this._padBits = this._currentParser.getPadBits();
            this._currentParser = this.getNextParser();
            if (this._currentParser == null) {
               this._currentStream = null;
               return var4 < 1 ? -1 : var4;
            }

            this._currentStream = this._currentParser.getBitStream();
         }
      }
   }

   @Override
   public int read() throws IOException {
      if (this._currentStream == null) {
         if (!this._first) {
            return -1;
         }

         this._currentParser = this.getNextParser();
         if (this._currentParser == null) {
            return -1;
         }

         this._first = false;
         this._currentStream = this._currentParser.getBitStream();
      }

      while (true) {
         int var1 = this._currentStream.read();
         if (var1 >= 0) {
            return var1;
         }

         this._padBits = this._currentParser.getPadBits();
         this._currentParser = this.getNextParser();
         if (this._currentParser == null) {
            this._currentStream = null;
            return -1;
         }

         this._currentStream = this._currentParser.getBitStream();
      }
   }

   private ASN1BitStringParser getNextParser() throws IOException {
      ASN1Encodable var1 = this._parser.readObject();
      if (var1 == null) {
         if (this._octetAligned && this._padBits != 0) {
            throw new IOException("expected octet-aligned bitstring, but found padBits: " + this._padBits);
         } else {
            return null;
         }
      } else if (var1 instanceof ASN1BitStringParser) {
         if (this._padBits != 0) {
            throw new IOException("only the last nested bitstring can have padding");
         } else {
            return (ASN1BitStringParser)var1;
         }
      } else {
         throw new IOException("unknown object encountered: " + var1.getClass());
      }
   }
}
