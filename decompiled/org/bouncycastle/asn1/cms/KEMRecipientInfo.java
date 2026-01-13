package org.bouncycastle.asn1.cms;

import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.ASN1TaggedObject;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.DERTaggedObject;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;

public class KEMRecipientInfo extends ASN1Object {
   private final ASN1Integer cmsVersion;
   private final RecipientIdentifier rid;
   private final AlgorithmIdentifier kem;
   private final ASN1OctetString kemct;
   private final AlgorithmIdentifier kdf;
   private final ASN1Integer kekLength;
   private final ASN1OctetString ukm;
   private final AlgorithmIdentifier wrap;
   private final ASN1OctetString encryptedKey;

   public KEMRecipientInfo(
      RecipientIdentifier var1,
      AlgorithmIdentifier var2,
      ASN1OctetString var3,
      AlgorithmIdentifier var4,
      ASN1Integer var5,
      ASN1OctetString var6,
      AlgorithmIdentifier var7,
      ASN1OctetString var8
   ) {
      if (var2 == null) {
         throw new NullPointerException("kem cannot be null");
      } else if (var7 == null) {
         throw new NullPointerException("wrap cannot be null");
      } else if (var5.intValueExact() > 65535) {
         throw new IllegalArgumentException("kekLength must be <= 65535");
      } else {
         this.cmsVersion = new ASN1Integer(0L);
         this.rid = var1;
         this.kem = var2;
         this.kemct = var3;
         this.kdf = var4;
         this.kekLength = var5;
         this.ukm = var6;
         this.wrap = var7;
         this.encryptedKey = var8;
      }
   }

   public static KEMRecipientInfo getInstance(Object var0) {
      if (var0 instanceof KEMRecipientInfo) {
         return (KEMRecipientInfo)var0;
      } else {
         return var0 != null ? new KEMRecipientInfo(ASN1Sequence.getInstance(var0)) : null;
      }
   }

   private KEMRecipientInfo(ASN1Sequence var1) {
      if (var1.size() >= 8 && var1.size() <= 9) {
         this.cmsVersion = ASN1Integer.getInstance(var1.getObjectAt(0));
         this.rid = RecipientIdentifier.getInstance(var1.getObjectAt(1));
         this.kem = AlgorithmIdentifier.getInstance(var1.getObjectAt(2));
         this.kemct = ASN1OctetString.getInstance(var1.getObjectAt(3));
         this.kdf = AlgorithmIdentifier.getInstance(var1.getObjectAt(4));
         this.kekLength = ASN1Integer.getInstance(var1.getObjectAt(5));
         if (this.kekLength.intValueExact() > 65535) {
            throw new IllegalArgumentException("kekLength must be <= 65535");
         } else {
            int var2 = 6;
            if (var1.getObjectAt(6) instanceof ASN1TaggedObject) {
               this.ukm = ASN1OctetString.getInstance(ASN1TaggedObject.getInstance(var1.getObjectAt(var2++)), true);
            } else {
               this.ukm = null;
            }

            this.wrap = AlgorithmIdentifier.getInstance(var1.getObjectAt(var2++));
            this.encryptedKey = ASN1OctetString.getInstance(var1.getObjectAt(var2++));
         }
      } else {
         throw new IllegalArgumentException("bad sequence size: " + var1.size());
      }
   }

   public RecipientIdentifier getRecipientIdentifier() {
      return this.rid;
   }

   public AlgorithmIdentifier getKem() {
      return this.kem;
   }

   public ASN1OctetString getKemct() {
      return this.kemct;
   }

   public AlgorithmIdentifier getKdf() {
      return this.kdf;
   }

   public AlgorithmIdentifier getWrap() {
      return this.wrap;
   }

   public byte[] getUkm() {
      return this.ukm == null ? null : this.ukm.getOctets();
   }

   public ASN1OctetString getEncryptedKey() {
      return this.encryptedKey;
   }

   @Override
   public ASN1Primitive toASN1Primitive() {
      ASN1EncodableVector var1 = new ASN1EncodableVector();
      var1.add(this.cmsVersion);
      var1.add(this.rid);
      var1.add(this.kem);
      var1.add(this.kemct);
      var1.add(this.kdf);
      var1.add(this.kekLength);
      if (this.ukm != null) {
         var1.add(new DERTaggedObject(true, 0, this.ukm));
      }

      var1.add(this.wrap);
      var1.add(this.encryptedKey);
      return new DERSequence(var1);
   }
}
