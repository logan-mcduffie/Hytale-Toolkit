package org.bouncycastle.asn1;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import org.bouncycastle.util.Arrays;
import org.bouncycastle.util.Properties;

public class ASN1RelativeOID extends ASN1Primitive {
   static final ASN1UniversalType TYPE = new ASN1UniversalType(ASN1RelativeOID.class, 13) {
      @Override
      ASN1Primitive fromImplicitPrimitive(DEROctetString var1) {
         return ASN1RelativeOID.createPrimitive(var1.getOctets(), false);
      }
   };
   private static final int MAX_CONTENTS_LENGTH = 4096;
   private static final int MAX_IDENTIFIER_LENGTH = 16383;
   private static final long LONG_LIMIT = 72057594037927808L;
   private static final ConcurrentMap<ASN1ObjectIdentifier.OidHandle, ASN1RelativeOID> pool = new ConcurrentHashMap<>();
   private final byte[] contents;
   private String identifier;

   public static ASN1RelativeOID fromContents(byte[] var0) {
      if (var0 == null) {
         throw new NullPointerException("'contents' cannot be null");
      } else {
         return createPrimitive(var0, true);
      }
   }

   public static ASN1RelativeOID getInstance(Object var0) {
      if (var0 != null && !(var0 instanceof ASN1RelativeOID)) {
         if (var0 instanceof ASN1Encodable) {
            ASN1Primitive var1 = ((ASN1Encodable)var0).toASN1Primitive();
            if (var1 instanceof ASN1RelativeOID) {
               return (ASN1RelativeOID)var1;
            }
         } else if (var0 instanceof byte[]) {
            byte[] var4 = (byte[])var0;

            try {
               return (ASN1RelativeOID)TYPE.fromByteArray(var4);
            } catch (IOException var3) {
               throw new IllegalArgumentException("failed to construct relative OID from byte[]: " + var3.getMessage());
            }
         }

         throw new IllegalArgumentException("illegal object in getInstance: " + var0.getClass().getName());
      } else {
         return (ASN1RelativeOID)var0;
      }
   }

   public static ASN1RelativeOID getInstance(ASN1TaggedObject var0, boolean var1) {
      return (ASN1RelativeOID)TYPE.getContextTagged(var0, var1);
   }

   public static ASN1RelativeOID getTagged(ASN1TaggedObject var0, boolean var1) {
      return (ASN1RelativeOID)TYPE.getTagged(var0, var1);
   }

   public static ASN1RelativeOID tryFromID(String var0) {
      if (var0 == null) {
         throw new NullPointerException("'identifier' cannot be null");
      } else {
         if (var0.length() <= 16383 && isValidIdentifier(var0, 0)) {
            byte[] var1 = parseIdentifier(var0);
            if (var1.length <= 4096) {
               return new ASN1RelativeOID(var1, var0);
            }
         }

         return null;
      }
   }

   public ASN1RelativeOID(String var1) {
      checkIdentifier(var1);
      byte[] var2 = parseIdentifier(var1);
      checkContentsLength(var2.length);
      this.contents = var2;
      this.identifier = var1;
   }

   private ASN1RelativeOID(byte[] var1, String var2) {
      this.contents = var1;
      this.identifier = var2;
   }

   public ASN1RelativeOID branch(String var1) {
      checkIdentifier(var1);
      byte[] var2;
      if (var1.length() <= 2) {
         checkContentsLength(this.contents.length + 1);
         int var3 = var1.charAt(0) - '0';
         if (var1.length() == 2) {
            var3 *= 10;
            var3 += var1.charAt(1) - '0';
         }

         var2 = Arrays.append(this.contents, (byte)var3);
      } else {
         byte[] var6 = parseIdentifier(var1);
         checkContentsLength(this.contents.length + var6.length);
         var2 = Arrays.concatenate(this.contents, var6);
      }

      String var7 = this.getId();
      String var4 = var7 + "." + var1;
      return new ASN1RelativeOID(var2, var4);
   }

   public synchronized String getId() {
      if (this.identifier == null) {
         this.identifier = parseContents(this.contents);
      }

      return this.identifier;
   }

   @Override
   public int hashCode() {
      return Arrays.hashCode(this.contents);
   }

   @Override
   public String toString() {
      return this.getId();
   }

   @Override
   boolean asn1Equals(ASN1Primitive var1) {
      if (this == var1) {
         return true;
      } else if (!(var1 instanceof ASN1RelativeOID)) {
         return false;
      } else {
         ASN1RelativeOID var2 = (ASN1RelativeOID)var1;
         return Arrays.areEqual(this.contents, var2.contents);
      }
   }

   @Override
   int encodedLength(boolean var1) {
      return ASN1OutputStream.getLengthOfEncodingDL(var1, this.contents.length);
   }

   @Override
   void encode(ASN1OutputStream var1, boolean var2) throws IOException {
      var1.writeEncodingDL(var2, 13, this.contents);
   }

