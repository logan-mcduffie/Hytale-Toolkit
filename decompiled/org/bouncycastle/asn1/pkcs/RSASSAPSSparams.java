package org.bouncycastle.asn1.pkcs;

import java.math.BigInteger;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.ASN1TaggedObject;
import org.bouncycastle.asn1.DERNull;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.DERTaggedObject;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.internal.asn1.oiw.OIWObjectIdentifiers;

public class RSASSAPSSparams extends ASN1Object {
   private AlgorithmIdentifier hashAlgorithm;
   private AlgorithmIdentifier maskGenAlgorithm;
   private ASN1Integer saltLength;
   private ASN1Integer trailerField;
   public static final AlgorithmIdentifier DEFAULT_HASH_ALGORITHM = new AlgorithmIdentifier(OIWObjectIdentifiers.idSHA1, DERNull.INSTANCE);
   public static final AlgorithmIdentifier DEFAULT_MASK_GEN_FUNCTION = new AlgorithmIdentifier(PKCSObjectIdentifiers.id_mgf1, DEFAULT_HASH_ALGORITHM);
   public static final ASN1Integer DEFAULT_SALT_LENGTH = new ASN1Integer(20L);
   public static final ASN1Integer DEFAULT_TRAILER_FIELD = new ASN1Integer(1L);

   public static RSASSAPSSparams getInstance(Object var0) {
      if (var0 instanceof RSASSAPSSparams) {
         return (RSASSAPSSparams)var0;
      } else {
         return var0 != null ? new RSASSAPSSparams(ASN1Sequence.getInstance(var0)) : null;
      }
   }

   public RSASSAPSSparams() {
      this.hashAlgorithm = DEFAULT_HASH_ALGORITHM;
      this.maskGenAlgorithm = DEFAULT_MASK_GEN_FUNCTION;
      this.saltLength = DEFAULT_SALT_LENGTH;
      this.trailerField = DEFAULT_TRAILER_FIELD;
   }

   public RSASSAPSSparams(AlgorithmIdentifier var1, AlgorithmIdentifier var2, ASN1Integer var3, ASN1Integer var4) {
      this.hashAlgorithm = var1;
      this.maskGenAlgorithm = var2;
      this.saltLength = var3;
      this.trailerField = var4;
   }

   private RSASSAPSSparams(ASN1Sequence var1) {
      this.hashAlgorithm = DEFAULT_HASH_ALGORITHM;
      this.maskGenAlgorithm = DEFAULT_MASK_GEN_FUNCTION;
      this.saltLength = DEFAULT_SALT_LENGTH;
      this.trailerField = DEFAULT_TRAILER_FIELD;

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
               this.saltLength = ASN1Integer.getInstance(var3, true);
               break;
            case 3:
               this.trailerField = ASN1Integer.getInstance(var3, true);
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

   public BigInteger getSaltLength() {
      return this.saltLength.getValue();
   }

   public BigInteger getTrailerField() {
      return this.trailerField.getValue();
   }

   @Override
   public ASN1Primitive toASN1Primitive() {
      ASN1EncodableVector var1 = new ASN1EncodableVector(4);
      if (!this.hashAlgorithm.equals(DEFAULT_HASH_ALGORITHM)) {
         var1.add(new DERTaggedObject(true, 0, this.hashAlgorithm));
      }

      if (!this.maskGenAlgorithm.equals(DEFAULT_MASK_GEN_FUNCTION)) {
         var1.add(new DERTaggedObject(true, 1, this.maskGenAlgorithm));
      }

      if (!this.saltLength.equals(DEFAULT_SALT_LENGTH)) {
         var1.add(new DERTaggedObject(true, 2, this.saltLength));
      }

      if (!this.trailerField.equals(DEFAULT_TRAILER_FIELD)) {
         var1.add(new DERTaggedObject(true, 3, this.trailerField));
      }

      return new DERSequence(var1);
   }
}
