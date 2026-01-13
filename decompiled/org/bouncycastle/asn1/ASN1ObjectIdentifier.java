package org.bouncycastle.asn1;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import org.bouncycastle.util.Arrays;

public class ASN1ObjectIdentifier extends ASN1Primitive {
   static final ASN1UniversalType TYPE = new ASN1UniversalType(ASN1ObjectIdentifier.class, 6) {
      @Override
      ASN1Primitive fromImplicitPrimitive(DEROctetString var1) {
         return ASN1ObjectIdentifier.createPrimitive(var1.getOctets(), false);
      }
   };
   private static final int MAX_CONTENTS_LENGTH = 4096;
   private static final int MAX_IDENTIFIER_LENGTH = 16385;
   private static final long LONG_LIMIT = 72057594037927808L;
   private static final ConcurrentMap<ASN1ObjectIdentifier.OidHandle, ASN1ObjectIdentifier> pool = new ConcurrentHashMap<>();
   private final byte[] contents;
   private String identifier;

   public static ASN1ObjectIdentifier fromContents(byte[] var0) {
      if (var0 == null) {
         throw new NullPointerException("'contents' cannot be null");
      } else {
         return createPrimitive(var0, true);
      }
   }

   public static ASN1ObjectIdentifier getInstance(Object var0) {
      if (var0 != null && !(var0 instanceof ASN1ObjectIdentifier)) {
         if (var0 instanceof ASN1Encodable) {
            ASN1Primitive var1 = ((ASN1Encodable)var0).toASN1Primitive();
            if (var1 instanceof ASN1ObjectIdentifier) {
               return (ASN1ObjectIdentifier)var1;
            }
         } else if (var0 instanceof byte[]) {
            try {
               return (ASN1ObjectIdentifier)TYPE.fromByteArray((byte[])var0);
            } catch (IOException var2) {
               throw new IllegalArgumentException("failed to construct object identifier from byte[]: " + var2.getMessage());
            }
         }

         throw new IllegalArgumentException("illegal object in getInstance: " + var0.getClass().getName());
      } else {
         return (ASN1ObjectIdentifier)var0;
      }
   }

   public static ASN1ObjectIdentifier getInstance(ASN1TaggedObject var0, boolean var1) {
      if (!var1 && !var0.isParsed() && var0.hasContextTag()) {
         ASN1Primitive var2 = var0.getBaseObject().toASN1Primitive();
         if (!(var2 instanceof ASN1ObjectIdentifier)) {
            return fromContents(ASN1OctetString.getInstance(var2).getOctets());
         }
      }

      return (ASN1ObjectIdentifier)TYPE.getContextTagged(var0, var1);
   }

   public static ASN1ObjectIdentifier getTagged(ASN1TaggedObject var0, boolean var1) {
      return (ASN1ObjectIdentifier)TYPE.getTagged(var0, var1);
   }

   public static ASN1ObjectIdentifier tryFromID(String var0) {
      if (var0 == null) {
         throw new NullPointerException("'identifier' cannot be null");
      } else {
         if (var0.length() <= 16385 && isValidIdentifier(var0)) {
            byte[] var1 = parseIdentifier(var0);
            if (var1.length <= 4096) {
               return new ASN1ObjectIdentifier(var1, var0);
            }
         }

         return null;
      }
   }

   public ASN1ObjectIdentifier(String var1) {
      checkIdentifier(var1);
      byte[] var2 = parseIdentifier(var1);
      checkContentsLength(var2.length);
      this.contents = var2;
      this.identifier = var1;
   }

   private ASN1ObjectIdentifier(byte[] var1, String var2) {
      this.contents = var1;
      this.identifier = var2;
   }

   public ASN1ObjectIdentifier branch(String var1) {
      ASN1RelativeOID.checkIdentifier(var1);
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
         byte[] var6 = ASN1RelativeOID.parseIdentifier(var1);
         checkContentsLength(this.contents.length + var6.length);
         var2 = Arrays.concatenate(this.contents, var6);
      }

