package org.bouncycastle.asn1;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

public class ASN1StreamParser {
   private final InputStream _in;
   private final int _limit;
   private final byte[][] tmpBuffers;

   public ASN1StreamParser(InputStream var1) {
      this(var1, StreamUtil.findLimit(var1));
   }

   public ASN1StreamParser(byte[] var1) {
      this(new ByteArrayInputStream(var1), var1.length);
   }

   public ASN1StreamParser(InputStream var1, int var2) {
      this(var1, var2, new byte[11][]);
   }

   ASN1StreamParser(InputStream var1, int var2, byte[][] var3) {
      this._in = var1;
      this._limit = var2;
      this.tmpBuffers = var3;
   }

   public ASN1Encodable readObject() throws IOException {
      int var1 = this._in.read();
      return var1 < 0 ? null : this.implParseObject(var1);
   }

   ASN1Encodable implParseObject(int var1) throws IOException {
      this.set00Check(false);
      int var2 = ASN1InputStream.readTagNumber(this._in, var1);
      int var3 = ASN1InputStream.readLength(this._in, this._limit, var2 == 3 || var2 == 4 || var2 == 16 || var2 == 17 || var2 == 8);
      if (var3 < 0) {
         if (0 == (var1 & 32)) {
            throw new IOException("indefinite-length primitive encoding encountered");
         } else {
            IndefiniteLengthInputStream var8 = new IndefiniteLengthInputStream(this._in, this._limit);
            ASN1StreamParser var9 = new ASN1StreamParser(var8, this._limit, this.tmpBuffers);
            int var10 = var1 & 192;
            return (ASN1Encodable)(0 != var10 ? new BERTaggedObjectParser(var10, var2, var9) : var9.parseImplicitConstructedIL(var2));
         }
      } else {
         DefiniteLengthInputStream var4 = new DefiniteLengthInputStream(this._in, var3, this._limit);
         if (0 == (var1 & 224)) {
            return this.parseImplicitPrimitive(var2, var4);
         } else {
            ASN1StreamParser var5 = new ASN1StreamParser(var4, var4.getLimit(), this.tmpBuffers);
            int var6 = var1 & 192;
            if (0 != var6) {
               boolean var7 = (var1 & 32) != 0;
               return new DLTaggedObjectParser(var6, var2, var7, var5);
            } else {
               return var5.parseImplicitConstructedDL(var2);
            }
         }
      }
   }

   ASN1Primitive loadTaggedDL(int var1, int var2, boolean var3) throws IOException {
      if (!var3) {
         byte[] var5 = ((DefiniteLengthInputStream)this._in).toByteArray();
         return ASN1TaggedObject.createPrimitive(var1, var2, var5);
      } else {
         ASN1EncodableVector var4 = this.readVector();
         return ASN1TaggedObject.createConstructedDL(var1, var2, var4);
      }
   }

   ASN1Primitive loadTaggedIL(int var1, int var2) throws IOException {
      ASN1EncodableVector var3 = this.readVector();
      return ASN1TaggedObject.createConstructedIL(var1, var2, var3);
   }

   ASN1Encodable parseImplicitConstructedDL(int var1) throws IOException {
      switch (var1) {
         case 3:
            return new BERBitStringParser(this);
         case 4:
            return new BEROctetStringParser(this);
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
            throw new ASN1Exception("unknown DL object encountered: 0x" + Integer.toHexString(var1));
         case 8:
            return new DERExternalParser(this);
         case 16:
            return new DLSequenceParser(this);
         case 17:
            return new DLSetParser(this);
      }
   }

   ASN1Encodable parseImplicitConstructedIL(int var1) throws IOException {
      switch (var1) {
         case 3:
            return new BERBitStringParser(this);
         case 4:
            return new BEROctetStringParser(this);
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
            throw new ASN1Exception("unknown BER object encountered: 0x" + Integer.toHexString(var1));
         case 8:
            return new DERExternalParser(this);
         case 16:
            return new BERSequenceParser(this);
         case 17:
            return new BERSetParser(this);
      }
   }

   ASN1Encodable parseImplicitPrimitive(int var1) throws IOException {
      return this.parseImplicitPrimitive(var1, (DefiniteLengthInputStream)this._in);
   }

   ASN1Encodable parseImplicitPrimitive(int var1, DefiniteLengthInputStream var2) throws IOException {
      switch (var1) {
         case 3:
            return new DLBitStringParser(var2);
         case 4:
            return new DEROctetStringParser(var2);
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
            try {
               return ASN1InputStream.createPrimitiveDERObject(var1, var2, this.tmpBuffers);
            } catch (IllegalArgumentException var4) {
               throw new ASN1Exception("corrupted stream detected", var4);
            }
         case 8:
            throw new ASN1Exception("externals must use constructed encoding (see X.690 8.18)");
         case 16:
            throw new ASN1Exception("sets must use constructed encoding (see X.690 8.11.1/8.12.1)");
         case 17:
            throw new ASN1Exception("sequences must use constructed encoding (see X.690 8.9.1/8.10.1)");
      }
   }

   ASN1Encodable parseObject(int var1) throws IOException {
      if (var1 >= 0 && var1 <= 30) {
         int var2 = this._in.read();
         if (var2 < 0) {
            return null;
         } else if ((var2 & -33) != var1) {
            throw new IOException("unexpected identifier encountered: " + var2);
         } else {
            return this.implParseObject(var2);
         }
      } else {
         throw new IllegalArgumentException("invalid universal tag number: " + var1);
      }
   }

   ASN1TaggedObjectParser parseTaggedObject() throws IOException {
      int var1 = this._in.read();
      if (var1 < 0) {
         return null;
      } else {
         int var2 = var1 & 192;
         if (0 == var2) {
            throw new ASN1Exception("no tagged object found");
         } else {
            return (ASN1TaggedObjectParser)this.implParseObject(var1);
         }
      }
   }

   ASN1EncodableVector readVector() throws IOException {
      int var1 = this._in.read();
      if (var1 < 0) {
         return new ASN1EncodableVector(0);
      } else {
         ASN1EncodableVector var2 = new ASN1EncodableVector();

         do {
            ASN1Encodable var3 = this.implParseObject(var1);
            if (var3 instanceof InMemoryRepresentable) {
               var2.add(((InMemoryRepresentable)var3).getLoadedObject());
            } else {
               var2.add(var3.toASN1Primitive());
            }
         } while ((var1 = this._in.read()) >= 0);

         return var2;
      }
   }

   private void set00Check(boolean var1) {
      if (this._in instanceof IndefiniteLengthInputStream) {
         ((IndefiniteLengthInputStream)this._in).setEofOn00(var1);
      }
   }
}
