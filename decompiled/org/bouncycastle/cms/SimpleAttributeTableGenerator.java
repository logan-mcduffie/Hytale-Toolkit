package org.bouncycastle.cms;

import java.util.Map;
import org.bouncycastle.asn1.cms.AttributeTable;

public class SimpleAttributeTableGenerator implements CMSAttributeTableGenerator {
   private final AttributeTable attributes;

   public SimpleAttributeTableGenerator(AttributeTable var1) {
      this.attributes = var1;
   }

   @Override
   public AttributeTable getAttributes(Map var1) {
      return this.attributes;
   }
}
