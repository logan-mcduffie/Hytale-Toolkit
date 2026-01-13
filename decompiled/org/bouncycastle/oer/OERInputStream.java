package org.bouncycastle.oer;

import java.io.ByteArrayInputStream;
import java.io.EOFException;
import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.math.BigInteger;
import java.util.List;
import org.bouncycastle.asn1.ASN1Boolean;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Enumerated;
import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.DERBitString;
import org.bouncycastle.asn1.DERIA5String;
import org.bouncycastle.asn1.DERNull;
import org.bouncycastle.asn1.DEROctetString;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.DERTaggedObject;
import org.bouncycastle.asn1.DERUTF8String;
import org.bouncycastle.util.BigIntegers;
import org.bouncycastle.util.Strings;
import org.bouncycastle.util.encoders.Hex;
import org.bouncycastle.util.io.Streams;

public class OERInputStream extends FilterInputStream {
   private static final int[] bits = new int[]{1, 2, 4, 8, 16, 32, 64, 128};
   private static final int[] bitsR = new int[]{128, 64, 32, 16, 8, 4, 2, 1};
   protected PrintWriter debugOutput = null;
   private int maxByteAllocation = 1048576;
   protected PrintWriter debugStream = null;

   public OERInputStream(InputStream var1) {
      super(var1);
   }

   public OERInputStream(InputStream var1, int var2) {
      super(var1);
      this.maxByteAllocation = var2;
   }

   public static ASN1Encodable parse(byte[] var0, Element var1) throws IOException {
      OERInputStream var2 = new OERInputStream(new ByteArrayInputStream(var0));
      return var2.parse(var1);
   }

   private int countOptionalChildTypes(Element var1) {
      int var2 = 0;

      for (Element var4 : var1.getChildren()) {
         var2 += var4.isExplicit() ? 0 : 1;
      }

      return var2;
   }