      String var7 = this.getId();
      String var4 = var7 + "." + var1;
      return new ASN1ObjectIdentifier(var2, var4);
   }

   public synchronized String getId() {
      if (this.identifier == null) {
         this.identifier = parseContents(this.contents);
      }

      return this.identifier;
   }

   public boolean on(ASN1ObjectIdentifier var1) {
      byte[] var2 = this.contents;
      byte[] var3 = var1.contents;
      int var4 = var3.length;
      return var2.length > var4 && Arrays.areEqual(var2, 0, var4, var3, 0, var4);
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
      var1.writeEncodingDL(var2, 6, this.contents);
   }

   @Override
   public int hashCode() {
      return Arrays.hashCode(this.contents);
   }

   @Override
   boolean asn1Equals(ASN1Primitive var1) {
      if (this == var1) {
         return true;
      } else if (!(var1 instanceof ASN1ObjectIdentifier)) {
         return false;
      } else {
         ASN1ObjectIdentifier var2 = (ASN1ObjectIdentifier)var1;
         return Arrays.areEqual(this.contents, var2.contents);
      }
   }

   @Override
   public String toString() {
      return this.getId();
   }

   static void checkContentsLength(int var0) {
      if (var0 > 4096) {
         throw new IllegalArgumentException("exceeded OID contents length limit");
      }
   }

   static void checkIdentifier(String var0) {
      if (var0 == null) {
         throw new NullPointerException("'identifier' cannot be null");
      } else if (var0.length() > 16385) {
         throw new IllegalArgumentException("exceeded OID contents length limit");
      } else if (!isValidIdentifier(var0)) {
         throw new IllegalArgumentException("string " + var0 + " not a valid OID");
      }
   }

   static ASN1ObjectIdentifier createPrimitive(byte[] var0, boolean var1) {
      checkContentsLength(var0.length);
      ASN1ObjectIdentifier.OidHandle var2 = new ASN1ObjectIdentifier.OidHandle(var0);
      ASN1ObjectIdentifier var3 = pool.get(var2);
      if (var3 != null) {
         return var3;
      } else if (!ASN1RelativeOID.isValidContents(var0)) {
         throw new IllegalArgumentException("invalid OID contents");
      } else {
         return new ASN1ObjectIdentifier(var1 ? Arrays.clone(var0) : var0, null);
      }
   }

   private static boolean isValidIdentifier(String var0) {
      if (var0.length() >= 3 && var0.charAt(1) == '.') {
         char var1 = var0.charAt(0);
         if (var1 < '0' || var1 > '2') {
            return false;
         } else if (!ASN1RelativeOID.isValidIdentifier(var0, 2)) {
            return false;
         } else if (var1 == '2') {
            return true;
         } else if (var0.length() == 3 || var0.charAt(3) == '.') {
            return true;
         } else {
            return var0.length() != 4 && var0.charAt(4) != '.' ? false : var0.charAt(2) < '4';
         }
      } else {
         return false;
      }
   }

   private static String parseContents(byte[] var0) {
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
                  if (var2 < 40L) {
                     var1.append('0');
                  } else if (var2 < 80L) {
                     var1.append('1');
                     var2 -= 40L;
                  } else {
                     var1.append('2');
                     var2 -= 80L;
                  }

                  var5 = false;
               }

               var1.append('.');
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
                  var1.append('2');
                  var4 = var4.subtract(BigInteger.valueOf(80L));
                  var5 = false;
               }

               var1.append('.');
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

   private static byte[] parseIdentifier(String var0) {
      ByteArrayOutputStream var1 = new ByteArrayOutputStream();
      OIDTokenizer var2 = new OIDTokenizer(var0);
      int var3 = Integer.parseInt(var2.nextToken()) * 40;
      String var4 = var2.nextToken();
      if (var4.length() <= 18) {
         ASN1RelativeOID.writeField(var1, var3 + Long.parseLong(var4));
      } else {
         ASN1RelativeOID.writeField(var1, new BigInteger(var4).add(BigInteger.valueOf(var3)));
      }

      while (var2.hasMoreTokens()) {
         String var5 = var2.nextToken();
         if (var5.length() <= 18) {
            ASN1RelativeOID.writeField(var1, Long.parseLong(var5));
         } else {
            ASN1RelativeOID.writeField(var1, new BigInteger(var5));
         }
      }

      return var1.toByteArray();
   }

   public ASN1ObjectIdentifier intern() {
      ASN1ObjectIdentifier.OidHandle var1 = new ASN1ObjectIdentifier.OidHandle(this.contents);
      ASN1ObjectIdentifier var2 = pool.get(var1);
      if (var2 == null) {
         synchronized (pool) {
            if (!pool.containsKey(var1)) {
               pool.put(var1, this);
               return this;
            } else {
               return pool.get(var1);
            }
         }
      } else {
         return var2;
      }
   }

   static class OidHandle {
      private final int key;
      private final byte[] contents;

      OidHandle(byte[] var1) {
         this.key = Arrays.hashCode(var1);
         this.contents = var1;
      }

      @Override
      public int hashCode() {
         return this.key;
      }

      @Override
      public boolean equals(Object var1) {
         return var1 instanceof ASN1ObjectIdentifier.OidHandle ? Arrays.areEqual(this.contents, ((ASN1ObjectIdentifier.OidHandle)var1).contents) : false;
      }
   }
}
