package org.bouncycastle.jcajce.provider.asymmetric.util;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1InputStream;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.ASN1OutputStream;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.DERBMPString;
import org.bouncycastle.asn1.pkcs.PKCSObjectIdentifiers;
import org.bouncycastle.jce.interfaces.PKCS12BagAttributeCarrier;

public class PKCS12BagAttributeCarrierImpl implements PKCS12BagAttributeCarrier {
   private Hashtable pkcs12Attributes;
   private Vector pkcs12Ordering;

   PKCS12BagAttributeCarrierImpl(Hashtable var1, Vector var2) {
      this.pkcs12Attributes = var1;
      this.pkcs12Ordering = var2;
   }

   public PKCS12BagAttributeCarrierImpl() {
      this(new Hashtable(), new Vector());
   }

   @Override
   public void setBagAttribute(ASN1ObjectIdentifier var1, ASN1Encodable var2) {
      if (this.pkcs12Attributes.containsKey(var1)) {
         this.pkcs12Attributes.put(var1, var2);
      } else {
         this.pkcs12Attributes.put(var1, var2);
         this.pkcs12Ordering.addElement(var1);
      }
   }

   @Override
   public ASN1Encodable getBagAttribute(ASN1ObjectIdentifier var1) {
      return (ASN1Encodable)this.pkcs12Attributes.get(var1);
   }

   @Override
   public Enumeration getBagAttributeKeys() {
      return this.pkcs12Ordering.elements();
   }

   @Override
   public boolean hasFriendlyName() {
      ASN1Encodable var1 = this.getBagAttribute(PKCSObjectIdentifiers.pkcs_9_at_friendlyName);
      return var1 != null;
   }

   @Override
   public void setFriendlyName(String var1) {
      DERBMPString var2 = new DERBMPString(var1);
      this.setBagAttribute(PKCSObjectIdentifiers.pkcs_9_at_friendlyName, var2);
   }

   int size() {
      return this.pkcs12Ordering.size();
   }

   Hashtable getAttributes() {
      return this.pkcs12Attributes;
   }

   Vector getOrdering() {
      return this.pkcs12Ordering;
   }

   public void writeObject(ObjectOutputStream var1) throws IOException {
      if (this.pkcs12Ordering.size() == 0) {
         var1.writeObject(new Hashtable());
         var1.writeObject(new Vector());
      } else {
         ByteArrayOutputStream var2 = new ByteArrayOutputStream();
         ASN1OutputStream var3 = ASN1OutputStream.create(var2);
         Enumeration var4 = this.getBagAttributeKeys();

         while (var4.hasMoreElements()) {
            ASN1ObjectIdentifier var5 = ASN1ObjectIdentifier.getInstance(var4.nextElement());
            var3.writeObject((ASN1Primitive)var5);
            var3.writeObject((ASN1Encodable)this.pkcs12Attributes.get(var5));
         }

         var1.writeObject(var2.toByteArray());
      }
   }

   public void readObject(ObjectInputStream var1) throws IOException, ClassNotFoundException {
      Object var2 = var1.readObject();
      if (var2 instanceof Hashtable) {
         this.pkcs12Attributes = (Hashtable)var2;
         this.pkcs12Ordering = (Vector)var1.readObject();
      } else {
         ASN1InputStream var3 = new ASN1InputStream((byte[])var2);

         ASN1ObjectIdentifier var4;
         while ((var4 = (ASN1ObjectIdentifier)var3.readObject()) != null) {
            this.setBagAttribute(var4, var3.readObject());
         }
      }
   }
}
