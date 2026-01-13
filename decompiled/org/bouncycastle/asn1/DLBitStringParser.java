package org.bouncycastle.asn1;

import java.io.IOException;
import java.io.InputStream;

@Deprecated
public class DLBitStringParser implements ASN1BitStringParser {
   private final DefiniteLengthInputStream stream;
   private int padBits = 0;

   DLBitStringParser(DefiniteLengthInputStream var1) {
      this.stream = var1;
   }

   @Override
   public InputStream getBitStream() throws IOException {
      return this.getBitStream(false);
   }

   @Override
   public InputStream getOctetStream() throws IOException {
      return this.getBitStream(true);
   }

   @Override
   public int getPadBits() {
      return this.padBits;
   }

   @Override
   public ASN1Primitive getLoadedObject() throws IOException {
      return ASN1BitString.createPrimitive(this.stream.toByteArray());
   }

   @Override
   public ASN1Primitive toASN1Primitive() {
      try {
         return this.getLoadedObject();
      } catch (IOException var2) {
         throw new ASN1ParsingException("IOException converting stream to byte array: " + var2.getMessage(), var2);
      }
   }

   private InputStream getBitStream(boolean var1) throws IOException {
      int var2 = this.stream.getRemaining();
      if (var2 < 1) {
         throw new IllegalStateException("content octets cannot be empty");
      } else {
         this.padBits = this.stream.read();
         if (this.padBits > 0) {
            if (var2 < 2) {
               throw new IllegalStateException("zero length data with non-zero pad bits");
            }

            if (this.padBits > 7) {
               throw new IllegalStateException("pad bits cannot be greater than 7 or less than 0");
            }

            if (var1) {
               throw new IOException("expected octet-aligned bitstring, but found padBits: " + this.padBits);
            }
         }

         return this.stream;
      }
   }
}
