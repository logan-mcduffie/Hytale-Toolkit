package org.bouncycastle.asn1;

import java.io.IOException;

public abstract class ASN1Util {
   static ASN1TaggedObject checkContextTag(ASN1TaggedObject var0, int var1) {
      return checkTag(var0, 128, var1);
   }

   static ASN1TaggedObjectParser checkContextTag(ASN1TaggedObjectParser var0, int var1) {
      return checkTag(var0, 128, var1);
   }

   static ASN1TaggedObject checkContextTagClass(ASN1TaggedObject var0) {
      return checkTagClass(var0, 128);
   }

   static ASN1TaggedObjectParser checkContextTagClass(ASN1TaggedObjectParser var0) {
      return checkTagClass(var0, 128);
   }

   static ASN1TaggedObject checkTag(ASN1TaggedObject var0, int var1, int var2) {
      if (!var0.hasTag(var1, var2)) {
         String var3 = getTagText(var1, var2);
         String var4 = getTagText(var0);
         throw new IllegalStateException("Expected " + var3 + " tag but found " + var4);
      } else {
         return var0;
      }
   }

   static ASN1TaggedObjectParser checkTag(ASN1TaggedObjectParser var0, int var1, int var2) {
      if (!var0.hasTag(var1, var2)) {
         String var3 = getTagText(var1, var2);
         String var4 = getTagText(var0);
         throw new IllegalStateException("Expected " + var3 + " tag but found " + var4);
      } else {
         return var0;
      }
   }

   static ASN1TaggedObject checkTagClass(ASN1TaggedObject var0, int var1) {
      if (!var0.hasTagClass(var1)) {
         String var2 = getTagClassText(var1);
         String var3 = getTagClassText(var0);
         throw new IllegalStateException("Expected " + var2 + " tag but found " + var3);
      } else {
         return var0;
      }
   }

   static ASN1TaggedObjectParser checkTagClass(ASN1TaggedObjectParser var0, int var1) {
      if (!var0.hasTagClass(var1)) {
         String var2 = getTagClassText(var1);
         String var3 = getTagClassText(var0);
         throw new IllegalStateException("Expected " + var2 + " tag but found " + var3);
      } else {
         return var0;
      }
   }

   public static Object getInstanceChoiceBaseObject(ASN1TaggedObject var0, boolean var1, String var2) {
      if (!var1) {
         String var3 = "Implicit tagging cannot be used with untagged choice type " + var2 + " (X.680 30.6, 30.8).";
         throw new IllegalArgumentException(var3);
      } else if (var0 == null) {
         throw new NullPointerException("'taggedObject' cannot be null");
      } else {
         return getExplicitContextBaseObject(var0);
      }
   }

   public static Object getTaggedChoiceBaseObject(ASN1TaggedObject var0, boolean var1, String var2) {
      if (!var1) {
         String var3 = "Implicit tagging cannot be used with untagged choice type " + var2 + " (X.680 30.6, 30.8).";
         throw new IllegalArgumentException(var3);
      } else if (var0 == null) {
         throw new NullPointerException("'taggedObject' cannot be null");
      } else {
         return var0.getExplicitBaseObject();
      }
   }

   static String getTagClassText(ASN1Tag var0) {
      return getTagClassText(var0.getTagClass());
   }

   public static String getTagClassText(ASN1TaggedObject var0) {
      return getTagClassText(var0.getTagClass());
   }

   public static String getTagClassText(ASN1TaggedObjectParser var0) {
      return getTagClassText(var0.getTagClass());
   }

   public static String getTagClassText(int var0) {
      switch (var0) {
         case 64:
            return "APPLICATION";
         case 128:
            return "CONTEXT";
         case 192:
            return "PRIVATE";
         default:
            return "UNIVERSAL";
      }
   }

   static String getTagText(ASN1Tag var0) {
      return getTagText(var0.getTagClass(), var0.getTagNumber());
   }

   public static String getTagText(ASN1TaggedObject var0) {
      return getTagText(var0.getTagClass(), var0.getTagNo());
   }

   public static String getTagText(ASN1TaggedObjectParser var0) {
      return getTagText(var0.getTagClass(), var0.getTagNo());
   }

   public static String getTagText(int var0, int var1) {
      switch (var0) {
         case 64:
            return "[APPLICATION " + var1 + "]";
         case 128:
            return "[CONTEXT " + var1 + "]";
         case 192:
            return "[PRIVATE " + var1 + "]";
         default:
            return "[UNIVERSAL " + var1 + "]";
      }
   }

   public static ASN1Object getExplicitBaseObject(ASN1TaggedObject var0, int var1) {
      return checkTagClass(var0, var1).getExplicitBaseObject();
   }