   public ASN1Object parse(Element var1) throws IOException {
      switch (var1.getBaseType()) {
         case OPAQUE:
            ElementSupplier var2 = var1.resolveSupplier();
            return this.parse(new Element(var2.build(), var1));
         case Switch:
            throw new IllegalStateException("A switch element should only be found within a sequence.");
         case Supplier:
            return this.parse(new Element(var1.getElementSupplier().build(), var1));
         case SEQ_OF:
            int var22 = this.readLength().intLength();
            byte[] var31 = this.allocateArray(var22);
            if (Streams.readFully(this, var31) != var31.length) {
               throw new IOException("could not read all of count of seq-of values");
            } else {
               int var35 = BigIntegers.fromUnsignedByteArray(var31).intValue();
               this.debugPrint(var1 + "(len = " + var35 + ")");
               ASN1EncodableVector var40 = new ASN1EncodableVector();
               if (var1.getChildren().get(0).getaSwitch() != null) {
                  throw new IllegalStateException("element def for item in SEQ OF has a switch, switches only supported in sequences");
               }

               for (int var42 = 0; var42 < var35; var42++) {
                  Element var45 = Element.expandDeferredDefinition(var1.getChildren().get(0), var1);
                  var40.add(this.parse(var45));
               }

               return new DERSequence(var40);
            }
         case SEQ:
            OERInputStream.Sequence var21 = new OERInputStream.Sequence(this.in, var1);
            this.debugPrint(var1 + var21.toString());
            ASN1EncodableVector var30 = new ASN1EncodableVector();
            List var34 = var1.getChildren();
            int var38 = 0;
            boolean var41 = false;

            for (var38 = 0; var38 < var34.size(); var38++) {
               Element var8 = (Element)var34.get(var38);
               if (var8.getBaseType() != OERDefinition.BaseType.EXTENSION) {
                  if (var8.getBlock() > 0) {
                     break;
                  }

                  var8 = Element.expandDeferredDefinition(var8, var1);
                  Element var9;
                  if (var8.getaSwitch() != null) {
                     var9 = var8.getaSwitch().result(new SwitchIndexer.Asn1EncodableVectorIndexer(var30));
                     if (var9.getParent() != var1) {
                        var9 = new Element(var9, var1);
                     }
                  } else {
                     var9 = var8;
                  }

                  if (var21.valuePresent == null) {
                     var30.add(this.parse(var9));
                  } else if (var21.valuePresent[var38]) {
                     if (var9.isExplicit()) {
                        var30.add(this.parse(var9));
                     } else {
                        var30.add(OEROptional.getInstance(this.parse(var9)));
                     }
                  } else if (var9.getDefaultValue() != null) {
                     var30.add(var8.getDefaultValue());
                  } else {
                     var30.add(this.absent(var8));
                  }
               }
            }

            if (var21.extensionFlagSet) {
               int var44 = this.readLength().intLength();
               byte[] var46 = this.allocateArray(var44);
               if (Streams.readFully(this.in, var46) != var46.length) {
                  throw new IOException("did not fully read presence list.");
               }

               int var10 = 8;

               for (int var11 = var46.length * 8 - var46[0]; var38 < var34.size() || var10 < var11; var38++) {
                  Element var12 = var38 < var34.size() ? (Element)var34.get(var38) : null;
                  if (var12 == null) {
                     if ((var46[var10 / 8] & bitsR[var10 % 8]) != 0) {
                        int var13 = this.readLength().intLength();

                        while (--var13 >= 0) {
                           this.in.read();
                        }
                     }
                  } else if (var10 < var11 && (var46[var10 / 8] & bitsR[var10 % 8]) != 0) {
                     var30.add(this.parseOpenType(var12));
                  } else {
                     if (var12.isExplicit()) {
                        throw new IOException("extension is marked as explicit but is not defined in presence list");
                     }

                     var30.add(OEROptional.ABSENT);
                  }

                  var10++;
               }
            }

            return new DERSequence(var30);
         case CHOICE:
            OERInputStream.Choice var20 = this.choice();
            this.debugPrint(var20.toString() + " " + var20.tag);
            if (var20.isContextSpecific()) {
               Element var29 = Element.expandDeferredDefinition(var1.getChildren().get(var20.getTag()), var1);
               if (var29.getBlock() > 0) {
                  this.debugPrint("Chosen (Ext): " + var29);
                  return new DERTaggedObject(var20.tag, this.parseOpenType(var29));
               }

               this.debugPrint("Chosen: " + var29);
               return new DERTaggedObject(var20.tag, this.parse(var29));
            } else if (var20.isApplicationTagClass()) {
               throw new IllegalStateException("Unimplemented tag type");
            } else if (var20.isPrivateTagClass()) {
               throw new IllegalStateException("Unimplemented tag type");
            } else {
               if (var20.isUniversalTagClass()) {
                  throw new IllegalStateException("Unimplemented tag type");
               }

               throw new IllegalStateException("Unimplemented tag type");
            }
         case ENUM:
            BigInteger var19 = this.enumeration();
            this.debugPrint(var1 + "ENUM(" + var19 + ") = " + var1.getChildren().get(var19.intValue()).getLabel());
            return new ASN1Enumerated(var19);
         case INT:
            int var33 = var1.intBytesForRange();
            byte[] var18;
            BigInteger var28;
            if (var33 != 0) {
               var18 = this.allocateArray(Math.abs(var33));
               Streams.readFully(this, var18);
               if (var33 < 0) {
                  var28 = new BigInteger(var18);
               } else {
                  var28 = BigIntegers.fromUnsignedByteArray(var18);
               }
            } else if (var1.isLowerRangeZero()) {
               OERInputStream.LengthInfo var36 = this.readLength();
               var18 = this.allocateArray(var36.intLength());
               Streams.readFully(this, var18);
               if (var18.length == 0) {
                  var28 = BigInteger.ZERO;
               } else {
                  var28 = new BigInteger(1, var18);
               }
            } else {
               OERInputStream.LengthInfo var37 = this.readLength();
               var18 = this.allocateArray(var37.intLength());
               Streams.readFully(this, var18);
               if (var18.length == 0) {
                  var28 = BigInteger.ZERO;
               } else {
                  var28 = new BigInteger(var18);
               }
            }

            if (this.debugOutput != null) {
               this.debugPrint(var1 + "INTEGER byteLen= " + var18.length + " hex= " + var28.toString(16) + ")");
            }

            return new ASN1Integer(var28);
         case OCTET_STRING:
            int var26 = 0;
            if (var1.getUpperBound() != null && var1.getUpperBound().equals(var1.getLowerBound())) {
               var26 = var1.getUpperBound().intValue();
            } else {
               var26 = this.readLength().intLength();
            }

            byte[] var17 = this.allocateArray(var26);
            if (Streams.readFully(this, var17) != var26) {
               throw new IOException("did not read all of " + var1.getLabel());
            }

            if (this.debugOutput != null) {
               int var32 = Math.min(var17.length, 32);
               this.debugPrint(var1 + "OCTET STRING (" + var17.length + ") = " + Hex.toHexString(var17, 0, var32) + " " + (var17.length > 32 ? "..." : ""));
            }

            return new DEROctetString(var17);
         case IA5String:
            byte[] var16;
            if (var1.isFixedLength()) {
               var16 = this.allocateArray(var1.getUpperBound().intValue());
            } else {
               var16 = this.allocateArray(this.readLength().intLength());
            }

            if (Streams.readFully(this, var16) != var16.length) {
               throw new IOException("could not read all of IA5 string");
            }

            String var25 = Strings.fromByteArray(var16);
            if (this.debugOutput != null) {
               this.debugPrint(var1.appendLabel("IA5 String (" + var16.length + ") = " + var25));
            }

            return new DERIA5String(var25);
         case UTF8_STRING:
            byte[] var15 = this.allocateArray(this.readLength().intLength());
            if (Streams.readFully(this, var15) != var15.length) {
               throw new IOException("could not read all of utf 8 string");
            }

            String var24 = Strings.fromUTF8ByteArray(var15);
            if (this.debugOutput != null) {
               this.debugPrint(var1 + "UTF8 String (" + var15.length + ") = " + var24);
            }

            return new DERUTF8String(var24);
         case BIT_STRING:
            byte[] var14;
            if (var1.isFixedLength()) {
               var14 = new byte[var1.getLowerBound().intValue() / 8];
            } else if (BigInteger.ZERO.compareTo(var1.getUpperBound()) > 0) {
               var14 = this.allocateArray(var1.getUpperBound().intValue() / 8);
            } else {
               var14 = this.allocateArray(this.readLength().intLength() / 8);
            }

            Streams.readFully(this, var14);
            if (this.debugOutput != null) {
               StringBuffer var23 = new StringBuffer();
               var23.append("BIT STRING(" + var14.length * 8 + ") = ");

               for (int var5 = 0; var5 != var14.length; var5++) {
                  byte var6 = var14[var5];

                  for (int var7 = 0; var7 < 8; var7++) {
                     var23.append((var6 & 128) > 0 ? "1" : "0");
                     var6 = (byte)(var6 << 1);
                  }
               }

               this.debugPrint(var1 + var23.toString());
            }

            return new DERBitString(var14);
         case NULL:
            this.debugPrint(var1 + "NULL");
            return DERNull.INSTANCE;
         case EXTENSION:
            OERInputStream.LengthInfo var3 = this.readLength();
            byte[] var4 = new byte[var3.intLength()];
            if (Streams.readFully(this, var4) != var3.intLength()) {
               throw new IOException("could not read all of count of open value in choice (...) ");
            }

            this.debugPrint("ext " + var3.intLength() + " " + Hex.toHexString(var4));
            return new DEROctetString(var4);
         case BOOLEAN:
            if (this.read() == 0) {
               return ASN1Boolean.FALSE;
            }

            return ASN1Boolean.TRUE;
         default:
            throw new IllegalStateException("Unhandled type " + var1.getBaseType());
      }
   }

