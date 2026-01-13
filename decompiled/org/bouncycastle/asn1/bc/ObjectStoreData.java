package org.bouncycastle.asn1.bc;

import java.math.BigInteger;
import java.util.Date;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1GeneralizedTime;
import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.ASN1UTF8String;
import org.bouncycastle.asn1.DERGeneralizedTime;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.DERUTF8String;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;

public class ObjectStoreData extends ASN1Object {
   private final BigInteger version;
   private final AlgorithmIdentifier integrityAlgorithm;
   private final ASN1GeneralizedTime creationDate;
   private final ASN1GeneralizedTime lastModifiedDate;
   private final ObjectDataSequence objectDataSequence;
   private final String comment;

   public ObjectStoreData(AlgorithmIdentifier var1, Date var2, Date var3, ObjectDataSequence var4, String var5) {
      this.version = BigInteger.valueOf(1L);
      this.integrityAlgorithm = var1;
      this.creationDate = new DERGeneralizedTime(var2);
      this.lastModifiedDate = new DERGeneralizedTime(var3);
      this.objectDataSequence = var4;
      this.comment = var5;
   }

   private ObjectStoreData(ASN1Sequence var1) {
      this.version = ASN1Integer.getInstance(var1.getObjectAt(0)).getValue();
      this.integrityAlgorithm = AlgorithmIdentifier.getInstance(var1.getObjectAt(1));
      this.creationDate = ASN1GeneralizedTime.getInstance(var1.getObjectAt(2));
      this.lastModifiedDate = ASN1GeneralizedTime.getInstance(var1.getObjectAt(3));
      this.objectDataSequence = ObjectDataSequence.getInstance(var1.getObjectAt(4));
      this.comment = var1.size() == 6 ? ASN1UTF8String.getInstance(var1.getObjectAt(5)).getString() : null;
   }

   public static ObjectStoreData getInstance(Object var0) {
      if (var0 instanceof ObjectStoreData) {
         return (ObjectStoreData)var0;
      } else {
         return var0 != null ? new ObjectStoreData(ASN1Sequence.getInstance(var0)) : null;
      }
   }

   public String getComment() {
      return this.comment;
   }

   public ASN1GeneralizedTime getCreationDate() {
      return this.creationDate;
   }

   public AlgorithmIdentifier getIntegrityAlgorithm() {
      return this.integrityAlgorithm;
   }

   public ASN1GeneralizedTime getLastModifiedDate() {
      return this.lastModifiedDate;
   }

   public ObjectDataSequence getObjectDataSequence() {
      return this.objectDataSequence;
   }

   public BigInteger getVersion() {
      return this.version;
   }

   @Override
   public ASN1Primitive toASN1Primitive() {
      ASN1EncodableVector var1 = new ASN1EncodableVector(6);
      var1.add(new ASN1Integer(this.version));
      var1.add(this.integrityAlgorithm);
      var1.add(this.creationDate);
      var1.add(this.lastModifiedDate);
      var1.add(this.objectDataSequence);
      if (this.comment != null) {
         var1.add(new DERUTF8String(this.comment));
      }

      return new DERSequence(var1);
   }
}
