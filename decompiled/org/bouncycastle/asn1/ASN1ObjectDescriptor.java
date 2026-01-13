package org.bouncycastle.asn1;

import java.io.IOException;

public final class ASN1ObjectDescriptor extends ASN1Primitive {
   static final ASN1UniversalType TYPE = new ASN1UniversalType(ASN1ObjectDescriptor.class, 7) {
      @Override
      ASN1Primitive fromImplicitPrimitive(DEROctetString var1) {
         return new ASN1ObjectDescriptor((ASN1GraphicString)ASN1GraphicString.TYPE.fromImplicitPrimitive(var1));
      }

      @Override
      ASN1Primitive fromImplicitConstructed(ASN1Sequence var1) {
         return new ASN1ObjectDescriptor((ASN1GraphicString)ASN1GraphicString.TYPE.fromImplicitConstructed(var1));
      }
   };
   private final ASN1GraphicString baseGraphicString;

   public static ASN1ObjectDescriptor getInstance(Object var0) {
      if (var0 != null && !(var0 instanceof ASN1ObjectDescriptor)) {
         if (var0 instanceof ASN1Encodable) {
            ASN1Primitive var1 = ((ASN1Encodable)var0).toASN1Primitive();
            if (var1 instanceof ASN1ObjectDescriptor) {
               return (ASN1ObjectDescriptor)var1;
            }
         } else if (var0 instanceof byte[]) {
            try {
               return (ASN1ObjectDescriptor)TYPE.fromByteArray((byte[])var0);
            } catch (IOException var2) {
               throw new IllegalArgumentException("failed to construct object descriptor from byte[]: " + var2.getMessage());
            }
         }

         throw new IllegalArgumentException("illegal object in getInstance: " + var0.getClass().getName());
      } else {
         return (ASN1ObjectDescriptor)var0;
      }
   }

   public static ASN1ObjectDescriptor getInstance(ASN1TaggedObject var0, boolean var1) {
      return (ASN1ObjectDescriptor)TYPE.getContextTagged(var0, var1);
   }

   public static ASN1ObjectDescriptor getTagged(ASN1TaggedObject var0, boolean var1) {
      return (ASN1ObjectDescriptor)TYPE.getTagged(var0, var1);
   }

   public ASN1ObjectDescriptor(ASN1GraphicString var1) {
      if (null == var1) {
         throw new NullPointerException("'baseGraphicString' cannot be null");
      } else {
         this.baseGraphicString = var1;
      }
   }

   public ASN1GraphicString getBaseGraphicString() {
      return this.baseGraphicString;
   }

   @Override
   boolean encodeConstructed() {
      return false;
   }

   @Override
   int encodedLength(boolean var1) {
      return this.baseGraphicString.encodedLength(var1);
   }

   @Override
   void encode(ASN1OutputStream var1, boolean var2) throws IOException {
      var1.writeIdentifier(var2, 7);
      this.baseGraphicString.encode(var1, false);
   }

   @Override
   ASN1Primitive toDERObject() {
      ASN1GraphicString var1 = (ASN1GraphicString)this.baseGraphicString.toDERObject();
      return var1 == this.baseGraphicString ? this : new ASN1ObjectDescriptor(var1);
   }

   @Override
   ASN1Primitive toDLObject() {
      ASN1GraphicString var1 = (ASN1GraphicString)this.baseGraphicString.toDLObject();
      return var1 == this.baseGraphicString ? this : new ASN1ObjectDescriptor(var1);
   }

   @Override
   boolean asn1Equals(ASN1Primitive var1) {
      if (!(var1 instanceof ASN1ObjectDescriptor)) {
         return false;
      } else {
         ASN1ObjectDescriptor var2 = (ASN1ObjectDescriptor)var1;
         return this.baseGraphicString.asn1Equals(var2.baseGraphicString);
      }
   }

   @Override
   public int hashCode() {
      return ~this.baseGraphicString.hashCode();
   }

   static ASN1ObjectDescriptor createPrimitive(byte[] var0) {
      return new ASN1ObjectDescriptor(ASN1GraphicString.createPrimitive(var0));
   }
}
