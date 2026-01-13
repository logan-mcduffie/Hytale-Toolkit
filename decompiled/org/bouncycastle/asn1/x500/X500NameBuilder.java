package org.bouncycastle.asn1.x500;

import java.util.Vector;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.x500.style.BCStyle;

public class X500NameBuilder {
   private X500NameStyle template;
   private Vector rdns = new Vector();

   public X500NameBuilder() {
      this(BCStyle.INSTANCE);
   }

   public X500NameBuilder(X500NameStyle var1) {
      this.template = var1;
   }

   public X500NameBuilder addRDN(ASN1ObjectIdentifier var1, String var2) {
      this.addRDN(var1, this.template.stringToValue(var1, var2));
      return this;
   }

   public X500NameBuilder addRDN(ASN1ObjectIdentifier var1, ASN1Encodable var2) {
      this.rdns.addElement(new RDN(var1, var2));
      return this;
   }

   public X500NameBuilder addRDN(AttributeTypeAndValue var1) {
      this.rdns.addElement(new RDN(var1));
      return this;
   }

   public X500NameBuilder addMultiValuedRDN(ASN1ObjectIdentifier[] var1, String[] var2) {
      ASN1Encodable[] var3 = new ASN1Encodable[var2.length];

      for (int var4 = 0; var4 != var3.length; var4++) {
         var3[var4] = this.template.stringToValue(var1[var4], var2[var4]);
      }

      return this.addMultiValuedRDN(var1, var3);
   }

   public X500NameBuilder addMultiValuedRDN(ASN1ObjectIdentifier[] var1, ASN1Encodable[] var2) {
      AttributeTypeAndValue[] var3 = new AttributeTypeAndValue[var1.length];

      for (int var4 = 0; var4 != var1.length; var4++) {
         var3[var4] = new AttributeTypeAndValue(var1[var4], var2[var4]);
      }

      return this.addMultiValuedRDN(var3);
   }

   public X500NameBuilder addMultiValuedRDN(AttributeTypeAndValue[] var1) {
      this.rdns.addElement(new RDN(var1));
      return this;
   }

   public X500Name build() {
      RDN[] var1 = new RDN[this.rdns.size()];

      for (int var2 = 0; var2 != var1.length; var2++) {
         var1[var2] = (RDN)this.rdns.elementAt(var2);
      }

      return new X500Name(this.template, var1);
   }
}
