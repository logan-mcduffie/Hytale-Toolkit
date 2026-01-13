package org.bouncycastle.asn1.tsp;

import java.util.Enumeration;
import org.bouncycastle.asn1.ASN1Boolean;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1GeneralizedTime;
import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.ASN1TaggedObject;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.DERTaggedObject;
import org.bouncycastle.asn1.x509.Extensions;
import org.bouncycastle.asn1.x509.GeneralName;

public class TSTInfo extends ASN1Object {
   private ASN1Integer version;
   private ASN1ObjectIdentifier tsaPolicyId;
   private MessageImprint messageImprint;
   private ASN1Integer serialNumber;
   private ASN1GeneralizedTime genTime;
   private Accuracy accuracy;
   private ASN1Boolean ordering;
   private ASN1Integer nonce;
   private GeneralName tsa;
   private Extensions extensions;

   public static TSTInfo getInstance(Object var0) {
      if (var0 instanceof TSTInfo) {
         return (TSTInfo)var0;
      } else {
         return var0 != null ? new TSTInfo(ASN1Sequence.getInstance(var0)) : null;
      }
   }

   private TSTInfo(ASN1Sequence var1) {
      Enumeration var2 = var1.getObjects();
      this.version = ASN1Integer.getInstance(var2.nextElement());
      this.tsaPolicyId = ASN1ObjectIdentifier.getInstance(var2.nextElement());
      this.messageImprint = MessageImprint.getInstance(var2.nextElement());
      this.serialNumber = ASN1Integer.getInstance(var2.nextElement());
      this.genTime = ASN1GeneralizedTime.getInstance(var2.nextElement());
      this.ordering = ASN1Boolean.getInstance(false);

      while (var2.hasMoreElements()) {
         ASN1Object var3 = (ASN1Object)var2.nextElement();
         if (var3 instanceof ASN1TaggedObject) {
            ASN1TaggedObject var4 = (ASN1TaggedObject)var3;
            switch (var4.getTagNo()) {
               case 0:
                  this.tsa = GeneralName.getInstance(var4, true);
                  break;
               case 1:
                  this.extensions = Extensions.getInstance(var4, false);
                  break;
               default:
                  throw new IllegalArgumentException("Unknown tag value " + var4.getTagNo());
            }
         } else if (var3 instanceof ASN1Sequence || var3 instanceof Accuracy) {
            this.accuracy = Accuracy.getInstance(var3);
         } else if (var3 instanceof ASN1Boolean) {
            this.ordering = ASN1Boolean.getInstance(var3);
         } else if (var3 instanceof ASN1Integer) {
            this.nonce = ASN1Integer.getInstance(var3);
         }
      }
   }

   public TSTInfo(
      ASN1ObjectIdentifier var1,
      MessageImprint var2,
      ASN1Integer var3,
      ASN1GeneralizedTime var4,
      Accuracy var5,
      ASN1Boolean var6,
      ASN1Integer var7,
      GeneralName var8,
      Extensions var9
   ) {
      this.version = new ASN1Integer(1L);
      this.tsaPolicyId = var1;
      this.messageImprint = var2;
      this.serialNumber = var3;
      this.genTime = var4;
      this.accuracy = var5;
      this.ordering = var6;
      this.nonce = var7;
      this.tsa = var8;
      this.extensions = var9;
   }

   public ASN1Integer getVersion() {
      return this.version;
   }

   public MessageImprint getMessageImprint() {
      return this.messageImprint;
   }

   public ASN1ObjectIdentifier getPolicy() {
      return this.tsaPolicyId;
   }

   public ASN1Integer getSerialNumber() {
      return this.serialNumber;
   }

   public Accuracy getAccuracy() {
      return this.accuracy;
   }

   public ASN1GeneralizedTime getGenTime() {
      return this.genTime;
   }

   public ASN1Boolean getOrdering() {
      return this.ordering;
   }

   public ASN1Integer getNonce() {
      return this.nonce;
   }

   public GeneralName getTsa() {
      return this.tsa;
   }

   public Extensions getExtensions() {
      return this.extensions;
   }

   @Override
   public ASN1Primitive toASN1Primitive() {
      ASN1EncodableVector var1 = new ASN1EncodableVector(10);
      var1.add(this.version);
      var1.add(this.tsaPolicyId);
      var1.add(this.messageImprint);
      var1.add(this.serialNumber);
      var1.add(this.genTime);
      if (this.accuracy != null) {
         var1.add(this.accuracy);
      }

      if (this.ordering != null && this.ordering.isTrue()) {
         var1.add(this.ordering);
      }

      if (this.nonce != null) {
         var1.add(this.nonce);
      }

      if (this.tsa != null) {
         var1.add(new DERTaggedObject(true, 0, this.tsa));
      }

      if (this.extensions != null) {
         var1.add(new DERTaggedObject(false, 1, this.extensions));
      }

      return new DERSequence(var1);
   }
}
