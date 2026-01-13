package org.bouncycastle.asn1.x509;

import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.ASN1TaggedObject;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.util.Properties;

public class Extensions extends ASN1Object {
   private Hashtable extensions = new Hashtable();
   private Vector ordering = new Vector();

   public static Extension getExtension(Extensions var0, ASN1ObjectIdentifier var1) {
      return null == var0 ? null : var0.getExtension(var1);
   }

   public static ASN1Encodable getExtensionParsedValue(Extensions var0, ASN1ObjectIdentifier var1) {
      return null == var0 ? null : var0.getExtensionParsedValue(var1);
   }

   public static ASN1OctetString getExtensionValue(Extensions var0, ASN1ObjectIdentifier var1) {
      return null == var0 ? null : var0.getExtensionValue(var1);
   }

   public static Extensions getInstance(ASN1TaggedObject var0, boolean var1) {
      return getInstance(ASN1Sequence.getInstance(var0, var1));
   }

   public static Extensions getInstance(Object var0) {
      if (var0 instanceof Extensions) {
         return (Extensions)var0;
      } else {
         return var0 != null ? new Extensions(ASN1Sequence.getInstance(var0)) : null;
      }
   }

   private Extensions(ASN1Sequence var1) {
      Enumeration var2 = var1.getObjects();

      while (var2.hasMoreElements()) {
         Extension var3 = Extension.getInstance(var2.nextElement());
         if (this.extensions.containsKey(var3.getExtnId()) && !Properties.isOverrideSet("org.bouncycastle.x509.ignore_repeated_extensions")) {
            throw new IllegalArgumentException("repeated extension found: " + var3.getExtnId());
         }

         this.extensions.put(var3.getExtnId(), var3);
         this.ordering.addElement(var3.getExtnId());
      }
   }

   public Extensions(Extension var1) {
      this.ordering.addElement(var1.getExtnId());
      this.extensions.put(var1.getExtnId(), var1);
   }

   public Extensions(Extension[] var1) {
      if (var1 != null && var1.length != 0) {
         for (int var2 = 0; var2 != var1.length; var2++) {
            Extension var3 = var1[var2];
            this.ordering.addElement(var3.getExtnId());
            this.extensions.put(var3.getExtnId(), var3);
         }
      } else {
         throw new IllegalArgumentException("extension array cannot be null or empty");
      }
   }

   public Enumeration oids() {
      return this.ordering.elements();
   }

   public Extension getExtension(ASN1ObjectIdentifier var1) {
      return (Extension)this.extensions.get(var1);
   }

   public ASN1Encodable getExtensionParsedValue(ASN1ObjectIdentifier var1) {
      Extension var2 = this.getExtension(var1);
      return var2 == null ? null : var2.getParsedValue();
   }

   public ASN1OctetString getExtensionValue(ASN1ObjectIdentifier var1) {
      Extension var2 = this.getExtension(var1);
      return var2 == null ? null : var2.getExtnValue();
   }

   @Override
   public ASN1Primitive toASN1Primitive() {
      ASN1EncodableVector var1 = new ASN1EncodableVector(this.ordering.size());
      Enumeration var2 = this.ordering.elements();

      while (var2.hasMoreElements()) {
         ASN1ObjectIdentifier var3 = (ASN1ObjectIdentifier)var2.nextElement();
         Extension var4 = (Extension)this.extensions.get(var3);
         var1.add(var4);
      }

      return new DERSequence(var1);
   }

   public boolean equivalent(Extensions var1) {
      if (this.extensions.size() != var1.extensions.size()) {
         return false;
      } else {
         Enumeration var2 = this.extensions.keys();

         while (var2.hasMoreElements()) {
            Object var3 = var2.nextElement();
            if (!this.extensions.get(var3).equals(var1.extensions.get(var3))) {
               return false;
            }
         }

         return true;
      }
   }

   public ASN1ObjectIdentifier[] getExtensionOIDs() {
      return this.toOidArray(this.ordering);
   }

   public ASN1ObjectIdentifier[] getNonCriticalExtensionOIDs() {
      return this.getExtensionOIDs(false);
   }

   public ASN1ObjectIdentifier[] getCriticalExtensionOIDs() {
      return this.getExtensionOIDs(true);
   }

   public boolean hasAnyCriticalExtensions() {
      for (int var1 = 0; var1 != this.ordering.size(); var1++) {
         Object var2 = this.ordering.elementAt(var1);
         if (((Extension)this.extensions.get(var2)).isCritical()) {
            return true;
         }
      }

      return false;
   }

   private ASN1ObjectIdentifier[] getExtensionOIDs(boolean var1) {
      Vector var2 = new Vector();

      for (int var3 = 0; var3 != this.ordering.size(); var3++) {
         Object var4 = this.ordering.elementAt(var3);
         if (((Extension)this.extensions.get(var4)).isCritical() == var1) {
            var2.addElement(var4);
         }
      }

      return this.toOidArray(var2);
   }

   private ASN1ObjectIdentifier[] toOidArray(Vector var1) {
      ASN1ObjectIdentifier[] var2 = new ASN1ObjectIdentifier[var1.size()];

      for (int var3 = 0; var3 != var2.length; var3++) {
         var2[var3] = (ASN1ObjectIdentifier)var1.elementAt(var3);
      }

      return var2;
   }
}
