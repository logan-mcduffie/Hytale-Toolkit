package org.bouncycastle.asn1.x500.style;

import java.io.IOException;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1String;
import org.bouncycastle.asn1.ASN1UniversalString;
import org.bouncycastle.asn1.x500.AttributeTypeAndValue;
import org.bouncycastle.asn1.x500.RDN;
import org.bouncycastle.asn1.x500.X500NameBuilder;
import org.bouncycastle.asn1.x500.X500NameStyle;
import org.bouncycastle.util.Strings;
import org.bouncycastle.util.encoders.Hex;

public class IETFUtils {
   private static String unescape(String var0) {
      if (var0.length() == 0) {
         return var0;
      } else if (var0.indexOf(92) < 0 && var0.indexOf(34) < 0) {
         return var0.trim();
      } else {
         boolean var1 = false;
         boolean var2 = false;
         StringBuilder var3 = new StringBuilder(var0.length());
         byte var4 = 0;
         if (var0.charAt(0) == '\\' && var0.charAt(1) == '#') {
            var4 = 2;
            var3.append("\\#");
         }

         boolean var5 = false;
         int var6 = 0;
         char var7 = 0;

         for (int var8 = var4; var8 != var0.length(); var8++) {
            char var9 = var0.charAt(var8);
            if (var9 != ' ') {
               var5 = true;
            }

            if (var9 == '"') {
               if (!var1) {
                  var2 = !var2;
               } else {
                  var3.append(var9);
                  var1 = false;
               }
            } else if (var9 == '\\' && !var1 && !var2) {
               var1 = true;
               var6 = var3.length();
            } else if (var9 != ' ' || var1 || var5) {
               if (!var1 || !isHexDigit(var9)) {
                  var3.append(var9);
                  var1 = false;
               } else if (var7 != 0) {
                  var3.append((char)(convertHex(var7) * 16 + convertHex(var9)));
                  var1 = false;
                  var7 = 0;
               } else {
                  var7 = var9;
               }
            }
         }

         if (var3.length() > 0) {
            while (var3.charAt(var3.length() - 1) == ' ' && var6 != var3.length() - 1) {
               var3.setLength(var3.length() - 1);
            }
         }

         return var3.toString();
      }
   }

   private static boolean isHexDigit(char var0) {
      return '0' <= var0 && var0 <= '9' || 'a' <= var0 && var0 <= 'f' || 'A' <= var0 && var0 <= 'F';
   }

   private static int convertHex(char var0) {
      if ('0' <= var0 && var0 <= '9') {
         return var0 - 48;
      } else {
         return 97 <= var0 && var0 <= 102 ? var0 - 97 + 10 : var0 - 65 + 10;
      }
   }

   public static RDN[] rDNsFromString(String var0, X500NameStyle var1) {
      X500NameTokenizer var2 = new X500NameTokenizer(var0);
      X500NameBuilder var3 = new X500NameBuilder(var1);
      addRDNs(var1, var3, var2);
      return var3.build().getRDNs();
   }

   private static void addRDNs(X500NameStyle var0, X500NameBuilder var1, X500NameTokenizer var2) {
      String var3;
      while ((var3 = var2.nextToken()) != null) {
         if (var3.indexOf(43) >= 0) {
            addMultiValuedRDN(var0, var1, new X500NameTokenizer(var3, '+'));
         } else {
            addRDN(var0, var1, var3);
         }
      }
   }

   private static void addMultiValuedRDN(X500NameStyle var0, X500NameBuilder var1, X500NameTokenizer var2) {
      String var3 = var2.nextToken();
      if (var3 == null) {
         throw new IllegalArgumentException("badly formatted directory string");
      } else if (!var2.hasMoreTokens()) {
         addRDN(var0, var1, var3);
      } else {
         Vector var4 = new Vector();
         Vector var5 = new Vector();

         do {
            collectAttributeTypeAndValue(var0, var4, var5, var3);
            var3 = var2.nextToken();
         } while (var3 != null);

         var1.addMultiValuedRDN(toOIDArray(var4), toValueArray(var5));
      }
   }

   private static void addRDN(X500NameStyle var0, X500NameBuilder var1, String var2) {
      X500NameTokenizer var3 = new X500NameTokenizer(var2, '=');
      String var4 = nextToken(var3, true);
      String var5 = nextToken(var3, false);
      ASN1ObjectIdentifier var6 = var0.attrNameToOID(var4.trim());
      String var7 = unescape(var5);
      var1.addRDN(var6, var7);
   }

