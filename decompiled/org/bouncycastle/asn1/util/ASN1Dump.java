package org.bouncycastle.asn1.util;

import org.bouncycastle.asn1.ASN1BMPString;
import org.bouncycastle.asn1.ASN1BitString;
import org.bouncycastle.asn1.ASN1Boolean;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1Enumerated;
import org.bouncycastle.asn1.ASN1External;
import org.bouncycastle.asn1.ASN1GeneralizedTime;
import org.bouncycastle.asn1.ASN1GraphicString;
import org.bouncycastle.asn1.ASN1IA5String;
import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.ASN1Null;
import org.bouncycastle.asn1.ASN1NumericString;
import org.bouncycastle.asn1.ASN1ObjectDescriptor;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1PrintableString;
import org.bouncycastle.asn1.ASN1RelativeOID;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.ASN1Set;
import org.bouncycastle.asn1.ASN1T61String;
import org.bouncycastle.asn1.ASN1TaggedObject;
import org.bouncycastle.asn1.ASN1UTCTime;
import org.bouncycastle.asn1.ASN1UTF8String;
import org.bouncycastle.asn1.ASN1Util;
import org.bouncycastle.asn1.ASN1VideotexString;
import org.bouncycastle.asn1.ASN1VisibleString;
import org.bouncycastle.asn1.BEROctetString;
import org.bouncycastle.asn1.BERSequence;
import org.bouncycastle.asn1.BERSet;
import org.bouncycastle.asn1.BERTaggedObject;
import org.bouncycastle.asn1.DERBitString;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.DERSet;
import org.bouncycastle.asn1.DERTaggedObject;
import org.bouncycastle.asn1.DLBitString;
import org.bouncycastle.util.Strings;
import org.bouncycastle.util.encoders.Hex;

public class ASN1Dump {
   private static final String TAB = "    ";
   private static final int SAMPLE_SIZE = 32;

