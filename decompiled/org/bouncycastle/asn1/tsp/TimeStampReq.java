package org.bouncycastle.asn1.tsp;

import org.bouncycastle.asn1.ASN1Boolean;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.ASN1TaggedObject;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.DERTaggedObject;
import org.bouncycastle.asn1.x509.Extensions;

public class TimeStampReq extends ASN1Object {
   ASN1Integer version;
   MessageImprint messageImprint;
   ASN1ObjectIdentifier tsaPolicy;
   ASN1Integer nonce;
   ASN1Boolean certReq;
   Extensions extensions;

   public static TimeStampReq getInstance(Object var0) {
      if (var0 instanceof TimeStampReq) {
         return (TimeStampReq)var0;
      } else {
         return var0 != null ? new TimeStampReq(ASN1Sequence.getInstance(var0)) : null;
      }
   }

   private TimeStampReq(ASN1Sequence var1) {
      int var2 = var1.size();
      int var3 = 0;
      this.version = ASN1Integer.getInstance(var1.getObjectAt(var3));
      this.messageImprint = MessageImprint.getInstance(var1.getObjectAt(++var3));

      for (int var4 = ++var3; var4 < var2; var4++) {
         if (var1.getObjectAt(var4) instanceof ASN1ObjectIdentifier) {
            this.checkOption(this.tsaPolicy, var4, 2);
            this.tsaPolicy = ASN1ObjectIdentifier.getInstance(var1.getObjectAt(var4));
         } else if (var1.getObjectAt(var4) instanceof ASN1Integer) {
            this.checkOption(this.nonce, var4, 3);
            this.nonce = ASN1Integer.getInstance(var1.getObjectAt(var4));
         } else if (var1.getObjectAt(var4) instanceof ASN1Boolean) {
            this.checkOption(this.certReq, var4, 4);
            this.certReq = ASN1Boolean.getInstance(var1.getObjectAt(var4));
         } else {
            if (!(var1.getObjectAt(var4) instanceof ASN1TaggedObject)) {
               throw new IllegalArgumentException("unidentified structure in sequence");
            }

            this.checkOption(this.extensions, var4, 5);
            ASN1TaggedObject var5 = (ASN1TaggedObject)var1.getObjectAt(var4);
            if (var5.getTagNo() == 0) {
               this.extensions = Extensions.getInstance(var5, false);
            }
         }
      }
   }

   private void checkOption(Object var1, int var2, int var3) {
      if (var1 != null || var2 > var3) {
         throw new IllegalArgumentException("badly placed optional in sequence");
      }
   }

   public TimeStampReq(MessageImprint var1, ASN1ObjectIdentifier var2, ASN1Integer var3, ASN1Boolean var4, Extensions var5) {
      this.version = new ASN1Integer(1L);
      this.messageImprint = var1;
      this.tsaPolicy = var2;
      this.nonce = var3;
      this.certReq = var4;
      this.extensions = var5;
   }

   public ASN1Integer getVersion() {
      return this.version;
   }

   public MessageImprint getMessageImprint() {
      return this.messageImprint;
   }

   public ASN1ObjectIdentifier getReqPolicy() {
      return this.tsaPolicy;
   }

   public ASN1Integer getNonce() {
      return this.nonce;
   }

   public ASN1Boolean getCertReq() {
      return this.certReq == null ? ASN1Boolean.FALSE : this.certReq;
   }

   public Extensions getExtensions() {
      return this.extensions;
   }

   @Override
   public ASN1Primitive toASN1Primitive() {
      ASN1EncodableVector var1 = new ASN1EncodableVector(6);
      var1.add(this.version);
      var1.add(this.messageImprint);
      if (this.tsaPolicy != null) {
         var1.add(this.tsaPolicy);
      }

      if (this.nonce != null) {
         var1.add(this.nonce);
      }

      if (this.certReq != null && this.certReq.isTrue()) {
         var1.add(this.certReq);
      }

      if (this.extensions != null) {
         var1.add(new DERTaggedObject(false, 0, this.extensions));
      }

      return new DERSequence(var1);
   }
}