   private ASN1Encodable absent(Element var1) {
      this.debugPrint(var1 + "Absent");
      return OEROptional.ABSENT;
   }

   private byte[] allocateArray(int var1) {
      if (var1 > this.maxByteAllocation) {
         throw new IllegalArgumentException("required byte array size " + var1 + " was greater than " + this.maxByteAllocation);
      } else {
         return new byte[var1];
      }
   }

   public BigInteger parseInt(boolean var1, int var2) throws Exception {
      byte[] var3 = new byte[var2];
      int var4 = Streams.readFully(this, var3);
      if (var4 != var3.length) {
         throw new IllegalStateException("integer not fully read");
      } else {
         return var1 ? new BigInteger(1, var3) : new BigInteger(var3);
      }
   }

   public BigInteger uint8() throws Exception {
      return this.parseInt(true, 1);
   }

   public BigInteger uint16() throws Exception {
      return this.parseInt(true, 2);
   }

   public BigInteger uint32() throws Exception {
      return this.parseInt(true, 4);
   }

   public BigInteger uint64() throws Exception {
      return this.parseInt(false, 8);
   }

   public BigInteger int8() throws Exception {
      return this.parseInt(false, 1);
   }

   public BigInteger int16() throws Exception {
      return this.parseInt(false, 2);
   }

