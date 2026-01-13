package org.bouncycastle.asn1;

import java.io.IOException;

public abstract class ASN1Null extends ASN1Primitive {
   static final ASN1UniversalType TYPE = new ASN1UniversalType(ASN1Null.class, 5) {
      @Override
      ASN1Primitive fromImplicitPrimitive(DEROctetString var1) {
         ASN1Null.checkContentsLength(var1.getOctetsLength());
         return ASN1Null.createPrimitive();
      }
   };

   public static ASN1Null getInstance(Object var0) {
      if (var0 instanceof ASN1Null) {
         return (ASN1Null)var0;
      } else if (var0 != null) {
         try {
            return (ASN1Null)TYPE.fromByteArray((byte[])var0);
         } catch (IOException var2) {
            throw new IllegalArgumentException("failed to construct NULL from byte[]: " + var2.getMessage());
         }
      } else {
         return null;
      }
   }

   public static ASN1Null getInstance(ASN1TaggedObject var0, boolean var1) {
      return (ASN1Null)TYPE.getContextTagged(var0, var1);
   }

   public static ASN1Null getTagged(ASN1TaggedObject var0, boolean var1) {
      return (ASN1Null)TYPE.getTagged(var0, var1);
   }

   ASN1Null() {
   }

   @Override
   public int hashCode() {
      return -1;
   }

   @Override
   boolean asn1Equals(ASN1Primitive var1) {
      return var1 instanceof ASN1Null;
   }

   @Override
   public String toString() {
      return "NULL";
   }

   static void checkContentsLength(int var0) {
      if (0 != var0) {
         throw new IllegalStateException("malformed NULL encoding encountered");
      }
   }

   static ASN1Null createPrimitive() {
      return DERNull.INSTANCE;
   }
}
