package org.bouncycastle.asn1.cms;

import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.ASN1TaggedObject;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.DERTaggedObject;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;

public class CMSAlgorithmProtection extends ASN1Object {
   public static final int SIGNATURE = 1;
   public static final int MAC = 2;
   private final AlgorithmIdentifier digestAlgorithm;
   private final AlgorithmIdentifier signatureAlgorithm;
   private final AlgorithmIdentifier macAlgorithm;

   public CMSAlgorithmProtection(AlgorithmIdentifier var1, int var2, AlgorithmIdentifier var3) {
      if (var1 != null && var3 != null) {
         this.digestAlgorithm = var1;
         if (var2 == 1) {
            this.signatureAlgorithm = var3;
            this.macAlgorithm = null;
         } else {
            if (var2 != 2) {
               throw new IllegalArgumentException("Unknown type: " + var2);
            }

            this.signatureAlgorithm = null;
            this.macAlgorithm = var3;
         }
      } else {
         throw new NullPointerException("AlgorithmIdentifiers cannot be null");
      }
   }

   private CMSAlgorithmProtection(ASN1Sequence var1) {
      if (var1.size() != 2) {
         throw new IllegalArgumentException("Sequence wrong size: One of signatureAlgorithm or macAlgorithm must be present");
      } else {
         this.digestAlgorithm = AlgorithmIdentifier.getInstance(var1.getObjectAt(0));
         ASN1TaggedObject var2 = ASN1TaggedObject.getInstance(var1.getObjectAt(1));
         if (var2.getTagNo() == 1) {
            this.signatureAlgorithm = AlgorithmIdentifier.getInstance(var2, false);
            this.macAlgorithm = null;
         } else {
            if (var2.getTagNo() != 2) {
               throw new IllegalArgumentException("Unknown tag found: " + var2.getTagNo());
            }

            this.signatureAlgorithm = null;
            this.macAlgorithm = AlgorithmIdentifier.getInstance(var2, false);
         }
      }
   }

   public static CMSAlgorithmProtection getInstance(Object var0) {
      if (var0 instanceof CMSAlgorithmProtection) {
         return (CMSAlgorithmProtection)var0;
      } else {
         return var0 != null ? new CMSAlgorithmProtection(ASN1Sequence.getInstance(var0)) : null;
      }
   }

   public AlgorithmIdentifier getDigestAlgorithm() {
      return this.digestAlgorithm;
   }

   public AlgorithmIdentifier getMacAlgorithm() {
      return this.macAlgorithm;
   }

   public AlgorithmIdentifier getSignatureAlgorithm() {
      return this.signatureAlgorithm;
   }

   @Override
   public ASN1Primitive toASN1Primitive() {
      ASN1EncodableVector var1 = new ASN1EncodableVector(3);
      var1.add(this.digestAlgorithm);
      if (this.signatureAlgorithm != null) {
         var1.add(new DERTaggedObject(false, 1, this.signatureAlgorithm));
      }

      if (this.macAlgorithm != null) {
         var1.add(new DERTaggedObject(false, 2, this.macAlgorithm));
      }

      return new DERSequence(var1);
   }
}