   public BigInteger int32() throws Exception {
      return this.parseInt(false, 4);
   }

   public BigInteger int64() throws Exception {
      return this.parseInt(false, 8);
   }

   public OERInputStream.LengthInfo readLength() throws IOException {
      boolean var1 = false;
      int var2 = this.read();
      if (var2 == -1) {
         throw new EOFException("expecting length");
      } else if ((var2 & 128) == 0) {
         this.debugPrint("Len (Short form): " + (var2 & 127));
         return new OERInputStream.LengthInfo(BigInteger.valueOf(var2 & 127), true);
      } else {
         byte[] var3 = new byte[var2 & 127];
         if (Streams.readFully(this, var3) != var3.length) {
            throw new EOFException("did not read all bytes of length definition");
         } else {
            this.debugPrint("Len (Long Form): " + (var2 & 127) + " actual len: " + Hex.toHexString(var3));
            return new OERInputStream.LengthInfo(BigIntegers.fromUnsignedByteArray(var3), false);
         }
      }
   }

   public BigInteger enumeration() throws IOException {
      int var1 = this.read();
      if (var1 == -1) {
         throw new EOFException("expecting prefix of enumeration");
      } else if ((var1 & 128) == 128) {
         int var2 = var1 & 127;
         if (var2 == 0) {
            return BigInteger.ZERO;
         } else {
            byte[] var3 = new byte[var2];
            int var4 = Streams.readFully(this, var3);
            if (var4 != var3.length) {
               throw new EOFException("unable to fully read integer component of enumeration");
            } else {
               return new BigInteger(1, var3);
            }
         }
      } else {
         return BigInteger.valueOf(var1);
      }
   }

   protected ASN1Encodable parseOpenType(Element var1) throws IOException {
      int var2 = this.readLength().intLength();
      byte[] var3 = this.allocateArray(var2);
      if (Streams.readFully(this.in, var3) != var3.length) {
         throw new IOException("did not fully read open type as raw bytes");
      } else {
         OERInputStream var4 = null;

         ASN1Object var6;
         try {
            ByteArrayInputStream var5 = new ByteArrayInputStream(var3);
            var4 = new OERInputStream(var5);
            var6 = var4.parse(var1);
         } finally {
            if (var4 != null) {
               var4.close();
            }
         }

         return var6;
      }
   }

   public OERInputStream.Choice choice() throws IOException {
      return new OERInputStream.Choice(this);
   }

   protected void debugPrint(String var1) {
      if (this.debugOutput != null) {
         StackTraceElement[] var2 = Thread.currentThread().getStackTrace();
         int var3 = -1;

         for (int var4 = 0; var4 != var2.length; var4++) {
            StackTraceElement var5 = var2[var4];
            if (var5.getMethodName().equals("debugPrint")) {
               var3 = 0;
            } else if (var5.getClassName().contains("OERInput")) {
               var3++;
            }
         }

         while (var3 > 0) {
            this.debugOutput.append("    ");
            var3--;
         }

         this.debugOutput.append(var1).append("\n");
         this.debugOutput.flush();
      }
   }

   public static class Choice extends OERInputStream {
      final int preamble = this.read();
      final int tag;
      final int tagClass;

