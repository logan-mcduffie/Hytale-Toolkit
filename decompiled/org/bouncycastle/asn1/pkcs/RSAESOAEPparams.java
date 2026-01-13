package org.bouncycastle.asn1.pkcs;

import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.ASN1TaggedObject;
import org.bouncycastle.asn1.DERNull;
import org.bouncycastle.asn1.DEROctetString;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.DERTaggedObject;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.internal.asn1.oiw.OIWObjectIdentifiers;

public class RSAESOAEPparams extends ASN1Object {
   private AlgorithmIdentifier hashAlgorithm;
   private AlgorithmIdentifier maskGenAlgorithm;
   private AlgorithmIdentifier pSourceAlgorithm;
   public static final AlgorithmIdentifier DEFAULT_HASH_ALGORITHM = new AlgorithmIdentifier(OIWObjectIdentifiers.idSHA1, DERNull.INSTANCE);
   public static final AlgorithmIdentifier DEFAULT_MASK_GEN_FUNCTION = new AlgorithmIdentifier(PKCSObjectIdentifiers.id_mgf1, DEFAULT_HASH_ALGORITHM);
   public static final AlgorithmIdentifier DEFAULT_P_SOURCE_ALGORITHM = new AlgorithmIdentifier(
      PKCSObjectIdentifiers.id_pSpecified, new DEROctetString(new byte[0])
   );

   public static RSAESOAEPparams getInstance(Object var0) {
      if (var0 instanceof RSAESOAEPparams) {
         return (RSAESOAEPparams)var0;
      } else {
         return var0 != null ? new RSAESOAEPparams(ASN1Sequence.getInstance(var0)) : null;
      }
   }

   public RSAESOAEPparams() {
      this.hashAlgorithm = DEFAULT_HASH_ALGORITHM;
      this.maskGenAlgorithm = DEFAULT_MASK_GEN_FUNCTION;
      this.pSourceAlgorithm = DEFAULT_P_SOURCE_ALGORITHM;
   }

   public RSAESOAEPparams(AlgorithmIdentifier var1, AlgorithmIdentifier var2, AlgorithmIdentifier var3) {
      this.hashAlgorithm = var1;
      this.maskGenAlgorithm = var2;
      this.pSourceAlgorithm = var3;
   }

   private RSAESOAEPparams(ASN1Sequence var1) {
      this.hashAlgorithm = DEFAULT_HASH_ALGORITHM;
      this.maskGenAlgorithm = DEFAULT_MASK_GEN_FUNCTION;
      this.pSourceAlgorithm = DEFAULT_P_SOURCE_ALGORITHM;

      for (int var2 = 0; var2 != var1.size(); var2++) {
         ASN1TaggedObject var3 = (ASN1TaggedObject)var1.getObjectAt(var2);
         switch (var3.getTagNo()) {
            case 0:
               this.hashAlgorithm = AlgorithmIdentifier.getInstance(var3, true);
               break;
            case 1:
               this.maskGenAlgorithm = AlgorithmIdentifier.getInstance(var3, true);
               break;
            case 2:
               this.pSourceAlgorithm = AlgorithmIdentifier.getInstance(var3, true);
               break;
            default:
               throw new IllegalArgumentException("unknown tag");
         }
      }
   }

   public AlgorithmIdentifier getHashAlgorithm() {
      return this.hashAlgorithm;
   }

   public AlgorithmIdentifier getMaskGenAlgorithm() {
      return this.maskGenAlgorithm;
   }

   public AlgorithmIdentifier getPSourceAlgorithm() {
      return this.pSourceAlgorithm;
   }

   public RSAESOAEPparams withDefaultPSource() {
      return DEFAULT_P_SOURCE_ALGORITHM == this.pSourceAlgorithm
         ? this
         : new RSAESOAEPparams(this.hashAlgorithm, this.maskGenAlgorithm, DEFAULT_P_SOURCE_ALGORITHM);
   }

   @Override
   public ASN1Primitive toASN1Primitive() {
      ASN1EncodableVector var1 = new ASN1EncodableVector(3);
      if (!this.hashAlgorithm.equals(DEFAULT_HASH_ALGORITHM)) {
         var1.add(new DERTaggedObject(true, 0, this.hashAlgorithm));
      }

      if (!this.maskGenAlgorithm.equals(DEFAULT_MASK_GEN_FUNCTION)) {
         var1.add(new DERTaggedObject(true, 1, this.maskGenAlgorithm));
      }

      if (!this.pSourceAlgorithm.equals(DEFAULT_P_SOURCE_ALGORITHM)) {
         var1.add(new DERTaggedObject(true, 2, this.pSourceAlgorithm));
      }

      return new DERSequence(var1);
   }
}
