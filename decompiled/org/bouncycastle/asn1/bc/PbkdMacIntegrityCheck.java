package org.bouncycastle.asn1.bc;

import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.DEROctetString;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.pkcs.KeyDerivationFunc;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.util.Arrays;

public class PbkdMacIntegrityCheck extends ASN1Object {
   private final AlgorithmIdentifier macAlgorithm;
   private final KeyDerivationFunc pbkdAlgorithm;
   private final ASN1OctetString mac;

   public PbkdMacIntegrityCheck(AlgorithmIdentifier var1, KeyDerivationFunc var2, byte[] var3) {
      this.macAlgorithm = var1;
      this.pbkdAlgorithm = var2;
      this.mac = new DEROctetString(Arrays.clone(var3));
   }

   private PbkdMacIntegrityCheck(ASN1Sequence var1) {
      this.macAlgorithm = AlgorithmIdentifier.getInstance(var1.getObjectAt(0));
      this.pbkdAlgorithm = KeyDerivationFunc.getInstance(var1.getObjectAt(1));
      this.mac = ASN1OctetString.getInstance(var1.getObjectAt(2));
   }

   public static PbkdMacIntegrityCheck getInstance(Object var0) {
      if (var0 instanceof PbkdMacIntegrityCheck) {
         return (PbkdMacIntegrityCheck)var0;
      } else {
         return var0 != null ? new PbkdMacIntegrityCheck(ASN1Sequence.getInstance(var0)) : null;
      }
   }

   public AlgorithmIdentifier getMacAlgorithm() {
      return this.macAlgorithm;
   }

   public KeyDerivationFunc getPbkdAlgorithm() {
      return this.pbkdAlgorithm;
   }

   public byte[] getMac() {
      return Arrays.clone(this.mac.getOctets());
   }

   @Override
   public ASN1Primitive toASN1Primitive() {
      ASN1EncodableVector var1 = new ASN1EncodableVector(3);
      var1.add(this.macAlgorithm);
      var1.add(this.pbkdAlgorithm);
      var1.add(this.mac);
      return new DERSequence(var1);
   }
}
