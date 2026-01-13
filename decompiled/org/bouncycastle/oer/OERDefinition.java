package org.bouncycastle.oer;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1Integer;

public class OERDefinition {
   static final BigInteger[] uIntMax = new BigInteger[]{
      new BigInteger("256"), new BigInteger("65536"), new BigInteger("4294967296"), new BigInteger("18446744073709551616")
   };
   static final BigInteger[][] sIntRange = new BigInteger[][]{
      {new BigInteger("-128"), new BigInteger("127")},
      {new BigInteger("-32768"), new BigInteger("32767")},
      {new BigInteger("-2147483648"), new BigInteger("2147483647")},
      {new BigInteger("-9223372036854775808"), new BigInteger("9223372036854775807")}
   };

   public static OERDefinition.Builder bool() {
      return new OERDefinition.Builder(OERDefinition.BaseType.BOOLEAN);
   }

   public static OERDefinition.Builder integer() {
      return new OERDefinition.Builder(OERDefinition.BaseType.INT);
   }

   public static OERDefinition.Builder integer(long var0) {
      return new OERDefinition.Builder(OERDefinition.BaseType.INT).defaultValue(new ASN1Integer(var0));
   }

   public static OERDefinition.Builder bitString(long var0) {
      return new OERDefinition.Builder(OERDefinition.BaseType.BIT_STRING).fixedSize(var0);
   }

   public static OERDefinition.Builder integer(BigInteger var0, BigInteger var1) {
      return new OERDefinition.Builder(OERDefinition.BaseType.INT).range(var0, var1);
   }

   public static OERDefinition.Builder integer(long var0, long var2) {
      return new OERDefinition.Builder(OERDefinition.BaseType.INT).range(BigInteger.valueOf(var0), BigInteger.valueOf(var2));
   }

   public static OERDefinition.Builder integer(long var0, long var2, ASN1Encodable var4) {
      return new OERDefinition.Builder(OERDefinition.BaseType.INT).range(var0, var2, var4);
   }

   public static OERDefinition.Builder nullValue() {
      return new OERDefinition.Builder(OERDefinition.BaseType.NULL);
   }

   public static OERDefinition.Builder seq() {
      return new OERDefinition.Builder(OERDefinition.BaseType.SEQ);
   }

   public static OERDefinition.Builder seq(Object... var0) {
      return new OERDefinition.Builder(OERDefinition.BaseType.SEQ).items(var0);
   }

   public static OERDefinition.Builder aSwitch(Switch var0) {
      return new OERDefinition.Builder(OERDefinition.BaseType.Switch).decodeSwitch(var0);
   }

   public static OERDefinition.Builder enumItem(String var0) {
      return new OERDefinition.Builder(OERDefinition.BaseType.ENUM_ITEM).label(var0);
   }

   public static OERDefinition.Builder enumItem(String var0, BigInteger var1) {
      return new OERDefinition.Builder(OERDefinition.BaseType.ENUM_ITEM).enumValue(var1).label(var0);
   }

   public static OERDefinition.Builder enumeration(Object... var0) {
      return new OERDefinition.Builder(OERDefinition.BaseType.ENUM).items(var0);
   }

   public static OERDefinition.Builder choice(Object... var0) {
      return new OERDefinition.Builder(OERDefinition.BaseType.CHOICE).items(var0);
   }

   public static OERDefinition.Builder placeholder() {
      return new OERDefinition.Builder(null);
   }

   public static OERDefinition.Builder seqof(Object... var0) {
      return new OERDefinition.Builder(OERDefinition.BaseType.SEQ_OF).items(var0);
   }

   public static OERDefinition.Builder octets() {
      return new OERDefinition.Builder(OERDefinition.BaseType.OCTET_STRING).unbounded();
   }

   public static OERDefinition.Builder octets(int var0) {
      return new OERDefinition.Builder(OERDefinition.BaseType.OCTET_STRING).fixedSize(var0);
   }

   public static OERDefinition.Builder octets(int var0, int var1) {
      return new OERDefinition.Builder(OERDefinition.BaseType.OCTET_STRING).range(BigInteger.valueOf(var0), BigInteger.valueOf(var1));
   }

   public static OERDefinition.Builder ia5String() {
      return new OERDefinition.Builder(OERDefinition.BaseType.IA5String);
   }

   public static OERDefinition.Builder utf8String() {
      return new OERDefinition.Builder(OERDefinition.BaseType.UTF8_STRING);
   }

   public static OERDefinition.Builder utf8String(int var0) {
      return new OERDefinition.Builder(OERDefinition.BaseType.UTF8_STRING).rangeToMAXFrom(var0);
   }

