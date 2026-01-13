package org.bouncycastle.cms;

import java.util.Date;
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
import org.bouncycastle.asn1.cms.Time;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;

public class DefaultSignedAttributeTableGenerator implements CMSAttributeTableGenerator {
   private final Hashtable table;

   public DefaultSignedAttributeTableGenerator() {
      this.table = new Hashtable();
   }

   public DefaultSignedAttributeTableGenerator(AttributeTable var1) {
      if (var1 != null) {
         this.table = var1.toHashtable();
      } else {
         this.table = new Hashtable();
      }
   }

   protected Hashtable createStandardAttributeTable(Map var1) {
      Hashtable var2 = copyHashTable(this.table);
      if (!var2.containsKey(CMSAttributes.contentType)) {
         ASN1ObjectIdentifier var3 = ASN1ObjectIdentifier.getInstance(var1.get("contentType"));
         if (var3 != null) {
            Attribute var4 = new Attribute(CMSAttributes.contentType, new DERSet(var3));
            var2.put(var4.getAttrType(), var4);
         }
      }

      if (!var2.containsKey(CMSAttributes.signingTime)) {
         Date var5 = new Date();
         Attribute var8 = new Attribute(CMSAttributes.signingTime, new DERSet(new Time(var5)));
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
            new DERSet(new CMSAlgorithmProtection((AlgorithmIdentifier)var1.get("digestAlgID"), 1, (AlgorithmIdentifier)var1.get("signatureAlgID")))
         );
         var2.put(var7.getAttrType(), var7);
      }

      return var2;
   }

   @Override
   public AttributeTable getAttributes(Map var1) {
      return new AttributeTable(this.createStandardAttributeTable(var1));
   }

   private static Hashtable copyHashTable(Hashtable var0) {
      Hashtable var1 = new Hashtable();
      Enumeration var2 = var0.keys();

      while (var2.hasMoreElements()) {
         Object var3 = var2.nextElement();
         var1.put(var3, var0.get(var3));
      }

      return var1;
   }
}
