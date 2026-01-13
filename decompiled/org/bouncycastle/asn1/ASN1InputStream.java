package org.bouncycastle.asn1;

import java.io.ByteArrayInputStream;
import java.io.EOFException;
import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import org.bouncycastle.util.io.Streams;

public class ASN1InputStream extends FilterInputStream implements BERTags {
   private final int limit;
   private final boolean lazyEvaluate;
   private final byte[][] tmpBuffers;

   public ASN1InputStream(InputStream var1) {
      this(var1, StreamUtil.findLimit(var1));
   }

   public ASN1InputStream(byte[] var1) {
      this(new ByteArrayInputStream(var1), var1.length);
   }

   public ASN1InputStream(byte[] var1, boolean var2) {
      this(new ByteArrayInputStream(var1), var1.length, var2);
   }

   public ASN1InputStream(InputStream var1, int var2) {
      this(var1, var2, false);
   }

   public ASN1InputStream(InputStream var1, boolean var2) {
      this(var1, StreamUtil.findLimit(var1), var2);
   }

   public ASN1InputStream(InputStream var1, int var2, boolean var3) {
      this(var1, var2, var3, new byte[11][]);
   }

   private ASN1InputStream(InputStream var1, int var2, boolean var3, byte[][] var4) {
      super(var1);
      this.limit = var2;
      this.lazyEvaluate = var3;
      this.tmpBuffers = var4;
   }

   int getLimit() {
      return this.limit;
   }

   protected int readLength() throws IOException {
      return readLength(this, this.limit, false);
   }

   protected void readFully(byte[] var1) throws IOException {
      if (Streams.readFully(this, var1, 0, var1.length) != var1.length) {
         throw new EOFException("EOF encountered in middle of object");
      }
   }

   protected ASN1Primitive buildObject(int var1, int var2, int var3) throws IOException {
      DefiniteLengthInputStream var4 = new DefiniteLengthInputStream(this, var3, this.limit);
      if (0 == (var1 & 224)) {
         return createPrimitiveDERObject(var2, var4, this.tmpBuffers);
      } else {
         int var5 = var1 & 192;
         if (0 != var5) {
            boolean var6 = (var1 & 32) != 0;
            return this.readTaggedObjectDL(var5, var2, var6, var4);
         } else {
            switch (var2) {
               case 3:
                  return this.buildConstructedBitString(this.readVector(var4));
               case 4:
                  return this.buildConstructedOctetString(this.readVector(var4));
               case 5:
               case 6:
               case 7:
               case 9:
               case 10:
               case 11:
               case 12:
               case 13:
               case 14:
               case 15:
               default:
                  throw new IOException("unknown tag " + var2 + " encountered");
               case 8:
                  return DLFactory.createSequence(this.readVector(var4)).toASN1External();
               case 16:
                  if (var4.getRemaining() < 1) {
                     return DLFactory.EMPTY_SEQUENCE;
                  } else {
                     if (this.lazyEvaluate) {
                        return new LazyEncodedSequence(var4.toByteArray());
                     }

                     return DLFactory.createSequence(this.readVector(var4));
                  }
               case 17:
                  return DLFactory.createSet(this.readVector(var4));
            }
         }
      }
   }

   public ASN1Primitive readObject() throws IOException {
      int var1 = this.read();
      if (var1 <= 0) {
         if (var1 == 0) {
            throw new IOException("unexpected end-of-contents marker");
         } else {
            return null;
         }
      } else {
         int var2 = readTagNumber(this, var1);
         int var3 = this.readLength();
         if (var3 >= 0) {
            try {
               return this.buildObject(var1, var2, var3);
            } catch (IllegalArgumentException var7) {
               throw new ASN1Exception("corrupted stream detected", var7);
            }
         } else if (0 == (var1 & 32)) {
            throw new IOException("indefinite-length primitive encoding encountered");
         } else {
            IndefiniteLengthInputStream var4 = new IndefiniteLengthInputStream(this, this.limit);
            ASN1StreamParser var5 = new ASN1StreamParser(var4, this.limit, this.tmpBuffers);
            int var6 = var1 & 192;
            if (0 != var6) {
               return var5.loadTaggedIL(var6, var2);
            } else {
               switch (var2) {
                  case 3:
                     return BERBitStringParser.parse(var5);
                  case 4:
                     return BEROctetStringParser.parse(var5);
                  case 5:
                  case 6:
                  case 7:
                  case 9:
                  case 10:
                  case 11:
                  case 12:
                  case 13:
                  case 14:
                  case 15:
                  default:
                     throw new IOException("unknown BER object encountered");
                  case 8:
                     return DERExternalParser.parse(var5);
                  case 16:
                     return BERSequenceParser.parse(var5);
                  case 17:
                     return BERSetParser.parse(var5);
               }
            }
         }
      }
   }

