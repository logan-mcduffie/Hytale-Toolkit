package org.bouncycastle.asn1.ess;

import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.DEROctetString;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.nist.NISTObjectIdentifiers;
import org.bouncycastle.asn1.oiw.OIWObjectIdentifiers;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.asn1.x509.IssuerSerial;
import org.bouncycastle.util.Arrays;

public class ESSCertIDv2 extends ASN1Object {
   private static final AlgorithmIdentifier DEFAULT_HASH_ALGORITHM = new AlgorithmIdentifier(NISTObjectIdentifiers.id_sha256);
   private AlgorithmIdentifier hashAlgorithm;
   private ASN1OctetString certHash;
   private IssuerSerial issuerSerial;

   public static ESSCertIDv2 from(ESSCertID var0) {
      AlgorithmIdentifier var1 = new AlgorithmIdentifier(OIWObjectIdentifiers.idSHA1);
      return new ESSCertIDv2(var1, var0.getCertHashObject(), var0.getIssuerSerial());
   }

   public static ESSCertIDv2 getInstance(Object var0) {
      if (var0 instanceof ESSCertIDv2) {
         return (ESSCertIDv2)var0;
      } else {
         return var0 != null ? new ESSCertIDv2(ASN1Sequence.getInstance(var0)) : null;
      }
   }

   private ESSCertIDv2(ASN1Sequence var1) {
      if (var1.size() > 3) {
         throw new IllegalArgumentException("Bad sequence size: " + var1.size());
      } else {
         int var2 = 0;
         if (var1.getObjectAt(0) instanceof ASN1OctetString) {
            this.hashAlgorithm = DEFAULT_HASH_ALGORITHM;
         } else {
            this.hashAlgorithm = AlgorithmIdentifier.getInstance(var1.getObjectAt(var2++));
         }

         this.certHash = ASN1OctetString.getInstance(var1.getObjectAt(var2++));
         if (var1.size() > var2) {
            this.issuerSerial = IssuerSerial.getInstance(var1.getObjectAt(var2));
         }
      }
   }

   public ESSCertIDv2(byte[] var1) {
      this(null, var1, null);
   }

   public ESSCertIDv2(AlgorithmIdentifier var1, byte[] var2) {
      this(var1, var2, null);
   }

   public ESSCertIDv2(byte[] var1, IssuerSerial var2) {
      this(null, var1, var2);
   }

   public ESSCertIDv2(AlgorithmIdentifier var1, byte[] var2, IssuerSerial var3) {
      if (var1 == null) {
         var1 = DEFAULT_HASH_ALGORITHM;
      }

      this.hashAlgorithm = var1;
      this.certHash = new DEROctetString(Arrays.clone(var2));
      this.issuerSerial = var3;
   }

   public ESSCertIDv2(AlgorithmIdentifier var1, ASN1OctetString var2, IssuerSerial var3) {
      if (var1 == null) {
         var1 = DEFAULT_HASH_ALGORITHM;
      }

      if (var2 == null) {
         throw new NullPointerException("'certHash' cannot be null");
      } else {
         this.hashAlgorithm = var1;
         this.certHash = var2;
         this.issuerSerial = var3;
      }
   }

   public AlgorithmIdentifier getHashAlgorithm() {
      return this.hashAlgorithm;
   }

   public ASN1OctetString getCertHashObject() {
      return this.certHash;
   }

   public byte[] getCertHash() {
      return Arrays.clone(this.certHash.getOctets());
   }

   public IssuerSerial getIssuerSerial() {
      return this.issuerSerial;
   }

   @Override
   public ASN1Primitive toASN1Primitive() {
      ASN1EncodableVector var1 = new ASN1EncodableVector(3);
      if (!DEFAULT_HASH_ALGORITHM.equals(this.hashAlgorithm)) {
         var1.add(this.hashAlgorithm);
      }

      var1.add(this.certHash);
      if (this.issuerSerial != null) {
         var1.add(this.issuerSerial);
      }

      return new DERSequence(var1);
   }
}
