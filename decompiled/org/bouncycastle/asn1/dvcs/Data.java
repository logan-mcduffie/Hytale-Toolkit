package org.bouncycastle.asn1.dvcs;

import org.bouncycastle.asn1.ASN1Choice;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.ASN1TaggedObject;
import org.bouncycastle.asn1.DEROctetString;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.DERTaggedObject;
import org.bouncycastle.asn1.x509.DigestInfo;

public class Data extends ASN1Object implements ASN1Choice {
   private ASN1OctetString message;
   private DigestInfo messageImprint;
   private ASN1Sequence certs;

   public Data(byte[] var1) {
      this.message = new DEROctetString(var1);
   }

   public Data(ASN1OctetString var1) {
      this.message = var1;
   }

   public Data(DigestInfo var1) {
      this.messageImprint = var1;
   }

   public Data(TargetEtcChain var1) {
      this.certs = new DERSequence(var1);
   }

   public Data(TargetEtcChain[] var1) {
      this.certs = new DERSequence(var1);
   }

   private Data(ASN1Sequence var1) {
      this.certs = var1;
   }

   public static Data getInstance(Object var0) {
      if (var0 instanceof Data) {
         return (Data)var0;
      } else if (var0 instanceof ASN1OctetString) {
         return new Data((ASN1OctetString)var0);
      } else if (var0 instanceof ASN1Sequence) {
         return new Data(DigestInfo.getInstance(var0));
      } else if (var0 instanceof ASN1TaggedObject) {
         return new Data(ASN1Sequence.getInstance((ASN1TaggedObject)var0, false));
      } else {
         throw new IllegalArgumentException("Unknown object submitted to getInstance: " + var0.getClass().getName());
      }
   }

   public static Data getInstance(ASN1TaggedObject var0, boolean var1) {
      return getInstance(var0.getExplicitBaseObject());
   }

   @Override
   public ASN1Primitive toASN1Primitive() {
      if (this.message != null) {
         return this.message.toASN1Primitive();
      } else {
         return (ASN1Primitive)(this.messageImprint != null ? this.messageImprint.toASN1Primitive() : new DERTaggedObject(false, 0, this.certs));
      }
   }

   @Override
   public String toString() {
      if (this.message != null) {
         return "Data {\n" + this.message + "}\n";
      } else {
         return this.messageImprint != null ? "Data {\n" + this.messageImprint + "}\n" : "Data {\n" + this.certs + "}\n";
      }
   }

   public ASN1OctetString getMessage() {
      return this.message;
   }

   public DigestInfo getMessageImprint() {
      return this.messageImprint;
   }

   public TargetEtcChain[] getCerts() {
      if (this.certs == null) {
         return null;
      } else {
         TargetEtcChain[] var1 = new TargetEtcChain[this.certs.size()];

         for (int var2 = 0; var2 != var1.length; var2++) {
            var1[var2] = TargetEtcChain.getInstance(this.certs.getObjectAt(var2));
         }

         return var1;
      }
   }
}
