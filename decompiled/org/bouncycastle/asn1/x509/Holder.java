package org.bouncycastle.asn1.x509;

import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.ASN1TaggedObject;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.DERTaggedObject;

public class Holder extends ASN1Object {
   public static final int V1_CERTIFICATE_HOLDER = 0;
   public static final int V2_CERTIFICATE_HOLDER = 1;
   IssuerSerial baseCertificateID;
   GeneralNames entityName;
   ObjectDigestInfo objectDigestInfo;
   private int version = 1;

   public static Holder getInstance(Object var0) {
      if (var0 instanceof Holder) {
         return (Holder)var0;
      } else if (var0 instanceof ASN1TaggedObject) {
         return new Holder(ASN1TaggedObject.getInstance(var0));
      } else {
         return var0 != null ? new Holder(ASN1Sequence.getInstance(var0)) : null;
      }
   }

   private Holder(ASN1TaggedObject var1) {
      switch (var1.getTagNo()) {
         case 0:
            this.baseCertificateID = IssuerSerial.getInstance(var1, true);
            break;
         case 1:
            this.entityName = GeneralNames.getInstance(var1, true);
            break;
         default:
            throw new IllegalArgumentException("unknown tag in Holder");
      }

      this.version = 0;
   }

   private Holder(ASN1Sequence var1) {
      if (var1.size() > 3) {
         throw new IllegalArgumentException("Bad sequence size: " + var1.size());
      } else {
         for (int var2 = 0; var2 != var1.size(); var2++) {
            ASN1TaggedObject var3 = ASN1TaggedObject.getInstance(var1.getObjectAt(var2));
            switch (var3.getTagNo()) {
               case 0:
                  this.baseCertificateID = IssuerSerial.getInstance(var3, false);
                  break;
               case 1:
                  this.entityName = GeneralNames.getInstance(var3, false);
                  break;
               case 2:
                  this.objectDigestInfo = ObjectDigestInfo.getInstance(var3, false);
                  break;
               default:
                  throw new IllegalArgumentException("unknown tag in Holder");
            }
         }

         this.version = 1;
      }
   }

   public Holder(IssuerSerial var1) {
      this(var1, 1);
   }

   public Holder(IssuerSerial var1, int var2) {
      this.baseCertificateID = var1;
      this.version = var2;
   }

   public int getVersion() {
      return this.version;
   }

   public Holder(GeneralNames var1) {
      this(var1, 1);
   }

   public Holder(GeneralNames var1, int var2) {
      this.entityName = var1;
      this.version = var2;
   }

   public Holder(ObjectDigestInfo var1) {
      this.objectDigestInfo = var1;
   }

   public IssuerSerial getBaseCertificateID() {
      return this.baseCertificateID;
   }

   public GeneralNames getEntityName() {
      return this.entityName;
   }

   public ObjectDigestInfo getObjectDigestInfo() {
      return this.objectDigestInfo;
   }

   @Override
   public ASN1Primitive toASN1Primitive() {
      if (this.version == 1) {
         ASN1EncodableVector var1 = new ASN1EncodableVector(3);
         if (this.baseCertificateID != null) {
            var1.add(new DERTaggedObject(false, 0, this.baseCertificateID));
         }

         if (this.entityName != null) {
            var1.add(new DERTaggedObject(false, 1, this.entityName));
         }

         if (this.objectDigestInfo != null) {
            var1.add(new DERTaggedObject(false, 2, this.objectDigestInfo));
         }

         return new DERSequence(var1);
      } else {
         return this.entityName != null ? new DERTaggedObject(true, 1, this.entityName) : new DERTaggedObject(true, 0, this.baseCertificateID);
      }
   }
}
