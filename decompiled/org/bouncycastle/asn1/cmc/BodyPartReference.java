package org.bouncycastle.asn1.cmc;

import java.io.IOException;
import org.bouncycastle.asn1.ASN1Choice;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;

public class BodyPartReference extends ASN1Object implements ASN1Choice {
   private final BodyPartID bodyPartID;
   private final BodyPartPath bodyPartPath;

   public BodyPartReference(BodyPartID var1) {
      this.bodyPartID = var1;
      this.bodyPartPath = null;
   }

   public BodyPartReference(BodyPartPath var1) {
      this.bodyPartID = null;
      this.bodyPartPath = var1;
   }

   public static BodyPartReference getInstance(Object var0) {
      if (var0 instanceof BodyPartReference) {
         return (BodyPartReference)var0;
      } else if (var0 != null) {
         if (var0 instanceof ASN1Encodable) {
            ASN1Primitive var1 = ((ASN1Encodable)var0).toASN1Primitive();
            if (var1 instanceof ASN1Integer) {
               return new BodyPartReference(BodyPartID.getInstance(var1));
            }

            if (var1 instanceof ASN1Sequence) {
               return new BodyPartReference(BodyPartPath.getInstance(var1));
            }
         }

         if (var0 instanceof byte[]) {
            try {
               return getInstance(ASN1Primitive.fromByteArray((byte[])var0));
            } catch (IOException var2) {
               throw new IllegalArgumentException("unknown encoding in getInstance()");
            }
         } else {
            throw new IllegalArgumentException("unknown object in getInstance(): " + var0.getClass().getName());
         }
      } else {
         return null;
      }
   }

   public boolean isBodyPartID() {
      return this.bodyPartID != null;
   }

   public BodyPartID getBodyPartID() {
      return this.bodyPartID;
   }

   public BodyPartPath getBodyPartPath() {
      return this.bodyPartPath;
   }

   @Override
   public ASN1Primitive toASN1Primitive() {
      return this.bodyPartID != null ? this.bodyPartID.toASN1Primitive() : this.bodyPartPath.toASN1Primitive();
   }
}
