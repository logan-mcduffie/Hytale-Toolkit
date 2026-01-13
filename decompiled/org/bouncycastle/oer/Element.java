package org.bouncycastle.oer;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import org.bouncycastle.asn1.ASN1Encodable;

public class Element {
   private final OERDefinition.BaseType baseType;
   private final List<Element> children;
   private final boolean explicit;
   private final String label;
   private final BigInteger lowerBound;
   private final BigInteger upperBound;
   private final boolean extensionsInDefinition;
   private final BigInteger enumValue;
   private final ASN1Encodable defaultValue;
   private final Switch aSwitch;
   private final boolean defaultValuesInChildren;
   private List<Element> optionalChildrenInOrder;
   private List<ASN1Encodable> validSwitchValues;
   private final ElementSupplier elementSupplier;
   private final boolean mayRecurse;
   private final String typeName;
   private final Map<String, ElementSupplier> supplierMap;
   private Element parent;
   private final int optionals;
   private final int block;

   public Element(
      OERDefinition.BaseType var1,
      List<Element> var2,
      boolean var3,
      String var4,
      BigInteger var5,
      BigInteger var6,
      boolean var7,
      BigInteger var8,
      ASN1Encodable var9,
      Switch var10,
      List<ASN1Encodable> var11,
      ElementSupplier var12,
      boolean var13,
      String var14,
      Map<String, ElementSupplier> var15,
      int var16,
      int var17,
      boolean var18
   ) {
      this.baseType = var1;
      this.children = var2;
      this.explicit = var3;
      this.label = var4;
      this.lowerBound = var5;
      this.upperBound = var6;
      this.extensionsInDefinition = var7;
      this.enumValue = var8;
      this.defaultValue = var9;
      this.aSwitch = var10;
      this.validSwitchValues = var11 != null ? Collections.unmodifiableList(var11) : null;
      this.elementSupplier = var12;
      this.mayRecurse = var13;
      this.typeName = var14;
      this.block = var16;
      this.optionals = var17;
      this.defaultValuesInChildren = var18;
      if (var15 == null) {
         this.supplierMap = Collections.emptyMap();
      } else {
         this.supplierMap = var15;
      }

      for (Element var20 : var2) {
         var20.parent = this;
      }
   }

   public Element(Element var1, Element var2) {
      this.baseType = var1.baseType;
      this.children = new ArrayList<>(var1.children);
      this.explicit = var1.explicit;
      this.label = var1.label;
      this.lowerBound = var1.lowerBound;
      this.upperBound = var1.upperBound;
      this.extensionsInDefinition = var1.extensionsInDefinition;
      this.enumValue = var1.enumValue;
      this.defaultValue = var1.defaultValue;
      this.aSwitch = var1.aSwitch;
      this.validSwitchValues = var1.validSwitchValues;
      this.elementSupplier = var1.elementSupplier;
      this.mayRecurse = var1.mayRecurse;
      this.typeName = var1.typeName;
      this.supplierMap = var1.supplierMap;
      this.parent = var2;
      this.block = var1.block;
      this.optionals = var1.optionals;
      this.defaultValuesInChildren = var1.defaultValuesInChildren;

      for (Element var4 : this.children) {
         var4.parent = this;
      }
   }

   public static Element expandDeferredDefinition(Element var0, Element var1) {
      if (var0.elementSupplier != null) {
         var0 = var0.elementSupplier.build();
         if (var0.getParent() != var1) {
            var0 = new Element(var0, var1);
         }
      }

      return var0;
   }

   public String rangeExpression() {
      return "("
         + (this.getLowerBound() != null ? this.getLowerBound().toString() : "MIN")
         + " ... "
         + (this.getUpperBound() != null ? this.getUpperBound().toString() : "MAX")
         + ")";
   }

   public String appendLabel(String var1) {
      return "[" + (this.getLabel() == null ? "" : this.getLabel()) + (this.isExplicit() ? " (E)" : "") + "] " + var1;
   }

   public List<Element> optionalOrDefaultChildrenInOrder() {
      synchronized (this) {
         if (this.getOptionalChildrenInOrder() == null) {
            ArrayList var2 = new ArrayList();

            for (Element var4 : this.getChildren()) {
               if (!var4.isExplicit() || var4.getDefaultValue() != null) {
                  var2.add(var4);
               }
            }

            this.optionalChildrenInOrder = Collections.unmodifiableList(var2);
         }

         return this.getOptionalChildrenInOrder();
      }
   }