   ASN1BitString buildConstructedBitString(ASN1EncodableVector var1) throws IOException {
      ASN1BitString[] var2 = new ASN1BitString[var1.size()];

      for (int var3 = 0; var3 != var2.length; var3++) {
         ASN1Encodable var4 = var1.get(var3);
         if (!(var4 instanceof ASN1BitString)) {
            throw new ASN1Exception("unknown object encountered in constructed BIT STRING: " + var4.getClass());
         }

         var2[var3] = (ASN1BitString)var4;
      }

      return new BERBitString(var2);
   }

   ASN1OctetString buildConstructedOctetString(ASN1EncodableVector var1) throws IOException {
      ASN1OctetString[] var2 = new ASN1OctetString[var1.size()];

      for (int var3 = 0; var3 != var2.length; var3++) {
         ASN1Encodable var4 = var1.get(var3);
         if (!(var4 instanceof ASN1OctetString)) {
            throw new ASN1Exception("unknown object encountered in constructed OCTET STRING: " + var4.getClass());
         }

         var2[var3] = (ASN1OctetString)var4;
      }

      return new BEROctetString(var2);
   }

   ASN1Primitive readTaggedObjectDL(int var1, int var2, boolean var3, DefiniteLengthInputStream var4) throws IOException {
      if (!var3) {
         byte[] var6 = var4.toByteArray();
         return ASN1TaggedObject.createPrimitive(var1, var2, var6);
      } else {
         ASN1EncodableVector var5 = this.readVector(var4);
         return ASN1TaggedObject.createConstructedDL(var1, var2, var5);
      }
   }

   ASN1EncodableVector readVector() throws IOException {
      ASN1Primitive var1 = this.readObject();
      if (null == var1) {
         return new ASN1EncodableVector(0);
      } else {
         ASN1EncodableVector var2 = new ASN1EncodableVector();

         do {
            var2.add(var1);
         } while ((var1 = this.readObject()) != null);

         return var2;
      }
   }

   ASN1EncodableVector readVector(DefiniteLengthInputStream var1) throws IOException {
      int var2 = var1.getRemaining();
      return var2 < 1 ? new ASN1EncodableVector(0) : new ASN1InputStream(var1, var2, this.lazyEvaluate, this.tmpBuffers).readVector();
   }

   static int readTagNumber(InputStream var0, int var1) throws IOException {
      int var2 = var1 & 31;
      if (var2 == 31) {
         int var3 = var0.read();
         if (var3 < 31) {
            if (var3 < 0) {
               throw new EOFException("EOF found inside tag value.");
            }

            throw new IOException("corrupted stream - high tag number < 31 found");
         }

         var2 = var3 & 127;
         if (0 == var2) {
            throw new IOException("corrupted stream - invalid high tag number found");
         }

         while ((var3 & 128) != 0) {
            if (var2 >>> 24 != 0) {
               throw new IOException("Tag number more than 31 bits");
            }

            var2 <<= 7;
            var3 = var0.read();
            if (var3 < 0) {
               throw new EOFException("EOF found inside tag value.");
            }

            var2 |= var3 & 127;
         }
      }

      return var2;
   }

   static int readLength(InputStream var0, int var1, boolean var2) throws IOException {
      int var3 = var0.read();
      if (0 == var3 >>> 7) {
         return var3;
      } else if (128 == var3) {
         return -1;
      } else if (var3 < 0) {
         throw new EOFException("EOF found when length expected");
      } else if (255 == var3) {
         throw new IOException("invalid long form definite-length 0xFF");
      } else {
         int var4 = var3 & 127;
         int var5 = 0;
         var3 = 0;

         do {
            int var6 = var0.read();
            if (var6 < 0) {
               throw new EOFException("EOF found reading length");
            }

            if (var3 >>> 23 != 0) {
               throw new IOException("long form definite-length more than 31 bits");
            }

            var3 = (var3 << 8) + var6;
         } while (++var5 < var4);

         if (var3 >= var1 && !var2) {
            throw new IOException("corrupted stream - out of bounds length found: " + var3 + " >= " + var1);
         } else {
            return var3;
         }
      }
   }

   private static byte[] getBuffer(DefiniteLengthInputStream var0, byte[][] var1) throws IOException {
      int var2 = var0.getRemaining();
      if (var2 >= var1.length) {
         return var0.toByteArray();
      } else {
         byte[] var3 = var1[var2];
         if (var3 == null) {
            var3 = var1[var2] = new byte[var2];
         }

         var0.readAllIntoByteArray(var3);
         return var3;
      }
   }

