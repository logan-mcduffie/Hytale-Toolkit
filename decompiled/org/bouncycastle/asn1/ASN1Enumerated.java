package org.bouncycastle.asn1;

import java.io.IOException;
import java.math.BigInteger;
import org.bouncycastle.util.Arrays;

public class ASN1Enumerated extends ASN1Primitive {
   static final ASN1UniversalType TYPE = new ASN1UniversalType(ASN1Enumerated.class, 10) {
      @Override
      ASN1Primitive fromImplicitPrimitive(DEROctetString var1) {
         return ASN1Enumerated.createPrimitive(var1.getOctets(), false);
      }
   };
   private final byte[] contents;
   private final int start;
   private static final ASN1Enumerated[] cache = new ASN1Enumerated[12];

   public static ASN1Enumerated getInstance(Object var0) {
      if (var0 == null || var0 instanceof ASN1Enumerated) {
         return (ASN1Enumerated)var0;
      } else if (var0 instanceof byte[]) {
         try {
            return (ASN1Enumerated)TYPE.fromByteArray((byte[])var0);
         } catch (Exception var2) {
            throw new IllegalArgumentException("encoding error in getInstance: " + var2.toString());
         }
      } else {
         throw new IllegalArgumentException("illegal object in getInstance: " + var0.getClass().getName());
      }
   }

   public static ASN1Enumerated getInstance(ASN1TaggedObject var0, boolean var1) {
      return (ASN1Enumerated)TYPE.getContextTagged(var0, var1);
   }

   public static ASN1Enumerated getTagged(ASN1TaggedObject var0, boolean var1) {
      return (ASN1Enumerated)TYPE.getTagged(var0, var1);
   }

   public ASN1Enumerated(int var1) {
      if (var1 < 0) {
         throw new IllegalArgumentException("enumerated must be non-negative");
      } else {
         this.contents = BigInteger.valueOf(var1).toByteArray();
         this.start = 0;
      }
   }

   public ASN1Enumerated(BigInteger var1) {
      if (var1.signum() < 0) {
         throw new IllegalArgumentException("enumerated must be non-negative");
      } else {
         this.contents = var1.toByteArray();
         this.start = 0;
      }
   }

   public ASN1Enumerated(byte[] var1) {
      this(var1, true);
   }

   ASN1Enumerated(byte[] var1, boolean var2) {
      if (ASN1Integer.isMalformed(var1)) {
         throw new IllegalArgumentException("malformed enumerated");
      } else if (0 != (var1[0] & 128)) {
         throw new IllegalArgumentException("enumerated must be non-negative");
      } else {
         this.contents = var2 ? Arrays.clone(var1) : var1;
         this.start = ASN1Integer.signBytesToSkip(var1);
      }
   }

   public BigInteger getValue() {
      return new BigInteger(this.contents);
   }

   public boolean hasValue(int var1) {
      return this.contents.length - this.start <= 4 && ASN1Integer.intValue(this.contents, this.start, -1) == var1;
   }

   public boolean hasValue(BigInteger var1) {
      return null != var1 && ASN1Integer.intValue(this.contents, this.start, -1) == var1.intValue() && this.getValue().equals(var1);
   }

   public int intValueExact() {
      int var1 = this.contents.length - this.start;
      if (var1 > 4) {
         throw new ArithmeticException("ASN.1 Enumerated out of int range");
      } else {
         return ASN1Integer.intValue(this.contents, this.start, -1);
      }
   }

   @Override
   boolean encodeConstructed() {
      return false;
   }

   @Override
   int encodedLength(boolean var1) {
      return ASN1OutputStream.getLengthOfEncodingDL(var1, this.contents.length);
   }

   @Override
   void encode(ASN1OutputStream var1, boolean var2) throws IOException {
      var1.writeEncodingDL(var2, 10, this.contents);
   }

   @Override
   boolean asn1Equals(ASN1Primitive var1) {
      if (!(var1 instanceof ASN1Enumerated)) {
         return false;
      } else {
         ASN1Enumerated var2 = (ASN1Enumerated)var1;
         return Arrays.areEqual(this.contents, var2.contents);
      }
   }

   @Override
   public int hashCode() {
      return Arrays.hashCode(this.contents);
   }

   static ASN1Enumerated createPrimitive(byte[] var0, boolean var1) {
      if (var0.length > 1) {
         return new ASN1Enumerated(var0, var1);
      } else if (var0.length == 0) {
         throw new IllegalArgumentException("ENUMERATED has zero length");
      } else {
         int var2 = var0[0] & 255;
         if (var2 >= cache.length) {
            return new ASN1Enumerated(var0, var1);
         } else {
            ASN1Enumerated var3 = cache[var2];
            if (var3 == null) {
               var3 = cache[var2] = new ASN1Enumerated(var0, var1);
            }

            return var3;
         }
      }
   }
}