   public boolean isUnbounded() {
      return this.getUpperBound() == null && this.getLowerBound() == null;
   }

   public boolean isLowerRangeZero() {
      return BigInteger.ZERO.equals(this.getLowerBound());
   }

   public boolean isUnsignedWithRange() {
      return this.isLowerRangeZero() && this.getUpperBound() != null && BigInteger.ZERO.compareTo(this.getUpperBound()) < 0;
   }

   public boolean canBeNegative() {
      return this.getLowerBound() != null && BigInteger.ZERO.compareTo(this.getLowerBound()) > 0;
   }

   public int intBytesForRange() {
      if (this.getLowerBound() != null && this.getUpperBound() != null) {
         if (BigInteger.ZERO.equals(this.getLowerBound())) {
            int var1 = 0;

            for (byte var2 = 1; var1 < OERDefinition.uIntMax.length; var2 *= 2) {
               if (this.getUpperBound().compareTo(OERDefinition.uIntMax[var1]) < 0) {
                  return var2;
               }

               var1++;
            }
         } else {
            int var3 = 0;

            for (byte var4 = 1; var3 < OERDefinition.sIntRange.length; var4 *= 2) {
               if (this.getLowerBound().compareTo(OERDefinition.sIntRange[var3][0]) >= 0
                  && this.getUpperBound().compareTo(OERDefinition.sIntRange[var3][1]) < 0) {
                  return -var4;
               }

               var3++;
            }
         }
      }

      return 0;
   }

   public boolean hasPopulatedExtension() {
      return this.extensionsInDefinition;
   }

   public boolean hasDefaultChildren() {
      return this.defaultValuesInChildren;
   }

   public ASN1Encodable getDefaultValue() {
      return this.defaultValue;
   }

   public Element getFirstChid() {
      return this.getChildren().get(0);
   }

   public boolean isFixedLength() {
      return this.getLowerBound() != null && this.getLowerBound().equals(this.getUpperBound());
   }

   @Override
   public String toString() {
      return "[" + this.typeName + " " + this.baseType.name() + " '" + this.getLabel() + "']";
   }

   public OERDefinition.BaseType getBaseType() {
      return this.baseType;
   }

   public List<Element> getChildren() {
      return this.children;
   }

   public boolean isExplicit() {
      return this.explicit;
   }

   public String getLabel() {
      return this.label;
   }

   public BigInteger getLowerBound() {
      return this.lowerBound;
   }

   public BigInteger getUpperBound() {
      return this.upperBound;
   }

   public boolean isExtensionsInDefinition() {
      return this.extensionsInDefinition;
   }

   public BigInteger getEnumValue() {
      return this.enumValue;
   }

   public Switch getaSwitch() {
      return this.aSwitch;
   }

   public List<Element> getOptionalChildrenInOrder() {
      return this.optionalChildrenInOrder;
   }

   public List<ASN1Encodable> getValidSwitchValues() {
      return this.validSwitchValues;
   }

   public ElementSupplier getElementSupplier() {
      return this.elementSupplier;
   }

   public boolean isMayRecurse() {
      return this.mayRecurse;
   }

   public String getTypeName() {
      return this.typeName;
   }

   public int getOptionals() {
      return this.optionals;
   }

   public int getBlock() {
      return this.block;
   }

   public String getDerivedTypeName() {
      return this.typeName != null ? this.typeName : this.baseType.name();
   }

   public ElementSupplier resolveSupplier() {
      if (this.supplierMap.containsKey(this.label)) {
         return this.supplierMap.get(this.label);
      } else if (this.parent != null) {
         return this.parent.resolveSupplier(this.label);
      } else {
         throw new IllegalStateException("unable to resolve: " + this.label);
      }
   }

   protected ElementSupplier resolveSupplier(String var1) {
      var1 = this.label + "." + var1;
      if (this.supplierMap.containsKey(var1)) {
         return this.supplierMap.get(var1);
      } else if (this.parent != null) {
         return this.parent.resolveSupplier(var1);
      } else {
         throw new IllegalStateException("unable to resolve: " + var1);
      }
   }

   public Element getParent() {
      return this.parent;
   }