   static void _dumpAsString(String var0, boolean var1, ASN1Primitive var2, StringBuilder var3) {
      String var4 = Strings.lineSeparator();
      var3.append(var0);
      if (var2 instanceof ASN1Null) {
         var3.append("NULL");
         var3.append(var4);
      } else if (var2 instanceof ASN1Sequence) {
         if (var2 instanceof BERSequence) {
            var3.append("BER Sequence");
         } else if (var2 instanceof DERSequence) {
            var3.append("DER Sequence");
         } else {
            var3.append("Sequence");
         }

         var3.append(var4);
         ASN1Sequence var5 = (ASN1Sequence)var2;
         String var6 = var0 + "    ";
         int var7 = 0;

         for (int var8 = var5.size(); var7 < var8; var7++) {
            _dumpAsString(var6, var1, var5.getObjectAt(var7).toASN1Primitive(), var3);
         }
      } else if (var2 instanceof ASN1Set) {
         if (var2 instanceof BERSet) {
            var3.append("BER Set");
         } else if (var2 instanceof DERSet) {
            var3.append("DER Set");
         } else {
            var3.append("Set");
         }

         var3.append(var4);
         ASN1Set var9 = (ASN1Set)var2;
         String var16 = var0 + "    ";
         int var19 = 0;

         for (int var20 = var9.size(); var19 < var20; var19++) {
            _dumpAsString(var16, var1, var9.getObjectAt(var19).toASN1Primitive(), var3);
         }
      } else if (var2 instanceof ASN1TaggedObject) {
         if (var2 instanceof BERTaggedObject) {
            var3.append("BER Tagged ");
         } else if (var2 instanceof DERTaggedObject) {
            var3.append("DER Tagged ");
         } else {
            var3.append("Tagged ");
         }

         ASN1TaggedObject var10 = (ASN1TaggedObject)var2;
         var3.append(ASN1Util.getTagText(var10));
         if (!var10.isExplicit()) {
            var3.append(" IMPLICIT");
         }

         var3.append(var4);
         String var17 = var0 + "    ";
         _dumpAsString(var17, var1, var10.getBaseObject().toASN1Primitive(), var3);
      } else if (var2 instanceof ASN1ObjectIdentifier) {
         var3.append("ObjectIdentifier(" + ((ASN1ObjectIdentifier)var2).getId() + ")" + var4);
      } else if (var2 instanceof ASN1RelativeOID) {
         var3.append("RelativeOID(" + ((ASN1RelativeOID)var2).getId() + ")" + var4);
      } else if (var2 instanceof ASN1Boolean) {
         var3.append("Boolean(" + ((ASN1Boolean)var2).isTrue() + ")" + var4);
      } else if (var2 instanceof ASN1Integer) {
         var3.append("Integer(" + ((ASN1Integer)var2).getValue() + ")" + var4);
      } else if (var2 instanceof ASN1OctetString) {
         ASN1OctetString var11 = (ASN1OctetString)var2;
         if (var2 instanceof BEROctetString) {
            var3.append("BER Constructed Octet String[");
         } else {
            var3.append("DER Octet String[");
         }

         var3.append(var11.getOctetsLength() + "]" + var4);
         if (var1) {
            dumpBinaryDataAsString(var3, var0, var11.getOctets());
         }
      } else if (var2 instanceof ASN1BitString) {
         ASN1BitString var12 = (ASN1BitString)var2;
         if (var12 instanceof DERBitString) {
            var3.append("DER Bit String[");
         } else if (var12 instanceof DLBitString) {
            var3.append("DL Bit String[");
         } else {
            var3.append("BER Bit String[");
         }

         var3.append(var12.getBytesLength() + ", " + var12.getPadBits() + "]" + var4);
         if (var1) {
            dumpBinaryDataAsString(var3, var0, var12.getBytes());
         }
      } else if (var2 instanceof ASN1IA5String) {
         var3.append("IA5String(" + ((ASN1IA5String)var2).getString() + ") " + var4);
      } else if (var2 instanceof ASN1UTF8String) {
         var3.append("UTF8String(" + ((ASN1UTF8String)var2).getString() + ") " + var4);
      } else if (var2 instanceof ASN1NumericString) {
         var3.append("NumericString(" + ((ASN1NumericString)var2).getString() + ") " + var4);
      } else if (var2 instanceof ASN1PrintableString) {
         var3.append("PrintableString(" + ((ASN1PrintableString)var2).getString() + ") " + var4);
      } else if (var2 instanceof ASN1VisibleString) {
         var3.append("VisibleString(" + ((ASN1VisibleString)var2).getString() + ") " + var4);
      } else if (var2 instanceof ASN1BMPString) {
         var3.append("BMPString(" + ((ASN1BMPString)var2).getString() + ") " + var4);
      } else if (var2 instanceof ASN1T61String) {
         var3.append("T61String(" + ((ASN1T61String)var2).getString() + ") " + var4);
      } else if (var2 instanceof ASN1GraphicString) {
         var3.append("GraphicString(" + ((ASN1GraphicString)var2).getString() + ") " + var4);
      } else if (var2 instanceof ASN1VideotexString) {
         var3.append("VideotexString(" + ((ASN1VideotexString)var2).getString() + ") " + var4);
      } else if (var2 instanceof ASN1UTCTime) {
         var3.append("UTCTime(" + ((ASN1UTCTime)var2).getTime() + ") " + var4);
      } else if (var2 instanceof ASN1GeneralizedTime) {
         var3.append("GeneralizedTime(" + ((ASN1GeneralizedTime)var2).getTime() + ") " + var4);
      } else if (var2 instanceof ASN1Enumerated) {
         ASN1Enumerated var13 = (ASN1Enumerated)var2;
         var3.append("DER Enumerated(" + var13.getValue() + ")" + var4);
      } else if (var2 instanceof ASN1ObjectDescriptor) {
         ASN1ObjectDescriptor var14 = (ASN1ObjectDescriptor)var2;
         var3.append("ObjectDescriptor(" + var14.getBaseGraphicString().getString() + ") " + var4);
      } else if (var2 instanceof ASN1External) {
         ASN1External var15 = (ASN1External)var2;
         var3.append("External " + var4);
         String var18 = var0 + "    ";
         if (var15.getDirectReference() != null) {
            var3.append(var18 + "Direct Reference: " + var15.getDirectReference().getId() + var4);
         }

         if (var15.getIndirectReference() != null) {
            var3.append(var18 + "Indirect Reference: " + var15.getIndirectReference().toString() + var4);
         }

         if (var15.getDataValueDescriptor() != null) {
            _dumpAsString(var18, var1, var15.getDataValueDescriptor(), var3);
         }

         var3.append(var18 + "Encoding: " + var15.getEncoding() + var4);
         _dumpAsString(var18, var1, var15.getExternalContent(), var3);
      } else {
         var3.append(var2.toString() + var4);
      }
   }

   public static String dumpAsString(Object var0) {
      return dumpAsString(var0, false);
   }

   public static String dumpAsString(Object var0, boolean var1) {
      ASN1Primitive var2;
      if (var0 instanceof ASN1Primitive) {
         var2 = (ASN1Primitive)var0;
      } else {
         if (!(var0 instanceof ASN1Encodable)) {
            return "unknown object type " + var0.toString();
         }

         var2 = ((ASN1Encodable)var0).toASN1Primitive();
      }

      StringBuilder var3 = new StringBuilder();
      _dumpAsString("", var1, var2, var3);
      return var3.toString();
   }

   private static void dumpBinaryDataAsString(StringBuilder var0, String var1, byte[] var2) {
      if (var2.length >= 1) {
         String var3 = Strings.lineSeparator();
         var1 = var1 + "    ";

         for (byte var4 = 0; var4 < var2.length; var4 += 32) {
            int var5 = var2.length - var4;
            int var6 = Math.min(var5, 32);
            var0.append(var1);
            var0.append(Hex.toHexString(var2, var4, var6));

            for (int var7 = var6; var7 < 32; var7++) {
               var0.append("  ");
            }

            var0.append("    ");
            appendAscString(var0, var2, var4, var6);
            var0.append(var3);
         }
      }
   }

   private static void appendAscString(StringBuilder var0, byte[] var1, int var2, int var3) {
      for (int var4 = var2; var4 != var2 + var3; var4++) {
         if (var1[var4] >= 32 && var1[var4] <= 126) {
            var0.append((char)var1[var4]);
         }
      }
   }
}
