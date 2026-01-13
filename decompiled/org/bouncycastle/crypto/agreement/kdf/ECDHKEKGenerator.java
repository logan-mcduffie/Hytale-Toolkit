package org.bouncycastle.crypto.agreement.kdf;

import java.io.IOException;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.DERNull;
import org.bouncycastle.asn1.DEROctetString;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.DERTaggedObject;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.crypto.DataLengthException;
import org.bouncycastle.crypto.DerivationParameters;
import org.bouncycastle.crypto.Digest;
import org.bouncycastle.crypto.DigestDerivationFunction;
import org.bouncycastle.crypto.generators.KDF2BytesGenerator;
import org.bouncycastle.crypto.params.KDFParameters;
import org.bouncycastle.util.Pack;

public class ECDHKEKGenerator implements DigestDerivationFunction {
   private DigestDerivationFunction kdf;
   private ASN1ObjectIdentifier algorithm;
   private int keySize;
   private byte[] z;

   public ECDHKEKGenerator(Digest var1) {
      this.kdf = new KDF2BytesGenerator(var1);
   }

   @Override
   public void init(DerivationParameters var1) {
      DHKDFParameters var2 = (DHKDFParameters)var1;
      this.algorithm = var2.getAlgorithm();
      this.keySize = var2.getKeySize();
      this.z = var2.getZ();
   }

   @Override
   public Digest getDigest() {
      return this.kdf.getDigest();
   }

   @Override
   public int generateBytes(byte[] var1, int var2, int var3) throws DataLengthException, IllegalArgumentException {
      if (var2 + var3 > var1.length) {
         throw new DataLengthException("output buffer too small");
      } else {
         ASN1EncodableVector var4 = new ASN1EncodableVector();
         var4.add(new AlgorithmIdentifier(this.algorithm, DERNull.INSTANCE));
         var4.add(new DERTaggedObject(true, 2, new DEROctetString(Pack.intToBigEndian(this.keySize))));

         try {
            this.kdf.init(new KDFParameters(this.z, new DERSequence(var4).getEncoded("DER")));
         } catch (IOException var6) {
            throw new IllegalArgumentException("unable to initialise kdf: " + var6.getMessage());
         }

         return this.kdf.generateBytes(var1, var2, var3);
      }
   }
}