   private static char[] getBMPCharBuffer(DefiniteLengthInputStream var0) throws IOException {
      int var1 = var0.getRemaining();
      if (0 != (var1 & 1)) {
         throw new IOException("malformed BMPString encoding encountered");
      } else {
         char[] var2 = new char[var1 / 2];
         int var3 = 0;

         byte[] var4;
         for (var4 = new byte[8]; var1 >= 8; var1 -= 8) {
            if (Streams.readFully(var0, var4, 0, 8) != 8) {
               throw new EOFException("EOF encountered in middle of BMPString");
            }

            var2[var3] = (char)(var4[0] << 8 | var4[1] & 255);
            var2[var3 + 1] = (char)(var4[2] << 8 | var4[3] & 255);
            var2[var3 + 2] = (char)(var4[4] << 8 | var4[5] & 255);
            var2[var3 + 3] = (char)(var4[6] << 8 | var4[7] & 255);
            var3 += 4;
         }

         if (var1 > 0) {
            if (Streams.readFully(var0, var4, 0, var1) != var1) {
               throw new EOFException("EOF encountered in middle of BMPString");
            }

            int var5 = 0;

            do {
               int var6 = var4[var5++] << 8;
               int var7 = var4[var5++] & 255;
               var2[var3++] = (char)(var6 | var7);
            } while (var5 < var1);
         }

         if (0 == var0.getRemaining() && var2.length == var3) {
            return var2;
         } else {
            throw new IllegalStateException();
         }
      }
   }

   static ASN1Primitive createPrimitiveDERObject(int var0, DefiniteLengthInputStream var1, byte[][] var2) throws IOException {
      try {
         switch (var0) {
            case 1:
               return ASN1Boolean.createPrimitive(getBuffer(var1, var2));
            case 2:
               return ASN1Integer.createPrimitive(var1.toByteArray());
            case 3:
               return ASN1BitString.createPrimitive(var1.toByteArray());
            case 4:
               return ASN1OctetString.createPrimitive(var1.toByteArray());
            case 5:
               ASN1Null.checkContentsLength(var1.getRemaining());
               return ASN1Null.createPrimitive();
            case 6:
               ASN1ObjectIdentifier.checkContentsLength(var1.getRemaining());
               return ASN1ObjectIdentifier.createPrimitive(getBuffer(var1, var2), true);
            case 7:
               return ASN1ObjectDescriptor.createPrimitive(var1.toByteArray());
            case 8:
            case 9:
            case 11:
            case 15:
            case 16:
            case 17:
            case 29:
            default:
               throw new IOException("unknown tag " + var0 + " encountered");
            case 10:
               return ASN1Enumerated.createPrimitive(getBuffer(var1, var2), true);
            case 12:
               return ASN1UTF8String.createPrimitive(var1.toByteArray());
            case 13:
               ASN1RelativeOID.checkContentsLength(var1.getRemaining());
               return ASN1RelativeOID.createPrimitive(getBuffer(var1, var2), true);
            case 14:
            case 31:
            case 32:
            case 33:
            case 34:
            case 35:
            case 36:
               throw new IOException("unsupported tag " + var0 + " encountered");
            case 18:
               return ASN1NumericString.createPrimitive(var1.toByteArray());
            case 19:
               return ASN1PrintableString.createPrimitive(var1.toByteArray());
            case 20:
               return ASN1T61String.createPrimitive(var1.toByteArray());
            case 21:
               return ASN1VideotexString.createPrimitive(var1.toByteArray());
            case 22:
               return ASN1IA5String.createPrimitive(var1.toByteArray());
            case 23:
               return ASN1UTCTime.createPrimitive(var1.toByteArray());
            case 24:
               return ASN1GeneralizedTime.createPrimitive(var1.toByteArray());
            case 25:
               return ASN1GraphicString.createPrimitive(var1.toByteArray());
            case 26:
               return ASN1VisibleString.createPrimitive(var1.toByteArray());
            case 27:
               return ASN1GeneralString.createPrimitive(var1.toByteArray());
            case 28:
               return ASN1UniversalString.createPrimitive(var1.toByteArray());
            case 30:
               return ASN1BMPString.createPrimitive(getBMPCharBuffer(var1));
         }
      } catch (IllegalArgumentException var4) {
         throw new ASN1Exception(var4.getMessage(), var4);
      } catch (IllegalStateException var5) {
         throw new ASN1Exception(var5.getMessage(), var5);
      }
   }
}