   private static void collectAttributeTypeAndValue(X500NameStyle var0, Vector var1, Vector var2, String var3) {
      X500NameTokenizer var4 = new X500NameTokenizer(var3, '=');
      String var5 = nextToken(var4, true);
      String var6 = nextToken(var4, false);
      ASN1ObjectIdentifier var7 = var0.attrNameToOID(var5.trim());
      String var8 = unescape(var6);
      var1.addElement(var7);
      var2.addElement(var8);
   }

   private static String nextToken(X500NameTokenizer var0, boolean var1) {
      String var2 = var0.nextToken();
      if (var2 != null && var0.hasMoreTokens() == var1) {
         return var2;
      } else {
         throw new IllegalArgumentException("badly formatted directory string");
      }
   }

   private static String[] toValueArray(Vector var0) {
      String[] var1 = new String[var0.size()];

      for (int var2 = 0; var2 != var1.length; var2++) {
         var1[var2] = (String)var0.elementAt(var2);
      }

      return var1;
   }

   private static ASN1ObjectIdentifier[] toOIDArray(Vector var0) {
      ASN1ObjectIdentifier[] var1 = new ASN1ObjectIdentifier[var0.size()];

      for (int var2 = 0; var2 != var1.length; var2++) {
         var1[var2] = (ASN1ObjectIdentifier)var0.elementAt(var2);
      }

      return var1;
   }

   public static String[] findAttrNamesForOID(ASN1ObjectIdentifier var0, Hashtable var1) {
      int var2 = 0;
      Enumeration var3 = var1.elements();

      while (var3.hasMoreElements()) {
         if (var0.equals(var3.nextElement())) {
            var2++;
         }
      }

      String[] var7 = new String[var2];
      var2 = 0;
      Enumeration var4 = var1.keys();

      while (var4.hasMoreElements()) {
         String var5 = (String)var4.nextElement();
         if (var0.equals(var1.get(var5))) {
            var7[var2++] = var5;
         }
      }

      return var7;
   }

   public static ASN1ObjectIdentifier decodeAttrName(String var0, Hashtable var1) {
      if (var0.regionMatches(true, 0, "OID.", 0, 4)) {
         return new ASN1ObjectIdentifier(var0.substring(4));
      } else {
         ASN1ObjectIdentifier var2 = ASN1ObjectIdentifier.tryFromID(var0);
         if (var2 != null) {
            return var2;
         } else {
            var2 = (ASN1ObjectIdentifier)var1.get(Strings.toLowerCase(var0));
            if (var2 != null) {
               return var2;
            } else {
               throw new IllegalArgumentException("Unknown object id - " + var0 + " - passed to distinguished name");
            }
         }
      }
   }

   public static ASN1Encodable valueFromHexString(String var0, int var1) throws IOException {
      byte[] var2 = new byte[(var0.length() - var1) / 2];

      for (int var3 = 0; var3 != var2.length; var3++) {
         char var4 = var0.charAt(var3 * 2 + var1);
         char var5 = var0.charAt(var3 * 2 + var1 + 1);
         var2[var3] = (byte)(convertHex(var4) << 4 | convertHex(var5));
      }

      return ASN1Primitive.fromByteArray(var2);
   }

   public static void appendRDN(StringBuilder var0, RDN var1, Hashtable var2) {
      if (var1.isMultiValued()) {
         AttributeTypeAndValue[] var3 = var1.getTypesAndValues();
         boolean var4 = true;

         for (int var5 = 0; var5 != var3.length; var5++) {
            if (var4) {
               var4 = false;
            } else {
               var0.append('+');
            }

            appendTypeAndValue(var0, var3[var5], var2);
         }
      } else if (var1.getFirst() != null) {
         appendTypeAndValue(var0, var1.getFirst(), var2);
      }
   }

   public static void appendRDN(StringBuffer var0, RDN var1, Hashtable var2) {
      if (var1.isMultiValued()) {
         AttributeTypeAndValue[] var3 = var1.getTypesAndValues();
         boolean var4 = true;

         for (int var5 = 0; var5 != var3.length; var5++) {
            if (var4) {
               var4 = false;
            } else {
               var0.append('+');
            }

            appendTypeAndValue(var0, var3[var5], var2);
         }
      } else if (var1.getFirst() != null) {
         appendTypeAndValue(var0, var1.getFirst(), var2);
      }
   }

   public static void appendTypeAndValue(StringBuilder var0, AttributeTypeAndValue var1, Hashtable var2) {
      String var3 = (String)var2.get(var1.getType());
      if (var3 != null) {
         var0.append(var3);
      } else {
         var0.append(var1.getType().getId());
      }

      var0.append('=');
      var0.append(valueToString(var1.getValue()));
   }

   public static void appendTypeAndValue(StringBuffer var0, AttributeTypeAndValue var1, Hashtable var2) {
      String var3 = (String)var2.get(var1.getType());
      if (var3 != null) {
         var0.append(var3);
      } else {
         var0.append(var1.getType().getId());
      }

      var0.append('=');
      var0.append(valueToString(var1.getValue()));
   }

