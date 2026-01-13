package org.bouncycastle.asn1.cmp;

import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.DEROctetString;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;

public class PBMParameter extends ASN1Object {
   private final ASN1OctetString salt;
   private final AlgorithmIdentifier owf;
   private final ASN1Integer iterationCount;
   private final AlgorithmIdentifier mac;

   private PBMParameter(ASN1Sequence var1) {
      this.salt = ASN1OctetString.getInstance(var1.getObjectAt(0));
      this.owf = AlgorithmIdentifier.getInstance(var1.getObjectAt(1));
      this.iterationCount = ASN1Integer.getInstance(var1.getObjectAt(2));
      this.mac = AlgorithmIdentifier.getInstance(var1.getObjectAt(3));
   }

   public PBMParameter(byte[] var1, AlgorithmIdentifier var2, int var3, AlgorithmIdentifier var4) {
      this(new DEROctetString(var1), var2, new ASN1Integer(var3), var4);
   }

   public PBMParameter(ASN1OctetString var1, AlgorithmIdentifier var2, ASN1Integer var3, AlgorithmIdentifier var4) {
      this.salt = var1;
      this.owf = var2;
      this.iterationCount = var3;
      this.mac = var4;
   }

   public static PBMParameter getInstance(Object var0) {
      if (var0 instanceof PBMParameter) {
         return (PBMParameter)var0;
      } else {
         return var0 != null ? new PBMParameter(ASN1Sequence.getInstance(var0)) : null;
      }
   }

   public ASN1OctetString getSalt() {
      return this.salt;
   }

   public AlgorithmIdentifier getOwf() {
      return this.owf;
   }

   public ASN1Integer getIterationCount() {
      return this.iterationCount;
   }

   public AlgorithmIdentifier getMac() {
      return this.mac;
   }

   @Override
   public ASN1Primitive toASN1Primitive() {
      ASN1EncodableVector var1 = new ASN1EncodableVector(4);
      var1.add(this.salt);
      var1.add(this.owf);
      var1.add(this.iterationCount);
      var1.add(this.mac);
      return new DERSequence(var1);
   }
}