      public Choice(InputStream var1) throws IOException {
         super(var1);
         if (this.preamble < 0) {
            throw new EOFException("expecting preamble byte of choice");
         } else {
            this.tagClass = this.preamble & 192;
            int var2 = this.preamble & 63;
            if (var2 >= 63) {
               var2 = 0;
               int var3 = 0;

               do {
                  var3 = var1.read();
                  if (var3 < 0) {
                     throw new EOFException("expecting further tag bytes");
                  }

                  var2 <<= 7;
                  var2 |= var3 & 127;
               } while ((var3 & 128) != 0);
            }

            this.tag = var2;
         }
      }

      @Override
      public String toString() {
         StringBuilder var1 = new StringBuilder();
         var1.append("CHOICE(");
         switch (this.tagClass) {
            case 0:
               var1.append("Universal ");
               break;
            case 64:
               var1.append("Application ");
               break;
            case 128:
               var1.append("ContextSpecific ");
               break;
            case 192:
               var1.append("Private ");
         }

         var1.append("Tag = " + this.tag);
         var1.append(")");
         return var1.toString();
      }

      public int getTagClass() {
         return this.tagClass;
      }

      public int getTag() {
         return this.tag;
      }

      public boolean isContextSpecific() {
         return this.tagClass == 128;
      }

      public boolean isUniversalTagClass() {
         return this.tagClass == 0;
      }

      public boolean isApplicationTagClass() {
         return this.tagClass == 64;
      }

      public boolean isPrivateTagClass() {
         return this.tagClass == 192;
      }
   }

   private static final class LengthInfo {
      private final BigInteger length;
      private final boolean shortForm;

      public LengthInfo(BigInteger var1, boolean var2) {
         this.length = var1;
         this.shortForm = var2;
      }

      private int intLength() {
         return BigIntegers.intValueExact(this.length);
      }
   }

   public static class Sequence extends OERInputStream {
      private final int preamble;
      private final boolean[] valuePresent;
      private final boolean extensionFlagSet;

      public Sequence(InputStream var1, Element var2) throws IOException {
         super(var1);
         if (!var2.hasPopulatedExtension() && var2.getOptionals() <= 0 && !var2.hasDefaultChildren()) {
            this.preamble = 0;
            this.extensionFlagSet = false;
            this.valuePresent = null;
         } else {
            this.preamble = this.in.read();
            if (this.preamble < 0) {
               throw new EOFException("expecting preamble byte of sequence");
            } else {
               this.extensionFlagSet = var2.hasPopulatedExtension() && (this.preamble & 128) == 128;
               this.valuePresent = new boolean[var2.getChildren().size()];
               byte var3 = 0;
               int var4 = var2.hasPopulatedExtension() ? 6 : 7;
               int var5 = this.preamble;
               int var6 = 0;

               for (Element var8 : var2.getChildren()) {
                  if (var8.getBaseType() != OERDefinition.BaseType.EXTENSION) {
                     if (var8.getBlock() != var3) {
                        break;
                     }

                     if (var8.isExplicit()) {
                        this.valuePresent[var6++] = true;
                     } else {
                        if (var4 < 0) {
                           var5 = var1.read();
                           if (var5 < 0) {
                              throw new EOFException("expecting mask byte sequence");
                           }

                           var4 = 7;
                        }

                        this.valuePresent[var6++] = (var5 & OERInputStream.bits[var4]) > 0;
                        var4--;
                     }
                  }
               }
            }
         }
      }

      public boolean hasOptional(int var1) {
         return this.valuePresent[var1];
      }

      public boolean hasExtension() {
         return this.extensionFlagSet;
      }

      @Override
      public String toString() {
         StringBuilder var1 = new StringBuilder();
         var1.append("SEQ(");
         var1.append(this.hasExtension() ? "Ext " : "");
         if (this.valuePresent == null) {
            var1.append("*");
         } else {
            for (int var2 = 0; var2 < this.valuePresent.length; var2++) {
               if (this.valuePresent[var2]) {
                  var1.append("1");
               } else {
                  var1.append("0");
               }
            }
         }

         var1.append(")");
         return var1.toString();
      }
   }
}
