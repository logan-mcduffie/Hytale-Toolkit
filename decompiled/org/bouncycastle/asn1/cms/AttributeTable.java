package org.bouncycastle.asn1.cms;

import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.ASN1Set;
import org.bouncycastle.asn1.DERSet;

public class AttributeTable {
   private Hashtable attributes = new Hashtable();

   public AttributeTable(Hashtable var1) {
      this.attributes = this.copyTable(var1);
   }

   public AttributeTable(ASN1EncodableVector var1) {
      for (int var2 = 0; var2 != var1.size(); var2++) {
         Attribute var3 = Attribute.getInstance(var1.get(var2));
         this.addAttribute(var3.getAttrType(), var3);
      }
   }

   public AttributeTable(ASN1Set var1) {
      for (int var2 = 0; var2 != var1.size(); var2++) {
         Attribute var3 = Attribute.getInstance(var1.getObjectAt(var2));
         this.addAttribute(var3.getAttrType(), var3);
      }
   }

   public AttributeTable(Attribute var1) {
      this.addAttribute(var1.getAttrType(), var1);
   }

   public AttributeTable(Attributes var1) {
      this(ASN1Set.getInstance(var1.toASN1Primitive()));
   }

   private void addAttribute(ASN1ObjectIdentifier var1, Attribute var2) {
      Object var3 = this.attributes.get(var1);
      if (var3 == null) {
         this.attributes.put(var1, var2);
      } else {
         Vector var4;
         if (var3 instanceof Attribute) {
            var4 = new Vector();
            var4.addElement(var3);
            var4.addElement(var2);
         } else {
            var4 = (Vector)var3;
            var4.addElement(var2);
         }

         this.attributes.put(var1, var4);
      }
   }

   public Attribute get(ASN1ObjectIdentifier var1) {
      Object var2 = this.attributes.get(var1);
      return var2 instanceof Vector ? (Attribute)((Vector)var2).elementAt(0) : (Attribute)var2;
   }

   public ASN1EncodableVector getAll(ASN1ObjectIdentifier var1) {
      ASN1EncodableVector var2 = new ASN1EncodableVector();
      Object var3 = this.attributes.get(var1);
      if (var3 instanceof Vector) {
         Enumeration var4 = ((Vector)var3).elements();

         while (var4.hasMoreElements()) {
            var2.add((Attribute)var4.nextElement());
         }
      } else if (var3 != null) {
         var2.add((Attribute)var3);
      }

      return var2;
   }

   public int size() {
      int var1 = 0;
      Enumeration var2 = this.attributes.elements();

      while (var2.hasMoreElements()) {
         Object var3 = var2.nextElement();
         if (var3 instanceof Vector) {
            var1 += ((Vector)var3).size();
         } else {
            var1++;
         }
      }

      return var1;
   }

   public Hashtable toHashtable() {
      return this.copyTable(this.attributes);
   }

   public ASN1EncodableVector toASN1EncodableVector() {
      ASN1EncodableVector var1 = new ASN1EncodableVector();
      Enumeration var2 = this.attributes.elements();

      while (var2.hasMoreElements()) {
         Object var3 = var2.nextElement();
         if (var3 instanceof Vector) {
            Enumeration var4 = ((Vector)var3).elements();

            while (var4.hasMoreElements()) {
               var1.add(Attribute.getInstance(var4.nextElement()));
            }
         } else {
            var1.add(Attribute.getInstance(var3));
         }
      }

      return var1;
   }

   public Attributes toASN1Structure() {
      return new Attributes(this.toASN1EncodableVector());
   }

   private Hashtable copyTable(Hashtable var1) {
      Hashtable var2 = new Hashtable();
      Enumeration var3 = var1.keys();

      while (var3.hasMoreElements()) {
         Object var4 = var3.nextElement();
         var2.put(var4, var1.get(var4));
      }

      return var2;
   }

   public AttributeTable add(ASN1ObjectIdentifier var1, ASN1Encodable var2) {
      AttributeTable var3 = new AttributeTable(this.attributes);
      var3.addAttribute(var1, new Attribute(var1, new DERSet(var2)));
      return var3;
   }

   public AttributeTable remove(ASN1ObjectIdentifier var1) {
      AttributeTable var2 = new AttributeTable(this.attributes);
      var2.attributes.remove(var1);
      return var2;
   }
}
