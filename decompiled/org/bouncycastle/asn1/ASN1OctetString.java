package org.bouncycastle.asn1;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import org.bouncycastle.util.Arrays;
import org.bouncycastle.util.Strings;
import org.bouncycastle.util.encoders.Hex;

public abstract class ASN1OctetString extends ASN1Primitive implements ASN1OctetStringParser {
   static final ASN1UniversalType TYPE = new ASN1UniversalType(ASN1OctetString.class, 4) {
      @Override
      ASN1Primitive fromImplicitPrimitive(DEROctetString var1) {
         return var1;
      }

      @Override
      ASN1Primitive fromImplicitConstructed(ASN1Sequence var1) {
         return var1.toASN1OctetString();
      }
   };
   static final byte[] EMPTY_OCTETS = new byte[0];
   byte[] string;

   public static ASN1OctetString getInstance(ASN1TaggedObject var0, boolean var1) {
      return (ASN1OctetString)TYPE.getContextTagged(var0, var1);
   }

   public static ASN1OctetString getTagged(ASN1TaggedObject var0, boolean var1) {
      return (ASN1OctetString)TYPE.getTagged(var0, var1);
   }

   public static ASN1OctetString getInstance(Object var0) {
      if (var0 != null && !(var0 instanceof ASN1OctetString)) {
         if (var0 instanceof ASN1Encodable) {
            ASN1Primitive var1 = ((ASN1Encodable)var0).toASN1Primitive();
            if (var1 instanceof ASN1OctetString) {
               return (ASN1OctetString)var1;
            }
         } else if (var0 instanceof byte[]) {
            try {
               return (ASN1OctetString)TYPE.fromByteArray((byte[])var0);
            } catch (IOException var2) {
               throw new IllegalArgumentException("failed to construct OCTET STRING from byte[]: " + var2.getMessage());
            }
         }

         throw new IllegalArgumentException("illegal object in getInstance: " + var0.getClass().getName());
      } else {
         return (ASN1OctetString)var0;
      }
   }

   public ASN1OctetString(byte[] var1) {
      if (var1 == null) {
         throw new NullPointerException("'string' cannot be null");
      } else {
         this.string = var1;
      }
   }

   @Override
   public InputStream getOctetStream() {
      return new ByteArrayInputStream(this.string);
   }

   public ASN1OctetStringParser parser() {
      return this;
   }

   public byte[] getOctets() {
      return this.string;
   }

   public int getOctetsLength() {
      return this.getOctets().length;
   }

   @Override
   public int hashCode() {
      return Arrays.hashCode(this.getOctets());
   }

   @Override
   boolean asn1Equals(ASN1Primitive var1) {
      if (!(var1 instanceof ASN1OctetString)) {
         return false;
      } else {
         ASN1OctetString var2 = (ASN1OctetString)var1;
         return Arrays.areEqual(this.string, var2.string);
      }
   }

   @Override
   public ASN1Primitive getLoadedObject() {
      return this.toASN1Primitive();
   }

   @Override
   ASN1Primitive toDERObject() {
      return new DEROctetString(this.string);
   }

   @Override
   ASN1Primitive toDLObject() {
      return new DEROctetString(this.string);
   }

   @Override
   public String toString() {
      return "#" + Strings.fromByteArray(Hex.encode(this.string));
   }

   static ASN1OctetString createPrimitive(byte[] var0) {
      return new DEROctetString(var0);
   }
}