   public static ASN1Object getExplicitBaseObject(ASN1TaggedObject var0, int var1, int var2) {
      return checkTag(var0, var1, var2).getExplicitBaseObject();
   }

   public static ASN1Object getExplicitContextBaseObject(ASN1TaggedObject var0) {
      return getExplicitBaseObject(var0, 128);
   }

   public static ASN1Object getExplicitContextBaseObject(ASN1TaggedObject var0, int var1) {
      return getExplicitBaseObject(var0, 128, var1);
   }

   public static ASN1Object tryGetExplicitBaseObject(ASN1TaggedObject var0, int var1) {
      return !var0.hasTagClass(var1) ? null : var0.getExplicitBaseObject();
   }

   public static ASN1Object tryGetExplicitBaseObject(ASN1TaggedObject var0, int var1, int var2) {
      return !var0.hasTag(var1, var2) ? null : var0.getExplicitBaseObject();
   }

   public static ASN1Object tryGetExplicitContextBaseObject(ASN1TaggedObject var0) {
      return tryGetExplicitBaseObject(var0, 128);
   }

   public static ASN1Object tryGetExplicitContextBaseObject(ASN1TaggedObject var0, int var1) {
      return tryGetExplicitBaseObject(var0, 128, var1);
   }

   public static ASN1TaggedObject getExplicitBaseTagged(ASN1TaggedObject var0, int var1) {
      return checkTagClass(var0, var1).getExplicitBaseTagged();
   }

   public static ASN1TaggedObject getExplicitBaseTagged(ASN1TaggedObject var0, int var1, int var2) {
      return checkTag(var0, var1, var2).getExplicitBaseTagged();
   }

   public static ASN1TaggedObject getExplicitContextBaseTagged(ASN1TaggedObject var0) {
      return getExplicitBaseTagged(var0, 128);
   }

   public static ASN1TaggedObject getExplicitContextBaseTagged(ASN1TaggedObject var0, int var1) {
      return getExplicitBaseTagged(var0, 128, var1);
   }

   public static ASN1TaggedObject tryGetExplicitBaseTagged(ASN1TaggedObject var0, int var1) {
      return !var0.hasTagClass(var1) ? null : var0.getExplicitBaseTagged();
   }

   public static ASN1TaggedObject tryGetExplicitBaseTagged(ASN1TaggedObject var0, int var1, int var2) {
      return !var0.hasTag(var1, var2) ? null : var0.getExplicitBaseTagged();
   }

   public static ASN1TaggedObject tryGetExplicitContextBaseTagged(ASN1TaggedObject var0) {
      return tryGetExplicitBaseTagged(var0, 128);
   }

   public static ASN1TaggedObject tryGetExplicitContextBaseTagged(ASN1TaggedObject var0, int var1) {
      return tryGetExplicitBaseTagged(var0, 128, var1);
   }

   public static ASN1TaggedObject getImplicitBaseTagged(ASN1TaggedObject var0, int var1, int var2, int var3, int var4) {
      return checkTag(var0, var1, var2).getImplicitBaseTagged(var3, var4);
   }

   public static ASN1TaggedObject getImplicitContextBaseTagged(ASN1TaggedObject var0, int var1, int var2, int var3) {
      return getImplicitBaseTagged(var0, 128, var1, var2, var3);
   }

   public static ASN1TaggedObject tryGetImplicitBaseTagged(ASN1TaggedObject var0, int var1, int var2, int var3, int var4) {
      return !var0.hasTag(var1, var2) ? null : var0.getImplicitBaseTagged(var3, var4);
   }

   public static ASN1TaggedObject tryGetImplicitContextBaseTagged(ASN1TaggedObject var0, int var1, int var2, int var3) {
      return tryGetImplicitBaseTagged(var0, 128, var1, var2, var3);
   }

   public static ASN1Primitive getBaseUniversal(ASN1TaggedObject var0, int var1, int var2, boolean var3, int var4) {
      return checkTag(var0, var1, var2).getBaseUniversal(var3, var4);
   }

   public static ASN1Primitive getContextBaseUniversal(ASN1TaggedObject var0, int var1, boolean var2, int var3) {
      return getBaseUniversal(var0, 128, var1, var2, var3);
   }

   public static ASN1Primitive tryGetBaseUniversal(ASN1TaggedObject var0, int var1, int var2, boolean var3, int var4) {
      return !var0.hasTag(var1, var2) ? null : var0.getBaseUniversal(var3, var4);
   }

   public static ASN1Primitive tryGetContextBaseUniversal(ASN1TaggedObject var0, int var1, boolean var2, int var3) {
      return tryGetBaseUniversal(var0, 128, var1, var2, var3);
   }

