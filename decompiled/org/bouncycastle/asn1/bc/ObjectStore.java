package org.bouncycastle.asn1.bc;

import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.DERSequence;

public class ObjectStore extends ASN1Object {
   private final ASN1Encodable storeData;
   private final ObjectStoreIntegrityCheck integrityCheck;

   public ObjectStore(ObjectStoreData var1, ObjectStoreIntegrityCheck var2) {
      this.storeData = var1;
      this.integrityCheck = var2;
   }

   public ObjectStore(EncryptedObjectStoreData var1, ObjectStoreIntegrityCheck var2) {
      this.storeData = var1;
      this.integrityCheck = var2;
   }

   private ObjectStore(ASN1Sequence var1) {
      if (var1.size() != 2) {
         throw new IllegalArgumentException("malformed sequence");
      } else {
         ASN1Encodable var2 = var1.getObjectAt(0);
         if (var2 instanceof EncryptedObjectStoreData) {
            this.storeData = var2;
         } else if (var2 instanceof ObjectStoreData) {
            this.storeData = var2;
         } else {
            ASN1Sequence var3 = ASN1Sequence.getInstance(var2);
            if (var3.size() == 2) {
               this.storeData = EncryptedObjectStoreData.getInstance(var3);
            } else {
               this.storeData = ObjectStoreData.getInstance(var3);
            }
         }

         this.integrityCheck = ObjectStoreIntegrityCheck.getInstance(var1.getObjectAt(1));
      }
   }

   public static ObjectStore getInstance(Object var0) {
      if (var0 instanceof ObjectStore) {
         return (ObjectStore)var0;
      } else {
         return var0 != null ? new ObjectStore(ASN1Sequence.getInstance(var0)) : null;
      }
   }

   public ObjectStoreIntegrityCheck getIntegrityCheck() {
      return this.integrityCheck;
   }

   public ASN1Encodable getStoreData() {
      return this.storeData;
   }

   @Override
   public ASN1Primitive toASN1Primitive() {
      return new DERSequence(this.storeData, this.integrityCheck);
   }
}
