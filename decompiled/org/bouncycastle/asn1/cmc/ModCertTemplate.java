package org.bouncycastle.asn1.cmc;

import org.bouncycastle.asn1.ASN1Boolean;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.crmf.CertTemplate;

public class ModCertTemplate extends ASN1Object {
   private final BodyPartPath pkiDataReference;
   private final BodyPartList certReferences;
   private final boolean replace;
   private final CertTemplate certTemplate;

   public ModCertTemplate(BodyPartPath var1, BodyPartList var2, boolean var3, CertTemplate var4) {
      this.pkiDataReference = var1;
      this.certReferences = var2;
      this.replace = var3;
      this.certTemplate = var4;
   }

   private ModCertTemplate(ASN1Sequence var1) {
      if (var1.size() != 4 && var1.size() != 3) {
         throw new IllegalArgumentException("incorrect sequence size");
      } else {
         this.pkiDataReference = BodyPartPath.getInstance(var1.getObjectAt(0));
         this.certReferences = BodyPartList.getInstance(var1.getObjectAt(1));
         if (var1.size() == 4) {
            this.replace = ASN1Boolean.getInstance(var1.getObjectAt(2)).isTrue();
            this.certTemplate = CertTemplate.getInstance(var1.getObjectAt(3));
         } else {
            this.replace = true;
            this.certTemplate = CertTemplate.getInstance(var1.getObjectAt(2));
         }
      }
   }

   public static ModCertTemplate getInstance(Object var0) {
      if (var0 instanceof ModCertTemplate) {
         return (ModCertTemplate)var0;
      } else {
         return var0 != null ? new ModCertTemplate(ASN1Sequence.getInstance(var0)) : null;
      }
   }

   public BodyPartPath getPkiDataReference() {
      return this.pkiDataReference;
   }

   public BodyPartList getCertReferences() {
      return this.certReferences;
   }

   public boolean isReplacingFields() {
      return this.replace;
   }

   public CertTemplate getCertTemplate() {
      return this.certTemplate;
   }

   @Override
   public ASN1Primitive toASN1Primitive() {
      ASN1EncodableVector var1 = new ASN1EncodableVector(4);
      var1.add(this.pkiDataReference);
      var1.add(this.certReferences);
      if (!this.replace) {
         var1.add(ASN1Boolean.getInstance(this.replace));
      }

      var1.add(this.certTemplate);
      return new DERSequence(var1);
   }
}