   public static OERDefinition.Builder utf8String(int var0, int var1) {
      return new OERDefinition.Builder(OERDefinition.BaseType.UTF8_STRING).range(BigInteger.valueOf(var0), BigInteger.valueOf(var1));
   }

   public static OERDefinition.Builder opaque() {
      return new OERDefinition.Builder(OERDefinition.BaseType.OPAQUE);
   }

   public static List<Object> optional(Object... var0) {
      return new OERDefinition.OptionalList(Arrays.asList(var0));
   }

   public static OERDefinition.ExtensionList extension(Object... var0) {
      return new OERDefinition.ExtensionList(1, Arrays.asList(var0));
   }

   public static OERDefinition.ExtensionList extension(int var0, Object... var1) {
      return new OERDefinition.ExtensionList(var0, Arrays.asList(var1));
   }

   public static OERDefinition.Builder deferred(ElementSupplier var0) {
      return new OERDefinition.Builder(OERDefinition.BaseType.Supplier).elementSupplier(var0);
   }

   public static enum BaseType {
      SEQ,
      SEQ_OF,
      CHOICE,
      ENUM,
      INT,
      OCTET_STRING,
      OPAQUE,
      UTF8_STRING,
      BIT_STRING,
      NULL,
      EXTENSION,
      ENUM_ITEM,
      BOOLEAN,
      IS0646String,
      PrintableString,
      NumericString,
      BMPString,
      UniversalString,
      IA5String,
      VisibleString,
      Switch,
      Supplier;
   }

   public static class Builder {
      protected final OERDefinition.BaseType baseType;
      protected ArrayList<OERDefinition.Builder> children = new ArrayList<>();
      protected boolean explicit = true;
      protected String typeName;
      protected String label;
      protected BigInteger upperBound;
      protected BigInteger lowerBound;
      protected BigInteger enumValue;
      protected ASN1Encodable defaultValue;
      protected OERDefinition.Builder placeholderValue;
      protected Boolean inScope;
      protected Switch aSwitch;
      protected ArrayList<ASN1Encodable> validSwitchValues = new ArrayList<>();
      protected ElementSupplier elementSupplier;
      protected boolean mayRecurse;
      protected Map<String, ElementSupplier> supplierMap = new HashMap<>();
      protected int block;
      private final OERDefinition.ItemProvider defaultItemProvider = new OERDefinition.ItemProvider() {
         @Override
         public OERDefinition.Builder existingChild(int var1, OERDefinition.Builder var2) {
            return var2.copy(Builder.this.defaultItemProvider);
         }
      };

      public Builder(OERDefinition.BaseType var1) {
         this.baseType = var1;
      }

      private OERDefinition.Builder copy(OERDefinition.ItemProvider var1) {
         OERDefinition.Builder var2 = new OERDefinition.Builder(this.baseType);
         int var3 = 0;

         for (OERDefinition.Builder var5 : this.children) {
            var2.children.add(var1.existingChild(var3++, var5));
         }

         var2.explicit = this.explicit;
         var2.label = this.label;
         var2.upperBound = this.upperBound;
         var2.lowerBound = this.lowerBound;
         var2.defaultValue = this.defaultValue;
         var2.enumValue = this.enumValue;
         var2.inScope = this.inScope;
         var2.aSwitch = this.aSwitch;
         var2.validSwitchValues = new ArrayList<>(this.validSwitchValues);
         var2.elementSupplier = this.elementSupplier;
         var2.mayRecurse = this.mayRecurse;
         var2.typeName = this.typeName;
         var2.supplierMap = new HashMap<>(this.supplierMap);
         var2.block = this.block;
         return var2;
      }

      protected OERDefinition.Builder block(int var1) {
         OERDefinition.Builder var2 = this.copy();
         var2.block = var1;
         return var2;
      }

      public OERDefinition.Builder copy() {
         return this.copy(this.defaultItemProvider);
      }

      public OERDefinition.Builder elementSupplier(ElementSupplier var1) {
         OERDefinition.Builder var2 = this.copy();
         var2.elementSupplier = var1;
         return var2;
      }

      public OERDefinition.Builder validSwitchValue(ASN1Encodable... var1) {
         OERDefinition.Builder var2 = this.copy();
         var2.validSwitchValues.addAll(Arrays.asList(var1));
         return var2;
      }

      public OERDefinition.Builder inScope(boolean var1) {
         OERDefinition.Builder var2 = this.copy();
         var2.inScope = var1;
         return var2;
      }

