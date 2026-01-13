package org.bouncycastle.asn1.pkcs;

import java.math.BigInteger;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.DEROctetString;
import org.bouncycastle.asn1.DERSequence;

public class RC2CBCParameter extends ASN1Object {
   ASN1Integer version;
   ASN1OctetString iv;

   public static RC2CBCParameter getInstance(Object var0) {
      if (var0 instanceof RC2CBCParameter) {
         return (RC2CBCParameter)var0;
      } else {
         return var0 != null ? new RC2CBCParameter(ASN1Sequence.getInstance(var0)) : null;
      }
   }

   public RC2CBCParameter(byte[] var1) {
      this.version = null;
      this.iv = new DEROctetString(var1);
   }

   public RC2CBCParameter(int var1, byte[] var2) {
      this.version = new ASN1Integer(var1);
      this.iv = new DEROctetString(var2);
   }

   private RC2CBCParameter(ASN1Sequence var1) {
      if (var1.size() == 1) {
         this.version = null;
         this.iv = (ASN1OctetString)var1.getObjectAt(0);
      } else {
         this.version = (ASN1Integer)var1.getObjectAt(0);
         this.iv = (ASN1OctetString)var1.getObjectAt(1);
      }
   }

   public BigInteger getRC2ParameterVersion() {
      return this.version == null ? null : this.version.getValue();
   }

   public ASN1Integer getRC2ParameterVersionData() {
      return this.version;
   }

   public byte[] getIV() {
      return this.iv.getOctets();
   }

   @Override
   public ASN1Primitive toASN1Primitive() {
      ASN1EncodableVector var1 = new ASN1EncodableVector(2);
      if (this.version != null) {
         var1.add(this.version);
      }

      var1.add(this.iv);
      return new DERSequence(var1);
   }
}