   public static ASN1TaggedObjectParser parseExplicitBaseTagged(ASN1TaggedObjectParser var0, int var1) throws IOException {
      return checkTagClass(var0, var1).parseExplicitBaseTagged();
   }

   public static ASN1TaggedObjectParser parseExplicitBaseTagged(ASN1TaggedObjectParser var0, int var1, int var2) throws IOException {
      return checkTag(var0, var1, var2).parseExplicitBaseTagged();
   }

   public static ASN1TaggedObjectParser parseExplicitContextBaseTagged(ASN1TaggedObjectParser var0) throws IOException {
      return parseExplicitBaseTagged(var0, 128);
   }

   public static ASN1TaggedObjectParser parseExplicitContextBaseTagged(ASN1TaggedObjectParser var0, int var1) throws IOException {
      return parseExplicitBaseTagged(var0, 128, var1);
   }

   public static ASN1TaggedObjectParser tryParseExplicitBaseTagged(ASN1TaggedObjectParser var0, int var1) throws IOException {
      return !var0.hasTagClass(var1) ? null : var0.parseExplicitBaseTagged();
   }

   public static ASN1TaggedObjectParser tryParseExplicitBaseTagged(ASN1TaggedObjectParser var0, int var1, int var2) throws IOException {
      return !var0.hasTag(var1, var2) ? null : var0.parseExplicitBaseTagged();
   }

   public static ASN1TaggedObjectParser tryParseExplicitContextBaseTagged(ASN1TaggedObjectParser var0) throws IOException {
      return tryParseExplicitBaseTagged(var0, 128);
   }

   public static ASN1TaggedObjectParser tryParseExplicitContextBaseTagged(ASN1TaggedObjectParser var0, int var1) throws IOException {
      return tryParseExplicitBaseTagged(var0, 128, var1);
   }

   public static ASN1TaggedObjectParser parseImplicitBaseTagged(ASN1TaggedObjectParser var0, int var1, int var2, int var3, int var4) throws IOException {
      return checkTag(var0, var1, var2).parseImplicitBaseTagged(var3, var4);
   }

   public static ASN1TaggedObjectParser parseImplicitContextBaseTagged(ASN1TaggedObjectParser var0, int var1, int var2, int var3) throws IOException {
      return parseImplicitBaseTagged(var0, 128, var1, var2, var3);
   }

   public static ASN1TaggedObjectParser tryParseImplicitBaseTagged(ASN1TaggedObjectParser var0, int var1, int var2, int var3, int var4) throws IOException {
      return !var0.hasTag(var1, var2) ? null : var0.parseImplicitBaseTagged(var3, var4);
   }

   public static ASN1TaggedObjectParser tryParseImplicitContextBaseTagged(ASN1TaggedObjectParser var0, int var1, int var2, int var3) throws IOException {
      return tryParseImplicitBaseTagged(var0, 128, var1, var2, var3);
   }

   public static ASN1Encodable parseBaseUniversal(ASN1TaggedObjectParser var0, int var1, int var2, boolean var3, int var4) throws IOException {
      return checkTag(var0, var1, var2).parseBaseUniversal(var3, var4);
   }

   public static ASN1Encodable parseContextBaseUniversal(ASN1TaggedObjectParser var0, int var1, boolean var2, int var3) throws IOException {
      return parseBaseUniversal(var0, 128, var1, var2, var3);
   }

   public static ASN1Encodable tryParseBaseUniversal(ASN1TaggedObjectParser var0, int var1, int var2, boolean var3, int var4) throws IOException {
      return !var0.hasTag(var1, var2) ? null : var0.parseBaseUniversal(var3, var4);
   }

   public static ASN1Encodable tryParseContextBaseUniversal(ASN1TaggedObjectParser var0, int var1, boolean var2, int var3) throws IOException {
      return tryParseBaseUniversal(var0, 128, var1, var2, var3);
   }

   public static ASN1Encodable parseExplicitBaseObject(ASN1TaggedObjectParser var0, int var1, int var2) throws IOException {
      return checkTag(var0, var1, var2).parseExplicitBaseObject();
   }

   public static ASN1Encodable parseExplicitContextBaseObject(ASN1TaggedObjectParser var0, int var1) throws IOException {
      return parseExplicitBaseObject(var0, 128, var1);
   }

   public static ASN1Encodable tryParseExplicitBaseObject(ASN1TaggedObjectParser var0, int var1, int var2) throws IOException {
      return !var0.hasTag(var1, var2) ? null : var0.parseExplicitBaseObject();
   }

   public static ASN1Encodable tryParseExplicitContextBaseObject(ASN1TaggedObjectParser var0, int var1) throws IOException {
      return tryParseExplicitBaseObject(var0, 128, var1);
   }
}
