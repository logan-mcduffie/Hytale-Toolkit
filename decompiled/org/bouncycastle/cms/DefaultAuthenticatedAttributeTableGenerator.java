package org.bouncycastle.cms;

import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Map;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.DEROctetString;
import org.bouncycastle.asn1.DERSet;
import org.bouncycastle.asn1.cms.Attribute;
import org.bouncycastle.asn1.cms.AttributeTable;
import org.bouncycastle.asn1.cms.CMSAlgorithmProtection;
import org.bouncycastle.asn1.cms.CMSAttributes;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;

public class DefaultAuthenticatedAttributeTableGenerator implements CMSAttributeTableGenerator {
   private final Hashtable table;

   public DefaultAuthenticatedAttributeTableGenerator() {
      this.table = new Hashtable();
   }

   public DefaultAuthenticatedAttributeTableGenerator(AttributeTable var1) {
      if (var1 != null) {
         this.table = var1.toHashtable();
      } else {
         this.table = new Hashtable();
      }
   }

   protected Hashtable createStandardAttributeTable(Map var1) {
      Hashtable var2 = new Hashtable();
      Enumeration var3 = this.table.keys();

      while (var3.hasMoreElements()) {
         Object var4 = var3.nextElement();
         var2.put(var4, this.table.get(var4));
      }

      if (!var2.containsKey(CMSAttributes.contentType)) {
         ASN1ObjectIdentifier var5 = ASN1ObjectIdentifier.getInstance(var1.get("contentType"));
         Attribute var8 = new Attribute(CMSAttributes.contentType, new DERSet(var5));
         var2.put(var8.getAttrType(), var8);
      }

      if (!var2.containsKey(CMSAttributes.messageDigest)) {
         byte[] var6 = (byte[])var1.get("digest");
         Attribute var9 = new Attribute(CMSAttributes.messageDigest, new DERSet(new DEROctetString(var6)));
         var2.put(var9.getAttrType(), var9);
      }

      if (!var2.contains(CMSAttributes.cmsAlgorithmProtect)) {
         Attribute var7 = new Attribute(
            CMSAttributes.cmsAlgorithmProtect,
            new DERSet(new CMSAlgorithmProtection((AlgorithmIdentifier)var1.get("digestAlgID"), 2, (AlgorithmIdentifier)var1.get("macAlgID")))
         );
         var2.put(var7.getAttrType(), var7);
      }

      return var2;
   }

   @Override
   public AttributeTable getAttributes(Map var1) {
      return new AttributeTable(this.createStandardAttributeTable(var1));
   }
}
