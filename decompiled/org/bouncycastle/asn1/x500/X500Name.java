package org.bouncycastle.asn1.x500;

import org.bouncycastle.asn1.ASN1Choice;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.ASN1TaggedObject;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.x500.style.BCStyle;

public class X500Name extends ASN1Object implements ASN1Choice {
   private static X500NameStyle defaultStyle = BCStyle.INSTANCE;
   private boolean isHashCodeCalculated;
   private int hashCodeValue;
   private X500NameStyle style;
   private RDN[] rdns;
   private DERSequence rdnSeq;

   /** @deprecated */
   public X500Name(X500NameStyle var1, X500Name var2) {
      this.style = var1;
      this.rdns = var2.rdns;
      this.rdnSeq = var2.rdnSeq;
   }

   public static X500Name getInstance(ASN1TaggedObject var0, boolean var1) {
      return getInstance(ASN1Sequence.getInstance(var0, true));
   }

   public static X500Name getInstance(Object var0) {
      if (var0 instanceof X500Name) {
         return (X500Name)var0;
      } else {
         return var0 != null ? new X500Name(ASN1Sequence.getInstance(var0)) : null;
      }
   }

   public static X500Name getInstance(X500NameStyle var0, Object var1) {
      if (var1 instanceof X500Name) {
         return new X500Name(var0, (X500Name)var1);
      } else {
         return var1 != null ? new X500Name(var0, ASN1Sequence.getInstance(var1)) : null;
      }
   }

   private X500Name(ASN1Sequence var1) {
      this(defaultStyle, var1);
   }

   private X500Name(X500NameStyle var1, ASN1Sequence var2) {
      int var3 = var2.size();
      this.style = var1;
      this.rdns = new RDN[var3];
      boolean var4 = true;

      for (int var5 = 0; var5 < var3; var5++) {
         ASN1Encodable var6 = var2.getObjectAt(var5);
         RDN var7 = RDN.getInstance(var6);
         var4 &= var7 == var6;
         this.rdns[var5] = var7;
      }

      if (var4) {
         this.rdnSeq = DERSequence.convert(var2);
      } else {
         this.rdnSeq = new DERSequence(this.rdns);
      }
   }

   public X500Name(RDN[] var1) {
      this(defaultStyle, var1);
   }

   public X500Name(X500NameStyle var1, RDN[] var2) {
      this.style = var1;
      this.rdns = (RDN[])var2.clone();
      this.rdnSeq = new DERSequence(this.rdns);
   }

   public X500Name(String var1) {
      this(defaultStyle, var1);
   }

   public X500Name(X500NameStyle var1, String var2) {
      this(var1.fromString(var2));
      this.style = var1;
   }

   public RDN[] getRDNs() {
      return (RDN[])this.rdns.clone();
   }

   public ASN1ObjectIdentifier[] getAttributeTypes() {
      int var1 = this.rdns.length;
      int var2 = 0;

      for (int var3 = 0; var3 < var1; var3++) {
         RDN var4 = this.rdns[var3];
         var2 += var4.size();
      }

      ASN1ObjectIdentifier[] var7 = new ASN1ObjectIdentifier[var2];
      int var8 = 0;

      for (int var5 = 0; var5 < var1; var5++) {
         RDN var6 = this.rdns[var5];
         var8 += var6.collectAttributeTypes(var7, var8);
      }

      return var7;
   }

   public RDN[] getRDNs(ASN1ObjectIdentifier var1) {
      RDN[] var2 = new RDN[this.rdns.length];
      int var3 = 0;

      for (int var4 = 0; var4 != this.rdns.length; var4++) {
         RDN var5 = this.rdns[var4];
         if (var5.containsAttributeType(var1)) {
            var2[var3++] = var5;
         }
      }

      if (var3 < var2.length) {
         RDN[] var6 = new RDN[var3];
         System.arraycopy(var2, 0, var6, 0, var6.length);
         var2 = var6;
      }

      return var2;
   }

   public int size() {
      return this.rdns.length;
   }

   @Override
   public ASN1Primitive toASN1Primitive() {
      return this.rdnSeq;
   }

   @Override
   public int hashCode() {
      if (this.isHashCodeCalculated) {
         return this.hashCodeValue;
      } else {
         this.isHashCodeCalculated = true;
         this.hashCodeValue = this.style.calculateHashCode(this);
         return this.hashCodeValue;
      }
   }

   @Override
   public boolean equals(Object var1) {
      if (var1 == this) {
         return true;
      } else if (!(var1 instanceof X500Name) && !(var1 instanceof ASN1Sequence)) {
         return false;
      } else {
         ASN1Primitive var2 = ((ASN1Encodable)var1).toASN1Primitive();
         if (this.toASN1Primitive().equals(var2)) {
            return true;
         } else {
            try {
               return this.style.areEqual(this, getInstance(var1));
            } catch (Exception var4) {
               return false;
            }
         }
      }
   }

   @Override
   public String toString() {
      return this.style.toString(this);
   }

   public static void setDefaultStyle(X500NameStyle var0) {
      if (var0 == null) {
         throw new NullPointerException("cannot set style to null");
      } else {
         defaultStyle = var0;
      }
   }

   public static X500NameStyle getDefaultStyle() {
      return defaultStyle;
   }
}
