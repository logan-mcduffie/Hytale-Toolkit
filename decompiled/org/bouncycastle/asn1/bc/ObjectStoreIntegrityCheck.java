package org.bouncycastle.asn1.bc;

import java.io.IOException;
import org.bouncycastle.asn1.ASN1Choice;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.ASN1TaggedObject;
import org.bouncycastle.asn1.DERTaggedObject;

public class ObjectStoreIntegrityCheck extends ASN1Object implements ASN1Choice {
   public static final int PBKD_MAC_CHECK = 0;
   public static final int SIG_CHECK = 1;
   private final int type;
   private final ASN1Object integrityCheck;

   public ObjectStoreIntegrityCheck(PbkdMacIntegrityCheck var1) {
      this((ASN1Encodable)var1);
   }

   public ObjectStoreIntegrityCheck(SignatureCheck var1) {
      this(new DERTaggedObject(0, var1));
   }

   private ObjectStoreIntegrityCheck(ASN1Encodable var1) {
      if (!(var1 instanceof ASN1Sequence) && !(var1 instanceof PbkdMacIntegrityCheck)) {
         if (!(var1 instanceof ASN1TaggedObject)) {
            throw new IllegalArgumentException("Unknown check object in integrity check.");
         }

         this.type = 1;
         this.integrityCheck = SignatureCheck.getInstance(((ASN1TaggedObject)var1).getExplicitBaseObject());
      } else {
         this.type = 0;
         this.integrityCheck = PbkdMacIntegrityCheck.getInstance(var1);
      }
   }

   public static ObjectStoreIntegrityCheck getInstance(Object var0) {
      if (var0 instanceof ObjectStoreIntegrityCheck) {
         return (ObjectStoreIntegrityCheck)var0;
      } else if (var0 instanceof byte[]) {
         try {
            return new ObjectStoreIntegrityCheck(ASN1Primitive.fromByteArray((byte[])var0));
         } catch (IOException var2) {
            throw new IllegalArgumentException("Unable to parse integrity check details.");
         }
      } else {
         return var0 != null ? new ObjectStoreIntegrityCheck((ASN1Encodable)var0) : null;
      }
   }

   public int getType() {
      return this.type;
   }

   public ASN1Object getIntegrityCheck() {
      return this.integrityCheck;
   }

   @Override
   public ASN1Primitive toASN1Primitive() {
      return (ASN1Primitive)(this.integrityCheck instanceof SignatureCheck
         ? new DERTaggedObject(0, this.integrityCheck)
         : this.integrityCheck.toASN1Primitive());
   }
}
