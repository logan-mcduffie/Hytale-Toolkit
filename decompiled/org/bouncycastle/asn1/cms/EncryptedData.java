package org.bouncycastle.asn1.cms;

import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.ASN1Set;
import org.bouncycastle.asn1.ASN1TaggedObject;
import org.bouncycastle.asn1.BERSequence;
import org.bouncycastle.asn1.BERTaggedObject;

public class EncryptedData extends ASN1Object {
   private ASN1Integer version;
   private EncryptedContentInfo encryptedContentInfo;
   private ASN1Set unprotectedAttrs;

   public static EncryptedData getInstance(Object var0) {
      if (var0 instanceof EncryptedData) {
         return (EncryptedData)var0;
      } else {
         return var0 != null ? new EncryptedData(ASN1Sequence.getInstance(var0)) : null;
      }
   }

   public EncryptedData(EncryptedContentInfo var1) {
      this(var1, null);
   }

   public EncryptedData(EncryptedContentInfo var1, ASN1Set var2) {
      this.version = new ASN1Integer(var2 == null ? 0L : 2L);
      this.encryptedContentInfo = var1;
      this.unprotectedAttrs = var2;
   }

   private EncryptedData(ASN1Sequence var1) {
      this.version = ASN1Integer.getInstance(var1.getObjectAt(0));
      this.encryptedContentInfo = EncryptedContentInfo.getInstance(var1.getObjectAt(1));
      if (var1.size() == 3) {
         this.unprotectedAttrs = ASN1Set.getInstance((ASN1TaggedObject)var1.getObjectAt(2), false);
      }
   }

   public ASN1Integer getVersion() {
      return this.version;
   }

   public EncryptedContentInfo getEncryptedContentInfo() {
      return this.encryptedContentInfo;
   }

   public ASN1Set getUnprotectedAttrs() {
      return this.unprotectedAttrs;
   }

   @Override
   public ASN1Primitive toASN1Primitive() {
      ASN1EncodableVector var1 = new ASN1EncodableVector(3);
      var1.add(this.version);
      var1.add(this.encryptedContentInfo);
      if (this.unprotectedAttrs != null) {
         var1.add(new BERTaggedObject(false, 1, this.unprotectedAttrs));
      }

      return new BERSequence(var1);
   }
}