      public OERDefinition.Builder limitScopeTo(String... var1) {
         OERDefinition.Builder var2 = this.copy();
         HashSet var3 = new HashSet();
         var3.addAll(Arrays.asList(var1));
         ArrayList var4 = new ArrayList();

         for (OERDefinition.Builder var6 : this.children) {
            var4.add(var6.copy().inScope(var3.contains(var6.label)));
         }

         var2.children = var4;
         return var2;
      }

      public OERDefinition.Builder typeName(String var1) {
         OERDefinition.Builder var2 = this.copy();
         var2.typeName = var1;
         if (var2.label == null) {
            var2.label = var1;
         }

         return var2;
      }

      public OERDefinition.Builder unbounded() {
         OERDefinition.Builder var1 = this.copy();
         var1.lowerBound = null;
         var1.upperBound = null;
         return var1;
      }

      public OERDefinition.Builder decodeSwitch(Switch var1) {
         OERDefinition.Builder var2 = this.copy();
         var2.aSwitch = var1;
         return var2;
      }

      public OERDefinition.Builder labelPrefix(String var1) {
         OERDefinition.Builder var2 = this.copy();
         var2.label = var1 + " " + this.label;
         return var2;
      }

      public OERDefinition.Builder explicit(boolean var1) {
         OERDefinition.Builder var2 = this.copy();
         var2.explicit = var1;
         return var2;
      }

      public OERDefinition.Builder defaultValue(ASN1Encodable var1) {
         OERDefinition.Builder var2 = this.copy();
         var2.defaultValue = var1;
         return var2;
      }

      protected OERDefinition.Builder wrap(boolean var1, Object var2) {
         if (var2 instanceof OERDefinition.Builder) {
            return ((OERDefinition.Builder)var2).explicit(var1);
         } else if (var2 instanceof OERDefinition.BaseType) {
            return new OERDefinition.Builder((OERDefinition.BaseType)var2).explicit(var1);
         } else if (var2 instanceof String) {
            return OERDefinition.enumItem((String)var2);
         } else {
            throw new IllegalStateException("Unable to wrap item in builder");
         }
      }

      protected void addExtensions(OERDefinition.Builder var1, OERDefinition.ExtensionList var2) {
         if (var2.isEmpty()) {
            OERDefinition.Builder var6 = new OERDefinition.Builder(OERDefinition.BaseType.EXTENSION);
            var6.block = var2.block;
            var1.children.add(var6);
         } else {
            for (Object var4 : var2) {
               if (var4 instanceof OERDefinition.OptionalList) {
                  this.addOptionals(var1, var2.block, (OERDefinition.OptionalList)var4);
               } else {
                  OERDefinition.Builder var5 = this.wrap(true, var4);
                  var5.block = var2.block;
                  var1.children.add(var5);
               }
            }
         }
      }

      protected void addOptionals(OERDefinition.Builder var1, int var2, OERDefinition.OptionalList var3) {
         for (Object var5 : var3) {
            if (var5 instanceof OERDefinition.ExtensionList) {
               this.addExtensions(var1, (OERDefinition.ExtensionList)var5);
            } else {
               OERDefinition.Builder var6 = this.wrap(false, var5);
               var6.block = var2;
               var1.children.add(var6);
            }
         }
      }

      public OERDefinition.Builder items(Object... var1) {
         OERDefinition.Builder var2 = this.copy();

         for (int var3 = 0; var3 != var1.length; var3++) {
            Object var4 = var1[var3];
            if (var4 instanceof OERDefinition.ExtensionList) {
               this.addExtensions(var2, (OERDefinition.ExtensionList)var4);
            } else if (var4 instanceof OERDefinition.OptionalList) {
               this.addOptionals(var2, var2.block, (OERDefinition.OptionalList)var4);
            } else if (var4.getClass().isArray()) {
               for (int var5 = 0; var5 < ((Object[])var4).length; var5++) {
                  var2.children.add(this.wrap(true, ((Object[])var4)[var5]));
               }
            } else {
               var2.children.add(this.wrap(true, var4));
            }
         }

         return var2;
      }

      public OERDefinition.Builder label(String var1) {
         OERDefinition.Builder var2 = this.copy();
         var2.label = var1;
         return var2;
      }

      public OERDefinition.Builder mayRecurse(boolean var1) {
         OERDefinition.Builder var2 = this.copy();
         var2.mayRecurse = var1;
         return var2;
      }

