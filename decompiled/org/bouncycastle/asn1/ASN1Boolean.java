package org.bouncycastle.asn1;

import java.io.IOException;

public class ASN1Boolean extends ASN1Primitive {
   static final ASN1UniversalType TYPE = new ASN1UniversalType(ASN1Boolean.class, 1) {
      @Override
      ASN1Primitive fromImplicitPrimitive(DEROctetString var1) {
         return ASN1Boolean.createPrimitive(var1.getOctets());
      }
   };
   private static final byte FALSE_VALUE = 0;
   private static final byte TRUE_VALUE = -1;
   public static final ASN1Boolean FALSE = new ASN1Boolean((byte)0);
   public static final ASN1Boolean TRUE = new ASN1Boolean((byte)-1);
   private final byte value;

   public static ASN1Boolean getInstance(Object var0) {
      if (var0 == null || var0 instanceof ASN1Boolean) {
         return (ASN1Boolean)var0;
      } else if (var0 instanceof byte[]) {
         byte[] var1 = (byte[])var0;

         try {
            return (ASN1Boolean)TYPE.fromByteArray(var1);
         } catch (IOException var3) {
            throw new IllegalArgumentException("failed to construct boolean from byte[]: " + var3.getMessage());
         }
      } else {
         throw new IllegalArgumentException("illegal object in getInstance: " + var0.getClass().getName());
      }
   }

   public static ASN1Boolean getInstance(boolean var0) {
      return var0 ? TRUE : FALSE;
   }

   public static ASN1Boolean getInstance(int var0) {
      return var0 != 0 ? TRUE : FALSE;
   }

   public static ASN1Boolean getInstance(ASN1TaggedObject var0, boolean var1) {
      return (ASN1Boolean)TYPE.getContextTagged(var0, var1);
   }

   public static ASN1Boolean getTagged(ASN1TaggedObject var0, boolean var1) {
      return (ASN1Boolean)TYPE.getTagged(var0, var1);
   }

   private ASN1Boolean(byte var1) {
      this.value = var1;
   }

   public boolean isTrue() {
      return this.value != 0;
   }

   @Override
   boolean encodeConstructed() {
      return false;
   }

   @Override
   int encodedLength(boolean var1) {
      return ASN1OutputStream.getLengthOfEncodingDL(var1, 1);
   }

   @Override
   void encode(ASN1OutputStream var1, boolean var2) throws IOException {
      var1.writeEncodingDL(var2, 1, this.value);
   }

   @Override
   boolean asn1Equals(ASN1Primitive var1) {
      if (!(var1 instanceof ASN1Boolean)) {
         return false;
      } else {
         ASN1Boolean var2 = (ASN1Boolean)var1;
         return this.isTrue() == var2.isTrue();
      }
   }

   @Override
   public int hashCode() {
      return this.isTrue() ? 1 : 0;
   }

   @Override
   ASN1Primitive toDERObject() {
      return this.isTrue() ? TRUE : FALSE;
   }

   @Override
   public String toString() {
      return this.isTrue() ? "TRUE" : "FALSE";
   }

   static ASN1Boolean createPrimitive(byte[] var0) {
      if (var0.length != 1) {
         throw new IllegalArgumentException("BOOLEAN value should have 1 byte in it");
      } else {
         byte var1 = var0[0];
         switch (var1) {
            case -1:
               return TRUE;
            case 0:
               return FALSE;
            default:
               return new ASN1Boolean(var1);
         }
      }
   }
}
