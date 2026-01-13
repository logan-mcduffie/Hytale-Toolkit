package org.bouncycastle.asn1.est;

import java.io.IOException;
import org.bouncycastle.asn1.ASN1Choice;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.pkcs.Attribute;

public class AttrOrOID extends ASN1Object implements ASN1Choice {
   private final ASN1ObjectIdentifier oid;
   private final Attribute attribute;

   public AttrOrOID(ASN1ObjectIdentifier var1) {
      this.oid = var1;
      this.attribute = null;
   }

   public AttrOrOID(Attribute var1) {
      this.oid = null;
      this.attribute = var1;
   }

   public static AttrOrOID getInstance(Object var0) {
      if (var0 instanceof AttrOrOID) {
         return (AttrOrOID)var0;
      } else if (var0 != null) {
         if (var0 instanceof ASN1Encodable) {
            ASN1Primitive var1 = ((ASN1Encodable)var0).toASN1Primitive();
            if (var1 instanceof ASN1ObjectIdentifier) {
               return new AttrOrOID(ASN1ObjectIdentifier.getInstance(var1));
            }

            if (var1 instanceof ASN1Sequence) {
               return new AttrOrOID(Attribute.getInstance(var1));
            }
         }

         if (var0 instanceof byte[]) {
            try {
               return getInstance(ASN1Primitive.fromByteArray((byte[])var0));
            } catch (IOException var2) {
               throw new IllegalArgumentException("unknown encoding in getInstance()");
            }
         } else {
            throw new IllegalArgumentException("unknown object in getInstance(): " + var0.getClass().getName());
         }
      } else {
         return null;
      }
   }

   public boolean isOid() {
      return this.oid != null;
   }

   public ASN1ObjectIdentifier getOid() {
      return this.oid;
   }

   public Attribute getAttribute() {
      return this.attribute;
   }

   @Override
   public ASN1Primitive toASN1Primitive() {
      return (ASN1Primitive)(this.oid != null ? this.oid : this.attribute.toASN1Primitive());
   }
}
