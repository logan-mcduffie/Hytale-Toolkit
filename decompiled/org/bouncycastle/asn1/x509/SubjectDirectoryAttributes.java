package org.bouncycastle.asn1.x509;

import java.util.Enumeration;
import java.util.Vector;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.DERSequence;

public class SubjectDirectoryAttributes extends ASN1Object {
   private Vector attributes = new Vector();

   public static SubjectDirectoryAttributes getInstance(Object var0) {
      if (var0 instanceof SubjectDirectoryAttributes) {
         return (SubjectDirectoryAttributes)var0;
      } else {
         return var0 != null ? new SubjectDirectoryAttributes(ASN1Sequence.getInstance(var0)) : null;
      }
   }

   private SubjectDirectoryAttributes(ASN1Sequence var1) {
      Enumeration var2 = var1.getObjects();

      while (var2.hasMoreElements()) {
         ASN1Sequence var3 = ASN1Sequence.getInstance(var2.nextElement());
         this.attributes.addElement(Attribute.getInstance(var3));
      }
   }

   public SubjectDirectoryAttributes(Vector var1) {
      Enumeration var2 = var1.elements();

      while (var2.hasMoreElements()) {
         this.attributes.addElement(var2.nextElement());
      }
   }

   @Override
   public ASN1Primitive toASN1Primitive() {
      ASN1EncodableVector var1 = new ASN1EncodableVector(this.attributes.size());
      Enumeration var2 = this.attributes.elements();

      while (var2.hasMoreElements()) {
         var1.add((Attribute)var2.nextElement());
      }

      return new DERSequence(var1);
   }

   public Vector getAttributes() {
      return this.attributes;
   }
}