   @Override
   boolean encodeConstructed() {
      return false;
   }

   static void checkContentsLength(int var0) {
      if (var0 > 4096) {
         throw new IllegalArgumentException("exceeded relative OID contents length limit");
      }
   }

   static void checkIdentifier(String var0) {
      if (var0 == null) {
         throw new NullPointerException("'identifier' cannot be null");
      } else if (var0.length() > 16383) {
         throw new IllegalArgumentException("exceeded relative OID contents length limit");
      } else if (!isValidIdentifier(var0, 0)) {
         throw new IllegalArgumentException("string " + var0 + " not a valid relative OID");
      }
   }

   static ASN1RelativeOID createPrimitive(byte[] var0, boolean var1) {
      checkContentsLength(var0.length);
      ASN1ObjectIdentifier.OidHandle var2 = new ASN1ObjectIdentifier.OidHandle(var0);
      ASN1RelativeOID var3 = pool.get(var2);
      if (var3 != null) {
         return var3;
      } else if (!isValidContents(var0)) {
         throw new IllegalArgumentException("invalid relative OID contents");
      } else {
         return new ASN1RelativeOID(var1 ? Arrays.clone(var0) : var0, null);
      }
   }

   static boolean isValidContents(byte[] var0) {
      if (Properties.isOverrideSet("org.bouncycastle.asn1.allow_wrong_oid_enc")) {
         return true;
      } else if (var0.length < 1) {
         return false;
      } else {
         boolean var1 = true;

         for (int var2 = 0; var2 < var0.length; var2++) {
            if (var1 && (var0[var2] & 255) == 128) {
               return false;
            }

            var1 = (var0[var2] & 128) == 0;
         }

         return var1;
      }
   }

   static boolean isValidIdentifier(String var0, int var1) {
      int var2 = 0;
      int var3 = var0.length();

      while (--var3 >= var1) {
         char var4 = var0.charAt(var3);
         if (var4 != '.') {
            if ('0' > var4 || var4 > '9') {
               return false;
            }

            var2++;
         } else {
            if (0 == var2 || var2 > 1 && var0.charAt(var3 + 1) == '0') {
               return false;
            }

            var2 = 0;
         }
      }

      return 0 != var2 && (var2 <= 1 || var0.charAt(var3 + 1) != '0');
   }

   static String parseContents(byte[] var0) {
      StringBuilder var1 = new StringBuilder();
      long var2 = 0L;
      BigInteger var4 = null;
      boolean var5 = true;

      for (int var6 = 0; var6 != var0.length; var6++) {
         int var7 = var0[var6] & 255;
         if (var2 <= 72057594037927808L) {
            var2 += var7 & 127;
            if ((var7 & 128) == 0) {
               if (var5) {
                  var5 = false;
               } else {
                  var1.append('.');
               }

               var1.append(var2);
               var2 = 0L;
            } else {
               var2 <<= 7;
            }
         } else {
            if (var4 == null) {
               var4 = BigInteger.valueOf(var2);
            }

            var4 = var4.or(BigInteger.valueOf(var7 & 127));
            if ((var7 & 128) == 0) {
               if (var5) {
                  var5 = false;
               } else {
                  var1.append('.');
               }

               var1.append(var4);
               var4 = null;
               var2 = 0L;
            } else {
               var4 = var4.shiftLeft(7);
            }
         }
      }

      return var1.toString();
   }

   static byte[] parseIdentifier(String var0) {
      ByteArrayOutputStream var1 = new ByteArrayOutputStream();
      OIDTokenizer var2 = new OIDTokenizer(var0);

      while (var2.hasMoreTokens()) {
         String var3 = var2.nextToken();
         if (var3.length() <= 18) {
            writeField(var1, Long.parseLong(var3));
         } else {
            writeField(var1, new BigInteger(var3));
         }
      }

      return var1.toByteArray();
   }

   static void writeField(ByteArrayOutputStream var0, long var1) {
      byte[] var3 = new byte[9];
      int var4 = 8;

      for (var3[var4] = (byte)((int)var1 & 127); var1 >= 128L; var3[--var4] = (byte)((int)var1 | 128)) {
         var1 >>= 7;
      }

      var0.write(var3, var4, 9 - var4);
   }

   static void writeField(ByteArrayOutputStream var0, BigInteger var1) {
      int var2 = (var1.bitLength() + 6) / 7;
      if (var2 == 0) {
         var0.write(0);
      } else {
         BigInteger var3 = var1;
         byte[] var4 = new byte[var2];

         for (int var5 = var2 - 1; var5 >= 0; var5--) {
            var4[var5] = (byte)(var3.intValue() | 128);
            var3 = var3.shiftRight(7);
         }

         var4[var2 - 1] = (byte)(var4[var2 - 1] & 127);
         var0.write(var4, 0, var4.length);
      }
   }
}