      public Element build() {
         ArrayList var1 = new ArrayList();
         boolean var2 = false;
         if (this.baseType == OERDefinition.BaseType.ENUM) {
            int var3 = 0;
            HashSet var4 = new HashSet();

            for (int var5 = 0; var5 < this.children.size(); var5++) {
               OERDefinition.Builder var6 = this.children.get(var5);
               if (var6.enumValue == null) {
                  var6.enumValue = BigInteger.valueOf(var3);
                  var3++;
               }

               if (var4.contains(var6.enumValue)) {
                  throw new IllegalStateException("duplicate enum value at index " + var5);
               }

               var4.add(var6.enumValue);
            }
         }

         int var7 = 0;
         boolean var8 = false;

         for (OERDefinition.Builder var10 : this.children) {
            if (!var2 && var10.block > 0) {
               var2 = true;
            }

            if (!var10.explicit) {
               var7++;
            }

            if (!var8 && var10.defaultValue != null) {
               var8 = true;
            }

            var1.add(var10.build());
         }

         return new Element(
            this.baseType,
            var1,
            this.defaultValue == null && this.explicit,
            this.label,
            this.lowerBound,
            this.upperBound,
            var2,
            this.enumValue,
            this.defaultValue,
            this.aSwitch,
            this.validSwitchValues.isEmpty() ? null : this.validSwitchValues,
            this.elementSupplier,
            this.mayRecurse,
            this.typeName,
            this.supplierMap.isEmpty() ? null : this.supplierMap,
            this.block,
            var7,
            var8
         );
      }

      public OERDefinition.Builder range(BigInteger var1, BigInteger var2) {
         OERDefinition.Builder var3 = this.copy();
         var3.lowerBound = var1;
         var3.upperBound = var2;
         return var3;
      }

      public OERDefinition.Builder rangeToMAXFrom(long var1) {
         OERDefinition.Builder var3 = this.copy();
         var3.lowerBound = BigInteger.valueOf(var1);
         var3.upperBound = null;
         return var3;
      }

      public OERDefinition.Builder rangeZeroTo(long var1) {
         OERDefinition.Builder var3 = this.copy();
         var3.upperBound = BigInteger.valueOf(var1);
         var3.lowerBound = BigInteger.ZERO;
         return var3;
      }

      public OERDefinition.Builder fixedSize(long var1) {
         OERDefinition.Builder var3 = this.copy();
         var3.upperBound = BigInteger.valueOf(var1);
         var3.lowerBound = BigInteger.valueOf(var1);
         return var3;
      }

      public OERDefinition.Builder range(long var1, long var3, ASN1Encodable var5) {
         OERDefinition.Builder var6 = this.copy();
         var6.lowerBound = BigInteger.valueOf(var1);
         var6.upperBound = BigInteger.valueOf(var3);
         var6.defaultValue = var5;
         return var6;
      }

      public OERDefinition.Builder enumValue(BigInteger var1) {
         OERDefinition.Builder var2 = this.copy();
         this.enumValue = var1;
         return var2;
      }

      public OERDefinition.Builder replaceChild(final int var1, final OERDefinition.Builder var2) {
         return this.copy(new OERDefinition.ItemProvider() {
            @Override
            public OERDefinition.Builder existingChild(int var1x, OERDefinition.Builder var2x) {
               return var1 == var1x ? var2 : var2x;
            }
         });
      }
   }

   private static class ExtensionList extends ArrayList<Object> {
      protected final int block;

      public ExtensionList(int var1, List<Object> var2) {
         this.block = var1;
         this.addAll(var2);
      }
   }

   public interface ItemProvider {
      OERDefinition.Builder existingChild(int var1, OERDefinition.Builder var2);
   }

   public static class MutableBuilder extends OERDefinition.Builder {
      private boolean frozen = false;

      public MutableBuilder(OERDefinition.BaseType var1) {
         super(var1);
      }

      public OERDefinition.MutableBuilder label(String var1) {
         this.label = var1;
         return this;
      }

      public OERDefinition.MutableBuilder addItemsAndFreeze(OERDefinition.Builder... var1) {
         if (this.frozen) {
            throw new IllegalStateException("build cannot be modified and must be copied only");
         } else {
            for (int var2 = 0; var2 != var1.length; var2++) {
               OERDefinition.Builder var3 = var1[var2];
               if (var3 instanceof OERDefinition.OptionalList) {
                  Iterator var8 = ((List)var3).iterator();

                  while (var8.hasNext()) {
                     super.children.add(this.wrap(false, var8.next()));
                  }
               } else if (var3.getClass().isArray()) {
                  for (Object var7 : (Object[])var3) {
                     super.children.add(this.wrap(true, var7));
                  }
               } else {
                  super.children.add(this.wrap(true, var3));
               }
            }

            this.frozen = true;
            return this;
         }
      }
   }

   private static class OptionalList extends ArrayList<Object> {
      public OptionalList(List<Object> var1) {
         this.addAll(var1);
      }
   }
}
