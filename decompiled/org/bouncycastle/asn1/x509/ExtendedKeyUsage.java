package org.bouncycastle.asn1.x509;

import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.ASN1TaggedObject;
import org.bouncycastle.asn1.DERSequence;

public class ExtendedKeyUsage extends ASN1Object {
   Hashtable usageTable = new Hashtable();
   ASN1Sequence seq;

   public static ExtendedKeyUsage getInstance(ASN1TaggedObject var0, boolean var1) {
      return getInstance(ASN1Sequence.getInstance(var0, var1));
   }

   public static ExtendedKeyUsage getInstance(Object var0) {
      if (var0 instanceof ExtendedKeyUsage) {
         return (ExtendedKeyUsage)var0;
      } else {
         return var0 != null ? new ExtendedKeyUsage(ASN1Sequence.getInstance(var0)) : null;
      }
   }

   public static ExtendedKeyUsage fromExtensions(Extensions var0) {
      return getInstance(Extensions.getExtensionParsedValue(var0, Extension.extendedKeyUsage));
   }

   public ExtendedKeyUsage(KeyPurposeId var1) {
      this.seq = new DERSequence(var1);
      this.usageTable.put(var1, var1);
   }

   private ExtendedKeyUsage(ASN1Sequence var1) {
      this.seq = var1;
      Enumeration var2 = var1.getObjects();

      while (var2.hasMoreElements()) {
         ASN1Encodable var3 = (ASN1Encodable)var2.nextElement();
         if (!(var3.toASN1Primitive() instanceof ASN1ObjectIdentifier)) {
            throw new IllegalArgumentException("Only ASN1ObjectIdentifiers allowed in ExtendedKeyUsage.");
         }

         this.usageTable.put(var3, var3);
      }
   }

   public ExtendedKeyUsage(KeyPurposeId[] var1) {
      ASN1EncodableVector var2 = new ASN1EncodableVector(var1.length);

      for (int var3 = 0; var3 != var1.length; var3++) {
         var2.add(var1[var3]);
         this.usageTable.put(var1[var3], var1[var3]);
      }

      this.seq = new DERSequence(var2);
   }

   /** @deprecated */
   public ExtendedKeyUsage(Vector var1) {
      ASN1EncodableVector var2 = new ASN1EncodableVector(var1.size());
      Enumeration var3 = var1.elements();

      while (var3.hasMoreElements()) {
         KeyPurposeId var4 = KeyPurposeId.getInstance(var3.nextElement());
         var2.add(var4);
         this.usageTable.put(var4, var4);
      }

      this.seq = new DERSequence(var2);
   }

   public boolean hasKeyPurposeId(KeyPurposeId var1) {
      return this.usageTable.get(var1) != null;
   }

   public KeyPurposeId[] getUsages() {
      KeyPurposeId[] var1 = new KeyPurposeId[this.seq.size()];
      int var2 = 0;
      Enumeration var3 = this.seq.getObjects();

      while (var3.hasMoreElements()) {
         var1[var2++] = KeyPurposeId.getInstance(var3.nextElement());
      }

      return var1;
   }

   public int size() {
      return this.usageTable.size();
   }

   @Override
   public ASN1Primitive toASN1Primitive() {
      return this.seq;
   }
}
