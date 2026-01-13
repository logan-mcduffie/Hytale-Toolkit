package org.bouncycastle.oer;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.math.BigInteger;
import java.util.Enumeration;
import java.util.List;
import org.bouncycastle.asn1.ASN1BitString;
import org.bouncycastle.asn1.ASN1Boolean;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1Enumerated;
import org.bouncycastle.asn1.ASN1IA5String;
import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.ASN1Set;
import org.bouncycastle.asn1.ASN1TaggedObject;
import org.bouncycastle.asn1.ASN1UTF8String;
import org.bouncycastle.util.BigIntegers;
import org.bouncycastle.util.Pack;
import org.bouncycastle.util.Strings;
import org.bouncycastle.util.encoders.Hex;

public class OEROutputStream extends OutputStream {
   private static final int[] bits = new int[]{1, 2, 4, 8, 16, 32, 64, 128};
   private final OutputStream out;
   protected PrintWriter debugOutput = null;

   public OEROutputStream(OutputStream var1) {
      this.out = var1;
   }

   public static int byteLength(long var0) {
      long var2 = -72057594037927936L;

      int var4;
      for (var4 = 8; var4 > 0 && (var0 & var2) == 0L; var4--) {
         var0 <<= 8;
      }

      return var4;
   }

   public void write(ASN1Encodable var1, Element var2) throws IOException {
      if (var1 != OEROptional.ABSENT) {
         if (var1 instanceof OEROptional) {
            this.write(((OEROptional)var1).get(), var2);
         } else {
            ASN1Primitive var12 = var1.toASN1Primitive();
            switch (var2.getBaseType()) {
               case Supplier:
                  this.write(var12, var2.getElementSupplier().build());
                  break;
               case SEQ:
                  ASN1Sequence var13 = ASN1Sequence.getInstance(var12);
                  int var14 = 7;
                  int var25 = 0;
                  boolean var34 = false;
                  if (var2.isExtensionsInDefinition()) {
                     for (int var41 = 0; var41 < var2.getChildren().size(); var41++) {
                        Element var47 = var2.getChildren().get(var41);
                        if (var47.getBaseType() == OERDefinition.BaseType.EXTENSION) {
                           break;
                        }

                        if (var47.getBlock() > 0 && var41 < var13.size() && !OEROptional.ABSENT.equals(var13.getObjectAt(var41))) {
                           var34 = true;
                           break;
                        }
                     }

                     if (var34) {
                        var25 |= bits[var14];
                     }

                     var14--;
                  }

                  for (int var42 = 0; var42 < var2.getChildren().size(); var42++) {
                     Element var48 = var2.getChildren().get(var42);
                     if (var48.getBaseType() != OERDefinition.BaseType.EXTENSION) {
                        if (var48.getBlock() > 0) {
                           break;
                        }

                        var48 = Element.expandDeferredDefinition(var48, var2);
                        if (var2.getaSwitch() != null) {
                           var48 = var2.getaSwitch().result(new SwitchIndexer.Asn1SequenceIndexer(var13));
                           var48 = Element.expandDeferredDefinition(var48, var2);
                        }

                        if (var14 < 0) {
                           this.out.write(var25);
                           var14 = 7;
                           var25 = 0;
                        }

                        ASN1Encodable var56 = var13.getObjectAt(var42);
                        if (var48.isExplicit() && var56 instanceof OEROptional) {
                           throw new IllegalStateException("absent sequence element that is required by oer definition");
                        }

                        if (!var48.isExplicit()) {
                           ASN1Encodable var58 = var13.getObjectAt(var42);
                           if (var48.getDefaultValue() != null) {
                              if (var58 instanceof OEROptional) {
                                 if (((OEROptional)var58).isDefined() && !((OEROptional)var58).get().equals(var48.getDefaultValue())) {
                                    var25 |= bits[var14];
                                 }
                              } else if (!var48.getDefaultValue().equals(var58)) {
                                 var25 |= bits[var14];
                              }
                           } else if (var56 != OEROptional.ABSENT) {
                              var25 |= bits[var14];
                           }

                           var14--;
                        }
                     }
                  }

                  if (var14 != 7) {
                     this.out.write(var25);
                  }

                  List var43 = var2.getChildren();

                  int var51;
                  for (var51 = 0; var51 < var43.size(); var51++) {
                     Element var57 = var2.getChildren().get(var51);
                     if (var57.getBaseType() != OERDefinition.BaseType.EXTENSION) {
                        if (var57.getBlock() > 0) {
                           break;
                        }

                        ASN1Encodable var59 = var13.getObjectAt(var51);
                        if (var57.getaSwitch() != null) {
                           var57 = var57.getaSwitch().result(new SwitchIndexer.Asn1SequenceIndexer(var13));
                        }

                        if (var57.getDefaultValue() == null || !var57.getDefaultValue().equals(var59)) {
                           this.write(var59, var57);
                        }
                     }
                  }

                  if (var34) {
                     ByteArrayOutputStream var60 = new ByteArrayOutputStream();
                     var14 = 7;
                     var25 = 0;

                     for (int var11 = var51; var11 < var43.size(); var11++) {
                        if (var14 < 0) {
                           var60.write(var25);
                           var14 = 7;
                           var25 = 0;
                        }

                        if (var11 < var13.size() && !OEROptional.ABSENT.equals(var13.getObjectAt(var11))) {
                           var25 |= bits[var14];
                        }

                        var14--;
                     }

                     if (var14 != 7) {
                        var60.write(var25);
                     }

                     this.encodeLength(var60.size() + 1);
                     if (var14 == 7) {
                        this.write(0);
                     } else {
                        this.write(var14 + 1);
                     }

                     this.write(var60.toByteArray());

                     for (; var51 < var43.size(); var51++) {
                        if (var51 < var13.size() && !OEROptional.ABSENT.equals(var13.getObjectAt(var51))) {
                           this.writePlainType(var13.getObjectAt(var51), (Element)var43.get(var51));
                        }
                     }
                  }

                  this.out.flush();
                  this.debugPrint(var2.appendLabel(""));
                  break;
               case SEQ_OF:
                  Enumeration var3;
                  if (var12 instanceof ASN1Set) {
                     var3 = ((ASN1Set)var12).getObjects();
                     this.encodeQuantity(((ASN1Set)var12).size());
                  } else {
                     if (!(var12 instanceof ASN1Sequence)) {
                        throw new IllegalStateException("encodable at for SEQ_OF is not a container");
                     }

                     var3 = ((ASN1Sequence)var12).getObjects();
                     this.encodeQuantity(((ASN1Sequence)var12).size());
                  }

                  Element var4 = Element.expandDeferredDefinition(var2.getFirstChid(), var2);

                  while (var3.hasMoreElements()) {
                     Object var24 = var3.nextElement();
                     this.write((ASN1Encodable)var24, var4);
                  }

                  this.out.flush();
                  this.debugPrint(var2.appendLabel(""));
                  break;
               case CHOICE:
                  ASN1Primitive var23 = var12.toASN1Primitive();
                  BitBuilder var33 = new BitBuilder();
                  Object var45 = null;
                  if (!(var23 instanceof ASN1TaggedObject)) {
                     throw new IllegalStateException("only support tagged objects");
                  }

                  ASN1TaggedObject var52 = (ASN1TaggedObject)var23;
                  int var10 = var52.getTagClass();
                  var33.writeBit(var10 & 128).writeBit(var10 & 64);
                  int var40 = var52.getTagNo();
                  var45 = var52.getBaseObject().toASN1Primitive();
                  if (var40 <= 63) {
                     var33.writeBits(var40, 6);
                  } else {
                     var33.writeBits(255L, 6);
                     var33.write7BitBytes(var40);
                  }

                  if (this.debugOutput != null && var23 instanceof ASN1TaggedObject) {
                     var52 = (ASN1TaggedObject)var23;
                     switch (var52.getTagClass()) {
                        case 64:
                           this.debugPrint(var2.appendLabel("AS"));
                           break;
                        case 128:
                           this.debugPrint(var2.appendLabel("CS"));
                           break;
                        case 192:
                           this.debugPrint(var2.appendLabel("PR"));
                     }
                  }

                  var33.writeAndClear(this.out);
                  Element var54 = var2.getChildren().get(var40);
                  Element var55 = Element.expandDeferredDefinition(var54, var2);
                  if (var55.getBlock() > 0) {
                     this.writePlainType((ASN1Encodable)var45, var55);
                  } else {
                     this.write((ASN1Encodable)var45, var55);
                  }

                  this.out.flush();
                  break;
               case ENUM:
                  BigInteger var22;
                  if (var12 instanceof ASN1Integer) {
                     var22 = ASN1Integer.getInstance(var12).getValue();
                  } else {
                     var22 = ASN1Enumerated.getInstance(var12).getValue();
                  }

                  for (Element var38 : var2.getChildren()) {
                     var38 = Element.expandDeferredDefinition(var38, var2);
                     if (var38.getEnumValue().equals(var22)) {
                        if (var22.compareTo(BigInteger.valueOf(127L)) > 0) {
                           byte[] var44 = var22.toByteArray();
                           int var9 = 128 | var44.length & 0xFF;
                           this.out.write(var9);
                           this.out.write(var44);
                        } else {
                           this.out.write(var22.intValue() & 127);
                        }

                        this.out.flush();
                        this.debugPrint(var2.appendLabel(var2.rangeExpression()));
                        return;
                     }
                  }

                  throw new IllegalArgumentException("enum value " + var22 + " " + Hex.toHexString(var22.toByteArray()) + " no in defined child list");
               case INT:
                  ASN1Integer var21 = ASN1Integer.getInstance(var12);
                  int var31 = var2.intBytesForRange();
                  if (var31 > 0) {
                     byte[] var35 = BigIntegers.asUnsignedByteArray(var31, var21.getValue());
                     switch (var31) {
                        case 1:
                        case 2:
                        case 4:
                        case 8:
                           this.out.write(var35);
                           break;
                        case 3:
                        case 5:
                        case 6:
                        case 7:
                        default:
                           throw new IllegalStateException("unknown uint length " + var31);
                     }
                  } else if (var31 < 0) {
                     BigInteger var8 = var21.getValue();
                     byte[] var36;
                     switch (var31) {
                        case -8:
                           var36 = Pack.longToBigEndian(BigIntegers.longValueExact(var8));
                           break;
                        case -7:
                        case -6:
                        case -5:
                        case -3:
                        default:
                           throw new IllegalStateException("unknown twos compliment length");
                        case -4:
                           var36 = Pack.intToBigEndian(BigIntegers.intValueExact(var8));
                           break;
                        case -2:
                           var36 = Pack.shortToBigEndian(BigIntegers.shortValueExact(var8));
                           break;
                        case -1:
                           var36 = new byte[]{BigIntegers.byteValueExact(var8)};
                     }

                     this.out.write(var36);
                  } else {
                     byte[] var37;
                     if (var2.isLowerRangeZero()) {
                        var37 = BigIntegers.asUnsignedByteArray(var21.getValue());
                     } else {
                        var37 = var21.getValue().toByteArray();
                     }

                     this.encodeLength(var37.length);
                     this.out.write(var37);
                  }

                  this.debugPrint(var2.appendLabel(var2.rangeExpression()));
                  this.out.flush();
                  break;
               case OCTET_STRING:
                  ASN1OctetString var20 = ASN1OctetString.getInstance(var12);
                  byte[] var30 = var20.getOctets();
                  if (var2.isFixedLength()) {
                     this.out.write(var30);
                  } else {
                     this.encodeLength(var30.length);
                     this.out.write(var30);
                  }

                  this.debugPrint(var2.appendLabel(var2.rangeExpression()));
                  this.out.flush();
                  break;
               case IA5String:
                  ASN1IA5String var19 = ASN1IA5String.getInstance(var12);
                  byte[] var29 = var19.getOctets();
                  if (var2.isFixedLength() && var2.getUpperBound().intValue() != var29.length) {
                     throw new IOException("IA5String string length does not equal declared fixed length " + var29.length + " " + var2.getUpperBound());
                  }

                  if (var2.isFixedLength()) {
                     this.out.write(var29);
                  } else {
                     this.encodeLength(var29.length);
                     this.out.write(var29);
                  }

                  this.debugPrint(var2.appendLabel(""));
                  this.out.flush();
                  break;
               case UTF8_STRING:
                  ASN1UTF8String var18 = ASN1UTF8String.getInstance(var12);
                  byte[] var28 = Strings.toUTF8ByteArray(var18.getString());
                  this.encodeLength(var28.length);
                  this.out.write(var28);
                  this.debugPrint(var2.appendLabel(""));
                  this.out.flush();
                  break;
               case BIT_STRING:
                  ASN1BitString var17 = ASN1BitString.getInstance(var12);
                  byte[] var27 = var17.getBytes();
                  if (var2.isFixedLength()) {
                     this.out.write(var27);
                     this.debugPrint(var2.appendLabel(var2.rangeExpression()));
                  } else {
                     int var7 = var17.getPadBits();
                     this.encodeLength(var27.length + 1);
                     this.out.write(var7);
                     this.out.write(var27);
                     this.debugPrint(var2.appendLabel(var2.rangeExpression()));
                  }

                  this.out.flush();
               case NULL:
               case ENUM_ITEM:
               default:
                  break;
               case EXTENSION:
                  ASN1OctetString var16 = ASN1OctetString.getInstance(var12);
                  byte[] var6 = var16.getOctets();
                  if (var2.isFixedLength()) {
                     this.out.write(var6);
                  } else {
                     this.encodeLength(var6.length);
                     this.out.write(var6);
                  }

                  this.debugPrint(var2.appendLabel(var2.rangeExpression()));
                  this.out.flush();
                  break;
               case BOOLEAN:
                  this.debugPrint(var2.getLabel());
                  ASN1Boolean var5 = ASN1Boolean.getInstance(var12);
                  if (var5.isTrue()) {
                     this.out.write(255);
                  } else {
                     this.out.write(0);
                  }

                  this.out.flush();
            }
         }
      }
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

   private void encodeLength(long var1) throws IOException {
      if (var1 <= 127L) {
         this.out.write((int)var1);
      } else {
         byte[] var3 = BigIntegers.asUnsignedByteArray(BigInteger.valueOf(var1));
         this.out.write(var3.length | 128);
         this.out.write(var3);
      }
   }

   private void encodeQuantity(long var1) throws IOException {
      byte[] var3 = BigIntegers.asUnsignedByteArray(BigInteger.valueOf(var1));
      this.out.write(var3.length);
      this.out.write(var3);
   }

   @Override
   public void write(int var1) throws IOException {
      this.out.write(var1);
   }

   public void writePlainType(ASN1Encodable var1, Element var2) throws IOException {
      ByteArrayOutputStream var3 = new ByteArrayOutputStream();
      OEROutputStream var4 = new OEROutputStream(var3);
      var4.write(var1, var2);
      var4.flush();
      var4.close();
      this.encodeLength(var3.size());
      this.write(var3.toByteArray());
   }
}