   public static String valueToString(ASN1Encodable var0) {
      StringBuilder var1 = new StringBuilder();
      if (var0 instanceof ASN1String && !(var0 instanceof ASN1UniversalString)) {
         String var2 = ((ASN1String)var0).getString();
         if (var2.length() > 0 && var2.charAt(0) == '#') {
            var1.append('\\');
         }

         var1.append(var2);
      } else {
         try {
            var1.append('#');
            var1.append(Hex.toHexString(var0.toASN1Primitive().getEncoded("DER")));
         } catch (IOException var6) {
            throw new IllegalArgumentException("Other value has no encoded form");
         }
      }

      int var7 = var1.length();
      int var3 = 0;
      if (var1.length() >= 2 && var1.charAt(0) == '\\' && var1.charAt(1) == '#') {
         var3 += 2;
      }

      while (var3 != var7) {
         switch (var1.charAt(var3)) {
            case '"':
            case '+':
            case ',':
            case ';':
            case '<':
            case '=':
            case '>':
            case '\\':
               var1.insert(var3, "\\");
               var3 += 2;
               var7++;
               break;
            default:
               var3++;
         }
      }

      byte var4 = 0;
      if (var1.length() > 0) {
         while (var1.length() > var4 && var1.charAt(var4) == ' ') {
            var1.insert(var4, "\\");
            var4 += 2;
         }
      }

      for (int var5 = var1.length() - 1; var5 >= var4 && var1.charAt(var5) == ' '; var5--) {
         var1.insert(var5, '\\');
      }

      return var1.toString();
   }

   public static String canonicalize(String var0) {
      if (var0.length() > 0 && var0.charAt(0) == '#') {
         ASN1Primitive var1 = decodeObject(var0);
         if (var1 instanceof ASN1String) {
            var0 = ((ASN1String)var1).getString();
         }
      }

      var0 = Strings.toLowerCase(var0);
      int var7 = var0.length();
      if (var7 < 2) {
         return var0;
      } else {
         byte var2 = 0;
         int var3 = var7 - 1;

         while (var2 < var3 && var0.charAt(var2) == '\\' && var0.charAt(var2 + 1) == ' ') {
            var2 += 2;
         }

         int var4 = var3;
         int var5 = var2 + 1;

         while (var4 > var5 && var0.charAt(var4 - 1) == '\\' && var0.charAt(var4) == ' ') {
            var4 -= 2;
         }

         if (var2 > 0 || var4 < var3) {
            var0 = var0.substring(var2, var4 + 1);
         }

         return stripInternalSpaces(var0);
      }
   }

   public static String canonicalString(ASN1Encodable var0) {
      return canonicalize(valueToString(var0));
   }

   private static ASN1Primitive decodeObject(String var0) {
      try {
         return ASN1Primitive.fromByteArray(Hex.decodeStrict(var0, 1, var0.length() - 1));
      } catch (IOException var2) {
         throw new IllegalStateException("unknown encoding in name: " + var2);
      }
   }

   public static String stripInternalSpaces(String var0) {
      if (var0.indexOf("  ") < 0) {
         return var0;
      } else {
         StringBuilder var1 = new StringBuilder();
         char var2 = var0.charAt(0);
         var1.append(var2);

         for (int var3 = 1; var3 < var0.length(); var3++) {
            char var4 = var0.charAt(var3);
            if (var2 != ' ' || var4 != ' ') {
               var1.append(var4);
               var2 = var4;
            }
         }

         return var1.toString();
      }
   }

   public static boolean rDNAreEqual(RDN var0, RDN var1) {
      if (var0.size() != var1.size()) {
         return false;
      } else {
         AttributeTypeAndValue[] var2 = var0.getTypesAndValues();
         AttributeTypeAndValue[] var3 = var1.getTypesAndValues();
         if (var2.length != var3.length) {
            return false;
         } else {
            for (int var4 = 0; var4 != var2.length; var4++) {
               if (!atvAreEqual(var2[var4], var3[var4])) {
                  return false;
               }
            }

            return true;
         }
      }
   }

   private static boolean atvAreEqual(AttributeTypeAndValue var0, AttributeTypeAndValue var1) {
      if (var0 == var1) {
         return true;
      } else if (null != var0 && null != var1) {
         ASN1ObjectIdentifier var2 = var0.getType();
         ASN1ObjectIdentifier var3 = var1.getType();
         if (!var2.equals(var3)) {
            return false;
         } else {
            String var4 = canonicalString(var0.getValue());
            String var5 = canonicalString(var1.getValue());
            return var4.equals(var5);
         }
      } else {
         return false;
      }
   }
}