   @Override
   public boolean equals(Object var1) {
      if (this == var1) {
         return true;
      } else if (var1 != null && this.getClass() == var1.getClass()) {
         Element var2 = (Element)var1;
         if (this.explicit != var2.explicit) {
            return false;
         } else if (this.extensionsInDefinition != var2.extensionsInDefinition) {
            return false;
         } else if (this.defaultValuesInChildren != var2.defaultValuesInChildren) {
            return false;
         } else if (this.mayRecurse != var2.mayRecurse) {
            return false;
         } else if (this.optionals != var2.optionals) {
            return false;
         } else if (this.block != var2.block) {
            return false;
         } else if (this.baseType != var2.baseType) {
            return false;
         } else if (this.children != null ? this.children.equals(var2.children) : var2.children == null) {
            if (this.label != null ? this.label.equals(var2.label) : var2.label == null) {
               if (this.lowerBound != null ? this.lowerBound.equals(var2.lowerBound) : var2.lowerBound == null) {
                  if (this.upperBound != null ? this.upperBound.equals(var2.upperBound) : var2.upperBound == null) {
                     if (this.enumValue != null ? this.enumValue.equals(var2.enumValue) : var2.enumValue == null) {
                        if (this.defaultValue != null ? this.defaultValue.equals(var2.defaultValue) : var2.defaultValue == null) {
                           if (this.aSwitch != null ? this.aSwitch.equals(var2.aSwitch) : var2.aSwitch == null) {
                              if (this.optionalChildrenInOrder != null
                                 ? this.optionalChildrenInOrder.equals(var2.optionalChildrenInOrder)
                                 : var2.optionalChildrenInOrder == null) {
                                 if (this.validSwitchValues != null ? this.validSwitchValues.equals(var2.validSwitchValues) : var2.validSwitchValues == null) {
                                    if (this.elementSupplier != null ? this.elementSupplier.equals(var2.elementSupplier) : var2.elementSupplier == null) {
                                       if (this.typeName != null ? this.typeName.equals(var2.typeName) : var2.typeName == null) {
                                          return this.supplierMap != null ? !this.supplierMap.equals(var2.supplierMap) : var2.supplierMap != null;
                                       } else {
                                          return false;
                                       }
                                    } else {
                                       return false;
                                    }
                                 } else {
                                    return false;
                                 }
                              } else {
                                 return false;
                              }
                           } else {
                              return false;
                           }
                        } else {
                           return false;
                        }
                     } else {
                        return false;
                     }
                  } else {
                     return false;
                  }
               } else {
                  return false;
               }
            } else {
               return false;
            }
         } else {
            return false;
         }
      } else {
         return false;
      }
   }

   @Override
   public int hashCode() {
      int var1 = this.baseType != null ? this.baseType.hashCode() : 0;
      var1 = 31 * var1 + (this.children != null ? this.children.hashCode() : 0);
      var1 = 31 * var1 + (this.explicit ? 1 : 0);
      var1 = 31 * var1 + (this.label != null ? this.label.hashCode() : 0);
      var1 = 31 * var1 + (this.lowerBound != null ? this.lowerBound.hashCode() : 0);
      var1 = 31 * var1 + (this.upperBound != null ? this.upperBound.hashCode() : 0);
      var1 = 31 * var1 + (this.extensionsInDefinition ? 1 : 0);
      var1 = 31 * var1 + (this.enumValue != null ? this.enumValue.hashCode() : 0);
      var1 = 31 * var1 + (this.defaultValue != null ? this.defaultValue.hashCode() : 0);
      var1 = 31 * var1 + (this.aSwitch != null ? this.aSwitch.hashCode() : 0);
      var1 = 31 * var1 + (this.defaultValuesInChildren ? 1 : 0);
      var1 = 31 * var1 + (this.optionalChildrenInOrder != null ? this.optionalChildrenInOrder.hashCode() : 0);
      var1 = 31 * var1 + (this.validSwitchValues != null ? this.validSwitchValues.hashCode() : 0);
      var1 = 31 * var1 + (this.elementSupplier != null ? this.elementSupplier.hashCode() : 0);
      var1 = 31 * var1 + (this.mayRecurse ? 1 : 0);
      var1 = 31 * var1 + (this.typeName != null ? this.typeName.hashCode() : 0);
      var1 = 31 * var1 + (this.supplierMap != null ? this.supplierMap.hashCode() : 0);
      var1 = 31 * var1 + this.optionals;
      return 31 * var1 + this.block;
   }
}
