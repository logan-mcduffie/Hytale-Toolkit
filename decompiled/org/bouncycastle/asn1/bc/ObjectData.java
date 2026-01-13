package org.bouncycastle.asn1.bc;

import java.math.BigInteger;
import java.util.Date;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1GeneralizedTime;
import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.ASN1UTF8String;
import org.bouncycastle.asn1.DERGeneralizedTime;
import org.bouncycastle.asn1.DEROctetString;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.DERUTF8String;
import org.bouncycastle.util.Arrays;

public class ObjectData extends ASN1Object {
   private final BigInteger type;
   private final String identifier;
   private final ASN1GeneralizedTime creationDate;
   private final ASN1GeneralizedTime lastModifiedDate;
   private final ASN1OctetString data;
   private final String comment;

   private ObjectData(ASN1Sequence var1) {
      this.type = ASN1Integer.getInstance(var1.getObjectAt(0)).getValue();
      this.identifier = ASN1UTF8String.getInstance(var1.getObjectAt(1)).getString();
      this.creationDate = ASN1GeneralizedTime.getInstance(var1.getObjectAt(2));
      this.lastModifiedDate = ASN1GeneralizedTime.getInstance(var1.getObjectAt(3));
      this.data = ASN1OctetString.getInstance(var1.getObjectAt(4));
      this.comment = var1.size() == 6 ? ASN1UTF8String.getInstance(var1.getObjectAt(5)).getString() : null;
   }

   public ObjectData(BigInteger var1, String var2, Date var3, Date var4, byte[] var5, String var6) {
      this.type = var1;
      this.identifier = var2;
      this.creationDate = new DERGeneralizedTime(var3);
      this.lastModifiedDate = new DERGeneralizedTime(var4);
      this.data = new DEROctetString(Arrays.clone(var5));
      this.comment = var6;
   }

   public static ObjectData getInstance(Object var0) {
      if (var0 instanceof ObjectData) {
         return (ObjectData)var0;
      } else {
         return var0 != null ? new ObjectData(ASN1Sequence.getInstance(var0)) : null;
      }
   }

   public String getComment() {
      return this.comment;
   }

   public ASN1GeneralizedTime getCreationDate() {
      return this.creationDate;
   }

   public byte[] getData() {
      return Arrays.clone(this.data.getOctets());
   }

   public String getIdentifier() {
      return this.identifier;
   }

   public ASN1GeneralizedTime getLastModifiedDate() {
      return this.lastModifiedDate;
   }

   public BigInteger getType() {
      return this.type;
   }

   @Override
   public ASN1Primitive toASN1Primitive() {
      ASN1EncodableVector var1 = new ASN1EncodableVector(6);
      var1.add(new ASN1Integer(this.type));
      var1.add(new DERUTF8String(this.identifier));
      var1.add(this.creationDate);
      var1.add(this.lastModifiedDate);
      var1.add(this.data);
      if (this.comment != null) {
         var1.add(new DERUTF8String(this.comment));
      }

      return new DERSequence(var1);
   }
}
